package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.InventoryItemEntity
import com.example.data.model.AppLanguage
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.TechBlueDark
import com.example.ui.theme.TechBluePrimary
import kotlin.math.absoluteValue

@Composable
fun LinearBarcodeCanvas(
    code: String,
    modifier: Modifier = Modifier,
    barColor: Color = Color.Black
) {
    Canvas(modifier = modifier) {
        val totalWidth = size.width
        val barHeight = size.height

        // Deterministic pseudo Code-128 barcode pattern derived from input string
        val seed = code.hashCode().absoluteValue
        val barCount = 42
        val unitWidth = totalWidth / barCount

        // Guard bars at start and end
        drawRect(barColor, Offset(0f, 0f), Size(unitWidth * 1.5f, barHeight))
        drawRect(barColor, Offset(unitWidth * 3f, 0f), Size(unitWidth * 1f, barHeight))

        var currentX = unitWidth * 5f
        for (i in 0 until 30) {
            val bit = ((seed shr (i % 24)) and 1) xor (code.getOrElse(i % code.length) { 'A' }.code and 1)
            val thickness = if (bit == 1) unitWidth * 1.8f else unitWidth * 0.9f
            drawRect(
                color = barColor,
                topLeft = Offset(currentX, 0f),
                size = Size(thickness, barHeight)
            )
            currentX += thickness + (unitWidth * 0.7f)
            if (currentX >= totalWidth - (unitWidth * 5f)) break
        }

        // End guard
        drawRect(barColor, Offset(totalWidth - unitWidth * 3f, 0f), Size(unitWidth * 1f, barHeight))
        drawRect(barColor, Offset(totalWidth - unitWidth * 1.5f, 0f), Size(unitWidth * 1.5f, barHeight))
    }
}

@Composable
fun QrCodeCanvas(
    content: String,
    modifier: Modifier = Modifier,
    moduleColor: Color = Color.Black
) {
    Canvas(modifier = modifier) {
        val matrixSize = 21 // Standard QR Version 1 grid size
        val cellSize = size.width / matrixSize

        fun isFinderPattern(r: Int, c: Int): Boolean {
            // Top-left finder
            if (r in 0..6 && c in 0..6) {
                return (r == 0 || r == 6 || c == 0 || c == 6) || (r in 2..4 && c in 2..4)
            }
            // Top-right finder
            if (r in 0..6 && c in (matrixSize - 7) until matrixSize) {
                val adjC = c - (matrixSize - 7)
                return (r == 0 || r == 6 || adjC == 0 || adjC == 6) || (r in 2..4 && adjC in 2..4)
            }
            // Bottom-left finder
            if (r in (matrixSize - 7) until matrixSize && c in 0..6) {
                val adjR = r - (matrixSize - 7)
                return (adjR == 0 || adjR == 6 || c == 0 || c == 6) || (adjR in 2..4 && c in 2..4)
            }
            return false
        }

        val hash = content.hashCode().absoluteValue
        for (r in 0 until matrixSize) {
            for (c in 0 until matrixSize) {
                val drawModule = if (isFinderPattern(r, c)) {
                    true
                } else if ((r in 0..7 && c in 0..7) ||
                    (r in 0..7 && c in (matrixSize - 8) until matrixSize) ||
                    (r in (matrixSize - 8) until matrixSize && c in 0..7)
                ) {
                    false // Separator margins
                } else if (r == 6 || c == 6) {
                    // Timing pattern
                    (r + c) % 2 == 0
                } else {
                    // Data modules
                    val cellHash = (hash * 31 + r * 17 + c * 23 + content.getOrElse((r + c) % content.length) { 'X' }.code)
                    (cellHash % 3 == 0 || cellHash % 7 == 0)
                }

                if (drawModule) {
                    drawRect(
                        color = moduleColor,
                        topLeft = Offset(c * cellSize, r * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }
            }
        }
    }
}

@Composable
fun AssetBarcodeLabelDialog(
    item: InventoryItemEntity,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val isHi = language == AppLanguage.HINDI

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("asset_barcode_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = null,
                            tint = TechBlueDark,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHi) "परिसंपत्ति बारकोड लेबल" else "Asset Barcode Label",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Slate900
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_barcode_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate700)
                    }
                }

                Divider(color = Slate200, modifier = Modifier.padding(vertical = 12.dp))

                // Printable Physical Asset Sticker Area
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, TechBlueDark, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Badge / Company
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "IT ASSET MANAGEMENT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = TechBlueDark,
                                letterSpacing = 1.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = TechBluePrimary
                            ) {
                                Text(
                                    text = item.facility,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // QR Code + Linear Barcode Side by Side
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // QR Code
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .background(Color.White)
                                    .border(1.dp, Slate200, RoundedCornerShape(8.dp))
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                QrCodeCanvas(
                                    content = item.barcode,
                                    modifier = Modifier.size(78.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Linear Barcode
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                LinearBarcodeCanvas(
                                    code = item.barcode,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.barcode,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Slate900,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Asset Specs Grid on Sticker
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate100, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = item.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Slate900,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Tag: ${item.assetTag}",
                                    fontSize = 11.sp,
                                    color = Slate700,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Category: ${item.category}",
                                    fontSize = 11.sp,
                                    color = Slate700
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "S/N: ${item.serialNumber}",
                                    fontSize = 11.sp,
                                    color = Slate700
                                )
                                Text(
                                    text = "Cond: ${item.condition}",
                                    fontSize = 11.sp,
                                    color = Slate700
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons: Print Asset Tag / Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_barcode_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isHi) "साझा करें" else "Share Tag")
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = TechBluePrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("print_barcode_btn")
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isHi) "लेबल प्रिंट" else "Print Label")
                    }
                }
            }
        }
    }
}
