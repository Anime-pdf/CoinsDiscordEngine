package me.animepdf.cde.utils

import me.animepdf.cde.config.LanguageConfig

object CurrencyUtils {
    fun formatCurrency(forms: Map<LanguageConfig.CurrencyForm, String>, amount: Double): String {
        val n: Int = amount.toInt() % 100
        return when {
            n in 11..14 -> forms[LanguageConfig.CurrencyForm.MANY]!!
            n % 10 == 1 -> forms[LanguageConfig.CurrencyForm.ONE]!!
            n % 10 in 2..4 -> forms[LanguageConfig.CurrencyForm.FEW]!!
            else -> forms[LanguageConfig.CurrencyForm.MANY]!!
        }
    }
}