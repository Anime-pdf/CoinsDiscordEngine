package me.animepdf.cde.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class LanguageConfig {
    var somethingWentWrong: String = "Something went wrong, transaction cancelled"

    var transactionMessage: String = "`{from}` sent `{to}` {amount} coins"
    var transactionMessagePurpose: String = "`{from}` sent `{to}` {amount} coins. Purpose: {reason}"

    companion object {
        fun createRussian(): LanguageConfig {
            val config = LanguageConfig()

            config.somethingWentWrong = "Что-то пошло не так, транзакция отменена"

            config.transactionMessage = "`{from}` отправил `{to}` {amount} монет"
            config.transactionMessagePurpose = "`{from}` отправил `{to}` {amount} монет. Назначение: {reason}"

            return config
        }

        fun createEnglish(): LanguageConfig {
            return LanguageConfig()
        }
    }
}