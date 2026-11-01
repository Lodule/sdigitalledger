package com.sdigitalledger

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sdigitalledger.analytics.AnalyticsManager
import com.sdigitalledger.network.LoopsEmailService
import com.sdigitalledger.ui.*
import com.sdigitalledger.ui.theme.SdigitalledgerTheme
import com.sdigitalledger.utils.PremiumManager
import com.sdigitalledger.viewmodel.DebtorViewModel
import com.sdigitalledger.viewmodel.InventoryViewModel
import com.sdigitalledger.viewmodel.SalesViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val analytics = AnalyticsManager.getInstance(this)
        analytics.logScreenView("MainActivity")

        setContent {
            SdigitalledgerTheme {
                var isSplashShowing by remember { mutableStateOf(true) }

                if (isSplashShowing) {
                    SplashScreen(onSplashComplete = { isSplashShowing = false })
                } else {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as DigitalLedgerApplication
    val premiumManager = remember { PremiumManager(context) }
    val analytics = remember { AnalyticsManager.getInstance(context) }
    val loopsService = remember { LoopsEmailService(context) }
    val scope = rememberCoroutineScope()

    val inventoryViewModel: InventoryViewModel = viewModel(
        factory = InventoryViewModel.InventoryViewModelFactory(app.inventoryRepository)
    )
    val salesViewModel: SalesViewModel = viewModel(
        factory = SalesViewModel.SalesViewModelFactory(
            app.salesRepository,
            app.inventoryRepository,
            app.debtorRepository
        )
    )
    val debtorViewModel: DebtorViewModel = viewModel(
        factory = DebtorViewModel.DebtorViewModelFactory(app.debtorRepository)
    )

    var selectedTab by remember { mutableIntStateOf(1) } // Default to Sales / Dashboard
    var isPremium by remember { mutableStateOf(premiumManager.isPremium) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }

    val tabs = listOf("Inventory", "Sales / Dashboard", "Debtors")
    val icons = listOf(Icons.AutoMirrored.Filled.List, Icons.Default.ShoppingCart, Icons.Default.Person)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isPremium) "Digital Ledger Pro 👑" else "Digital Ledger Free") },
                actions = {
                    // Feedback Board Button
                    IconButton(onClick = { showFeedbackDialog = true }) {
                        Icon(Icons.Default.Star, contentDescription = "Feedback Board")
                    }
                    // Freemium Upgrade / Mobile Money Payment Button
                    TextButton(onClick = {
                        if (!isPremium) {
                            showPaymentDialog = true
                        } else {
                            Toast.makeText(context, "You are currently on Digital Ledger Pro (UGX 5,000/Mo)", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text(if (isPremium) "PRO 👑" else "UPGRADE", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = title) },
                        label = { Text(title) },
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            analytics.logScreenView(title)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> InventoryScreen(inventoryViewModel)
                1 -> SalesScreen(
                    salesViewModel = salesViewModel,
                    inventoryViewModel = inventoryViewModel,
                    isPremium = isPremium,
                    onUpgradeClick = { showPaymentDialog = true }
                )
                2 -> DebtorScreen(debtorViewModel)
            }
        }

        // Feedback Board Dialog
        if (showFeedbackDialog) {
            FeedbackBoardDialog(
                onDismiss = { showFeedbackDialog = false },
                onSubmitFeedback = { rating, text, userEmail ->
                    analytics.logFeedbackSubmitted(rating)
                    Toast.makeText(context, "Thank you for your feedback!", Toast.LENGTH_SHORT).show()
                    if (userEmail.isNotBlank()) {
                        scope.launch {
                            loopsService.sendEmailSequence(
                                userEmail = userEmail,
                                eventName = "feedback_submitted",
                                eventProperties = mapOf("rating" to rating.toString(), "feedback" to text)
                            )
                        }
                    }
                }
            )
        }

        // Mobile Money Payment Dialog (UGX 5,000 / Month)
        if (showPaymentDialog) {
            MobileMoneyPaymentDialog(
                onDismiss = { showPaymentDialog = false },
                onPaymentSuccess = {
                    premiumManager.grantPremiumAccess()
                    isPremium = true
                    analytics.logPremiumUpgrade()
                    Toast.makeText(context, "Welcome to Digital Ledger Pro!", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}
