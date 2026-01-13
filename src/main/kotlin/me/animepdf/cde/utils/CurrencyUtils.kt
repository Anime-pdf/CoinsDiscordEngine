package me.animepdf.cde.utils

import me.animepdf.cde.config.LanguageConfig

object CurrencyUtils {
    fun formatCurrency(forms: LanguageConfig.CurrencyForms, amount: Double): String {
        val n: Int = amount.toInt() % 100
        return when {
            n in 11..14 -> forms.many
            n % 10 == 1 -> forms.one
            n % 10 in 2..4 -> forms.few
            else -> forms.many
        }
    }
}