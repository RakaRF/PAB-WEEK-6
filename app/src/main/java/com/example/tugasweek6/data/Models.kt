package com.example.tugasweek6.data

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ID_LOCALE: Locale = Locale.forLanguageTag("id-ID")

/** Paket diamond yang bisa dibeli. [diamonds] sudah termasuk [bonus]. */
data class DiamondPackage(
    val id: String,
    val diamonds: Int,
    val bonus: Int,
    val price: Long,
    val tag: String? = null,
) {
    val title: String get() = "$diamonds Diamond"
    val subtitle: String get() = if (bonus > 0) "Termasuk bonus $bonus" else "Tanpa bonus"
}

data class PaymentMethod(
    val id: String,
    val name: String,
    val emoji: String,
    val note: String,
    val fee: Long,
)

data class Transaction(
    val id: String,
    val nickname: String,
    val userId: String,
    val zoneId: String,
    val packageTitle: String,
    val diamonds: Int,
    val paymentName: String,
    val total: Long,
    val timestamp: Long,
)

/** Data contoh (bukan harga resmi). */
object Catalog {
    val packages = listOf(
        DiamondPackage("d5", 5, 0, 1_500),
        DiamondPackage("d12", 12, 1, 3_500),
        DiamondPackage("d28", 28, 3, 8_000),
        DiamondPackage("d59", 59, 6, 16_500),
        DiamondPackage("d86", 86, 8, 24_000, tag = "POPULER"),
        DiamondPackage("d172", 172, 16, 48_000),
        DiamondPackage("d257", 257, 25, 72_000),
        DiamondPackage("d344", 344, 34, 96_000),
        DiamondPackage("d514", 514, 50, 144_000),
        DiamondPackage("d706", 706, 70, 197_000, tag = "HEMAT"),
    )

    val payments = listOf(
        PaymentMethod("gopay", "GoPay", "🟢", "E-Wallet", 0),
        PaymentMethod("ovo", "OVO", "🟣", "E-Wallet", 0),
        PaymentMethod("dana", "DANA", "🔵", "E-Wallet", 0),
        PaymentMethod("shopeepay", "ShopeePay", "🟠", "E-Wallet", 0),
        PaymentMethod("qris", "QRIS", "📱", "Semua e-wallet & m-banking", 750),
        PaymentMethod("bca", "BCA Virtual Account", "🏦", "Transfer bank", 2_500),
        PaymentMethod("minimarket", "Alfamart / Indomaret", "🏪", "Bayar di kasir", 3_000),
    )
}

fun Long.toRupiah(): String =
    "Rp " + NumberFormat.getIntegerInstance(ID_LOCALE).format(this)

fun formatDateTime(millis: Long): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", ID_LOCALE).format(Date(millis))
