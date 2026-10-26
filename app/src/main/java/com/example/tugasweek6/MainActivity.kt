package com.example.tugasweek6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.ViewModelProvider
import com.example.tugasweek6.ui.TopUpViewModel
import com.example.tugasweek6.ui.screens.HistoryScreen
import com.example.tugasweek6.ui.screens.ProfileScreen
import com.example.tugasweek6.ui.screens.TopUpScreen
import com.example.tugasweek6.ui.theme.TugasWeek6Theme

class MainActivity : ComponentActivity() {

    private val viewModel: TopUpViewModel by lazy {
        ViewModelProvider(this)[TopUpViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TugasWeek6Theme {
                TugasWeek6App(viewModel)
            }
        }
    }
}

@Composable
fun TugasWeek6App(vm: TopUpViewModel) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.TOPUP) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val contentModifier = Modifier.padding(innerPadding)
            when (currentDestination) {
                AppDestinations.TOPUP -> TopUpScreen(vm, contentModifier)
                AppDestinations.HISTORY -> HistoryScreen(vm, contentModifier)
                AppDestinations.PROFILE -> ProfileScreen(vm, contentModifier)
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    TOPUP("Top Up", R.drawable.ic_diamond),
    HISTORY("Riwayat", R.drawable.ic_history),
    PROFILE("Profil", R.drawable.ic_account_box),
}

@PreviewScreenSizes
@Composable
fun TugasWeek6AppPreview() {
    TugasWeek6Theme {
        TugasWeek6App(TopUpViewModel())
    }
}
