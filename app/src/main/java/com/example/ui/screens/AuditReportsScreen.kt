package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TransactionEntity
import com.example.data.model.AppLanguage
import com.example.data.model.Facility
import com.example.ui.components.FacilitySelectorBar
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedLight
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight
import com.example.ui.theme.TechBlueDark
import com.example.ui.theme.TechBlueLight
import com.example.ui.theme.TechBluePrimary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberLight
import com.example.ui.viewmodel.InventoryUiState
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AuditReportsScreen(
    state: InventoryUiState,
    onSelectFacility: (Facility) -> Unit,
    onPerformAuditAdjust: (assetTag: String, physicalCount: Int, reason: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHi = state.language == AppLanguage.HINDI
    val context = LocalContext.current
    var selectedReportTab by remember { mutableIntStateOf(0) }
    var exportSuccessMessage by remember { mutableStateOf<String?>(null) }

    val reportTabs = if (isHi) {
        listOf("लेनदेन लॉग (Audit Trail)", "स्टॉक लेज़र (Stock Register)", "विभाग खपत (Dept Consumption)", "लाइसेंस एवं वारंटी", "भौतिक सत्यापन (Physical Audit)")
    } else {
        listOf("Audit Trail", "Stock Register", "Dept Consumption", "License & Warranty", "Physical Audit")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("audit_reports_screen")
    ) {
        // Location Filter
        FacilitySelectorBar(
            selected = state.selectedFacility,
            language = state.language,
            onSelect = onSelectFacility
        )

        // Export to CSV Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(10.dp),
            color = TechBlueLight.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(1.dp, TechBluePrimary.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = TechBlueDark, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHi) "ऑडिट रिपोर्ट निर्यात (CSV / Excel)" else "Export Central Audit Report (CSV)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechBlueDark
                    )
                }

                Button(
                    onClick = {
                        val csv = generateCsvReport(state)
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("IT Inventory CSV Report", csv)
                        clipboard.setPrimaryClip(clip)
                        exportSuccessMessage = if (isHi) "रिपोर्ट क्लिपबोर्ड पर कॉपी की गई!" else "CSV Export copied to clipboard!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TechBluePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("export_csv_btn")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isHi) "कॉपी CSV" else "Export CSV", fontSize = 11.sp)
                }
            }
        }

        exportSuccessMessage?.let { msg ->
            Text(
                text = msg,
                color = SuccessGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        // Subtabs
        ScrollableTabRow(
            selectedTabIndex = selectedReportTab,
            containerColor = Color.White,
            contentColor = TechBluePrimary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedReportTab]),
                    color = TechBluePrimary,
                    height = 3.dp
                )
            }
        ) {
            reportTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedReportTab == index,
                    onClick = { selectedReportTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedReportTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("report_tab_$index")
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (selectedReportTab) {
                0 -> AuditTrailList(state)
                1 -> StockRegisterView(state)
                2 -> DepartmentConsumptionView(state)
                3 -> LicenseWarrantyView(state)
                4 -> PhysicalAuditReconciliationView(state, onPerformAuditAdjust)
            }
        }
    }
}

@Composable
fun AuditTrailList(state: InventoryUiState) {
    val isHi = state.language == AppLanguage.HINDI
    val transactions = state.transactions

    if (transactions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(if (isHi) "कोई ऑडिट लेनदेन नहीं मिला।" else "No audit transactions logged yet.")
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize().testTag("audit_trail_list")
        ) {
            items(transactions, key = { it.id }) { txn ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Type Badge
                            TransactionTypeBadge(txn.type)

                            val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(txn.timestamp))
                            Text(
                                text = dateStr,
                                fontSize = 11.sp,
                                color = Slate700
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${txn.itemName} (${txn.assetTag})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Slate900
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Details: From -> To / Employee
                        val movementText = buildString {
                            if (txn.fromFacility != null && txn.toFacility != null) {
                                append("Transfer: ${txn.fromFacility} ➔ ${txn.toFacility}")
                            } else if (txn.fromFacility != null) {
                                append("Facility: ${txn.fromFacility}")
                            } else if (txn.toFacility != null) {
                                append("Facility: ${txn.toFacility}")
                            }
                            if (!txn.employeeName.isNullOrBlank()) {
                                append(" • Employee: ${txn.employeeName}")
                            }
                            if (!txn.toDepartment.isNullOrBlank()) {
                                append(" (${txn.toDepartment})")
                            }
                        }

                        Text(
                            text = movementText,
                            fontSize = 11.sp,
                            color = TechBlueDark,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Quantity: ${txn.quantity} | Approved by: ${txn.approvedBy}",
                            fontSize = 11.sp,
                            color = Slate700
                        )

                        if (txn.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Note: ${txn.notes}",
                                fontSize = 11.sp,
                                color = Slate700,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionTypeBadge(type: String) {
    val (bgColor, textColor, label) = when (type) {
        "RECEIVE" -> Triple(SuccessGreenLight, SuccessGreen, "STOCK RECEIPT")
        "ISSUE" -> Triple(TechBlueLight, TechBlueDark, "ISSUED TO DEPT")
        "RETURN" -> Triple(WarningAmberLight, WarningAmber, "RETURNED")
        "TRANSFER" -> Triple(PurpleLight, PurpleAccent, "LOCATION TRANSFER")
        "ADJUSTMENT" -> Triple(DangerRedLight, DangerRed, "AUDIT ADJUST")
        else -> Triple(Slate200, Slate700, type)
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun StockRegisterView(state: InventoryUiState) {
    val isHi = state.language == AppLanguage.HINDI
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize().testTag("stock_register_view")
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate100),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (isHi) "कुल परिसंपत्तियां: ${state.filteredItems.size}" else "Total Line Items: ${state.filteredItems.size}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(if (isHi) "कुल मूल्य: ${currencyFormatter.format(state.totalValuation)}" else "Total Valuation: ${currencyFormatter.format(state.totalValuation)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TechBlueDark)
                }
            }
        }

        items(state.filteredItems) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Slate900,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${item.facility}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = TechBlueDark
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Tag: ${item.assetTag}", fontSize = 11.sp, color = Slate700)
                        Text(text = "Unit Cost: ${currencyFormatter.format(item.unitPrice)}", fontSize = 11.sp, color = Slate700)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Stock Movement Metrics
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate100, RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Total Stock", fontSize = 10.sp, color = Slate700)
                            Text(text = "${item.totalQuantity}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Available", fontSize = 10.sp, color = SuccessGreen)
                            Text(text = "${item.availableQuantity}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SuccessGreen)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Issued", fontSize = 10.sp, color = TechBlueDark)
                            Text(text = "${item.totalQuantity - item.availableQuantity}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TechBlueDark)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Book Value", fontSize = 10.sp, color = Slate700)
                            Text(text = currencyFormatter.format(item.totalQuantity * item.unitPrice), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Slate900)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DepartmentConsumptionView(state: InventoryUiState) {
    val isHi = state.language == AppLanguage.HINDI
    val deptData = state.departmentCounts
    val totalIssued = deptData.values.sum().coerceAtLeast(1)

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize().testTag("dept_consumption_view")
    ) {
        item {
            Text(
                text = if (isHi) "विभाग-वार हार्डवेयर एवं उपभोग्य आवंटन" else "Department-Wise Hardware & Consumables Allocation",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Slate900
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isHi) "वर्तमान में सक्रिय तैनाती और उपकरण खपत" else "Active deployment & department consumption across all facilities",
                fontSize = 11.sp,
                color = Slate700
            )
        }

        items(deptData.entries.toList().sortedByDescending { it.value }) { entry ->
            val ratio = entry.value.toFloat() / totalIssued.toFloat()
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = entry.key,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Slate900
                        )
                        Text(
                            text = "${entry.value} units (${(ratio * 100).toInt()}%)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TechBlueDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { ratio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = TechBluePrimary,
                        trackColor = Slate200,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
            }
        }
    }
}

@Composable
fun LicenseWarrantyView(state: InventoryUiState) {
    val isHi = state.language == AppLanguage.HINDI
    var revealedKeyTag by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize().testTag("license_warranty_view")
    ) {
        item {
            Text(
                text = if (isHi) "सॉफ्टवेयर लाइसेंस एवं हार्डवेयर वारंटी ट्रैकर" else "Software Licenses & Hardware Warranty Tracker",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Slate900
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isHi) "नवीनीकरण समय-सीमा, सुरक्षित लाइसेंस कुंजी एवं समाप्ति अलर्ट" else "Renewal reminders, secure license keys & expiry countdowns",
                fontSize = 11.sp,
                color = Slate700
            )
        }

        val trackableItems = state.items.filter { it.category == "SOFTWARE" || it.warrantyExpiryDate.isNotBlank() }

        items(trackableItems) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Slate900,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = TechBlueLight
                        ) {
                            Text(
                                text = item.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechBlueDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Tag: ${item.assetTag}", fontSize = 11.sp, color = Slate700)
                        Text(
                            text = "Expires: ${item.softwareExpiryDate ?: item.warrantyExpiryDate}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DangerRed
                        )
                    }

                    if (!item.licenseKey.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val isRevealed = revealedKeyTag == item.assetTag
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Slate100,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    revealedKeyTag = if (isRevealed) null else item.assetTag
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Key, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isRevealed) item.licenseKey else "••••••••-••••-••••-••••",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                }
                                Text(
                                    text = if (isRevealed) "Hide" else "Reveal Key",
                                    fontSize = 10.sp,
                                    color = TechBluePrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PhysicalAuditReconciliationView(
    state: InventoryUiState,
    onPerformAuditAdjust: (String, Int, String) -> Unit
) {
    val isHi = state.language == AppLanguage.HINDI
    var selectedItem by remember { mutableStateOf(state.items.firstOrNull()) }
    var physicalCountText by remember { mutableStateOf("4") }
    var reasonText by remember { mutableStateOf("Annual quarterly physical asset stock verification at Lucknow HQ") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = WarningAmberLight.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp)) {
                Icon(Icons.Default.FactCheck, contentDescription = null, tint = WarningAmber)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHi)
                        "भौतिक सत्यापन एवं समायोजन: भौतिक गणना की तुलना सिस्टम बैलेंस से करें।"
                    else
                        "Physical Stock Verification: Compare physical count with system ledger to log variance.",
                    fontSize = 12.sp,
                    color = Slate900
                )
            }
        }

        selectedItem?.let { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "Tag: ${item.assetTag} | Location: ${item.facility} (${item.locationDetails})", fontSize = 11.sp, color = Slate700)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Current Recorded Available: ${item.availableQuantity} units (Total ${item.totalQuantity})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TechBlueDark)
                }
            }

            OutlinedTextField(
                value = physicalCountText,
                onValueChange = { physicalCountText = it.filter { c -> c.isDigit() } },
                label = { Text(if (isHi) "भौतिक रूप से गिनी गई मात्रा (Physical Count)" else "Actual Physically Counted Units") },
                modifier = Modifier.fillMaxWidth().testTag("audit_physical_count_input"),
                shape = RoundedCornerShape(10.dp)
            )

            val currentAvail = item.availableQuantity
            val counted = physicalCountText.toIntOrNull() ?: currentAvail
            val diff = counted - currentAvail

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (diff == 0) SuccessGreenLight else DangerRedLight,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (diff == 0) "No discrepancy: physical count matches system balance." else "Variance detected: $diff units difference from system.",
                    color = if (diff == 0) SuccessGreen else DangerRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }

            OutlinedTextField(
                value = reasonText,
                onValueChange = { reasonText = it },
                label = { Text(if (isHi) "समायोजन कारण / टिप्पणी" else "Audit Discrepancy Reason") },
                modifier = Modifier.fillMaxWidth().testTag("audit_reason_input"),
                shape = RoundedCornerShape(10.dp),
                minLines = 2
            )

            Button(
                onClick = {
                    onPerformAuditAdjust(item.assetTag, counted, reasonText)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TechBluePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_audit_adjust_btn")
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isHi) "ऑडिट समायोजन सुरक्षित करें" else "Save & Reconcile Physical Audit")
            }
        }
    }
}

fun generateCsvReport(state: InventoryUiState): String {
    val sb = StringBuilder()
    sb.append("Asset Tag,Name,Category,Facility,Location,Total Stock,Available,Unit Price,Valuation,Assigned Employee,Department\n")
    state.filteredItems.forEach { item ->
        val totalVal = item.totalQuantity * item.unitPrice
        sb.append("\"${item.assetTag}\",\"${item.name}\",\"${item.category}\",\"${item.facility}\",\"${item.locationDetails}\",${item.totalQuantity},${item.availableQuantity},${item.unitPrice},$totalVal,\"${item.assignedToEmployee ?: ""}\",\"${item.assignedDepartment ?: ""}\"\n")
    }
    return sb.toString()
}
