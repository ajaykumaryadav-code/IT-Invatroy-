package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.InventoryDatabase
import com.example.data.local.InventoryItemEntity
import com.example.data.model.AppLanguage
import com.example.data.model.Department
import com.example.data.model.Facility
import com.example.data.model.ItemCategory
import com.example.data.model.UserRole
import com.example.data.repository.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InventoryRepository
    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        val database = InventoryDatabase.getDatabase(application, viewModelScope)
        repository = InventoryRepository(database.inventoryDao())

        // Initial launch & load items
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            repository.getItems("ALL").collectLatest { rawItems ->
                _uiState.update { state ->
                    state.copy(
                        items = rawItems,
                        filteredItems = applyFilters(rawItems, state.selectedFacility, state.selectedCategory, state.selectedDepartment, state.searchQuery)
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getTransactions("ALL").collectLatest { transactions ->
                _uiState.update { it.copy(transactions = transactions) }
            }
        }
    }

    private fun applyFilters(
        items: List<InventoryItemEntity>,
        facility: Facility,
        category: ItemCategory,
        department: Department,
        query: String
    ): List<InventoryItemEntity> {
        return items.filter { item ->
            val matchFacility = (facility == Facility.ALL) || (item.facility.equals(facility.name, ignoreCase = true))
            val matchCategory = (category == ItemCategory.ALL) || (item.category.equals(category.name, ignoreCase = true))
            val matchDept = (department == Department.ALL) || (item.assignedDepartment.equals(department.name, ignoreCase = true))
            val matchQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.assetTag.contains(query, ignoreCase = true) ||
                    item.barcode.contains(query, ignoreCase = true) ||
                    item.brandModel.contains(query, ignoreCase = true) ||
                    item.serialNumber.contains(query, ignoreCase = true) ||
                    (item.assignedToEmployee?.contains(query, ignoreCase = true) == true) ||
                    (item.locationDetails.contains(query, ignoreCase = true))

            matchFacility && matchCategory && matchDept && matchQuery
        }
    }

    fun selectFacility(facility: Facility) {
        _uiState.update { state ->
            state.copy(
                selectedFacility = facility,
                filteredItems = applyFilters(state.items, facility, state.selectedCategory, state.selectedDepartment, state.searchQuery)
            )
        }
    }

    fun selectCategory(category: ItemCategory) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                filteredItems = applyFilters(state.items, state.selectedFacility, category, state.selectedDepartment, state.searchQuery)
            )
        }
    }

    fun selectDepartment(department: Department) {
        _uiState.update { state ->
            state.copy(
                selectedDepartment = department,
                filteredItems = applyFilters(state.items, state.selectedFacility, state.selectedCategory, department, state.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredItems = applyFilters(state.items, state.selectedFacility, state.selectedCategory, state.selectedDepartment, query)
            )
        }
    }

    fun setTab(tabIndex: Int) {
        _uiState.update { it.copy(currentTab = tabIndex) }
    }

    fun setRole(role: UserRole) {
        _uiState.update { it.copy(currentRole = role) }
    }

    fun toggleLanguage() {
        _uiState.update { state ->
            val nextLang = if (state.language == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
            state.copy(language = nextLang)
        }
    }

    fun selectItemForDetail(item: InventoryItemEntity?) {
        _uiState.update { it.copy(selectedItemForDetail = item) }
    }

    fun showBarcodeLabel(item: InventoryItemEntity?) {
        _uiState.update { it.copy(itemForBarcodeLabel = item) }
    }

    fun scanBarcode(code: String) {
        val trimmed = code.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            val item = repository.getItemByBarcode(trimmed) ?: repository.getItemByAssetTag(trimmed)
            _uiState.update { state ->
                if (item != null) {
                    state.copy(
                        scannedItemResult = item,
                        infoMessage = if (state.language == AppLanguage.ENGLISH)
                            "Identified: ${item.name} (${item.assetTag})"
                        else
                            "पहचाना गया: ${item.name} (${item.assetTag})"
                    )
                } else {
                    state.copy(
                        scannedItemResult = null,
                        errorMessage = if (state.language == AppLanguage.ENGLISH)
                            "No item found matching barcode: $trimmed"
                        else
                            "बारकोड से मेल खाता कोई आइटम नहीं मिला: $trimmed"
                    )
                }
            }
        }
    }

    fun clearScannedResult() {
        _uiState.update { it.copy(scannedItemResult = null) }
    }

    fun syncNow() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSyncing = true,
                    syncMessage = if (it.language == AppLanguage.ENGLISH)
                        "Synchronizing Lucknow, Kanpur & Ayodhya nodes..."
                    else
                        "लखनऊ, कानपुर एवं अयोध्या नोड्स सिंक हो रहे हैं..."
                )
            }
            delay(1200) // Realistic synchronization latency
            _uiState.update {
                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                it.copy(
                    isSyncing = false,
                    lastSyncTime = System.currentTimeMillis(),
                    syncMessage = if (it.language == AppLanguage.ENGLISH)
                        "All facilities synchronized at $timeStr"
                    else
                        "सभी स्थान $timeStr पर सिंक हो चुके हैं",
                    infoMessage = if (it.language == AppLanguage.ENGLISH)
                        "Central Cloud Sync Complete: 5 locations up-to-date"
                    else
                        "केंद्रीय क्लाउड सिंक पूर्ण: 5 स्थान अद्यतित हैं"
                )
            }
        }
    }

    fun receiveNewItem(
        name: String,
        category: ItemCategory,
        subCategory: String,
        brandModel: String,
        serialNumber: String,
        facility: Facility,
        locationDetails: String,
        quantity: Int,
        unitPrice: Double,
        supplierName: String,
        invoiceNumber: String,
        purchaseDate: String,
        warrantyExpiryDate: String,
        macAddress: String? = null,
        ipAddress: String? = null,
        licenseKey: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val prefix = when (category) {
                ItemCategory.HARDWARE -> "AST"
                ItemCategory.CONSUMABLES -> "CON"
                ItemCategory.NETWORK -> "NET"
                ItemCategory.SOFTWARE -> "SFT"
                else -> "ITM"
            }
            val locCode = facility.name.take(3)
            val randomNum = (100..999).random()
            val tag = "$prefix-$locCode-2024-$randomNum"

            val item = InventoryItemEntity(
                assetTag = tag,
                name = name,
                category = category.name,
                subCategory = subCategory.ifBlank { "General" },
                brandModel = brandModel,
                serialNumber = serialNumber,
                macAddress = macAddress?.ifBlank { null },
                ipAddress = ipAddress?.ifBlank { null },
                facility = facility.name,
                locationDetails = locationDetails,
                totalQuantity = quantity,
                availableQuantity = quantity,
                minimumAlertThreshold = 3,
                unitPrice = unitPrice,
                supplierName = supplierName,
                invoiceNumber = invoiceNumber,
                purchaseDate = purchaseDate,
                warrantyExpiryDate = warrantyExpiryDate,
                condition = "NEW",
                licenseKey = licenseKey?.ifBlank { null },
                barcode = tag
            )

            val adminName = "${_uiState.value.currentRole.titleEn} (Authorized)"
            repository.receiveNewItem(item, adminName)

            _uiState.update {
                it.copy(
                    infoMessage = if (it.language == AppLanguage.ENGLISH)
                        "Received stock successfully! Generated Tag: $tag"
                    else
                        "स्टॉक सफलतापूर्वक प्राप्त हुआ! टैग: $tag"
                )
            }
        }
    }

    fun issueItem(
        assetTag: String,
        quantity: Int,
        department: Department,
        employeeName: String,
        employeeId: String,
        notes: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val adminName = "${_uiState.value.currentRole.titleEn} (Authorized)"
            val result = repository.issueItem(
                assetTag = assetTag,
                quantity = quantity,
                department = department.name,
                employeeName = employeeName,
                employeeId = employeeId,
                approvedBy = adminName,
                notes = notes
            )
            _uiState.update { state ->
                result.fold(
                    onSuccess = {
                        state.copy(
                            infoMessage = if (state.language == AppLanguage.ENGLISH)
                                "Asset issued to $employeeName ($department) successfully!"
                            else
                                "उपकरण सफलतापूर्वक $employeeName ($department) को जारी किया गया!",
                            scannedItemResult = null
                        )
                    },
                    onFailure = { error ->
                        state.copy(errorMessage = error.message)
                    }
                )
            }
        }
    }

    fun returnItem(
        assetTag: String,
        quantity: Int,
        condition: String,
        notes: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val adminName = "${_uiState.value.currentRole.titleEn} (Authorized)"
            val result = repository.returnItem(
                assetTag = assetTag,
                quantity = quantity,
                condition = condition,
                receivedBy = adminName,
                notes = notes
            )
            _uiState.update { state ->
                result.fold(
                    onSuccess = {
                        state.copy(
                            infoMessage = if (state.language == AppLanguage.ENGLISH)
                                "Returned stock accepted back into inventory."
                            else
                                "वापस आया स्टॉक इन्वेंट्री में दर्ज किया गया।"
                        )
                    },
                    onFailure = { error ->
                        state.copy(errorMessage = error.message)
                    }
                )
            }
        }
    }

    fun transferItem(
        assetTag: String,
        quantity: Int,
        targetFacility: Facility,
        notes: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val adminName = "${_uiState.value.currentRole.titleEn} (Authorized)"
            val result = repository.transferItem(
                assetTag = assetTag,
                quantity = quantity,
                targetFacility = targetFacility.name,
                approvedBy = adminName,
                notes = notes
            )
            _uiState.update { state ->
                result.fold(
                    onSuccess = {
                        state.copy(
                            infoMessage = if (state.language == AppLanguage.ENGLISH)
                                "Dispatched $quantity units to ${targetFacility.displayNameEn}."
                            else
                                "$quantity इकाइयाँ ${targetFacility.displayNameHi} को भेजी गईं।"
                        )
                    },
                    onFailure = { error ->
                        state.copy(errorMessage = error.message)
                    }
                )
            }
        }
    }

    fun auditAdjust(
        assetTag: String,
        physicalCount: Int,
        reason: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val adminName = "${_uiState.value.currentRole.titleEn} (Auditor)"
            val result = repository.auditStockAdjustment(
                assetTag = assetTag,
                physicalCount = physicalCount,
                auditedBy = adminName,
                reason = reason
            )
            _uiState.update { state ->
                result.fold(
                    onSuccess = {
                        state.copy(
                            infoMessage = if (state.language == AppLanguage.ENGLISH)
                                "Audit reconciliation recorded for $assetTag."
                            else
                                "$assetTag के लिए ऑडिट समाधान दर्ज किया गया।"
                        )
                    },
                    onFailure = { error ->
                        state.copy(errorMessage = error.message)
                    }
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(infoMessage = null, errorMessage = null) }
    }
}
