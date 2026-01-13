package me.animepdf.cde.utils

import github.scarsz.discordsrv.DiscordSRV
import me.animepdf.cde.CoinsDiscordEngine

class DiscordLogger(private val plugin: CoinsDiscordEngine) {
    fun sendLog(
        messageTemplate: String,
        placeholders: Map<String, String>
    ) {
        val channelId = plugin.conf().channelId
        if (channelId == 0L) return

        val channel = DiscordSRV.getPlugin().jda.getTextChannelById(channelId) ?: return

        var finalMessage = messageTemplate
        placeholders.forEach { (key, value) ->
            finalMessage = finalMessage.replace("{$key}", value)
        }

        channel.sendMessage(finalMessage).queue()
    }
}