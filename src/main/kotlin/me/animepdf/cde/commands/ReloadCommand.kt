package me.animepdf.cde.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import me.animepdf.cde.CoinsDiscordEngine
import su.nightexpress.coinsengine.api.CoinsEngineAPI
import su.nightexpress.coinsengine.config.Perms
import su.nightexpress.nightcore.core.config.CoreLang

class ReloadCommand(val plugin: CoinsDiscordEngine) {
    fun createCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("reload")
            .requires { it.sender.hasPermission(Perms.COMMAND_RELOAD.name) }
            .executes { execute(it) }
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        plugin.configContainer.reloadConfigs()
        CoreLang.PLUGIN_RELOADED.withPrefix(CoinsEngineAPI.plugin()).send(ctx.source.sender)
        return 1
    }
}