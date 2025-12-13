// File: OrderManualActivity.kt (Sudah Diperbaiki)
package com.mobile.amigomobile

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View // PENTING: Import View untuk mengatur VISIBLE/GONE
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout // PENTING: Import ConstraintLayout
import android.widget.DatePicker // Diperlukan untuk DatePickerDialog listener (walaupun tidak digunakan secara eksplisit, lebih aman ada)

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date

// >>> HAPUS: Definisi OrderModel telah dihapus karena sudah ada di OrderModel.kt
// data class OrderModel(...)

// PENTING: Anda harus memastikan file OrderModel.kt berisi definisi:
/*
data class OrderModel(
    @get:Exclude var id: String = "",
    val customerName: String = "",
    // ... properti lain menggunakan default non-null
    val pickupDate: Date? = null,
    @ServerTimestamp val orderDate: Date? = null
)
*/


class OrderManualActivity : AppCompatActivity() {
// ... (Properti kelas tidak berubah)

    private val TAG = "OrderManualActivity"
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // Komponen Form
    private lateinit var inputCustomerName: EditText
    private lateinit var inputCustomerContact: EditText
    private lateinit var inputAddress: EditText
    private lateinit var inputProductName: EditText
    private lateinit var inputDiameter: EditText
    private lateinit var inputPickupDate: EditText
    private lateinit var inputPrice: EditText
    private lateinit var buttonOrder: Button
    private lateinit var ivProfileIcon: ImageView

    // **KOMPONEN POP-UP BARU**
    private lateinit var popupLayout: ConstraintLayout // Kontainer Pop-up utama (untuk show/hide)
    private lateinit var popupCloseButton: Button // Tombol Close di Pop-up

    // Bottom Navigation Components
    private lateinit var navHomeContainer: LinearLayout
    private lateinit var navManualOrderContainer: LinearLayout
    private lateinit var navReviewContainer: LinearLayout
    private lateinit var navOrderListContainer: LinearLayout
    private lateinit var navTopicContainer: LinearLayout

    // State untuk menyimpan tanggal yang dipilih oleh DatePicker
    private var selectedPickupDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_manual)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) { goToLogin(); return }

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        // ... (Fungsi tidak berubah)
        inputCustomerName = findViewById(R.id.input_customer_name)
        inputCustomerContact = findViewById(R.id.input_customer_contact)
        inputAddress = findViewById(R.id.input_address)
        inputProductName = findViewById(R.id.input_product_name)
        inputDiameter = findViewById(R.id.input_diameter)
        inputPickupDate = findViewById(R.id.input_pickup_date)
        inputPrice = findViewById(R.id.input_price)
        buttonOrder = findViewById(R.id.button_order)
        ivProfileIcon = findViewById(R.id.profile_icon)

        popupLayout = findViewById(R.id.popup_order_successful)
        popupCloseButton = findViewById(R.id.popup_button_close)

        navHomeContainer = findViewById(R.id.nav_home_container)
        navManualOrderContainer = findViewById(R.id.nav_manual_order_container)
        navReviewContainer = findViewById(R.id.nav_review_container)
        navOrderListContainer = findViewById(R.id.nav_order_list_container)
        navTopicContainer = findViewById(R.id.nav_topic_container)
    }

    private fun setupListeners() {
        // 1. Date Picker Listener
        inputPickupDate.setOnClickListener {
            showDatePicker()
        }
        inputPickupDate.keyListener = null

        // 2. Order Button Listener (Memanggil validasi dan submit)
        buttonOrder.setOnClickListener {
            validateAndSubmitOrder()
        }

        // 3. Ikon profile di header
        ivProfileIcon.setOnClickListener {
            goToActivity(ProfileActivity::class.java)
        }

        // 4. Pop-up Close Button Listener
        popupCloseButton.setOnClickListener {
            popupLayout.visibility = View.GONE // Sembunyikan Pop-up
        }

        // 5. Navigation Listeners
        navHomeContainer.setOnClickListener { goToActivity(HomeActivity::class.java) }
        navReviewContainer.setOnClickListener { goToActivity(OrderRecapActivity::class.java) }
        navOrderListContainer.setOnClickListener { goToActivity(OrderListActivity::class.java) }
        navTopicContainer.setOnClickListener { goToActivity(TopicActivity::class.java) }
    }


    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        if (selectedPickupDate != null) {
            calendar.time = selectedPickupDate!!
        }

        val datePicker = DatePickerDialog(
            this,
            // Perbaikan: Menambahkan tipe data eksplisit pada parameter DatePickerDialog
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                selectedPickupDate = selectedCalendar.time
                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
                // Perbaikan: Menggunakan safe call `?.` atau non-null asserted `!!` setelah memastikan tidak null
                inputPickupDate.setText(selectedPickupDate?.let { dateFormat.format(it) } ?: "")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        datePicker.show()
    }

    private fun validateAndSubmitOrder() {
        val name = inputCustomerName.text.toString().trim()
        val contact = inputCustomerContact.text.toString().trim()
        val address = inputAddress.text.toString().trim()
        val product = inputProductName.text.toString().trim()
        val diameterStr = inputDiameter.text.toString().trim()
        val priceStr = inputPrice.text.toString().trim()

        if (name.isEmpty() || contact.isEmpty() || product.isEmpty() || priceStr.isEmpty() || selectedPickupDate == null) {
            Toast.makeText(this, "Nama, Kontak, Produk, Tanggal Pickup, dan Harga wajib diisi.", Toast.LENGTH_LONG).show()
            return
        }

        val totalPrice: Long = priceStr.toLongOrNull() ?: run {
            Toast.makeText(this, "Harga harus berupa angka yang valid.", Toast.LENGTH_SHORT).show()
            return
        }
        if (totalPrice <= 0) {
            Toast.makeText(this, "Harga harus lebih besar dari nol.", Toast.LENGTH_SHORT).show()
            return
        }

        // Perbaikan: Menggunakan konstruktor OrderModel yang non-nullable String/Long (sesuai OrderModel.kt)
        val newOrder = OrderModel(
            customerName = name,
            customerContact = contact,
            address = address,
            productName = product,
            diameter = diameterStr,
            quantity = 1,
            unitPrice = totalPrice,
            totalPrice = totalPrice,
            notes = "",
            status = "Pending",
            pickupDate = selectedPickupDate,
            orderDate = null
        )

        // 4. Simpan ke Firestore
        db.collection("orders")
            .add(newOrder)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Order added with ID: ${documentReference.id}")
                Toast.makeText(this, "✅ Order berhasil disimpan!", Toast.LENGTH_LONG).show()

                showOrderSuccessfulLayout()

                clearForm()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding order", e)
                Toast.makeText(this, "❌ Gagal menambahkan pesanan: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Menampilkan ConstraintLayout Pop-up 'Order Successful'.
     */
    private fun showOrderSuccessfulLayout() {
        popupLayout.visibility = View.VISIBLE
    }

    private fun clearForm() {
        // ... (Fungsi tidak berubah)
        inputCustomerName.setText("")
        inputCustomerContact.setText("")
        inputAddress.setText("")
        inputProductName.setText("")
        inputDiameter.setText("")
        inputPickupDate.setText("")
        inputPrice.setText("")
        selectedPickupDate = null
        inputCustomerName.requestFocus()
    }

    // --- FUNGSI NAVIGASI DAN UTILITY ---

    private fun goToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
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