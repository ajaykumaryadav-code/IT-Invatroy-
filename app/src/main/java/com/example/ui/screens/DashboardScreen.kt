package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.InventoryItemEntity
import com.example.data.model.AppLanguage
import com.example.data.model.Facility
import com.example.ui.components.FacilitySelectorBar
import com.example.ui.components.StatCard
import com.example.ui.components.SyncStatusBanner
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
import java.util.Locale

@Composable
fun DashboardScreen(
    state: InventoryUiState,
    onSelectFacility: (Facility) -> Unit,
    onSyncClick: () -> Unit,
    onNavigateTab: (Int) -> Unit,
    onSelectItem: (InventoryItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHi = state.language == AppLanguage.HINDI
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Location Filter Chips
        item {
            Column {
                FacilitySelectorBar(
                    selected = state.selectedFacility,
                    language = state.language,
                    onSelect = onSelectFacility
                )

                // Sync status banner
                SyncStatusBanner(
                    isSyncing = state.isSyncing,
                    syncMessage = state.syncMessage,
                    language = state.language,
                    onSyncClick = onSyncClick
                )
            }
        }

        // Top 4 Primary KPI Stat Cards
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = if (isHi) "कुल स्टॉक" else "Total Inventory",
                        value = "${state.totalAssetsCount}",
                        subtext = if (isHi) "${state.totalAvailableCount} उपलब्ध" else "${state.totalAvailableCount} available units",
                        icon = Icons.Default.Devices,
                        iconColor = TechBluePrimary,
                        containerColor = TechBlueLight,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(1) }
                    )

                    StatCard(
                        title = if (isHi) "कम स्टॉक चेतावनी" else "Low-Stock Alerts",
                        value = "${state.lowStockCount}",
                        subtext = if (isHi) "तत्काल पुनः आपूर्ति" else "Critical reorder alert",
                        icon = Icons.Default.Warning,
                        iconColor = DangerRed,
                        containerColor = if (state.lowStockCount > 0) DangerRedLight else Slate100,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(1) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = if (isHi) "आवंटित परिसंपत्तियां" else "Assigned Assets",
                        value = "${state.totalAssignedCount}",
                        subtext = if (isHi) "विभागों में सक्रिय" else "In active deployment",
                        icon = Icons.Default.AssignmentTurnedIn,
                        iconColor = SuccessGreen,
                        containerColor = SuccessGreenLight,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(3) }
                    )

                    val formattedValuation = try {
                        val num = state.totalValuation
                        if (num >= 10000000) String.format("₹%.2f Cr", num / 10000000.0)
                        else if (num >= 100000) String.format("₹%.2f L", num / 100000.0)
                        else currencyFormatter.format(num)
                    } catch (e: Exception) {
                        "₹${state.totalValuation.toInt()}"
                    }

                    StatCard(
                        title = if (isHi) "कुल मूल्यांकन" else "Total Valuation",
                        value = formattedValuation,
                        subtext = if (isHi) "वर्तमान परिसंपत्ति मूल्य" else "Capital book valuation",
                        icon = Icons.Default.CurrencyRupee,
                        iconColor = PurpleAccent,
                        containerColor = PurpleLight,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(4) }
                    )
                }
            }
        }

        // Quick Workflow Action Buttons
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isHi) "त्वरित इन्वेंट्री कार्यप्रवाह" else "Quick Inventory Actions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ActionIconItem(
                            icon = Icons.Default.QrCodeScanner,
                            label = if (isHi) "स्कैन करें" else "Scan QR",
                            tint = TechBluePrimary,
                            tag = "quick_action_scan",
                            onClick = { onNavigateTab(2) }
                        )
                        ActionIconItem(
                            icon = Icons.Default.AddBusiness,
                            label = if (isHi) "प्राप्ति" else "Receive",
                            tint = SuccessGreen,
                            tag = "quick_action_receive",
                            onClick = { onNavigateTab(3) }
                        )
                        ActionIconItem(
                            icon = Icons.Default.Send,
                            label = if (isHi) "निर्गम" else "Issue",
                            tint = PurpleAccent,
                            tag = "quick_action_issue",
                            onClick = { onNavigateTab(3) }
                        )
                        ActionIconItem(
                            icon = Icons.Default.MoveDown,
                            label = if (isHi) "स्थानांतरण" else "Transfer",
                            tint = WarningAmber,
                            tag = "quick_action_transfer",
                            onClick = { onNavigateTab(3) }
                        )
                        ActionIconItem(
                            icon = Icons.Default.FactCheck,
                            label = if (isHi) "ऑडिट" else "Audit Log",
                            tint = TechBlueDark,
                            tag = "quick_action_audit",
                            onClick = { onNavigateTab(4) }
                        )
                    }
                }
            }
        }

        // Category Breakdown Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHi) "श्रेणी-वार परिसंपत्ति वितरण" else "Asset Category Breakdown",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Slate900
                        )
                        Text(
                            text = if (isHi) "4 श्रेणियां" else "4 Categories",
                            fontSize = 12.sp,
                            color = Slate700
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val total = (state.items.size).coerceAtLeast(1)
                    CategoryProgressRow(
                        name = if (isHi) "हार्डवेयर (लैपटॉप/मॉनिटर/पीसी)" else "Hardware (Laptops/Monitors/PC)",
                        count = state.hardwareCount,
                        total = total,
                        color = TechBluePrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CategoryProgressRow(
                        name = if (isHi) "नेटवर्क उपकरण (राउटर/स्विच/एपी)" else "Network Equipment (Switches/AP)",
                        count = state.networkCount,
                        total = total,
                        color = CyanAccent
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CategoryProgressRow(
                        name = if (isHi) "उपभोग्य सामग्री (टोनर/केबल/रैम)" else "Consumables (Toners/Cables/RAM)",
                        count = state.consumablesCount,
                        total = total,
                        color = WarningAmber
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CategoryProgressRow(
                        name = if (isHi) "सॉफ्टवेयर एवं लाइसेंस" else "Software & Enterprise Licenses",
                        count = state.softwareCount,
                        total = total,
                        color = PurpleAccent
                    )
                }
            }
        }

        // Multi-Location Distribution Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Hub, contentDescription = null, tint = TechBlueDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHi) "बहु-स्थान वितरण (लखनऊ • कानपुर • अयोध्या)" else "Multi-Location Inventory Split",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Slate900
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val facilityData = state.facilityCounts
                    val totalFacAssets = facilityData.values.sum().coerceAtLeast(1)

                    listOf(
                        Triple("LUCKNOW", "Lucknow HQ (लखनऊ)", TechBluePrimary),
                        Triple("KANPUR", "Kanpur Depot (कानपुर)", WarningAmber),
                        Triple("AYODHYA", "Ayodhya Branch (अयोध्या)", SuccessGreen),
                        Triple("NOIDA", "Noida Center (नोएडा)", PurpleAccent),
                        Triple("VARANASI", "Varanasi Hub (वाराणसी)", CyanAccent)
                    ).forEach { (code, label, color) ->
                        val count = facilityData[code] ?: 0
                        CategoryProgressRow(
                            name = label,
                            count = count,
                            total = totalFacAssets,
                            color = color
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // Low-Stock and Expiry Urgent Alerts
        val criticalItems = state.items.filter { it.availableQuantity <= it.minimumAlertThreshold }
        if (criticalItems.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHi) "कम स्टॉक एवं तत्काल अलर्ट (${criticalItems.size})" else "Urgent Stock Alerts (${criticalItems.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = DangerRed
                            )
                        }
                    }
                }
            }

            items(criticalItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onSelectItem(item) }
                        .testTag("alert_item_${item.assetTag}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DangerRedLight.copy(alpha = 0.5f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Slate900,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${item.assetTag} • ${item.facility} (${item.locationDetails})",
                                fontSize = 11.sp,
                                color = Slate700,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DangerRed
                        ) {
                            Text(
                                text = if (isHi) "${item.availableQuantity} शेष" else "${item.availableQuantity} Left",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ActionIconItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
            .testTag(tag)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(tint.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Slate800
        )
    }
}

@Composable
fun CategoryProgressRow(
    name: String,
    count: Int,
    total: Int,
    color: Color
) {
    val progress = (count.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                fontSize = 12.sp,
                color = Slate800,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$count units (${(progress * 100).toInt()}%)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = color,
            trackColor = Slate200,
            strokeCap = StrokeCap.Round
        )
    }
}
