package me.animepdf.cde.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import me.animepdf.cde.CoinsDiscordEngine
import me.animepdf.cde.utils.CommandUtils
import me.animepdf.cde.utils.CurrencyUtils
import me.animepdf.cde.utils.DiscordLogger
import su.nightexpress.coinsengine.api.CoinsEngineAPI
import su.nightexpress.coinsengine.api.currency.Currency
import su.nightexpress.coinsengine.config.Perms
import su.nightexpress.coinsengine.currency.operation.NotificationTarget
import su.nightexpress.coinsengine.currency.operation.OperationContext
import su.nightexpress.coinsengine.currency.operation.OperationResult
import su.nightexpress.coinsengine.data.impl.CoinsUser
import su.nightexpress.nightcore.core.config.CoreLang

class RemoveCommand(
    private val plugin: CoinsDiscordEngine,
    private val discordLogger: DiscordLogger
) {
    fun createCommand(): List<LiteralArgumentBuilder<CommandSourceStack>> {
        return CommandUtils.buildEconomyCommand(
            aliases = plugin.conf().alias.remove,
            permission = Perms.COMMAND_CURRENCY_TAKE.name,
            executor = ::execute
        )
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

            if (result == OperationResult.SUCCESS && plugin.conf().notification.remove) {
                val config = plugin.lang()
                val messageTemplate = if (reason == null) config.removeMessage else config.removeMessageReason

                val placeholders = mapOf(
                    "source" to from.name,
                    "target" to player,
                    "amount" to currency.formatValue(amount),
                    "currency" to CurrencyUtils.formatCurrency(config.currencyForms, amount),
                    "reason" to (reason ?: "")
                )

                discordLogger.sendLog(messageTemplate, placeholders)
            }
        }

        return 1
    }
}