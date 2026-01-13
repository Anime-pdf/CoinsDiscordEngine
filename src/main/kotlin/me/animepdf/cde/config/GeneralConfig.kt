package me.animepdf.cde.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class GeneralConfig {
    var prefix: String = "mycustomprefix"
    var prefixedCommands: Boolean = false
    var nonPrefixedCommands: Boolean = true

    var currencyId: String = "coins";
    var channelId: Long = 0;

    @ConfigSerializable
    class Alias {
        var pay: List<String> = listOf("pay", "send")
        var add: List<String> = listOf("add", "deposit")
        var remove: List<String> = listOf("remove", "withdraw")
    }
    var alias = Alias()

    @ConfigSerializable
    class Log {
        var logPayToFile = true
        var logPayToConsole = false
    }
    var log = Log()

    @ConfigSerializable
    class Notification {
        var pay: Boolean = true
        var add: Boolean = true
        var remove: Boolean = true
    }
    var notification = Notification()


}