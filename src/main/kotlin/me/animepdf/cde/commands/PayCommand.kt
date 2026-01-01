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

class PayCommand(val plugin: CoinsDiscordEngine) {
    fun createCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("pay")
            .then(
                Commands.argument("player", StringArgumentType.string())
                    .suggests { ctx, builder ->
                        for (player in Bukkit.getOnlinePlayers()) {
                            if(player.name == ctx.source.sender.name)
                                continue
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

    }

    private fun execute(ctx: CommandContext<CommandSourceStack>, reason: String?): Int {
        val player = ctx.getArgument("player", String::class.java)
        val amount = ctx.getArgument("amount", Double::class.java)

        val currency: Currency? = CoinsEngineAPI.getCurrency(plugin.configContainer.generalConfig.currency)
        if (currency == null || ctx.source.sender !is Player) {
            ctx.source.sender.sendPlainMessage(plugin.configContainer.languageConfig.somethingWentWrong)
            return 0
        }

        val oldBalanceSender = CoinsEngineAPI.getBalance(ctx.source.sender as Player, currency)
        var success = CoinsEngineAPI.plugin().currencyManager.sendCurrency(ctx.source.sender as Player, player, currency, amount)
        val newBalanceSender = CoinsEngineAPI.getBalance(ctx.source.sender as Player, currency)

        success = success && newBalanceSender == oldBalanceSender - amount // I fucking hate CoinsEngine developer, go think a little or smth

        if(success) {
            DiscordSRV.getPlugin().jda
                .getTextChannelById(plugin.configContainer.generalConfig.channelId)
                ?.sendMessage(
                    if (reason == null)
                        plugin.configContainer.languageConfig.transactionMessage
                            .replace("{from}", ctx.source.sender.name)
                            .replace("{to}", player)
                            .replace("{amount}", currency.formatValue(amount))
                    else
                        plugin.configContainer.languageConfig.transactionMessageReason
                            .replace("{from}", ctx.source.sender.name)
                            .replace("{to}", player)
                            .replace("{amount}", currency.formatValue(amount))
                            .replace("{reason}", reason)
                )?.queue()
        }

        return 1;
    }
}