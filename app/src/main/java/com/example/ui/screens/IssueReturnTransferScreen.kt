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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.InventoryItemEntity
import com.example.data.model.AppLanguage
import com.example.data.model.Department
import com.example.data.model.Facility
import com.example.data.model.ItemCategory
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight
import com.example.ui.theme.TechBlueDark
import com.example.ui.theme.TechBlueLight
import com.example.ui.theme.TechBluePrimary
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.InventoryUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IssueReturnTransferScreen(
    state: InventoryUiState,
    initialTab: Int = 0,
    preSelectedAsset: InventoryItemEntity? = null,
    onReceiveNew: (
        name: String, category: ItemCategory, subCategory: String, brandModel: String,
        serialNumber: String, facility: Facility, locationDetails: String, quantity: Int,
        unitPrice: Double, supplierName: String, invoiceNumber: String, purchaseDate: String,
        warrantyExpiryDate: String, macAddress: String?, ipAddress: String?, licenseKey: String?
    ) -> Unit,
    onIssue: (assetTag: String, quantity: Int, department: Department, employeeName: String, employeeId: String, notes: String) -> Unit,
    onTransfer: (assetTag: String, quantity: Int, targetFacility: Facility, notes: String) -> Unit,
    onReturn: (assetTag: String, quantity: Int, condition: String, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHi = state.language == AppLanguage.HINDI
    var activeSubTab by remember { mutableIntStateOf(initialTab) }

    val tabTitles = if (isHi) {
        listOf("स्टॉक निर्गम (Issue)", "प्राप्ति (Receive)", "स्थानांतरण (Transfer)", "वापसी (Return)")
    } else {
        listOf("Issue Stock", "Receive Stock", "Transfer Location", "Return Stock")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("issue_return_transfer_screen")
    ) {
        TabRow(
            selectedTabIndex = activeSubTab,
            containerColor = Color.White,
            contentColor = TechBluePrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                    color = TechBluePrimary,
                    height = 3.dp
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = activeSubTab == index,
                    onClick = { activeSubTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (activeSubTab == index) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    },
                    modifier = Modifier.testTag("subtab_$index")
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (activeSubTab) {
                0 -> IssueForm(state, preSelectedAsset, onIssue)
                1 -> ReceiveForm(state, onReceiveNew)
                2 -> TransferForm(state, preSelectedAsset, onTransfer)
                3 -> ReturnForm(state, preSelectedAsset, onReturn)
            }
        }
    }
}

@Composable
fun IssueForm(
    state: InventoryUiState,
    preSelectedAsset: InventoryItemEntity?,
    onSubmit: (String, Int, Department, String, String, String) -> Unit
) {
    val isHi = state.language == AppLanguage.HINDI
    val scrollState = rememberScrollState()

    var selectedItem by remember { mutableStateOf(preSelectedAsset ?: state.items.firstOrNull()) }
    var quantityText by remember { mutableStateOf("1") }
    var selectedDept by remember { mutableStateOf(Department.ENGINEERING) }
    var employeeName by remember { mutableStateOf("") }
    var employeeId by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var itemDropdownOpen by remember { mutableStateOf(false) }
    var deptDropdownOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TechBlueLight.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = TechBluePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHi)
                        "कर्मचारी अथवा विभाग को उपकरण जारी करें। स्वीकृति रिकॉर्ड दर्ज होगा।"
                    else
                        "Issue hardware/consumables to employee. Transaction will be audited.",
                    fontSize = 12.sp,
                    color = TechBlueDark,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Asset Selector Dropdown
        Text(
            text = if (isHi) "परिसंपत्ति चुनें:" else "Select Asset / Item:",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Slate900
        )
        Box {
            OutlinedTextField(
                value = selectedItem?.let { "${it.assetTag} - ${it.name} (${it.availableQuantity} available)" } ?: "Select item",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { itemDropdownOpen = true }
                    .testTag("issue_asset_selector"),
                trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechBluePrimary,
                    unfocusedBorderColor = Slate200
                )
            )
            DropdownMenu(
                expanded = itemDropdownOpen,
                onDismissRequest = { itemDropdownOpen = false }
            ) {
                state.items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = "${item.assetTag} - ${item.name}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Loc: ${item.facility} | Avail: ${item.availableQuantity}/${item.totalQuantity}",
                                    fontSize = 10.sp,
                                    color = Slate700
                                )
                            }
                        },
                        onClick = {
                            selectedItem = item
                            itemDropdownOpen = false
                        }
                    )
                }
            }
        }

        // Quantity Input
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = quantityText,
                onValueChange = { quantityText = it.filter { char -> char.isDigit() } },
                label = { Text(if (isHi) "मात्रा" else "Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .testTag("issue_qty_input"),
                shape = RoundedCornerShape(10.dp)
            )

            // Department Dropdown
            Box(modifier = Modifier.weight(1.5f)) {
                OutlinedTextField(
                    value = if (isHi) selectedDept.displayNameHi else selectedDept.displayNameEn,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(if (isHi) "विभाग" else "Department") },
                    trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { deptDropdownOpen = true }
                        .testTag("issue_dept_selector"),
                    shape = RoundedCornerShape(10.dp)
                )
                DropdownMenu(
                    expanded = deptDropdownOpen,
                    onDismissRequest = { deptDropdownOpen = false }
                ) {
                    Department.realDepartments().forEach { dept ->
                        DropdownMenuItem(
                            text = { Text(if (isHi) dept.displayNameHi else dept.displayNameEn) },
                            onClick = {
                                selectedDept = dept
                                deptDropdownOpen = false
                            }
                        )
                    }
                }
            }
        }

        // Employee Name & ID
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = employeeName,
                onValueChange = { employeeName = it },
                label = { Text(if (isHi) "कर्मचारी का नाम" else "Employee Name") },
                modifier = Modifier
                    .weight(1.5f)
                    .testTag("issue_emp_name_input"),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = employeeId,
                onValueChange = { employeeId = it },
                label = { Text(if (isHi) "कर्मचारी आईडी" else "Employee ID") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("issue_emp_id_input"),
                shape = RoundedCornerShape(10.dp)
            )
        }

        // Notes / Purpose
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text(if (isHi) "उद्देश्य / अनुमोदन टिप्पणी" else "Approval Remarks / Purpose") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("issue_notes_input"),
            shape = RoundedCornerShape(10.dp),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Submit Button
        Button(
            onClick = {
                val item = selectedItem ?: return@Button
                val qty = quantityText.toIntOrNull() ?: 1
                if (employeeName.isBlank()) return@Button
                onSubmit(item.assetTag, qty, selectedDept, employeeName, employeeId, notes)
            },
            enabled = selectedItem != null && employeeName.isNotBlank() && (quantityText.toIntOrNull() ?: 0) > 0,
            colors = ButtonDefaults.buttonColors(containerColor = TechBluePrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_issue_btn")
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isHi) "निर्गम पुष्टि करें (Issue Asset)" else "Confirm Stock Issue")
        }
    }
}

@Composable
fun ReceiveForm(
    state: InventoryUiState,
    onSubmit: (
        name: String, category: ItemCategory, subCategory: String, brandModel: String,
        serialNumber: String, facility: Facility, locationDetails: String, quantity: Int,
        unitPrice: Double, supplierName: String, invoiceNumber: String, purchaseDate: String,
        warrantyExpiryDate: String, macAddress: String?, ipAddress: String?, licenseKey: String?
    ) -> Unit
) {
    val isHi = state.language == AppLanguage.HINDI
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf(ItemCategory.HARDWARE) }
    var subCategory by remember { mutableStateOf("Laptops") }
    var brandModel by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }
    var selectedFacility by remember { mutableStateOf(Facility.LUCKNOW) }
    var locationDetails by remember { mutableStateOf("Floor 2 Store, Shelf 1") }
    var quantityText by remember { mutableStateOf("5") }
    var unitPriceText by remember { mutableStateOf("55000") }
    var supplierName by remember { mutableStateOf("TechInfra India Pvt Ltd") }
    var invoiceNumber by remember { mutableStateOf("INV-2024-9100") }
    var ipAddress by remember { mutableStateOf("") }
    var macAddress by remember { mutableStateOf("") }
    var licenseKey by remember { mutableStateOf("") }

    var catDropdownOpen by remember { mutableStateOf(false) }
    var facDropdownOpen by remember { mutableStateOf(false) }

    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SuccessGreenLight.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AddBusiness, contentDescription = null, tint = SuccessGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHi)
                        "नई खरीद प्राप्ति: सिस्टम स्वचालित रूप से अद्वितीय बारकोड उत्पन्न करेगा।"
                    else
                        "Stock Procurement: System auto-generates unique QR/Barcode asset tags.",
                    fontSize = 12.sp,
                    color = Slate900,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Item Name
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(if (isHi) "उपकरण / आइटम का नाम" else "Item Name / Description") },
            placeholder = { Text("e.g. Dell Latitude 5440 i7") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("receive_name_input"),
            shape = RoundedCornerShape(10.dp)
        )

        // Category & Facility
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = if (isHi) selectedCat.displayNameHi else selectedCat.displayNameEn,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(if (isHi) "श्रेणी" else "Category") },
                    trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { catDropdownOpen = true }
                        .testTag("receive_cat_selector"),
                    shape = RoundedCornerShape(10.dp)
                )
                DropdownMenu(
                    expanded = catDropdownOpen,
                    onDismissRequest = { catDropdownOpen = false }
                ) {
                    ItemCategory.realCategories().forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(if (isHi) cat.displayNameHi else cat.displayNameEn) },
                            onClick = {
                                selectedCat = cat
                                catDropdownOpen = false
                            }
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = if (isHi) selectedFacility.displayNameHi else selectedFacility.displayNameEn,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(if (isHi) "स्थान / कार्यालय" else "Facility") },
                    trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { facDropdownOpen = true }
                        .testTag("receive_fac_selector"),
                    shape = RoundedCornerShape(10.dp)
                )
                DropdownMenu(
                    expanded = facDropdownOpen,
                    onDismissRequest = { facDropdownOpen = false }
                ) {
                    Facility.locationList().forEach { fac ->
                        DropdownMenuItem(
                            text = { Text(if (isHi) fac.displayNameHi else fac.displayNameEn) },
                            onClick = {
                                selectedFacility = fac
                                facDropdownOpen = false
                            }
                        )
                    }
                }
            }
        }

        // Brand & Serial Number
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = brandModel,
                onValueChange = { brandModel = it },
                label = { Text(if (isHi) "ब्रांड एवं मॉडल" else "Brand & Model") },
                placeholder = { Text("Dell 5440") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("receive_brand_input"),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = serialNumber,
                onValueChange = { serialNumber = it },
                label = { Text(if (isHi) "सीरियल नंबर" else "Serial Number") },
                placeholder = { Text("SN-99812") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("receive_serial_input"),
                shape = RoundedCornerShape(10.dp)
            )
        }

        // Quantity & Unit Price
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = quantityText,
                onValueChange = { quantityText = it.filter { c -> c.isDigit() } },
                label = { Text(if (isHi) "मात्रा" else "Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .testTag("receive_qty_input"),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = unitPriceText,
                onValueChange = { unitPriceText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text(if (isHi) "इकाई मूल्य (₹)" else "Unit Price (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .weight(1f)
                    .testTag("receive_price_input"),
                shape = RoundedCornerShape(10.dp)
            )
        }

        // Supplier & Invoice
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = supplierName,
                onValueChange = { supplierName = it },
                label = { Text(if (isHi) "आपूर्तिकर्ता (Supplier)" else "Supplier Name") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("receive_supplier_input"),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = invoiceNumber,
                onValueChange = { invoiceNumber = it },
                label = { Text(if (isHi) "चालान संख्या (Invoice)" else "Invoice No.") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("receive_invoice_input"),
                shape = RoundedCornerShape(10.dp)
            )
        }

        // Optional IP / MAC for Network or License Key for Software
        if (selectedCat == ItemCategory.NETWORK) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    label = { Text("IP Address") },
                    placeholder = { Text("192.168.1.10") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = macAddress,
                    onValueChange = { macAddress = it },
                    label = { Text("MAC Address") },
                    placeholder = { Text("00:1A:2B:3C:4D:5E") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        } else if (selectedCat == ItemCategory.SOFTWARE) {
            OutlinedTextField(
                value = licenseKey,
                onValueChange = { licenseKey = it },
                label = { Text("Software License Key / Subscription ID") },
                placeholder = { Text("XXXXX-XXXXX-XXXXX") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }

        OutlinedTextField(
            value = locationDetails,
            onValueChange = { locationDetails = it },
            label = { Text(if (isHi) "विशिष्ट भंडारण स्थान" else "Location Details / Rack") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (name.isBlank()) return@Button
                val qty = quantityText.toIntOrNull() ?: 1
                val price = unitPriceText.toDoubleOrNull() ?: 0.0
                val warranty = "2027-01-01"
                onSubmit(
                    name, selectedCat, subCategory, brandModel, serialNumber,
                    selectedFacility, locationDetails, qty, price, supplierName,
                    invoiceNumber, today, warranty, macAddress, ipAddress, licenseKey
                )
            },
            enabled = name.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_receive_btn")
        ) {
            Icon(Icons.Default.AddBusiness, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isHi) "स्टॉक प्राप्ति एवं बारकोड निर्माण" else "Receive Stock & Generate Barcode")
        }
    }
}

@Composable
fun TransferForm(
    state: InventoryUiState,
    preSelectedAsset: InventoryItemEntity?,
    onSubmit: (String, Int, Facility, String) -> Unit
) {
    val isHi = state.language == AppLanguage.HINDI
    val scrollState = rememberScrollState()

    var selectedItem by remember { mutableStateOf(preSelectedAsset ?: state.items.firstOrNull()) }
    var quantityText by remember { mutableStateOf("1") }
    var targetFacility by remember { mutableStateOf(Facility.AYODHYA) }
    var notes by remember { mutableStateOf("Internal office setup transfer") }

    var itemDropdownOpen by remember { mutableStateOf(false) }
    var facDropdownOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PurpleAccent.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.MoveDown, contentDescription = null, tint = PurpleAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHi)
                        "कार्यालयों के बीच स्थानांतरण (उदा. लखनऊ -> अयोध्या)। केंद्रीय स्टॉक स्वतः अद्यतित होगा।"
                    else
                        "Transfer between facilities (e.g. Lucknow -> Ayodhya). Central balances update automatically.",
                    fontSize = 12.sp,
                    color = Slate900,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Asset Selector
        Text(text = if (isHi) "स्थानांतरित करने हेतु आइटम:" else "Item to Transfer:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Box {
            OutlinedTextField(
                value = selectedItem?.let { "${it.assetTag} - ${it.name} (${it.facility}: ${it.availableQuantity} avail)" } ?: "Select item",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { itemDropdownOpen = true }
                    .testTag("transfer_asset_selector"),
                trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) },
                shape = RoundedCornerShape(10.dp)
            )
            DropdownMenu(
                expanded = itemDropdownOpen,
                onDismissRequest = { itemDropdownOpen = false }
            ) {
                state.items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text("${item.assetTag} (${item.facility}): ${item.name} [${item.availableQuantity} avail]")
                        },
                        onClick = {
                            selectedItem = item
                            itemDropdownOpen = false
                        }
                    )
                }
            }
        }

        // Destination Facility Dropdown
        Text(text = if (isHi) "गंतव्य स्थान (Destination Facility):" else "Destination Facility:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Box {
            OutlinedTextField(
                value = if (isHi) targetFacility.displayNameHi else targetFacility.displayNameEn,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { facDropdownOpen = true }
                    .testTag("transfer_fac_selector"),
                shape = RoundedCornerShape(10.dp)
            )
            DropdownMenu(
                expanded = facDropdownOpen,
                onDismissRequest = { facDropdownOpen = false }
            ) {
                Facility.locationList()
                    .filter { it.name != selectedItem?.facility }
                    .forEach { fac ->
                        DropdownMenuItem(
                            text = { Text(if (isHi) fac.displayNameHi else fac.displayNameEn) },
                            onClick = {
                                targetFacility = fac
                                facDropdownOpen = false
                            }
                        )
                    }
            }
        }

        // Quantity Input
        OutlinedTextField(
            value = quantityText,
            onValueChange = { quantityText = it.filter { c -> c.isDigit() } },
            label = { Text(if (isHi) "स्थानांतरण मात्रा" else "Transfer Quantity") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("transfer_qty_input"),
            shape = RoundedCornerShape(10.dp)
        )

        // Notes
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text(if (isHi) "ट्रांजिट एवं अनुमोदन टिप्पणी" else "Transit / Courier / Reason Notes") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val item = selectedItem ?: return@Button
                val qty = quantityText.toIntOrNull() ?: 1
                onSubmit(item.assetTag, qty, targetFacility, notes)
            },
            enabled = selectedItem != null && (quantityText.toIntOrNull() ?: 0) > 0,
            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_transfer_btn")
        ) {
            Icon(Icons.Default.MoveDown, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isHi) "स्थानांतरण प्रेषित करें" else "Dispatch Location Transfer")
        }
    }
}

@Composable
fun ReturnForm(
    state: InventoryUiState,
    preSelectedAsset: InventoryItemEntity?,
    onSubmit: (String, Int, String, String) -> Unit
) {
    val isHi = state.language == AppLanguage.HINDI
    val scrollState = rememberScrollState()

    var selectedItem by remember { mutableStateOf(preSelectedAsset ?: state.items.firstOrNull { it.totalQuantity > it.availableQuantity } ?: state.items.firstOrNull()) }
    var quantityText by remember { mutableStateOf("1") }
    var condition by remember { mutableStateOf("GOOD") }
    var notes by remember { mutableStateOf("Returned after project completion in working condition.") }

    var itemDropdownOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Replay, contentDescription = null, tint = WarningAmber)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHi)
                        "कर्मचारी/विभाग से उपकरण वापसी। उपलब्ध स्टॉक तुरंत बहाल हो जाएगा।"
                    else
                        "Stock Return from employee/department. Restores available stock count.",
                    fontSize = 12.sp,
                    color = Slate900,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Asset Selector
        Text(text = if (isHi) "वापसी हेतु आइटम:" else "Item to Return:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Box {
            OutlinedTextField(
                value = selectedItem?.let { "${it.assetTag} - ${it.name} (${it.assignedToEmployee ?: "Issued"})" } ?: "Select item",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { itemDropdownOpen = true }
                    .testTag("return_asset_selector"),
                trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) },
                shape = RoundedCornerShape(10.dp)
            )
            DropdownMenu(
                expanded = itemDropdownOpen,
                onDismissRequest = { itemDropdownOpen = false }
            ) {
                state.items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text("${item.assetTag}: ${item.name} (Assigned: ${item.assignedToEmployee ?: "N/A"})")
                        },
                        onClick = {
                            selectedItem = item
                            itemDropdownOpen = false
                        }
                    )
                }
            }
        }

        // Quantity
        OutlinedTextField(
            value = quantityText,
            onValueChange = { quantityText = it.filter { c -> c.isDigit() } },
            label = { Text(if (isHi) "वापसी मात्रा" else "Return Quantity") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("return_qty_input"),
            shape = RoundedCornerShape(10.dp)
        )

        // Condition Assessment Selection
        Text(text = if (isHi) "वापसी स्थिति (Condition):" else "Return Condition:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("GOOD" to "Good / Working", "UNDER_REPAIR" to "Needs Repair", "SCRAPPED" to "Damaged").forEach { (code, label) ->
                val isSelected = condition == code
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { condition = code },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) TechBlueLight else Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) TechBluePrimary else Slate200
                    )
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) TechBlueDark else Slate700,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Remarks
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text(if (isHi) "निरीक्षण टिप्पणी" else "Inspection Remarks") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val item = selectedItem ?: return@Button
                val qty = quantityText.toIntOrNull() ?: 1
                onSubmit(item.assetTag, qty, condition, notes)
            },
            enabled = selectedItem != null,
            colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_return_btn")
        ) {
            Icon(Icons.Default.Replay, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isHi) "वापसी स्वीकार करें" else "Accept Stock Return")
        }
    }
}
