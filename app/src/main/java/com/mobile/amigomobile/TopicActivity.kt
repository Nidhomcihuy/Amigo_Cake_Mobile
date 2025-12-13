// File: TopicActivity.kt (Diperbaiki)
package com.mobile.amigomobile

import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObjects
// import com.google.firebase.firestore.ktx.firestore // Tidak digunakan, dihapus
// import com.google.firebase.ktx.Firebase // Tidak digunakan, dihapus

class TopicActivity : AppCompatActivity() {

    private val TAG = "TopicActivity"
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // UI Components
    private lateinit var recyclerViewTopics: RecyclerView
    private lateinit var fabAddTopic: FloatingActionButton
    private lateinit var ivAdminIcon: ImageView
    private lateinit var tvEmptyState: TextView

    // Bottom Navigation Components
    private lateinit var navHomeContainer: LinearLayout
    private lateinit var navManualOrderContainer: LinearLayout
    private lateinit var navReviewContainer: LinearLayout
    private lateinit var navOrderListContainer: LinearLayout
    private lateinit var navTopicContainer: LinearLayout

    // RecyclerView Components
    private lateinit var topicAdapter: TopicAdapter
    private val topics = mutableListOf<TopicModel>() // Digunakan hanya untuk inisialisasi awal adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pastikan R.layout.activity_topic sudah didefinisikan
        setContentView(R.layout.activity_topic)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        if (auth.currentUser == null) {
            goToLogin()
            return
        }

        initializeViews()
        setupRecyclerView()
        setupListeners()
        fetchTopics()
    }

    private fun initializeViews() {
        recyclerViewTopics = findViewById(R.id.recyclerViewTopics)
        fabAddTopic = findViewById(R.id.fabAddTopic)
        // Pastikan ID ini benar di layout
        ivAdminIcon = findViewById(R.id.profile_icon)
        // Pastikan ID ini benar di layout
        tvEmptyState = findViewById(R.id.tv_empty_state)

        // Bottom Navigation Bar
        navHomeContainer = findViewById(R.id.nav_home_container)
        navManualOrderContainer = findViewById(R.id.nav_manual_order_container)
        navReviewContainer = findViewById(R.id.nav_review_container)
        navOrderListContainer = findViewById(R.id.nav_order_list_container)
        navTopicContainer = findViewById(R.id.nav_topic_container)
    }

    private fun setupRecyclerView() {
        // Inisialisasi adapter dengan listener untuk aksi Delete
        topicAdapter = TopicAdapter(topics) { topic ->
            showDeleteConfirmation(topic)
        }
        recyclerViewTopics.layoutManager = LinearLayoutManager(this)
        recyclerViewTopics.adapter = topicAdapter
    }

    private fun setupListeners() {
        // Listener FAB untuk menambah topik
        fabAddTopic.setOnClickListener {
            showAddTopicDialog()
        }

        // Listener Ikon Admin/Profile (Menampilkan menu Logout)
        ivAdminIcon.setOnClickListener {
            showProfileMenu()
        }

        // --- Listener Bottom Navigation Bar ---
        navHomeContainer.setOnClickListener { goToActivity(HomeActivity::class.java) }
        navManualOrderContainer.setOnClickListener { goToActivity(OrderManualActivity::class.java) }
        // Perbaiki nama aktivitas jika berbeda dari OrderRecapActivity
        navReviewContainer.setOnClickListener { goToActivity(OrderRecapActivity::class.java) }
        navOrderListContainer.setOnClickListener { goToActivity(OrderListActivity::class.java) }
        // navTopicContainer (halaman ini) tidak perlu listener
    }

    // --- FUNGSI CRUD FIREBASE ---

    /**
     * READ: Mengambil semua topik/kategori dari Firestore secara real-time.
     */
    private fun fetchTopics() {
        // Menggunakan addSnapshotListener untuk update real-time
        db.collection("topics")
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed for topics.", e)
                    Toast.makeText(this, "Gagal memuat topik.", Toast.LENGTH_SHORT).show()
                    showEmptyState("Gagal memuat data topik (Error: ${e.message}).")
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    // Mengambil data dan memastikan ID dokumen dipetakan
                    val newTopics = snapshots.documents.map { document ->
                        // Menggunakan toObject() dari dokumen, lalu secara manual menetapkan ID
                        document.toObject(TopicModel::class.java)?.apply {
                            this.id = document.id
                        } ?: TopicModel(id = document.id, name = "Data Error")
                    }.filterNotNull().toMutableList()

                    topicAdapter.updateData(newTopics)
                    recyclerViewTopics.visibility = View.VISIBLE
                    tvEmptyState.visibility = View.GONE
                } else {
                    topicAdapter.updateData(emptyList())
                    // Tampilkan Empty State
                    showEmptyState("Belum ada topik atau kategori yang ditambahkan.")
                }
            }
    }

    /**
     * CREATE: Menampilkan dialog untuk memasukkan nama topik baru.
     */
    private fun showAddTopicDialog() {
        val input = EditText(this).apply {
            hint = "Nama Topik/Kategori (misal: Kue Ulang Tahun Premium)"
        }

        android.app.AlertDialog.Builder(this)
            .setTitle("Tambah Topik Baru")
            .setView(input)
            .setPositiveButton("Simpan") { _, _ ->
                val topicName = input.text.toString().trim()
                if (topicName.isNotEmpty()) {
                    createTopic(topicName)
                } else {
                    Toast.makeText(this, "Nama topik tidak boleh kosong.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    /**
     * CREATE: Menyimpan topik baru ke Firestore.
     */
    private fun createTopic(name: String) {
        // Membuat objek baru, biarkan ID dokumen dan ServerTimestamp diisi oleh Firestore/Model
        val newTopic = TopicModel(name = name)

        db.collection("topics")
            // Menggunakan set() tanpa ID untuk membiarkan Firestore membuat ID
            .add(newTopic)
            .addOnSuccessListener {
                Toast.makeText(this, "Topik '$name' berhasil ditambahkan.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding topic", e)
                Toast.makeText(this, "Gagal menambahkan topik: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * DELETE: Menampilkan konfirmasi sebelum menghapus topik.
     */
    private fun showDeleteConfirmation(topic: TopicModel) {
        android.app.AlertDialog.Builder(this)
            .setTitle("Hapus Topik")
            .setMessage("Anda yakin ingin menghapus topik '${topic.name}'? Aksi ini tidak dapat dibatalkan.")
            .setPositiveButton("Hapus") { _, _ ->
                // Pastikan topic.id tidak kosong
                if (topic.id.isNotEmpty()) {
                    deleteTopic(topic.id)
                } else {
                    Toast.makeText(this, "Error: ID topik tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    /**
     * DELETE: Menghapus topik dari Firestore.
     */
    private fun deleteTopic(topicId: String) {
        db.collection("topics").document(topicId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Topik berhasil dihapus.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting topic", e)
                Toast.makeText(this, "Gagal menghapus topik: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Fungsi untuk menampilkan pesan kosong (Empty State) atau error.
     */
    private fun showEmptyState(message: String) {
        recyclerViewTopics.visibility = View.GONE
        tvEmptyState.text = message
        tvEmptyState.visibility = View.VISIBLE
    }

    // --- FUNGSI NAVIGASI DAN UTILITY ---

    // Fungsi yang baru ditambahkan/diperbaiki untuk menangani menu profil (logout)
    private fun showProfileMenu() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Opsi Akun")
            .setMessage("Pilih tindakan:")
            .setItems(arrayOf("Logout")) { _, which ->
                when (which) {
                    0 -> { // Logout
                        android.app.AlertDialog.Builder(this)
                            .setTitle("Konfirmasi Logout")
                            .setMessage("Apakah Anda yakin ingin keluar dari akun?")
                            .setPositiveButton("Ya, Logout") { _, _ ->
                                auth.signOut()
                                goToLogin()
                            }
                            .setNegativeButton("Batal", null)
                            .show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun goToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        // Menghapus semua aktivitas sebelumnya dari stack
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}