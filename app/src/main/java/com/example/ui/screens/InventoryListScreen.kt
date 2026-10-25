package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.InventoryItemEntity
import com.example.data.model.AppLanguage
import com.example.data.model.Facility
import com.example.data.model.ItemCategory
import com.example.ui.components.CategoryFilterBar
import com.example.ui.components.ConditionChip
import com.example.ui.components.FacilitySelectorBar
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedLight
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight
import com.example.ui.theme.TechBlueDark
import com.example.ui.theme.TechBlueLight
import com.example.ui.theme.TechBluePrimary
import com.example.ui.viewmodel.InventoryUiState

@Composable
fun InventoryListScreen(
    state: InventoryUiState,
    onSearchChange: (String) -> Unit,
    onSelectFacility: (Facility) -> Unit,
    onSelectCategory: (ItemCategory) -> Unit,
    onSelectItem: (InventoryItemEntity) -> Unit,
    onShowBarcodeLabel: (InventoryItemEntity) -> Unit,
    onAddNewItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHi = state.language == AppLanguage.HINDI

    Box(modifier = modifier.fillMaxSize().testTag("inventory_list_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Search Bar & Filter Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inventory_search_field"),
                        placeholder = {
                            Text(
                                text = if (isHi) "टैग, नाम, बारकोड, कर्मचारी से खोजें..." else "Search tag, name, barcode, employee...",
                                fontSize = 13.sp,
                                color = Slate400
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = TechBluePrimary)
                        },
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Slate700)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechBluePrimary,
                            unfocusedBorderColor = Slate200,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }
            }

            // Facility Filter Bar
            item {
                FacilitySelectorBar(
                    selected = state.selectedFacility,
                    language = state.language,
                    onSelect = onSelectFacility
                )
            }

            // Category Filter Bar
            item {
                CategoryFilterBar(
                    selected = state.selectedCategory,
                    language = state.language,
                    onSelect = onSelectCategory
                )
            }

            // Header summary count
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHi)
                            "${state.filteredItems.size} आइटम मिले"
                        else
                            "${state.filteredItems.size} items matched",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate700
                    )
                    Text(
                        text = if (state.selectedFacility == Facility.ALL)
                            (if (isHi) "सभी 5 स्थान" else "All 5 Facilities")
                        else
                            state.selectedFacility.displayNameEn,
                        fontSize = 11.sp,
                        color = TechBlueDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Empty state
            if (state.filteredItems.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isHi) "कोई आइटम नहीं मिला" else "No Inventory Items Found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHi) "कृपया अन्य फ़िल्टर या खोज शब्द आज़माएं।" else "Try adjusting search terms or facility filter.",
                            fontSize = 12.sp,
                            color = Slate700
                        )
                    }
                }
            } else {
                items(state.filteredItems, key = { it.id }) { item ->
                    InventoryItemCard(
                        item = item,
                        language = state.language,
                        onClick = { onSelectItem(item) },
                        onBarcodeClick = { onShowBarcodeLabel(item) }
                    )
                }
            }
        }

        // FAB to add stock
        FloatingActionButton(
            onClick = onAddNewItemClick,
            containerColor = TechBluePrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_new_asset_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Receive New Asset")
        }
    }
}

@Composable
fun InventoryItemCard(
    item: InventoryItemEntity,
    language: AppLanguage,
    onClick: () -> Unit,
    onBarcodeClick: () -> Unit
) {
    val isHi = language == AppLanguage.HINDI
    val isLowStock = item.availableQuantity <= item.minimumAlertThreshold

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clickable { onClick() }
            .testTag("inventory_item_${item.assetTag}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLowStock) DangerRed.copy(alpha = 0.5f) else CardBorderLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Category Icon + Tag Badge + Location Badge + Barcode Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TechBlueLight
                    ) {
                        Text(
                            text = item.assetTag,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = TechBlueDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Slate100
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Slate700,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = item.facility,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate700
                            )
                        }
                    }
                }

                // QR / Barcode Quick Button
                IconButton(
                    onClick = onBarcodeClick,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("barcode_btn_${item.assetTag}")
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "View Barcode Tag",
                        tint = TechBluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Item Name & Sub-Category
            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Slate900,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Sub details: Brand/Model & Serial
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${item.brandModel} • S/N: ${item.serialNumber}",
                    fontSize = 11.sp,
                    color = Slate700,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                ConditionChip(condition = item.condition)
            }

            // Location Specifics or Network IP/MAC
            if (!item.ipAddress.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "IP: ${item.ipAddress} | MAC: ${item.macAddress ?: "N/A"}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TechBlueDark
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stock Availability Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Available vs Total badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isLowStock) DangerRedLight else SuccessGreenLight
                    ) {
                        Text(
                            text = if (isHi)
                                "${item.availableQuantity} / ${item.totalQuantity} उपलब्ध"
                            else
                                "${item.availableQuantity} / ${item.totalQuantity} Available",
                            color = if (isLowStock) DangerRed else SuccessGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    if (isLowStock) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHi) "कम स्टॉक!" else "Low Stock!",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DangerRed
                        )
                    }
                }

                // Assigned employee if any
                if (!item.assignedToEmployee.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AssignmentInd,
                            contentDescription = null,
                            tint = Slate700,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.assignedToEmployee} (${item.assignedDepartment})",
                            fontSize = 11.sp,
                            color = Slate700,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
