package me.animepdf.cde.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class GeneralConfig {
    var prefix: String = "mycustomprefix"
    var prefixedCommands: Boolean = false
    var nonPrefixedCommands: Boolean = true

    var currency: String = "coins";
    var channelId: Long = 0;

    var payAliases: List<String> = listOf("pay", "send")
    var addAliases: List<String> = listOf("add", "deposit")
    var removeAliases: List<String> = listOf("remove", "withdraw")

    var logPayToFile = true
    var logPayToConsole = false

    var paymentNotification: Boolean = true
    var addingNotification: Boolean = true
    var removingNotification: Boolean = true
}