package bitcoin.wallet.core

import android.hardware.fingerprint.FingerprintManager
import bitcoin.wallet.entities.BiometryType
import bitcoin.wallet.entities.Currency
import bitcoin.wallet.entities.CurrencyValue
import bitcoin.wallet.entities.TransactionRecord
import bitcoin.wallet.entities.coins.Coin
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.*

interface ILocalStorage {
    var currentLanguage: String?
    var isBackedUp: Boolean
    var isBiometricOn: Boolean
    var isLightModeOn: Boolean
    var iUnderstand: Boolean
    var lastExitDate: Long
    var unlockAttemptsLeft: Int
    var baseCurrency: Currency
    var blockTillDate: Long?
    fun clearAll()
}

interface ISecuredStorage {
    val savedWords: List<String>?
    fun saveWords(words: List<String>)
    fun wordsAreEmpty(): Boolean
    val savedPin: String?
    fun savePin(pin: String)
    fun pinIsEmpty(): Boolean
}

interface IRandomProvider {
    fun getRandomIndexes(count: Int): List<Int>
}

interface INetworkManager {
    fun getLatestRate(coinCode: String, currency: String): Flowable<Double>
    fun getRate(coinCode: String, currency: String, year: Int, month: String, day: String, hour: String, minute: String): Flowable<Double>
    fun getRateByDay(coinCode: String, currency: String, year: Int, month: String, day: String): Flowable<Double>
    fun getCurrencies(): Flowable<List<Currency>>
}

interface IEncryptionManager {
    fun encrypt(data: String): String
    fun decrypt(data: String): String
    fun getCryptoObject(): FingerprintManager.CryptoObject?
}

interface IClipboardManager {
    fun copyText(text: String)
    fun getCopiedText(): String
}

interface ICurrencyManager {
    fun getBaseCurrencyFlowable(): Flowable<Currency>
}

interface IExchangeRateManager {
    fun getRate(coinCode: String, currency: String, timestamp: Long): Flowable<Double>
    fun getExchangeRates(): MutableMap<Coin, CurrencyValue>
    fun getLatestExchangeRateSubject(): PublishSubject<MutableMap<Coin, CurrencyValue>>
}

interface IKeyStoreSafeExecute {
    fun safeExecute(action: Runnable, onSuccess: Runnable? = null, onFailure: Runnable? = null)
}

interface IAdapterManager {
    var adapters: MutableList<IAdapter>
    var subject: PublishSubject<Any>
    fun start()
    fun refresh()
    fun clear()
}

interface IWordsManager {
    fun safeLoad()
    var words: List<String>?
    var isBackedUp: Boolean
    var isLoggedIn: Boolean
    var backedUpSubject: PublishSubject<Boolean>
    fun createWords()
    fun validate(words: List<String>)
    fun restore(words: List<String>)
    fun removeWords()
}

interface ILanguageManager {
    var currentLanguage: Locale
    var preferredLanguage: Locale?
    var availableLanguages: List<Locale>
}

interface IAdapter {
    val id: String
    val coin: Coin
    val balance: Double

    val balanceSubject: PublishSubject<Double>
    val progressSubject: BehaviorSubject<Double>

    val latestBlockHeight: Int
    val latestBlockHeightSubject: PublishSubject<Any>

    val transactionRecords: List<TransactionRecord>
    val transactionRecordsSubject: PublishSubject<Any>

    val receiveAddress: String

    fun debugInfo()

    fun start()
    fun refresh()
    fun clear()

    fun send(address: String, value: Double, completion: ((Throwable?) -> (Unit))? = null)
    fun fee(value: Int, senderPay: Boolean): Double
    fun validate(address: String): Boolean
}

interface ISystemInfoManager {
    var appVersion: String
    var biometryType: BiometryType
    fun phoneHasFingerprintSensor(): Boolean
    fun touchSensorCanBeUsed(): Boolean
}
