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
import su.nightexpress.coinsengine.api.CoinsEngineAPI
import su.nightexpress.coinsengine.api.currency.Currency
import su.nightexpress.coinsengine.config.Perms
import su.nightexpress.coinsengine.currency.operation.NotificationTarget
import su.nightexpress.coinsengine.currency.operation.OperationContext
import su.nightexpress.coinsengine.currency.operation.OperationResult
import su.nightexpress.coinsengine.data.impl.CoinsUser
import su.nightexpress.nightcore.core.config.CoreLang

class RemoveCommand(val plugin: CoinsDiscordEngine) {
    fun createCommand(): List<LiteralArgumentBuilder<CommandSourceStack>> {
        val commands: ArrayList<LiteralArgumentBuilder<CommandSourceStack>> = ArrayList()

        for (alias in plugin.configContainer.generalConfig.removeAliases) {
            commands.add(
                Commands.literal(alias)
                    .requires { it.sender.hasPermission(Perms.COMMAND_CURRENCY_TAKE.name) }
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

        val currency: Currency? = CoinsEngineAPI.getCurrency(plugin.conf().currencyId)
        if (currency == null) {
            from.sendPlainMessage(plugin.lang().somethingWentWrong)
            return 0
        }

        CoinsEngineAPI.getUserManager().manageUser(player) { user: CoinsUser? ->
            if (user == null) {
                CoreLang.ERROR_INVALID_PLAYER.withPrefix(CoinsEngineAPI.plugin()).send(from);
                return@manageUser
            }
            val operationContext: OperationContext = OperationContext.of(from)
                .silentFor(NotificationTarget.CONSOLE_LOGGER)
                .silentFor(NotificationTarget.USER, false)
                .silentFor(NotificationTarget.EXECUTOR, false)
            val result = CoinsEngineAPI.plugin().currencyManager.remove(operationContext, user, currency, amount)

            if (result == OperationResult.SUCCESS && plugin.configContainer.generalConfig.removingNotification) {
                DiscordSRV.getPlugin().jda
                    .getTextChannelById(plugin.configContainer.generalConfig.channelId)
                    ?.sendMessage(
                        if (reason == null)
                            plugin.configContainer.languageConfig.removeMessage
                                .replace("{target}", player)
                                .replace("{source}", from.name)
                                .replace("{amount}", currency.formatValue(amount))
                        else
                            plugin.configContainer.languageConfig.removeMessageReason
                                .replace("{target}", player)
                                .replace("{source}", from.name)
                                .replace("{amount}", currency.formatValue(amount))
                                .replace("{reason}", reason)
                    )?.queue()
            }
        }

        return 1
    }
}