package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.InventoryItemEntity
import com.example.data.model.AppLanguage
import com.example.ui.components.ConditionChip
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedLight
import com.example.ui.theme.PurpleAccent
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
import com.example.ui.viewmodel.InventoryUiState

@Composable
fun BarcodeScannerScreen(
    state: InventoryUiState,
    onScanCode: (String) -> Unit,
    onClearScannedResult: () -> Unit,
    onShowBarcodeLabel: (InventoryItemEntity) -> Unit,
    onQuickAction: (actionType: String, item: InventoryItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHi = state.language == AppLanguage.HINDI
    var inputCode by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // Scanning red laser beam animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("barcode_scanner_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Mode Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHi) "मोबाइल कैमरा एवं यूएसबी बारकोड स्कैनर" else "Mobile Camera & USB Barcode Scanner",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isHi)
                        "स्कैन करते ही इन्वेंट्री रिकॉर्ड तुरंत खुल जाएगा।"
                    else
                        "Instantly receive, issue, transfer, or return stock on scan.",
                    color = Slate400,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Camera / Optical Viewfinder Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(Slate900, RoundedCornerShape(16.dp))
                .border(2.dp, TechBluePrimary, RoundedCornerShape(16.dp))
                .testTag("viewfinder_box"),
            contentAlignment = Alignment.Center
        ) {
            // Viewfinder reticle
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw red laser beam
                val laserY = h * laserPosition
                drawLine(
                    color = Color(0xFFFF2222),
                    start = Offset(40f, laserY),
                    end = Offset(w - 40f, laserY),
                    strokeWidth = 4f
                )

                // Corner brackets
                val cornerLen = 32.dp.toPx()
                val pad = 36.dp.toPx()
                val bracketColor = Color(0xFF00E5FF)
                val strokeW = 4.dp.toPx()

                // Top-Left
                drawLine(bracketColor, Offset(pad, pad), Offset(pad + cornerLen, pad), strokeW)
                drawLine(bracketColor, Offset(pad, pad), Offset(pad, pad + cornerLen), strokeW)

                // Top-Right
                drawLine(bracketColor, Offset(w - pad, pad), Offset(w - pad - cornerLen, pad), strokeW)
                drawLine(bracketColor, Offset(w - pad, pad), Offset(w - pad, pad + cornerLen), strokeW)

                // Bottom-Left
                drawLine(bracketColor, Offset(pad, h - pad), Offset(pad + cornerLen, h - pad), strokeW)
                drawLine(bracketColor, Offset(pad, h - pad), Offset(pad, h - pad - cornerLen), strokeW)

                // Bottom-Right
                drawLine(bracketColor, Offset(w - pad, h - pad), Offset(w - pad - cornerLen, h - pad), strokeW)
                drawLine(bracketColor, Offset(w - pad, h - pad), Offset(w - pad, h - pad - cornerLen), strokeW)
            }

            // Viewfinder center instructions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(52.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isHi) "बारकोड या क्यूआर कोड को फ्रेम में रखें" else "Align QR / Barcode Inside Viewfinder",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Quick Simulated Barcodes for Fast Testing on Emulator
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHi) "त्वरित परीक्षण बारकोड (1-टैप परीक्षण)" else "Quick-Test Barcodes (Emulator / Demo):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val demoTags = listOf(
                    "AST-LKO-2024-001" to "Dell i7 (LKO)",
                    "AST-LKO-2024-002" to "MacBook M2",
                    "NET-AYD-2024-102" to "Fortinet FG (AYD)",
                    "CON-LKO-2024-201" to "HP 88A Toner",
                    "SFT-KNP-2024-303" to "Adobe CC (KNP)"
                )
                items(demoTags) { (tag, label) ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TechBlueLight,
                        modifier = Modifier
                            .clickable {
                                inputCode = tag
                                onScanCode(tag)
                            }
                            .testTag("demo_scan_$tag")
                    ) {
                        Text(
                            text = "$tag ($label)",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechBlueDark,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Manual & USB Barcode Scanner Entry Field
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Usb, contentDescription = null, tint = TechBluePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHi) "यूएसबी स्कैनर या मैन्युअल कोड प्रविष्टि" else "USB Scanner or Manual Code Input",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputCode,
                        onValueChange = {
                            inputCode = it
                            if (it.length >= 12 && it.startsWith("AST") || it.startsWith("NET") || it.startsWith("CON") || it.startsWith("SFT")) {
                                onScanCode(it)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("manual_barcode_input"),
                        placeholder = { Text("e.g. AST-LKO-2024-001", fontSize = 12.sp, color = Slate400) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onScanCode(inputCode) }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechBluePrimary,
                            unfocusedBorderColor = Slate200
                        )
                    )

                    Button(
                        onClick = { onScanCode(inputCode) },
                        colors = ButtonDefaults.buttonColors(containerColor = TechBluePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("scan_lookup_btn")
                    ) {
                        Text(if (isHi) "खोजें" else "Lookup")
                    }
                }
            }
        }

        // Live Scanned Result Card
        state.scannedItemResult?.let { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scanned_result_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, TechBluePrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHi) "सत्यापित परिसंपत्ति रिकॉर्ड" else "Verified Asset Record",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TechBlueDark
                            )
                        }

                        IconButton(
                            onClick = onClearScannedResult,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Replay, contentDescription = "Clear", tint = Slate700)
                        }
                    }

                    Divider(color = Slate200, modifier = Modifier.padding(vertical = 10.dp))

                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tag: ${item.assetTag} • S/N: ${item.serialNumber}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Facility: ${item.facility} (${item.locationDetails})",
                        fontSize = 12.sp,
                        color = Slate700
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SuccessGreenLight
                        ) {
                            Text(
                                text = if (isHi)
                                    "${item.availableQuantity} उपलब्ध (कुल ${item.totalQuantity})"
                                else
                                    "${item.availableQuantity} Available (Total ${item.totalQuantity})",
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        ConditionChip(condition = item.condition)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Instant 1-Tap Workflow Actions
                    Text(
                        text = if (isHi) "त्वरित क्रिया निष्पादित करें:" else "Execute Action on Scanned Item:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onQuickAction("ISSUE", item) },
                            colors = ButtonDefaults.buttonColors(containerColor = TechBluePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("scan_action_issue")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isHi) "निर्गम" else "Issue", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { onQuickAction("TRANSFER", item) },
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("scan_action_transfer")
                        ) {
                            Icon(Icons.Default.MoveDown, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isHi) "ट्रांसफर" else "Transfer", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { onShowBarcodeLabel(item) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("scan_action_label")
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isHi) "लेबल" else "Label", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
