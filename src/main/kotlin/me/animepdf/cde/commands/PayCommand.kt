package me.animepdf.cde.commands

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import github.scarsz.discordsrv.DiscordSRV
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import me.animepdf.cde.CoinsDiscordEngine
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import su.nightexpress.coinsengine.Placeholders
import su.nightexpress.coinsengine.api.CoinsEngineAPI
import su.nightexpress.coinsengine.api.currency.Currency
import su.nightexpress.coinsengine.config.Lang
import su.nightexpress.coinsengine.currency.operation.impl.SendOperation
import su.nightexpress.coinsengine.data.impl.CoinsUser
import su.nightexpress.nightcore.util.placeholder.Replacer

class PayCommand(val plugin: CoinsDiscordEngine) {
    fun createCommand(): List<LiteralArgumentBuilder<CommandSourceStack>> {
        val commands: ArrayList<LiteralArgumentBuilder<CommandSourceStack>> = ArrayList()

        for (alias in plugin.configContainer.generalConfig.payAliases) {
            commands.add(
                Commands.literal(alias)
                    .then(
                        Commands.argument("player", StringArgumentType.string())
                            .suggests { ctx, builder ->
                                for (player in Bukkit.getOnlinePlayers()) {
                                    if (player.name == ctx.source.sender.name)
                                        continue
                                    builder.suggest(player.name)
                                }
                                builder.buildFuture()
                            }
                            .then(
                                Commands.argument("amount", DoubleArgumentType.doubleArg(1.0))
                                    .executes { execute(it, null) }
                                    .then(
                                        Commands.argument("purpose", StringArgumentType.greedyString())
                                            .executes { execute(it, it.getArgument("purpose", String::class.java)) })
                            )
                    )
            )
        }

        return commands
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>, purpose: String?): Int {
        val from = ctx.source.sender
        val player = ctx.getArgument("player", String::class.java)
        val amount = ctx.getArgument("amount", Double::class.java)

        val currency: Currency? = CoinsEngineAPI.getCurrency(plugin.configContainer.generalConfig.currency)
        if (currency == null || from !is Player) {
            from.sendPlainMessage(plugin.configContainer.languageConfig.somethingWentWrong)
            return 0
        }

        if (from.name.equals(player, ignoreCase = true)) {
            Lang.ERROR_COMMAND_NOT_YOURSELF.message.send(from)
            return 0
        }

        val minAmount = currency.minTransferAmount
        if (minAmount > 0 && amount < minAmount) {
            currency.sendPrefixed(Lang.CURRENCY_SEND_ERROR_TOO_LOW, from) { replacer: Replacer? ->
                replacer!!.replace(
                    Placeholders.GENERIC_AMOUNT, currency.format(minAmount)
                )
            }
            return 0
        }

        val fromUser: CoinsUser = CoinsEngineAPI.getUserManager().getOrFetch(from)
        if (amount > fromUser.getBalance(currency)) {
            currency.sendPrefixed(
                Lang.CURRENCY_SEND_ERROR_NOT_ENOUGH,
                from
            ) { replacer: Replacer? -> replacer!!.replace(currency.replacePlaceholders()) }
            return 0
        }
        CoinsEngineAPI.getUserManager().manageUser(player) { targetUser: CoinsUser? ->
            if (targetUser == null) {
                Lang.ERROR_INVALID_PLAYER.getMessage(CoinsEngineAPI.plugin()).send(from)
                return@manageUser
            }
            val settings = targetUser.getSettings(currency)
            if (!settings.isPaymentsEnabled) {
                currency.sendPrefixed(Lang.CURRENCY_SEND_ERROR_NO_PAYMENTS, from) { replacer: Replacer? ->
                    replacer!!
                        .replace(Placeholders.PLAYER_NAME, targetUser.getName())
                        .replace(currency.replacePlaceholders())
                }
                return@manageUser
            }

            val operation = SendOperation(currency, amount, targetUser, from, fromUser)
            val operationSuccess = CoinsEngineAPI.plugin().currencyManager.performOperation(operation)
            CoinsEngineAPI.getUserManager().save(fromUser)

            if (operationSuccess && plugin.configContainer.generalConfig.paymentNotification) {
                val channel =
                    DiscordSRV.getPlugin().jda.getTextChannelById(plugin.configContainer.generalConfig.channelId)
                val message = channel?.sendMessage(
                    if (purpose == null)
                        plugin.configContainer.languageConfig.transactionMessage
                            .replace("{from}", from.name)
                            .replace("{to}", player)
                            .replace("{amount}", currency.formatValue(amount))
                    else
                        plugin.configContainer.languageConfig.transactionMessagePurpose
                            .replace("{from}", from.name)
                            .replace("{to}", player)
                            .replace("{amount}", currency.formatValue(amount))
                            .replace("{purpose}", purpose)
                )
                message?.queue()
            }
        }
        return 1
    }
}