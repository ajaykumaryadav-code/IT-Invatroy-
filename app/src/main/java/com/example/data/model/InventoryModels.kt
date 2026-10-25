package com.example.data.model

enum class Facility(val displayNameEn: String, val displayNameHi: String, val officeType: String) {
    ALL("All Facilities", "सभी स्थान", "Central Overview"),
    LUCKNOW("Lucknow (HO)", "लखनऊ (मुख्यालय)", "Central IT Hub, Gomti Nagar"),
    KANPUR("Kanpur", "कानपुर", "Civil Lines Logistics & Depot"),
    AYODHYA("Ayodhya", "अयोध्या", "Branch Operations & Datacenter"),
    NOIDA("Noida", "नोएडा", "Sector 62 Tech Center"),
    VARANASI("Varanasi", "वाराणसी", "Eastern Regional Support Hub");

    companion object {
        fun locationList(): List<Facility> = entries.filter { it != ALL }
    }
}

enum class ItemCategory(val displayNameEn: String, val displayNameHi: String) {
    ALL("All Categories", "सभी श्रेणियां"),
    HARDWARE("Hardware", "हार्डवेयर"),
    CONSUMABLES("Consumables", "उपभोग्य सामग्री"),
    NETWORK("Network Equipment", "नेटवर्क उपकरण"),
    SOFTWARE("Software & Licenses", "सॉफ्टवेयर एवं लाइसेंस");

    companion object {
        fun realCategories(): List<ItemCategory> = entries.filter { it != ALL }
    }
}

enum class ItemCondition(val displayNameEn: String, val displayNameHi: String) {
    NEW("Brand New", "नया"),
    GOOD("Good / Active", "सक्रिय / उत्तम"),
    UNDER_REPAIR("Under Repair", "मरम्मत में"),
    SCRAPPED("Scrapped", "स्क्रैप / निष्कासित")
}

enum class Department(val displayNameEn: String, val displayNameHi: String) {
    ALL("All Departments", "सभी विभाग"),
    IT("IT & Infrastructure", "आईटी एवं अवसंरचना"),
    ENGINEERING("Engineering & Dev", "इंजीनियरिंग"),
    HR("Human Resources", "मानव संसाधन"),
    ACCOUNTS("Finance & Accounts", "वित्त एवं लेखा"),
    OPERATIONS("Operations", "परिचालन"),
    SUPPORT("Customer Support", "ग्राहक सहायता"),
    EXECUTIVE("Executive Leadership", "कार्यकारी नेतृत्व");

    companion object {
        fun realDepartments(): List<Department> = entries.filter { it != ALL }
    }
}

enum class TransactionType(val displayNameEn: String, val displayNameHi: String) {
    RECEIVE("Stock In / Purchase", "स्टॉक प्राप्ति"),
    ISSUE("Issued to Dept", "विभाग को जारी"),
    RETURN("Returned to Stock", "स्टॉक में वापसी"),
    TRANSFER("Location Transfer", "स्थान स्थानांतरण"),
    ADJUSTMENT("Audit Adjustment", "ऑडिट समायोजन")
}

enum class UserRole(val titleEn: String, val titleHi: String, val accessDescription: String) {
    ADMIN("IT Administrator", "आईटी व्यवस्थापक", "Full System Access & Central Stock Control"),
    STAFF("IT Support Staff", "आईटी सपोर्ट स्टाफ", "Issue, Receive & Scan Equipment"),
    DEPT_HEAD("Department Head", "विभागाध्यक्ष", "View Department Inventory & Request Stock")
}

enum class AppLanguage(val label: String, val code: String) {
    ENGLISH("English", "en"),
    HINDI("हिन्दी", "hi")
}
