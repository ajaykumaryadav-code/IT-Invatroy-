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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.InventoryItemEntity
import com.example.data.model.AppLanguage
import com.example.ui.components.ConditionChip
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedLight
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight
import com.example.ui.theme.TechBlueDark
import com.example.ui.theme.TechBlueLight
import com.example.ui.theme.TechBluePrimary
import com.example.ui.theme.WarningAmber
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ItemDetailDialog(
    item: InventoryItemEntity,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onShowBarcodeLabel: () -> Unit,
    onActionSelected: (String) -> Unit
) {
    val isHi = language == AppLanguage.HINDI
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    var keyRevealed by remember { mutableStateOf(false) }
    val isLowStock = item.availableQuantity <= item.minimumAlertThreshold

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("item_detail_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TechBlueLight
                    ) {
                        Text(
                            text = item.assetTag,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TechBlueDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate700)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Slate900
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = TechBlueDark, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.facility} (${item.locationDetails})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TechBlueDark
                        )
                    }
                    ConditionChip(condition = item.condition)
                }

                Divider(color = Slate200, modifier = Modifier.padding(vertical = 12.dp))

                // Stock Availability & Valuation Card
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Slate100,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = if (isHi) "उपलब्ध" else "Available", fontSize = 11.sp, color = Slate700)
                            Text(
                                text = "${item.availableQuantity}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = if (isLowStock) DangerRed else SuccessGreen
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = if (isHi) "कुल स्टॉक" else "Total Stock", fontSize = 11.sp, color = Slate700)
                            Text(text = "${item.totalQuantity}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = if (isHi) "इकाई मूल्य" else "Unit Cost", fontSize = 11.sp, color = Slate700)
                            Text(text = currencyFormatter.format(item.unitPrice), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Technical Specs
                DetailRow(label = "Category", value = item.category)
                DetailRow(label = "Brand / Model", value = item.brandModel)
                DetailRow(label = "Serial Number", value = item.serialNumber)
                DetailRow(label = "Supplier", value = item.supplierName)
                DetailRow(label = "Invoice Number", value = item.invoiceNumber)
                DetailRow(label = "Purchase Date", value = item.purchaseDate)
                DetailRow(label = "Warranty Expiry", value = item.warrantyExpiryDate)

                // Network or License specifics
                if (!item.ipAddress.isNullOrBlank()) {
                    DetailRow(label = "IP Address", value = item.ipAddress)
                }
                if (!item.macAddress.isNullOrBlank()) {
                    DetailRow(label = "MAC Address", value = item.macAddress)
                }
                if (!item.licenseKey.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TechBlueLight.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { keyRevealed = !keyRevealed }
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Key, contentDescription = null, tint = TechBlueDark, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (keyRevealed) item.licenseKey else "••••••••-••••-••••-••••",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                            }
                            Text(text = if (keyRevealed) "Hide" else "Reveal", fontSize = 10.sp, color = TechBluePrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Assignment details if issued
                if (!item.assignedToEmployee.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SuccessGreenLight.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AssignmentInd, contentDescription = null, tint = SuccessGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Assigned to: ${item.assignedToEmployee} (${item.assignedEmployeeId ?: "Staff"})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Slate900
                                )
                                Text(
                                    text = "Department: ${item.assignedDepartment ?: "IT"} • Since: ${item.assignedDate ?: "2024"}",
                                    fontSize = 11.sp,
                                    color = Slate700
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Barcode Sticker Label button
                OutlinedButton(
                    onClick = onShowBarcodeLabel,
                    modifier = Modifier.fillMaxWidth().testTag("view_barcode_sticker_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isHi) "बारकोड / क्यूआर लेबल देखें" else "View / Print Barcode Label")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Movement Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onActionSelected("ISSUE") },
                        colors = ButtonDefaults.buttonColors(containerColor = TechBluePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("detail_action_issue")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isHi) "निर्गम" else "Issue", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onActionSelected("TRANSFER") },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("detail_action_transfer")
                    ) {
                        Icon(Icons.Default.MoveDown, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isHi) "ट्रांसफर" else "Transfer", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onActionSelected("RETURN") },
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("detail_action_return")
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isHi) "वापसी" else "Return", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Slate700)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
    }
}
