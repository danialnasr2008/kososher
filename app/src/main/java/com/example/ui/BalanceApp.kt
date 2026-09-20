package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.screens.*
import kotlinx.coroutines.flow.collectLatest

enum class MainDestination(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    BALANCE("بانک و نقدینگی", Icons.Default.AccountBalanceWallet),
    PERSONAL("مالی شخصی", Icons.Default.Savings),
    SME("کسب‌وکار", Icons.Default.Storefront),
    CORPORATE("حسابداری دوبل", Icons.Default.MenuBook),
    AUDIO("آموزش صوتی", Icons.Default.Headphones)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceApp(
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    var currentDestination by remember { mutableStateOf(MainDestination.BALANCE) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showSettingsDialog by remember { mutableStateOf(false) }

    if (showSettingsDialog) {
        SettingsDialog(
            viewModel = viewModel,
            onDismiss = { showSettingsDialog = false }
        )
    }

    // Runtime Permission Request for SMS
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Permissions handled
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val ungranted = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (ungranted.isNotEmpty()) {
            permissionLauncher.launch(ungranted.toTypedArray())
        }
    }

    // Collect status messages from ViewModel
    LaunchedEffect(viewModel) {
        viewModel.statusMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "حسابداری اروند",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                "سامانه یکپارچه مالی و بانکی",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            // Settings & Customization Button (M3 Expressive)
                            IconButton(onClick = { showSettingsDialog = true }) {
                                Icon(
                                    Icons.Default.Palette,
                                    contentDescription = "تنظیمات و شخصی‌سازی تم",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Privacy Mask Toggle Button
                            IconButton(onClick = { viewModel.togglePrivacyMask() }) {
                                val isMasked by viewModel.isPrivacyMasked.collectAsState()
                                Icon(
                                    if (isMasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "حالت حریم خصوصی",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                MainDestination.entries.forEach { destination ->
                    val isSelected = currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                destination.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                MainDestination.BALANCE -> BankBalanceScreen(viewModel = viewModel)
                MainDestination.PERSONAL -> PersonalFinanceScreen(viewModel = viewModel)
                MainDestination.SME -> SmeScreen(viewModel = viewModel)
                MainDestination.CORPORATE -> CorporateScreen(viewModel = viewModel)
                MainDestination.AUDIO -> ArticleReaderScreen(viewModel = viewModel)
            }
        }
    }
}
