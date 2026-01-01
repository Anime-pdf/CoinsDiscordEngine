package me.animepdf.cde.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class GeneralConfig {
    var currency: String = "coins";
    var channelId: Long = 0;
}