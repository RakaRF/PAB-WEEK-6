package com.example.tugasweek6.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tugasweek6.data.DiamondPackage
import com.example.tugasweek6.data.PaymentMethod
import com.example.tugasweek6.data.Transaction
import kotlinx.coroutines.delay

/**
 * State holder untuk seluruh alur top up. Dipakai sebagai ViewModel supaya
 * data tidak hilang saat layar diputar. Semua proses (cek akun, pembayaran)
 * masih SIMULASI, belum terhubung ke server/payment gateway.
 */
class TopUpViewModel : ViewModel() {

    var userId by mutableStateOf("")
        private set
    var zoneId by mutableStateOf("")
        private set
    var nickname by mutableStateOf<String?>(null)
        private set
    var isChecking by mutableStateOf(false)
        private set
    var selectedPackage by mutableStateOf<DiamondPackage?>(null)
        private set
    var selectedPayment by mutableStateOf<PaymentMethod?>(null)
        private set
    var lastOrder by mutableStateOf<Transaction?>(null)
        private set

    val history = mutableStateListOf<Transaction>()

    // ---- turunan state ----
    val isIdValid: Boolean get() = userId.length in 6..12 && zoneId.length in 3..5
    val total: Long get() = (selectedPackage?.price ?: 0L) + (selectedPayment?.fee ?: 0L)
    val canPurchase: Boolean
        get() = nickname != null && selectedPackage != null && selectedPayment != null
    val hint: String?
        get() = when {
            nickname == null -> "Cek akun terlebih dahulu"
            selectedPackage == null -> "Pilih nominal diamond"
            selectedPayment == null -> "Pilih metode pembayaran"
            else -> null
        }
    val totalSpent: Long get() = history.sumOf { it.total }
    val totalDiamonds: Int get() = history.sumOf { it.diamonds }

    // ---- aksi ----
    fun onUserIdChange(value: String) {
        userId = value.filter { it.isDigit() }.take(12)
        nickname = null // ID berubah -> harus dicek ulang
    }

    fun onZoneIdChange(value: String) {
        zoneId = value.filter { it.isDigit() }.take(5)
        nickname = null
    }

    /** Simulasi pengecekan akun: nickname dibuat dari kombinasi ID. */
    suspend fun checkAccount() {
        if (!isIdValid || isChecking) return
        isChecking = true
        try {
            delay(900)
            val seed = userId.toLong() + zoneId.toLong()
            nickname = NICKNAMES[(seed % NICKNAMES.size).toInt()]
        } finally {
            isChecking = false
        }
    }

    fun selectPackage(pkg: DiamondPackage) {
        selectedPackage = pkg
    }

    fun selectPayment(method: PaymentMethod) {
        selectedPayment = method
    }

    fun purchase() {
        val pkg = selectedPackage ?: return
        val pay = selectedPayment ?: return
        val nick = nickname ?: return
        val now = System.currentTimeMillis()
        val trx = Transaction(
            id = "ML" + now.toString().takeLast(10),
            nickname = nick,
            userId = userId,
            zoneId = zoneId,
            packageTitle = pkg.title,
            diamonds = pkg.diamonds,
            paymentName = pay.name,
            total = total,
            timestamp = now,
        )
        history.add(0, trx)
        lastOrder = trx
        selectedPackage = null
        selectedPayment = null
    }

    fun dismissOrder() {
        lastOrder = null
    }

    fun clearHistory() {
        history.clear()
    }

    private companion object {
        val NICKNAMES = listOf(
            "AlucardSlayer", "LayLaMantap", "GusionPro", "MiyaCrit",
            "FannyCable", "ZilongRush", "NanaCantik", "HayabusaShadow",
            "KaguraUmbrella", "TigrealTank",
        )
    }
}
