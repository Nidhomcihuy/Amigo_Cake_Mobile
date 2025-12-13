package com.mobile.amigomobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageView
import android.widget.LinearLayout // Penting: Import LinearLayout
import java.util.*
import java.text.SimpleDateFormat
import java.util.Calendar

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat

// PENTING: Pastikan Activity ini ada: ManualOrderActivity, OrderRecapActivity, OrderListActivity, TopicActivity, ProfileActivity

class HomeActivity : AppCompatActivity() {

    // --- FIREBASE INITIATION ---
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private lateinit var db: FirebaseFirestore

    // UI components (Dasar & Statistik)
    private lateinit var tvGreeting: TextView
    private lateinit var btnManualOrder: Button
    private lateinit var tvHeaderReview: TextView
    private lateinit var tvOrderListHeader: TextView
    private lateinit var tvTotalOrderCount: TextView
    private lateinit var tvRevenueTodayValue: TextView
    private lateinit var tvRevenueMonthValue: TextView
    private lateinit var tvOrderListCount: TextView
    private lateinit var tvDeadlineItemName: TextView
    private lateinit var tvDeadlineDate: TextView
    private lateinit var ivProfileSettings: ImageView

    // UI components (Bottom Bar - MENGGUNAKAN ID CONTAINER BARU)
    private lateinit var navHomeContainer: LinearLayout
    private lateinit var navManualOrderContainer: LinearLayout
    private lateinit var navReviewContainer: LinearLayout
    private lateinit var navOrderListContainer: LinearLayout
    private lateinit var navTopicContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 1. Cek Keamanan
        if (auth.currentUser == null) {
            goToLogin()
            return
        }

        // INISIALISASI FIRESTORE
        db = FirebaseFirestore.getInstance()

        initializeViews()
        setupUserData()
        fetchDashboardData()
        setupListeners()
        // Ikon Home sudah aktif (merah) sesuai desain XML
    }

    private fun initializeViews() {
        // Bagian Header & Tombol Utama
        tvGreeting = findViewById(R.id.tv_greeting)
        ivProfileSettings = findViewById(R.id.iv_profile_settings)
        btnManualOrder = findViewById(R.id.btn_manual_order)
        tvHeaderReview = findViewById(R.id.tv_header_review)
        tvOrderListHeader = findViewById(R.id.tv_order_list_header)

        // Bagian Statistik
        tvTotalOrderCount = findViewById(R.id.tv_total_order_count)
        tvRevenueTodayValue = findViewById(R.id.tv_revenue_today_value)
        tvRevenueMonthValue = findViewById(R.id.tv_revenue_month_value)
        tvOrderListCount = findViewById(R.id.tv_order_list_count)
        tvDeadlineItemName = findViewById(R.id.tv_deadline_item_name)
        tvDeadlineDate = findViewById(R.id.tv_deadline_date)

        // Bagian Menu Bar - Menggunakan ID CONTAINER BARU DARI XML
        navHomeContainer = findViewById(R.id.nav_home_container)
        navManualOrderContainer = findViewById(R.id.nav_manual_order_container)
        navReviewContainer = findViewById(R.id.nav_review_container)
        navOrderListContainer = findViewById(R.id.nav_order_list_container)
        navTopicContainer = findViewById(R.id.nav_topic_container)
    }

    private fun setupUserData() {
        auth.currentUser?.let { user ->
            // Logika untuk menampilkan nama admin
            val userName = user.email?.split('@')?.firstOrNull()?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            } ?: "Admin"
            tvGreeting.text = "Hi, $userName"
        }
    }

    /**
     * Fungsi utama untuk mengambil dan menampilkan data dashboard dari Firestore.
     */
    private fun fetchDashboardData() {
        fetchTotalOrdersAndActiveCount()
        fetchRevenueData()
        fetchNextDeadline()
    }

    /**
     * Mengambil jumlah total order yang selesai dan jumlah order yang aktif.
     */
    private fun fetchTotalOrdersAndActiveCount() {
        // Logika Firestore untuk Total Order Selesai dan Order Aktif
        db.collection("orders")
            .whereEqualTo("status", "completed")
            .get()
            .addOnSuccessListener { querySnapshot ->
                tvTotalOrderCount.text = querySnapshot.size().toString()
            }
            .addOnFailureListener {
                tvTotalOrderCount.text = "Error"
            }

        db.collection("orders")
            .whereIn("status", listOf("pending", "processing"))
            .get()
            .addOnSuccessListener { querySnapshot ->
                tvOrderListCount.text = querySnapshot.size().toString()
            }
    }

    /**
     * Mengambil total pendapatan hari ini dan bulan ini.
     */
    private fun fetchRevenueData() {
        val calendar = Calendar.getInstance()

        // --- Filter BULAN INI ---
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.time

        db.collection("orders")
            .whereEqualTo("status", "completed")
            .whereGreaterThanOrEqualTo("createdAt", startOfMonth)
            .get()
            .addOnSuccessListener { querySnapshot ->
                var totalMonthlyRevenue = 0L
                for (doc in querySnapshot.documents) {
                    val amount = doc.getDouble("amount")?.toLong() ?: doc.getLong("amount") ?: 0L
                    totalMonthlyRevenue += amount
                }
                tvRevenueMonthValue.text = formatRupiah(totalMonthlyRevenue)
            }

        // --- Filter HARI INI ---
        calendar.time = Date() // Reset ke waktu saat ini
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time

        db.collection("orders")
            .whereEqualTo("status", "completed")
            .whereGreaterThanOrEqualTo("createdAt", startOfDay)
            .get()
            .addOnSuccessListener { querySnapshot ->
                var totalDailyRevenue = 0L
                for (doc in querySnapshot.documents) {
                    val amount = doc.getDouble("amount")?.toLong() ?: doc.getLong("amount") ?: 0L
                    totalDailyRevenue += amount
                }
                tvRevenueTodayValue.text = formatRupiah(totalDailyRevenue)
            }
    }

    /**
     * Mengambil pesanan aktif dengan deadline terdekat.
     */
    private fun fetchNextDeadline() {
        db.collection("orders")
            .whereIn("status", listOf("pending", "processing"))
            .orderBy("deadlineDate")
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    tvDeadlineItemName.text = "Tidak ada pesanan aktif"
                    tvDeadlineDate.text = ""
                    return@addOnSuccessListener
                }

                val doc = querySnapshot.documents[0]
                val item = doc.getString("itemName") ?: "Pesanan"
                val customer = doc.getString("customerName") ?: "Anonim"
                val deadlineTimestamp = doc.getTimestamp("deadlineDate")

                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

                tvDeadlineItemName.text = "$item ($customer)"
                tvDeadlineDate.text = if (deadlineTimestamp != null) dateFormat.format(deadlineTimestamp.toDate()) else "Tanggal tidak tersedia"
            }
            .addOnFailureListener {
                tvDeadlineItemName.text = "Gagal memuat deadline"
            }
    }

    private fun setupListeners() {
        // --- LISTENERS TOMBOL DI DASHBOARD ---

        // Tombol Profile/Settings (Arahkan ke ProfileActivity)
        ivProfileSettings.setOnClickListener {
            goToActivity(ProfileActivity::class.java)
        }

        // Tombol Manual Order
        btnManualOrder.setOnClickListener {
            goToActivity(OrderManualActivity::class.java)
        }

        // Order Review Header
        tvHeaderReview.setOnClickListener {
            goToActivity(OrderRecapActivity::class.java)
        }

        // Order List Header
        tvOrderListHeader.setOnClickListener {
            goToActivity(OrderListActivity::class.java)
        }

        // --- LISTENERS BOTTOM NAVIGATION BAR (MENGGUNAKAN ID BARU) ---

        // 1. Home (Ikon Merah, hanya refresh data)
        navHomeContainer.setOnClickListener {
            Toast.makeText(this, "Dashboard diperbarui", Toast.LENGTH_SHORT).show()
            fetchDashboardData()
        }

        // 2. Manual Order
        navManualOrderContainer.setOnClickListener {
            goToActivity(OrderManualActivity::class.java)
        }

        // 3. Order Review / Recap
        navReviewContainer.setOnClickListener {
            goToActivity(OrderRecapActivity::class.java)
        }

        // 4. Order List
        navOrderListContainer.setOnClickListener {
            goToActivity(OrderListActivity::class.java)
        }

        // 5. Topic/Info
        navTopicContainer.setOnClickListener {
            goToActivity(TopicActivity::class.java)
        }
    }

    /**
     * Fungsi helper untuk memformat angka Long menjadi format Rupiah (IDR).
     */
    private fun formatRupiah(number: Long): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        format.maximumFractionDigits = 0
        return "Rp " + format.format(number).replace("Rp", "").trim()
    }

    /**
     * Fungsi helper untuk navigasi antar Activity menu utama.
     */
    private fun goToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        // Gunakan Flag untuk Activity yang sudah ada di tumpukan
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}