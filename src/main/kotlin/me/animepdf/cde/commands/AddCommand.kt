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
import su.nightexpress.coinsengine.api.CoinsEngineAPI
import su.nightexpress.coinsengine.api.currency.Currency
import su.nightexpress.coinsengine.config.Lang
import su.nightexpress.coinsengine.config.Perms
import su.nightexpress.coinsengine.currency.CurrencyOperations

class AddCommand(val plugin: CoinsDiscordEngine) {
    fun createCommand(): List<LiteralArgumentBuilder<CommandSourceStack>> {
        val commands: ArrayList<LiteralArgumentBuilder<CommandSourceStack>> = ArrayList()

        for (alias in plugin.configContainer.generalConfig.addAliases) {
            commands.add(
                Commands.literal(alias)
                    .requires { it.sender.hasPermission(Perms.COMMAND_CURRENCY_GIVE.name) }
                    .then(
                        Commands.argument("player", StringArgumentType.string())
                            .suggests { ctx, builder ->
                                for (player in Bukkit.getOnlinePlayers()) {
                                    builder.suggest(player.name)
                                }
                                builder.buildFuture()
                            }
                            .then(
                                Commands.argument("amount", DoubleArgumentType.doubleArg(1.0))
                                    .executes { execute(it, null) }
                                    .then(
                                        Commands.argument("reason", StringArgumentType.greedyString())
                                            .executes { execute(it, it.getArgument("reason", String::class.java)) })
                            )
                    )
            )
        }

        return commands
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>, reason: String?): Int {
        val from = ctx.source.sender
        val player = ctx.getArgument("player", String::class.java)
        val amount = ctx.getArgument("amount", Double::class.java)

        val currency: Currency? = CoinsEngineAPI.getCurrency(plugin.configContainer.generalConfig.currency)
        if (currency == null || from !is Player) {
            from.sendPlainMessage(plugin.configContainer.languageConfig.somethingWentWrong)
            return 0
        }

        val user = CoinsEngineAPI.getUserManager().getOrFetch(player)
        if (user == null) {
            Lang.ECONOMY_ERROR_INVALID_PLAYER.asComponent().send(from)
            return 0
        }

        val operation = CurrencyOperations.forAdd(currency, amount, user, from)
        val success = CoinsEngineAPI.plugin().currencyManager.performOperation(operation)

        if (success && plugin.configContainer.generalConfig.addingNotification) {
            DiscordSRV.getPlugin().jda
                .getTextChannelById(plugin.configContainer.generalConfig.channelId)
                ?.sendMessage(
                    if (reason == null)
                        plugin.configContainer.languageConfig.addMessage
                            .replace("{target}", player)
                            .replace("{source}", from.name)
                            .replace("{amount}", currency.formatValue(amount))
                    else
                        plugin.configContainer.languageConfig.addMessageReason
                            .replace("{target}", player)
                            .replace("{source}", from.name)
                            .replace("{amount}", currency.formatValue(amount))
                            .replace("{reason}", reason)
                )?.queue()
        }

        return 1;
    }
}