package com.ruangcurhat

object DataStoreManager {

    // 1. Struktur Data Konselor
    data class Counselor(
        val name: String,
        val pangkat: String,
        val nrp: String,
        val jabatan: String,
        val kesatuan: String,
        val telegram: String,
        val religion: String,
        val emoji: String
    )

    // 2. Struktur Data Akun Anggota Dinas (User)
    data class UserAccount(
        val email: String,
        val name: String,
        val pangkat: String,
        val nrp: String,
        val jabatan: String,
        val kesatuan: String,
        val telegram: String,
        val role: String // 'admin' atau 'user'
    )

    private val counselorDatabase = mutableListOf<Counselor>()
    private val userDatabase = mutableListOf<UserAccount>()

    init {
        // Seeders Pembimbing Lintas Agama Bawaan
        counselorDatabase.add(Counselor("Ustadz Drs. H. Ahmad Fauzi, M.Ag.", "MAYOR (SUS)", "5241029", "Kasi Bintal Islam", "Mabesau", "+628123456789", "Islam", "👳‍♂️"))
        counselorDatabase.add(Counselor("Pendeta Yoseph Siregar, S.Th.", "KAPTEN (SUS)", "5321045", "Kasi Bintal Kristen", "Lanud Abdulrachman Saleh", "yosephsiregar", "Kristen", "👨‍💼"))
        counselorDatabase.add(Counselor("Romo FX. Fransiskanus, Pr.", "KAPTEN (SUS)", "5432091", "Pati Rohani Katolik", "Lanud Iswahjudi", "romo_frans", "Katolik", "⛪"))

        // Seeders Akun Pengembang & Anggota Dinas Bawaan
        userDatabase.add(UserAccount("admin@gmail.com", "Agatha Herma (PM)", "KAPTEN (SUS)", "5410291", "Kasi Bintalud", "Lanud Abdulrachman Saleh", "+6281234567890", "admin"))
        userDatabase.add(UserAccount("user@gmail.com", "Anonim Damar", "LETDA (SUS)", "5430221", "Prajurit Dinas", "Lanud Adisutjipto", "+628987654321", "user"))
    }

    // --- LOGIKA UTILITY KELOLA KONSELOR ---
    fun addCounselor(c: Counselor) {
        counselorDatabase.add(c)
    }

    fun deleteCounselor(nrp: String) {
        counselorDatabase.removeAll { it.nrp == nrp }
    }

    fun getAllCounselors(): List<Counselor> {
        return counselorDatabase
    }

    fun getCounselorsByReligion(religion: String): List<Counselor> {
        return counselorDatabase.filter { it.religion.equals(religion, ignoreCase = true) }
    }

    // --- LOGIKA UTILITY KELOLA ANGGOTA DINAS ---
    fun addUserAccount(u: UserAccount) {
        userDatabase.add(u)
    }

    fun validateUserLogin(email: String): UserAccount? {
        return userDatabase.find { it.email.equals(email, ignoreCase = true) }
    }
}