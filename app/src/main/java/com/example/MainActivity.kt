package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.InventoryItemEntity
import com.example.data.model.AppLanguage
import com.example.data.model.UserRole
import com.example.ui.components.AssetBarcodeLabelDialog
import com.example.ui.screens.AuditReportsScreen
import com.example.ui.screens.BarcodeScannerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InventoryListScreen
import com.example.ui.screens.IssueReturnTransferScreen
import com.example.ui.screens.ItemDetailDialog
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TechBlueDark
import com.example.ui.theme.TechBlueLight
import com.example.ui.theme.TechBluePrimary
import com.example.ui.viewmodel.InventoryViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: InventoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

data class NavItem(
    val titleEn: String,
    val titleHi: String,
    val icon: ImageVector,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: InventoryViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val isHi = uiState.language == AppLanguage.HINDI
    val snackbarHostState = remember { SnackbarHostState() }

    var showRoleDialog by remember { mutableStateOf(false) }
    var preselectedMovementAction by remember { mutableStateOf<String?>(null) }
    var preselectedMovementAsset by remember { mutableStateOf<InventoryItemEntity?>(null) }

    // Toast/Snackbar notifications
    LaunchedEffect(uiState.infoMessage, uiState.errorMessage) {
        val msg = uiState.infoMessage ?: uiState.errorMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessages()
        }
    }

    val navItems = listOf(
        NavItem("Dashboard", "डैशबोर्ड", Icons.Default.Dashboard, "nav_dashboard"),
        NavItem("Inventory", "इन्वेंट्री", Icons.Default.Inventory2, "nav_inventory"),
        NavItem("Scanner", "स्कैनर", Icons.Default.QrCodeScanner, "nav_scanner"),
        NavItem("Movements", "कार्यप्रवाह", Icons.Default.SyncAlt, "nav_movements"),
        NavItem("Reports", "रिपोर्ट", Icons.Default.FactCheck, "nav_reports")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isHi) "आईटी इन्वेंट्री" else "IT Inventory",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Slate900
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = TechBluePrimary
                            ) {
                                Text(
                                    text = "MULTI-LOCATION",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isHi) "लखनऊ • कानपुर • अयोध्या • नोएडा" else "Lucknow • Kanpur • Ayodhya • Noida",
                            fontSize = 10.sp,
                            color = Slate700
                        )
                    }
                },
                actions = {
                    // Language Switcher Button (English / हिन्दी)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Slate100,
                        modifier = Modifier
                            .clickable { viewModel.toggleLanguage() }
                            .testTag("language_toggle_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = TechBlueDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (uiState.language == AppLanguage.ENGLISH) "हिन्दी" else "EN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechBlueDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Role Switcher Button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = TechBlueLight,
                        modifier = Modifier
                            .clickable { showRoleDialog = true }
                            .testTag("role_switcher_header")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = TechBlueDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isHi) uiState.currentRole.titleHi.take(6) else uiState.currentRole.titleEn.substringBefore(" "),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechBlueDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Slate900
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = TechBluePrimary,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar")
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = uiState.currentTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(index) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.titleEn,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = if (isHi) item.titleHi else item.titleEn,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = TechBlueDark,
                            indicatorColor = TechBluePrimary,
                            unselectedIconColor = Slate700,
                            unselectedTextColor = Slate700
                        ),
                        modifier = Modifier.testTag(item.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Slate100.copy(alpha = 0.5f))
        ) {
            when (uiState.currentTab) {
                0 -> DashboardScreen(
                    state = uiState,
                    onSelectFacility = { viewModel.selectFacility(it) },
                    onSyncClick = { viewModel.syncNow() },
                    onNavigateTab = { viewModel.setTab(it) },
                    onSelectItem = { viewModel.selectItemForDetail(it) }
                )
                1 -> InventoryListScreen(
                    state = uiState,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onSelectFacility = { viewModel.selectFacility(it) },
                    onSelectCategory = { viewModel.selectCategory(it) },
                    onSelectItem = { viewModel.selectItemForDetail(it) },
                    onShowBarcodeLabel = { viewModel.showBarcodeLabel(it) },
                    onAddNewItemClick = {
                        preselectedMovementAction = "RECEIVE"
                        viewModel.setTab(3)
                    }
                )
                2 -> BarcodeScannerScreen(
                    state = uiState,
                    onScanCode = { viewModel.scanBarcode(it) },
                    onClearScannedResult = { viewModel.clearScannedResult() },
                    onShowBarcodeLabel = { viewModel.showBarcodeLabel(it) },
                    onQuickAction = { action, item ->
                        preselectedMovementAsset = item
                        preselectedMovementAction = action
                        viewModel.setTab(3)
                    }
                )
                3 -> {
                    val initialSubTab = when (preselectedMovementAction) {
                        "RECEIVE" -> 1
                        "TRANSFER" -> 2
                        "RETURN" -> 3
                        else -> 0 // ISSUE
                    }
                    IssueReturnTransferScreen(
                        state = uiState,
                        initialTab = initialSubTab,
                        preSelectedAsset = preselectedMovementAsset,
                        onReceiveNew = { name, cat, sub, brand, sn, fac, loc, qty, price, sup, inv, pDate, wDate, mac, ip, key ->
                            viewModel.receiveNewItem(name, cat, sub, brand, sn, fac, loc, qty, price, sup, inv, pDate, wDate, mac, ip, key)
                            viewModel.setTab(1)
                        },
                        onIssue = { tag, qty, dept, emp, empId, notes ->
                            viewModel.issueItem(tag, qty, dept, emp, empId, notes)
                            viewModel.setTab(1)
                        },
                        onTransfer = { tag, qty, targetFac, notes ->
                            viewModel.transferItem(tag, qty, targetFac, notes)
                            viewModel.setTab(1)
                        },
                        onReturn = { tag, qty, cond, notes ->
                            viewModel.returnItem(tag, qty, cond, notes)
                            viewModel.setTab(1)
                        }
                    )
                }
                4 -> AuditReportsScreen(
                    state = uiState,
                    onSelectFacility = { viewModel.selectFacility(it) },
                    onPerformAuditAdjust = { tag, count, reason ->
                        viewModel.auditAdjust(tag, count, reason)
                    }
                )
            }
        }
    }

    // Item Detail Dialog
    uiState.selectedItemForDetail?.let { item ->
        ItemDetailDialog(
            item = item,
            language = uiState.language,
            onDismiss = { viewModel.selectItemForDetail(null) },
            onShowBarcodeLabel = {
                viewModel.selectItemForDetail(null)
                viewModel.showBarcodeLabel(item)
            },
            onActionSelected = { action ->
                viewModel.selectItemForDetail(null)
                preselectedMovementAsset = item
                preselectedMovementAction = action
                viewModel.setTab(3)
            }
        )
    }

    // Asset Barcode Label Dialog
    uiState.itemForBarcodeLabel?.let { item ->
        AssetBarcodeLabelDialog(
            item = item,
            language = uiState.language,
            onDismiss = { viewModel.showBarcodeLabel(null) }
        )
    }

    // Role Switch Dialog (Admin, Staff, Dept Head)
    if (showRoleDialog) {
        Dialog(onDismissRequest = { showRoleDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .testTag("role_switch_dialog"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHi) "भूमिका एवं अनुमतियां चुनें" else "Role-Based Access Control",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Slate900
                        )
                        IconButton(onClick = { showRoleDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate700)
                        }
                    }

                    Divider(color = Slate200, modifier = Modifier.padding(vertical = 10.dp))

                    UserRole.entries.forEach { role ->
                        val isSelected = uiState.currentRole == role
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.setRole(role)
                                    showRoleDialog = false
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) TechBlueLight else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) TechBluePrimary else Slate200
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            if (isSelected) TechBluePrimary else Slate100,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else Slate700,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isHi) role.titleHi else role.titleEn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) TechBlueDark else Slate900
                                    )
                                    Text(
                                        text = role.accessDescription,
                                        fontSize = 11.sp,
                                        color = Slate700
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = TechBluePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
