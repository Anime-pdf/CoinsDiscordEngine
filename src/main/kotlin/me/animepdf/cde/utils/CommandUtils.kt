package me.animepdf.cde.utils

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit

object CommandUtils {
    fun buildEconomyCommand(
        aliases: List<String>,
        permission: String?,
        executor: (CommandContext<CommandSourceStack>, String?) -> Int
    ): List<LiteralArgumentBuilder<CommandSourceStack>> {
        return aliases.map { alias ->
            Commands.literal(alias)
                .requires { if (permission == null) true else it.sender.hasPermission(permission) }
                .then(
                    Commands.argument("player", StringArgumentType.string())
                        .suggests { _, builder ->
                            for (player in Bukkit.getOnlinePlayers()) {
                                builder.suggest(player.name)
                            }
                            builder.buildFuture()
                        }
                        .then(
                            Commands.argument("amount", DoubleArgumentType.doubleArg(1.0))
                                .executes { executor(it, null) }
                                .then(
                                    Commands.argument("reason", StringArgumentType.greedyString())
                                        .executes { executor(it, it.getArgument("reason", String::class.java)) })
                        )
                )
        }
    }
}