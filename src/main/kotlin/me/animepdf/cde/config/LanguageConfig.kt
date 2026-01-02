package me.animepdf.cde.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class LanguageConfig {
    var somethingWentWrong: String = "Something went wrong, transaction cancelled"

    var transactionMessage: String = "`{from}` sent `{to}` {amount} coins"
    var transactionMessagePurpose: String = "`{from}` sent `{to}` {amount} coins. Purpose: {purpose}"

    var addMessage: String = "`{target}` account was replenished with {amount} coins"
    var addMessageReason: String = "`{target}` account was replenished with {amount} coins. Reason: {reason}"

    var removeMessage: String = "{amount} coins were withdrawn from `{target}`'s account"
    var removeMessageReason: String = "`{amount} coins were withdrawn from `{target}`'s account. Reason: {reason}"

    companion object {
        fun createRussian(): LanguageConfig {
            val config = LanguageConfig()

            config.somethingWentWrong = "Что-то пошло не так, транзакция отменена"

            config.transactionMessage = "`{from}` отправил `{to}` {amount} монет"
            config.transactionMessagePurpose = "`{from}` отправил `{to}` {amount} монет. Назначение: {purpose}"

            config.addMessage = "Счёт `{target}` был пополнен на {amount} монет"
            config.addMessageReason = "Счёт `{target}` был пополнен на {amount} монет. Причина: {reason}"

            config.removeMessage = "Со счёта `{target}` было снято {amount} монет"
            config.removeMessageReason = "Со счёта `{target}` было снято {amount} монет. Причина: {reason}"

            return config
        }

        fun createEnglish(): LanguageConfig {
            return LanguageConfig()
        }
    }
}