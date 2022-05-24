package io.horizontalsystems.bankwallet.core.managers

import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.IAppNumberFormatter
import io.horizontalsystems.bankwallet.core.providers.Translator
import io.horizontalsystems.bankwallet.modules.market.Value
import io.horizontalsystems.core.ILanguageManager
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

class NumberFormatter(
        private val languageManager: ILanguageManager
        ) : IAppNumberFormatter {

    private var formatters: MutableMap<String, NumberFormat> = mutableMapOf()
    private val numberRounding = NumberRounding()

    override fun format(value: Number, minimumFractionDigits: Int, maximumFractionDigits: Int, prefix: String, suffix: String): String {
        val bigDecimalValue = when (value) {
            is Double -> value.toBigDecimal()
            is Float -> value.toBigDecimal()
            is BigDecimal -> value
            else -> throw UnsupportedOperationException()
        }

        val formatter = getFormatter(languageManager.currentLocale, minimumFractionDigits, maximumFractionDigits)

        val mostLowValue = BigDecimal(BigInteger.ONE, maximumFractionDigits)

        return if (bigDecimalValue > BigDecimal.ZERO && bigDecimalValue < mostLowValue) {
            "< " + prefix + formatter.format(mostLowValue) + suffix
        } else {
            prefix + formatter.format(bigDecimalValue) + suffix
        }
    }

    override fun formatCoinFull(value: BigDecimal, code: String, coinDecimals: Int): String {
        val rounded = numberRounding.getRoundedCoinFull(value, coinDecimals)

        val formattedNumber = format(rounded.value, 0, Int.MAX_VALUE)
        val res = when (rounded) {
            is BigDecimalRounded.Large -> {
                val suffixResId = when (rounded.suffix) {
                    NumberSuffix.Blank -> null
                    NumberSuffix.Thousand -> R.string.CoinPage_MarketCap_Thousand
                    NumberSuffix.Million -> R.string.CoinPage_MarketCap_Million
                    NumberSuffix.Billion -> R.string.CoinPage_MarketCap_Billion
                    NumberSuffix.Trillion -> R.string.CoinPage_MarketCap_Trillion
                }

                formattedNumber + suffixResId?.let {
                    " " + Translator.getString(it)
                }
            }
            is BigDecimalRounded.LessThen -> {
                "<$formattedNumber"
            }
            is BigDecimalRounded.Regular -> {
                formattedNumber
            }
        }

        return "$res $code"
    }

    override fun formatCoinShort(value: BigDecimal, code: String, coinDecimals: Int): String {
        val rounded = numberRounding.getRoundedCoinShort(value, coinDecimals)

        val formattedNumber = format(rounded.value, 0, Int.MAX_VALUE)
        val res = when (rounded) {
            is BigDecimalRounded.Large -> {
                val suffixResId = when (rounded.suffix) {
                    NumberSuffix.Blank -> null
                    NumberSuffix.Thousand -> R.string.CoinPage_MarketCap_Thousand
                    NumberSuffix.Million -> R.string.CoinPage_MarketCap_Million
                    NumberSuffix.Billion -> R.string.CoinPage_MarketCap_Billion
                    NumberSuffix.Trillion -> R.string.CoinPage_MarketCap_Trillion
                }

                formattedNumber + suffixResId?.let {
                    " " + Translator.getString(it)
                }
            }
            is BigDecimalRounded.LessThen -> {
                "<$formattedNumber"
            }
            is BigDecimalRounded.Regular -> {
                formattedNumber
            }
        }

        return "$res $code"
    }

    override fun formatNumberShort(value: BigDecimal, maximumFractionDigits: Int): String {
        val rounded = numberRounding.getRoundedShort(value, maximumFractionDigits)
        val formattedNumber = format(rounded.value, 0, Int.MAX_VALUE)

        return when (rounded) {
            is BigDecimalRounded.Large -> {
                val suffixResId = when (rounded.suffix) {
                    NumberSuffix.Blank -> null
                    NumberSuffix.Thousand -> R.string.CoinPage_MarketCap_Thousand
                    NumberSuffix.Million -> R.string.CoinPage_MarketCap_Million
                    NumberSuffix.Billion -> R.string.CoinPage_MarketCap_Billion
                    NumberSuffix.Trillion -> R.string.CoinPage_MarketCap_Trillion
                }

                formattedNumber + suffixResId?.let {
                    " " + Translator.getString(it)
                }
            }
            is BigDecimalRounded.LessThen -> {
                "<$formattedNumber"
            }
            is BigDecimalRounded.Regular -> {
                formattedNumber
            }
        }
    }

    override fun formatFiatFull(value: BigDecimal, symbol: String): String {
        val rounded = numberRounding.getRoundedCurrencyFull(value)

        val formattedNumber = format(rounded.value, 0, Int.MAX_VALUE, prefix = symbol)

        return when (rounded) {
            is BigDecimalRounded.Large -> {
                val suffixResId = when (rounded.suffix) {
                    NumberSuffix.Blank -> null
                    NumberSuffix.Thousand -> R.string.CoinPage_MarketCap_Thousand
                    NumberSuffix.Million -> R.string.CoinPage_MarketCap_Million
                    NumberSuffix.Billion -> R.string.CoinPage_MarketCap_Billion
                    NumberSuffix.Trillion -> R.string.CoinPage_MarketCap_Trillion
                }

                formattedNumber + suffixResId?.let {
                    " " + Translator.getString(it)
                }
            }
            is BigDecimalRounded.LessThen -> {
                "<$formattedNumber"
            }
            is BigDecimalRounded.Regular -> {
                formattedNumber
            }
        }
    }

    override fun formatFiatShort(
        value: BigDecimal,
        symbol: String,
        currencyDecimals: Int
    ): String {
        val rounded = numberRounding.getRoundedCurrencyShort(value, currencyDecimals)

        val formattedNumber = format(rounded.value, 0, Int.MAX_VALUE, prefix = symbol)

        return when (rounded) {
            is BigDecimalRounded.Large -> {
                val suffixResId = when (rounded.suffix) {
                    NumberSuffix.Blank -> null
                    NumberSuffix.Thousand -> R.string.CoinPage_MarketCap_Thousand
                    NumberSuffix.Million -> R.string.CoinPage_MarketCap_Million
                    NumberSuffix.Billion -> R.string.CoinPage_MarketCap_Billion
                    NumberSuffix.Trillion -> R.string.CoinPage_MarketCap_Trillion
                }

                formattedNumber + suffixResId?.let {
                    " " + Translator.getString(it)
                }
            }
            is BigDecimalRounded.LessThen -> {
                "<$formattedNumber"
            }
            is BigDecimalRounded.Regular -> {
                formattedNumber
            }
        }
    }

    private fun getFormatter(locale: Locale, minimumFractionDigits: Int, maximumFractionDigits: Int): NumberFormat {
        val formatterId = "${locale.language}-$minimumFractionDigits-$maximumFractionDigits"

        if (formatters[formatterId] == null) {
            formatters[formatterId] = NumberFormat.getInstance(locale).apply {
                this.roundingMode = RoundingMode.FLOOR

                this.minimumFractionDigits = minimumFractionDigits
                this.maximumFractionDigits = maximumFractionDigits
            }
        }

        return formatters[formatterId] ?: throw Exception("No formatter")
    }

    override fun formatValueAsDiff(value: Value): String =
        when (value) {
            is Value.Currency -> {
                val currencyValue = value.currencyValue
                formatFiatShort(currencyValue.value, currencyValue.currency.symbol, currencyValue.currency.decimal)
            }
            is Value.Percent -> {
                format(value.percent.abs(), 0, 2, sign(value.percent), "%")
            }
        }

    private fun sign(value: BigDecimal): String {
        return when (value.signum()) {
            1 -> "+"
            -1 -> "-"
            else -> ""
        }
    }
}
