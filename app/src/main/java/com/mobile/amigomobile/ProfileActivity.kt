package com.mobile.amigomobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Locale // Penting: Tambahkan import Locale

class ProfileActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { Firebase.auth }

    // Inisialisasi UI Components dari activity_profile.xml
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnBack: ImageView
    private lateinit var ivProfileAvatar: ImageView // BARU: Dari XML yang diperbagus
    // Dihapus: private lateinit var btnEditProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (auth.currentUser == null) {
            goToLogin()
            return
        }

        initializeViews()
        setupUserData()
        setupListeners()
    }

    private fun initializeViews() {
        // ID Utama
        tvName = findViewById(R.id.tvProfileName)
        tvEmail = findViewById(R.id.tvProfileEmail)
        btnLogout = findViewById(R.id.btnLogout)
        btnBack = findViewById(R.id.btnBack)

        // ID Tambahan dari Desain XML Baru
        ivProfileAvatar = findViewById(R.id.ivProfileAvatar)
        // Dihapus: btnEditProfile = findViewById(R.id.btnEditProfile)
    }

    private fun setupUserData() {
        auth.currentUser?.let { user ->
            val email = user.email ?: "Admin (No Email)"

            // Mengambil bagian pertama dari email sebagai nama dan mengubah huruf pertama menjadi kapital (Titlecase)
            val name = email.split('@').firstOrNull()?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            } ?: "Admin"

            tvName.text = name
            tvEmail.text = email
        }
    }

    private fun setupListeners() {
        // Tombol Logout
        btnLogout.setOnClickListener {
            // Konfirmasi Logout sebelum keluar
            showLogoutConfirmation()
        }

        // Tombol Kembali
        btnBack.setOnClickListener {
            finish() // Kembali ke Activity sebelumnya
        }

        // Listener untuk Edit Profile DIHAPUS.
    }

    private fun showLogoutConfirmation() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Logout Akun")
            .setMessage("Anda yakin ingin keluar dari sesi admin saat ini?")
            .setPositiveButton("Logout") { _, _ ->
                auth.signOut()
                Toast.makeText(this, "Anda berhasil Logout.", Toast.LENGTH_SHORT).show()
                goToLogin()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}