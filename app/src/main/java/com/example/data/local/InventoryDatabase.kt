package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [InventoryItemEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class InventoryDatabase : RoomDatabase() {
    abstract fun inventoryDao(): InventoryDao

    companion object {
        @Volatile
        private var INSTANCE: InventoryDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): InventoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InventoryDatabase::class.java,
                    "it_inventory.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Seed initial data
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.inventoryDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: InventoryDao) {
            val initialItems = listOf(
                // Hardware - Laptops & Desktops
                InventoryItemEntity(
                    assetTag = "AST-LKO-2024-001",
                    name = "Dell Latitude 5440 (Intel Core i7-1365U, 16GB, 512GB SSD)",
                    category = "HARDWARE",
                    subCategory = "Laptops",
                    brandModel = "Dell Latitude 5440",
                    serialNumber = "DL-LKO-99214A",
                    facility = "LUCKNOW",
                    locationDetails = "Gomti Nagar HQ, Store Room A, Shelf 2",
                    totalQuantity = 15,
                    availableQuantity = 4,
                    minimumAlertThreshold = 5,
                    unitPrice = 86500.0,
                    supplierName = "TechInfra India Pvt Ltd",
                    invoiceNumber = "INV-2024-8841",
                    purchaseDate = "2024-01-15",
                    warrantyExpiryDate = "2027-01-15",
                    condition = "GOOD",
                    barcode = "AST-LKO-2024-001",
                    assignedToEmployee = "Amit Verma",
                    assignedEmployeeId = "EMP-1042",
                    assignedDepartment = "ENGINEERING",
                    assignedDate = "2024-02-10"
                ),
                InventoryItemEntity(
                    assetTag = "AST-LKO-2024-002",
                    name = "Apple MacBook Air 15\" M2 (16GB RAM, 512GB SSD)",
                    category = "HARDWARE",
                    subCategory = "Laptops",
                    brandModel = "Apple MacBook Air 15 M2",
                    serialNumber = "AP-M2-88190X",
                    facility = "LUCKNOW",
                    locationDetails = "Executive Bay Floor 3, IT Locker 1",
                    totalQuantity = 6,
                    availableQuantity = 1,
                    minimumAlertThreshold = 2,
                    unitPrice = 134900.0,
                    supplierName = "Unicorn Apple Enterprise",
                    invoiceNumber = "INV-APP-7712",
                    purchaseDate = "2024-03-20",
                    warrantyExpiryDate = "2026-03-20",
                    condition = "GOOD",
                    barcode = "AST-LKO-2024-002",
                    assignedToEmployee = "Priya Sharma",
                    assignedEmployeeId = "EMP-1008",
                    assignedDepartment = "EXECUTIVE",
                    assignedDate = "2024-04-01"
                ),
                InventoryItemEntity(
                    assetTag = "AST-KNP-2024-003",
                    name = "Lenovo ThinkPad T14 Gen 4 (AMD Ryzen 7, 32GB, 1TB SSD)",
                    category = "HARDWARE",
                    subCategory = "Laptops",
                    brandModel = "Lenovo ThinkPad T14",
                    serialNumber = "LN-TP-664319",
                    facility = "KANPUR",
                    locationDetails = "Civil Lines Depot, IT Workstation Desk 4",
                    totalQuantity = 10,
                    availableQuantity = 2,
                    minimumAlertThreshold = 3,
                    unitPrice = 92000.0,
                    supplierName = "Kanpur IT Peripherals",
                    invoiceNumber = "INV-KNP-1102",
                    purchaseDate = "2024-02-05",
                    warrantyExpiryDate = "2027-02-05",
                    condition = "GOOD",
                    barcode = "AST-KNP-2024-003",
                    assignedToEmployee = "Rajesh Gupta",
                    assignedEmployeeId = "EMP-2015",
                    assignedDepartment = "OPERATIONS",
                    assignedDate = "2024-02-28"
                ),
                InventoryItemEntity(
                    assetTag = "AST-AYD-2024-004",
                    name = "Dell OptiPlex 7000 Micro Desktop (i5-12500, 16GB, 512GB SSD)",
                    category = "HARDWARE",
                    subCategory = "Desktops",
                    brandModel = "Dell OptiPlex 7000",
                    serialNumber = "DL-OPT-44129",
                    facility = "AYODHYA",
                    locationDetails = "Ayodhya Branch Server Room, Admin Desk 2",
                    totalQuantity = 8,
                    availableQuantity = 5,
                    minimumAlertThreshold = 2,
                    unitPrice = 54000.0,
                    supplierName = "Avadh Solutions",
                    invoiceNumber = "INV-AVD-4411",
                    purchaseDate = "2024-04-12",
                    warrantyExpiryDate = "2027-04-12",
                    condition = "NEW",
                    barcode = "AST-AYD-2024-004"
                ),
                InventoryItemEntity(
                    assetTag = "AST-LKO-2024-005",
                    name = "Dell UltraSharp 27\" 4K USB-C Hub Monitor (U2723QE)",
                    category = "HARDWARE",
                    subCategory = "Monitors",
                    brandModel = "Dell U2723QE",
                    serialNumber = "DL-MON-55821",
                    facility = "LUCKNOW",
                    locationDetails = "HQ Server Room Store Rack 2",
                    totalQuantity = 20,
                    availableQuantity = 6,
                    minimumAlertThreshold = 4,
                    unitPrice = 49500.0,
                    supplierName = "TechInfra India Pvt Ltd",
                    invoiceNumber = "INV-2024-9012",
                    purchaseDate = "2024-01-20",
                    warrantyExpiryDate = "2027-01-20",
                    condition = "GOOD",
                    barcode = "AST-LKO-2024-005",
                    assignedToEmployee = "Neha Singh",
                    assignedEmployeeId = "EMP-1077",
                    assignedDepartment = "ENGINEERING",
                    assignedDate = "2024-02-15"
                ),
                InventoryItemEntity(
                    assetTag = "AST-LKO-2024-006",
                    name = "APC Smart-UPS 1500VA LCD 230V with SmartConnect",
                    category = "HARDWARE",
                    subCategory = "UPS & Power",
                    brandModel = "APC SMT1500IC",
                    serialNumber = "APC-UPS-77291B",
                    facility = "LUCKNOW",
                    locationDetails = "Gomti Nagar Server Room Main Power Rack",
                    totalQuantity = 4,
                    availableQuantity = 1,
                    minimumAlertThreshold = 2,
                    unitPrice = 42000.0,
                    supplierName = "Schneider Electric India",
                    invoiceNumber = "INV-SCH-3310",
                    purchaseDate = "2023-11-10",
                    warrantyExpiryDate = "2025-11-10",
                    condition = "GOOD",
                    barcode = "AST-LKO-2024-006"
                ),
                // Network Equipment with IP / MAC / Location
                InventoryItemEntity(
                    assetTag = "NET-LKO-2024-101",
                    name = "Cisco Catalyst 9200L 24-Port Gigabit PoE+ Managed Switch",
                    category = "NETWORK",
                    subCategory = "Switches",
                    brandModel = "Cisco C9200L-24P-4G",
                    serialNumber = "CSCO-CAT-99120",
                    macAddress = "00:1E:13:4B:92:0A",
                    ipAddress = "192.168.10.2",
                    facility = "LUCKNOW",
                    locationDetails = "Floor 2 Central Datacenter, Rack Unit 12",
                    totalQuantity = 3,
                    availableQuantity = 1,
                    minimumAlertThreshold = 1,
                    unitPrice = 168000.0,
                    supplierName = "Cisco Direct Partner North",
                    invoiceNumber = "INV-CSCO-0091",
                    purchaseDate = "2023-08-15",
                    warrantyExpiryDate = "2028-08-15",
                    condition = "GOOD",
                    barcode = "NET-LKO-2024-101"
                ),
                InventoryItemEntity(
                    assetTag = "NET-AYD-2024-102",
                    name = "Fortinet FortiGate 60F Next-Gen Hardware Firewall",
                    category = "NETWORK",
                    subCategory = "Firewalls",
                    brandModel = "Fortinet FG-60F",
                    serialNumber = "FGT-60F-882194",
                    macAddress = "70:4C:A5:66:3D:11",
                    ipAddress = "10.20.1.1",
                    facility = "AYODHYA",
                    locationDetails = "Ayodhya Branch IT Room, Network Cabinet A",
                    totalQuantity = 2,
                    availableQuantity = 1,
                    minimumAlertThreshold = 1,
                    unitPrice = 78500.0,
                    supplierName = "SecureNet Cyber Ltd",
                    invoiceNumber = "INV-SNET-412",
                    purchaseDate = "2024-02-01",
                    warrantyExpiryDate = "2027-02-01",
                    condition = "GOOD",
                    barcode = "NET-AYD-2024-102"
                ),
                InventoryItemEntity(
                    assetTag = "NET-KNP-2024-103",
                    name = "Ubiquiti UniFi 6 Pro WiFi-6 Dual-Band Access Point",
                    category = "NETWORK",
                    subCategory = "Access Points",
                    brandModel = "Ubiquiti U6-Pro",
                    serialNumber = "UBQ-U6-902148",
                    macAddress = "68:D7:9A:81:4F:2E",
                    ipAddress = "192.168.20.50",
                    facility = "KANPUR",
                    locationDetails = "Depot Warehouse Ceiling Mount Zone B",
                    totalQuantity = 6,
                    availableQuantity = 2,
                    minimumAlertThreshold = 2,
                    unitPrice = 18500.0,
                    supplierName = "Kanpur IT Peripherals",
                    invoiceNumber = "INV-KNP-1390",
                    purchaseDate = "2024-03-05",
                    warrantyExpiryDate = "2026-03-05",
                    condition = "GOOD",
                    barcode = "NET-KNP-2024-103"
                ),
                // IT Consumables
                InventoryItemEntity(
                    assetTag = "CON-LKO-2024-201",
                    name = "HP 88A Black LaserJet Toner Cartridge (CC388A)",
                    category = "CONSUMABLES",
                    subCategory = "Toners & Ink",
                    brandModel = "HP 88A",
                    serialNumber = "HP-TN-88A-7731",
                    facility = "LUCKNOW",
                    locationDetails = "HQ Stationery & Consumables Cupboard 3",
                    totalQuantity = 25,
                    availableQuantity = 3, // LOW STOCK ALERT
                    minimumAlertThreshold = 6,
                    unitPrice = 3850.0,
                    supplierName = "Apex Office Supplies",
                    invoiceNumber = "INV-APX-882",
                    purchaseDate = "2024-05-10",
                    warrantyExpiryDate = "2025-05-10",
                    condition = "NEW",
                    barcode = "CON-LKO-2024-201"
                ),
                InventoryItemEntity(
                    assetTag = "CON-KNP-2024-202",
                    name = "D-Link Cat6 UTP 305-Meter Pure Copper Cable Roll",
                    category = "CONSUMABLES",
                    subCategory = "Cables & Wiring",
                    brandModel = "D-Link NCB-C6UBLUR-305",
                    serialNumber = "DLK-C6-305-101",
                    facility = "KANPUR",
                    locationDetails = "Network Spares Storage Bay 1",
                    totalQuantity = 8,
                    availableQuantity = 2, // LOW STOCK ALERT
                    minimumAlertThreshold = 3,
                    unitPrice = 7900.0,
                    supplierName = "Kanpur IT Peripherals",
                    invoiceNumber = "INV-KNP-1450",
                    purchaseDate = "2024-04-18",
                    warrantyExpiryDate = "2026-04-18",
                    condition = "NEW",
                    barcode = "CON-KNP-2024-202"
                ),
                InventoryItemEntity(
                    assetTag = "CON-AYD-2024-203",
                    name = "Kingston FURY Beast 16GB DDR5 5200MHz Desktop RAM",
                    category = "CONSUMABLES",
                    subCategory = "RAM & SSD Spares",
                    brandModel = "Kingston KF552C40BB-16",
                    serialNumber = "KNG-D5-99218",
                    facility = "AYODHYA",
                    locationDetails = "Branch IT Spares Bin #4",
                    totalQuantity = 12,
                    availableQuantity = 7,
                    minimumAlertThreshold = 3,
                    unitPrice = 4800.0,
                    supplierName = "Avadh Solutions",
                    invoiceNumber = "INV-AVD-5501",
                    purchaseDate = "2024-06-01",
                    warrantyExpiryDate = "2029-06-01",
                    condition = "NEW",
                    barcode = "CON-AYD-2024-203"
                ),
                // Software & Licenses
                InventoryItemEntity(
                    assetTag = "SFT-LKO-2024-301",
                    name = "Microsoft 365 Enterprise E3 (50 User Annual Subscription)",
                    category = "SOFTWARE",
                    subCategory = "Cloud Subscriptions",
                    brandModel = "Microsoft Cloud License",
                    serialNumber = "MS365-ENT-E3-50U",
                    facility = "LUCKNOW",
                    locationDetails = "Central Azure AD Cloud Tenant",
                    totalQuantity = 50,
                    availableQuantity = 8,
                    minimumAlertThreshold = 10,
                    unitPrice = 28500.0,
                    supplierName = "Microsoft Enterprise Direct",
                    invoiceNumber = "INV-MS-2024-0012",
                    purchaseDate = "2024-01-01",
                    warrantyExpiryDate = "2025-01-01",
                    condition = "GOOD",
                    licenseKey = "MS365-XXXXX-99812-PROD-LKO",
                    softwareExpiryDate = "2025-01-01",
                    barcode = "SFT-LKO-2024-301",
                    assignedDepartment = "IT"
                ),
                InventoryItemEntity(
                    assetTag = "SFT-LKO-2024-302",
                    name = "CrowdStrike Falcon Endpoint Security (100 Nodes)",
                    category = "SOFTWARE",
                    subCategory = "Security & Antivirus",
                    brandModel = "CrowdStrike Falcon Complete",
                    serialNumber = "CS-FALC-100N-ENT",
                    facility = "LUCKNOW",
                    locationDetails = "Security Operations Center (SOC)",
                    totalQuantity = 100,
                    availableQuantity = 15,
                    minimumAlertThreshold = 20,
                    unitPrice = 4500.0,
                    supplierName = "CyberShield Security Ltd",
                    invoiceNumber = "INV-CS-99128",
                    purchaseDate = "2024-02-15",
                    warrantyExpiryDate = "2025-02-15",
                    condition = "GOOD",
                    licenseKey = "CRWD-SEC-8819-KEY-ACTIVATED",
                    softwareExpiryDate = "2025-02-15",
                    barcode = "SFT-LKO-2024-302",
                    assignedDepartment = "IT"
                ),
                InventoryItemEntity(
                    assetTag = "SFT-KNP-2024-303",
                    name = "Adobe Creative Cloud All Apps (Team Annual License)",
                    category = "SOFTWARE",
                    subCategory = "Design Software",
                    brandModel = "Adobe CC Enterprise",
                    serialNumber = "ADB-CC-TEAM-10",
                    facility = "KANPUR",
                    locationDetails = "Creative & Marketing Terminal 1",
                    totalQuantity = 5,
                    availableQuantity = 1, // Alert
                    minimumAlertThreshold = 2,
                    unitPrice = 64000.0,
                    supplierName = "Adobe Systems India",
                    invoiceNumber = "INV-ADB-2024-77",
                    purchaseDate = "2024-03-01",
                    warrantyExpiryDate = "2025-03-01",
                    condition = "GOOD",
                    licenseKey = "ADBE-7719-2241-CC-TEAM",
                    softwareExpiryDate = "2025-03-01",
                    barcode = "SFT-KNP-2024-303",
                    assignedToEmployee = "Vikas Mishra",
                    assignedEmployeeId = "EMP-2098",
                    assignedDepartment = "HR",
                    assignedDate = "2024-03-05"
                )
            )

            dao.insertAllItems(initialItems)

            // Seed initial Audit / Transaction Logs
            val initialTransactions = listOf(
                TransactionEntity(
                    assetTag = "AST-LKO-2024-001",
                    itemName = "Dell Latitude 5440",
                    type = "RECEIVE",
                    quantity = 15,
                    toFacility = "LUCKNOW",
                    approvedBy = "Vikram Aditya (IT Admin)",
                    notes = "New procurement via TechInfra PO-8841. Initial stock received at Lucknow HQ.",
                    timestamp = System.currentTimeMillis() - (86400000L * 30)
                ),
                TransactionEntity(
                    assetTag = "AST-LKO-2024-001",
                    itemName = "Dell Latitude 5440",
                    type = "ISSUE",
                    quantity = 1,
                    fromFacility = "LUCKNOW",
                    toDepartment = "ENGINEERING",
                    employeeName = "Amit Verma",
                    employeeId = "EMP-1042",
                    approvedBy = "Ritu Saxena (IT Lead)",
                    notes = "Issued for Mobile Platform Development Project.",
                    timestamp = System.currentTimeMillis() - (86400000L * 15)
                ),
                TransactionEntity(
                    assetTag = "AST-LKO-2024-001",
                    itemName = "Dell Latitude 5440",
                    type = "TRANSFER",
                    quantity = 3,
                    fromFacility = "LUCKNOW",
                    toFacility = "AYODHYA",
                    approvedBy = "Vikram Aditya (IT Admin)",
                    notes = "Inter-facility transfer for Ayodhya branch setup via secure transit.",
                    timestamp = System.currentTimeMillis() - (86400000L * 7)
                ),
                TransactionEntity(
                    assetTag = "NET-LKO-2024-101",
                    itemName = "Cisco Catalyst 9200L 24-Port Switch",
                    type = "RECEIVE",
                    quantity = 3,
                    toFacility = "LUCKNOW",
                    approvedBy = "Vikram Aditya (IT Admin)",
                    notes = "Datacenter switch upgrade receipt. Configured with IP 192.168.10.2.",
                    timestamp = System.currentTimeMillis() - (86400000L * 45)
                ),
                TransactionEntity(
                    assetTag = "CON-LKO-2024-201",
                    itemName = "HP 88A Black LaserJet Toner",
                    type = "ISSUE",
                    quantity = 5,
                    fromFacility = "LUCKNOW",
                    toDepartment = "ACCOUNTS",
                    employeeName = "Suresh Pandey",
                    employeeId = "EMP-1088",
                    approvedBy = "Ritu Saxena (IT Lead)",
                    notes = "Quarterly audit printing toner consumption for Accounts dept.",
                    timestamp = System.currentTimeMillis() - (86400000L * 2)
                )
            )

            dao.insertAllTransactions(initialTransactions)
        }
    }
}
