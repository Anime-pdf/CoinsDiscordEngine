package me.animepdf.cde

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import me.animepdf.cde.commands.AddCommand
import me.animepdf.cde.commands.PayCommand
import me.animepdf.cde.commands.ReloadCommand
import me.animepdf.cde.commands.RemoveCommand
import me.animepdf.cde.config.ConfigContainer
import me.animepdf.cde.config.GeneralConfig
import me.animepdf.cde.config.LanguageConfig
import me.animepdf.cde.utils.DiscordLogger
import org.bukkit.plugin.java.JavaPlugin
import su.nightexpress.coinsengine.api.CoinsEngineAPI

class CoinsDiscordEngine : JavaPlugin() {
    lateinit var configContainer: ConfigContainer
    lateinit var discordLogger: DiscordLogger

    fun registerCommands() {
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) {
            val root = Commands.literal("cde")
            val prefix = Commands.literal(conf().prefix)

            val aliases =
                AddCommand(this, discordLogger).createCommand() +
                        RemoveCommand(this, discordLogger).createCommand() +
                        PayCommand(this, discordLogger).createCommand() +
                        ReloadCommand(this).createCommand()

            for (alias in aliases) {
                root.then(alias)
                prefix.then(alias)

                if (conf().nonPrefixedCommands) {
                    it.registrar().register(alias.build())
                }
            }

            it.registrar().register(root.build())

            if (conf().prefixedCommands) {
                it.registrar().register(prefix.build())
            }
        }
    }

    override fun onEnable() {
        configContainer = ConfigContainer(dataFolder)
        configContainer.loadConfigs()

        discordLogger = DiscordLogger(this)

        val currency = CoinsEngineAPI.getCurrency(conf().currencyId)
        if (currency == null) {
            logger.severe("Can't find specified currency, check config!")
            server.pluginManager.disablePlugin(this)
            return
        }

        registerCommands()
    }

    fun conf(): GeneralConfig {
        return configContainer.generalConfig;
    }

    fun lang(): LanguageConfig {
        return configContainer.languageConfig;
    }
}
