package me.animepdf.cde.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class LanguageConfig {
    @ConfigSerializable
    enum class CurrencyForm {
        ONE,
        FEW,
        MANY
    }

    var somethingWentWrong: String = "Something went wrong, transaction cancelled"

    @Comment("Placeholders: {from}, {to}, {amount}, {currency}")
    var transactionMessage: String = "`{from}` sent `{to}` {amount} {currency}"
    @Comment("Placeholders: {from}, {to}, {amount}, {currency}, {purpose}")
    var transactionMessagePurpose: String = "`{from}` sent `{to}` {amount} {currency}. Purpose: {purpose}"

    @Comment("Placeholders: {source}, {target}, {amount}, {currency}")
    var addMessage: String = "`{target}` account was replenished with {amount} {currency}"
    @Comment("Placeholders: {source}, {target}, {amount}, {currency}, {reason}")
    var addMessageReason: String = "`{target}` account was replenished with {amount} {currency}. Reason: {reason}"

    @Comment("Placeholders: {source}, {target}, {amount}, {currency}")
    var removeMessage: String = "{amount} {currency} were withdrawn from `{target}`'s account"
    @Comment("Placeholders: {source}, {target}, {amount}, {currency}, {reason}")
    var removeMessageReason: String = "`{amount} {currency} were withdrawn from `{target}`'s account. Reason: {reason}"

    @Comment("ONE: 1\nFEW: 2-4\nMANY: 11-14 and everything else")
    var currencyForms: Map<CurrencyForm, String> = hashMapOf(
        CurrencyForm.ONE to "coin",
        CurrencyForm.FEW to "coins",
        CurrencyForm.MANY to "coins"
    )

    companion object {
        fun createRussian(): LanguageConfig {
            val config = LanguageConfig()

            config.somethingWentWrong = "Что-то пошло не так, транзакция отменена"

            config.transactionMessage = "`{from}` отправил `{to}` {amount} {currency}"
            config.transactionMessagePurpose = "`{from}` отправил `{to}` {amount} {currency}. Назначение: {purpose}"

            config.addMessage = "Счёт `{target}` пополнен на {amount} {currency}"
            config.addMessageReason = "Счёт `{target}` пополнен на {amount} {currency}. Причина: {reason}"

            config.removeMessage = "Со счёта `{target}` снято {amount} {currency}"
            config.removeMessageReason = "Со счёта `{target}` снято {amount} {currency}. Причина: {reason}"

            config.currencyForms = hashMapOf(
                CurrencyForm.ONE to "монета",
                CurrencyForm.FEW to "монеты",
                CurrencyForm.MANY to "монет"
            )

            return config
        }

        fun createEnglish(): LanguageConfig {
            return LanguageConfig()
        }
    }
}