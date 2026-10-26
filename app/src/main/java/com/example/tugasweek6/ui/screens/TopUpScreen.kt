package com.example.tugasweek6.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugasweek6.data.Catalog
import com.example.tugasweek6.data.DiamondPackage
import com.example.tugasweek6.data.PaymentMethod
import com.example.tugasweek6.data.toRupiah
import com.example.tugasweek6.ui.TopUpViewModel
import kotlinx.coroutines.launch

@Composable
fun TopUpScreen(vm: TopUpViewModel, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    var showConfirm by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { PromoBanner() }

            item { StepHeader(1, "Masukkan ID Akun") }
            item { AccountCard(vm, onCheck = { scope.launch { vm.checkAccount() } }) }

            item { StepHeader(2, "Pilih Nominal Diamond") }
            items(Catalog.packages.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowItems.forEach { pkg ->
                        PackageCard(
                            pkg = pkg,
                            selected = vm.selectedPackage?.id == pkg.id,
                            onClick = { vm.selectPackage(pkg) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        )
                    }
                    if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            item { StepHeader(3, "Pilih Metode Pembayaran") }
            items(Catalog.payments) { method ->
                PaymentRow(
                    method = method,
                    selected = vm.selectedPayment?.id == method.id,
                    onClick = { vm.selectPayment(method) },
                )
            }
        }

        CheckoutBar(vm = vm, onBuy = { showConfirm = true })
    }

    if (showConfirm) {
        ConfirmDialog(
            vm = vm,
            onDismiss = { showConfirm = false },
            onConfirm = {
                showConfirm = false
                vm.purchase()
            },
        )
    }

    vm.lastOrder?.let { order ->
        AlertDialog(
            onDismissRequest = vm::dismissOrder,
            title = { Text("Top Up Berhasil 🎉") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Diamond sudah dikirim ke akun kamu.")
                    DetailLine("No. Order", order.id)
                    DetailLine("Nickname", order.nickname)
                    DetailLine("ID", "${order.userId} (${order.zoneId})")
                    DetailLine("Item", order.packageTitle)
                    DetailLine("Total", order.total.toRupiah())
                }
            },
            confirmButton = { TextButton(onClick = vm::dismissOrder) { Text("Tutup") } },
        )
    }
}

// ───────────────────────── komponen ─────────────────────────

@Composable
private fun PromoBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(listOf(Color(0xFF1B2A6B), Color(0xFF5B2A9E)))
            )
            .padding(20.dp),
    ) {
        Column(Modifier.align(Alignment.CenterStart)) {
            Text(
                "MOBILE LEGENDS",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Top Up Diamond",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Proses instan • Pembayaran aman",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
            )
        }
        Text("💎", fontSize = 56.sp, modifier = Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
private fun StepHeader(step: Int, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                step.toString(),
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AccountCard(vm: TopUpViewModel, onCheck: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = vm.userId,
                    onValueChange = vm::onUserIdChange,
                    label = { Text("User ID") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(2f),
                )
                OutlinedTextField(
                    value = vm.zoneId,
                    onValueChange = vm::onZoneIdChange,
                    label = { Text("Zone ID") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )
            }

            Button(
                onClick = onCheck,
                enabled = vm.isIdValid && !vm.isChecking,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (vm.isChecking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Memeriksa...")
                } else {
                    Text("Cek Akun")
                }
            }

            val nick = vm.nickname
            if (nick != null) {
                Text(
                    "✅ Akun ditemukan: $nick",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold,
                )
            } else {
                Text(
                    "User ID 6–12 digit dan Zone ID 3–5 digit. Cek di profil game kamu.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PackageCard(
    pkg: DiamondPackage,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val surface = MaterialTheme.colorScheme.surface
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) primary else MaterialTheme.colorScheme.outline,
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) primary.copy(alpha = 0.12f).compositeOver(surface) else surface
        ),
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💎", fontSize = 20.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(pkg.title, fontWeight = FontWeight.Bold)
                }
                Text(
                    pkg.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    pkg.price.toRupiah(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = primary,
                )
            }
            if (pkg.tag != null) {
                Text(
                    pkg.tag,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(
                            MaterialTheme.colorScheme.secondary,
                            RoundedCornerShape(bottomStart = 12.dp),
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary,
                )
            }
        }
    }
}

@Composable
private fun PaymentRow(method: PaymentMethod, selected: Boolean, onClick: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val surface = MaterialTheme.colorScheme.surface
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) primary else MaterialTheme.colorScheme.outline,
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) primary.copy(alpha = 0.12f).compositeOver(surface) else surface
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(method.emoji, fontSize = 24.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(method.name, fontWeight = FontWeight.SemiBold)
                Text(
                    method.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                if (method.fee == 0L) "Gratis" else "+${method.fee.toRupiah()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            RadioButton(selected = selected, onClick = null, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun CheckoutBar(vm: TopUpViewModel, onBuy: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Total Bayar",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    vm.total.toRupiah(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                vm.hint?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
            Button(onClick = onBuy, enabled = vm.canPurchase) { Text("Beli Sekarang") }
        }
    }
}

@Composable
private fun ConfirmDialog(vm: TopUpViewModel, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val pkg = vm.selectedPackage
    val pay = vm.selectedPayment
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Pesanan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                DetailLine("Nickname", vm.nickname ?: "-")
                DetailLine("ID", "${vm.userId} (${vm.zoneId})")
                DetailLine("Item", pkg?.title ?: "-")
                DetailLine("Harga", (pkg?.price ?: 0L).toRupiah())
                DetailLine("Pembayaran", pay?.name ?: "-")
                DetailLine("Biaya admin", (pay?.fee ?: 0L).toRupiah())
                DetailLine("Total", vm.total.toRupiah())
            }
        },
        confirmButton = { Button(onClick = onConfirm) { Text("Bayar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } },
    )
}

@Composable
fun DetailLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(12.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
