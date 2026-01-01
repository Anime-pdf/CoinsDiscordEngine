package me.animepdf.cde.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class LanguageConfig {
    var somethingWentWrong: String = "Something went wrong, transaction cancelled"

    var transactionMessage: String = "`{from}` sent `{to}` {amount} coins"
    var transactionMessageReason: String = "`{from}` sent `{to}` {amount} coins. Reason: {reason}"

    companion object {
        fun createRussian(): LanguageConfig {
            val config = LanguageConfig()

            config.somethingWentWrong = "Что-то пошло не так, транзакция отменена"

            config.transactionMessage = "`{from}` отправил `{to}` {amount} монет"
            config.transactionMessageReason = "`{from}` отправил `{to}` {amount} монет. Причина: {reason}"

            return config
        }

        fun createEnglish(): LanguageConfig {
            return LanguageConfig()
        }
    }
}