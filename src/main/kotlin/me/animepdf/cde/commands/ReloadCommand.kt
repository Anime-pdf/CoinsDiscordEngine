package me.animepdf.cde.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import me.animepdf.cde.CoinsDiscordEngine
import su.nightexpress.coinsengine.config.Lang
import su.nightexpress.coinsengine.config.Perms

class ReloadCommand(val plugin: CoinsDiscordEngine) {
    fun createCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("reload")
            .requires { it.sender.hasPermission(Perms.COMMAND_RELOAD.name) }
            .executes { execute(it) }
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        plugin.configContainer.reloadConfigs()
        Lang.COMMAND_RELOAD_DONE.message.send(ctx.source.sender)
        return 1;
    }
}