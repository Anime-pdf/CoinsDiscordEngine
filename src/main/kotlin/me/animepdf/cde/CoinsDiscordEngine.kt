package me.animepdf.cde

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import me.animepdf.cde.commands.PayCommand
import me.animepdf.cde.config.ConfigContainer
import org.bukkit.plugin.java.JavaPlugin
import su.nightexpress.coinsengine.api.CoinsEngineAPI

class CoinsDiscordEngine : JavaPlugin() {
    lateinit var configContainer: ConfigContainer

    override fun onEnable() {
        configContainer = ConfigContainer(dataFolder)
        configContainer.loadConfigs()

        val currency = CoinsEngineAPI.getCurrency(configContainer.generalConfig.currency)
        if(currency == null) {
            logger.severe("Can't find currency, check config!")
        }

        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) {
            it.registrar().register(PayCommand(this).createCommand().build())
        }
    }

    override fun onDisable() {
    }
}
