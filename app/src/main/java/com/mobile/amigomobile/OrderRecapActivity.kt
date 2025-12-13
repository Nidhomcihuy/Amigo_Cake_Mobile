package com.mobile.amigomobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import android.app.DatePickerDialog
import android.widget.DatePicker

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

// Import Binding yang akan digunakan
import com.mobile.amigomobile.databinding.ActivityOrderRecapBinding // Pastikan nama ini sesuai XML Anda

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter

import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class OrderRecapActivity : AppCompatActivity() {

    private val TAG = "OrderRecapActivity"
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // 1. Deklarasi View Binding
    private lateinit var binding: ActivityOrderRecapBinding

    private var currentRecapDate: Date = Date()
    private var currentRecapPeriod: String = "month" // day, month, year

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Inisialisasi Binding dan Set Content View
        binding = ActivityOrderRecapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (auth.currentUser == null) {
            goToLogin()
            return
        }

        // initializeViews() <-- Dihapus karena menggunakan binding
        setupListeners()
        configureChartBasicStyling()

        // currentRecapDate = Date() <-- Sudah diinisialisasi di atas
        loadRecapData(currentRecapDate)
    }

    // initializeViews() dihapus. Akses view langsung melalui 'binding.viewId'

    private fun setupListeners() {
        // Menggunakan binding.imageView7 (Asumsi ID dari ivProfileIcon)
        binding.imageView7.setOnClickListener {
            goToActivity(ProfileActivity::class.java)
        }

        // Menggunakan binding.buttonMonthPicker
        binding.buttonMonthPicker.setOnClickListener {
            if (currentRecapPeriod == "month" || currentRecapPeriod == "year") {
                showMonthYearPickerDialog()
            } else {
                showPeriodPopupMenu(it)
            }
        }

        // Navigasi Bawah menggunakan binding
        binding.navHomeContainer.setOnClickListener { goToActivity(HomeActivity::class.java) }
        binding.navManualOrderContainer.setOnClickListener { goToActivity(OrderManualActivity::class.java) }
        binding.navOrderListContainer.setOnClickListener { goToActivity(OrderListActivity::class.java) }
        binding.navTopicContainer.setOnClickListener { goToActivity(TopicActivity::class.java) }
    }

    private fun showPeriodPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menu.add(0, 1, 0, "Hari ini")
        popup.menu.add(0, 2, 0, "Bulan ini")
        popup.menu.add(0, 3, 0, "Tahun ini")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    currentRecapPeriod = "day"
                    loadRecapData(Date())
                    true
                }
                2 -> {
                    currentRecapPeriod = "month"
                    loadRecapData(Date())
                    true
                }
                3 -> {
                    currentRecapPeriod = "year"
                    loadRecapData(Date())
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showMonthYearPickerDialog() {
        val calendar = Calendar.getInstance().apply { time = currentRecapDate }

        val picker = DatePickerDialog(
            this,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, _: Int ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(selectedYear, selectedMonth, 1)

                if (currentRecapPeriod == "day" && newCalendar.time.after(Date())) {
                    Toast.makeText(this, "Tidak dapat memilih tanggal di masa depan untuk rekap hari ini.", Toast.LENGTH_SHORT).show()
                } else {
                    loadRecapData(newCalendar.time)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        try {
            val dayPickerId = resources.getIdentifier("day", "id", "android")
            if (dayPickerId != 0 && (currentRecapPeriod == "month" || currentRecapPeriod == "year")) {
                picker.findViewById<View>(dayPickerId)?.visibility = View.GONE
            }
        } catch (e: Exception) { Log.w(TAG, "Gagal menyembunyikan day picker: ${e.message}") }

        picker.show()
    }

    private fun loadRecapData(selectedDate: Date) {
        currentRecapDate = selectedDate

        val calendar = Calendar.getInstance().apply { time = selectedDate }
        val startOfPeriod: Date
        val endOfPeriod: Date

        // 3. VARIABEL startOfPeriod dan endOfPeriod TIDAK PERLU DIINISIALISASI DI LUAR 'when'
        // Karena Kotlin menjamin variabel diinisialisasi di semua cabang 'when'
        when (currentRecapPeriod) {
            "day" -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0); calendar.set(Calendar.MINUTE, 0); calendar.set(Calendar.SECOND, 0); calendar.set(Calendar.MILLISECOND, 0)
                startOfPeriod = calendar.time
                calendar.add(Calendar.DAY_OF_YEAR, 1); calendar.add(Calendar.MILLISECOND, -1)
                endOfPeriod = calendar.time
                // Menggunakan binding.buttonMonthPicker
                binding.buttonMonthPicker.text = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(selectedDate)
            }
            "year" -> {
                calendar.set(Calendar.MONTH, Calendar.JANUARY); calendar.set(Calendar.DAY_OF_MONTH, 1); calendar.set(Calendar.HOUR_OF_DAY, 0); calendar.set(Calendar.MINUTE, 0); calendar.set(Calendar.SECOND, 0); calendar.set(Calendar.MILLISECOND, 0)
                startOfPeriod = calendar.time
                calendar.add(Calendar.YEAR, 1); calendar.add(Calendar.MILLISECOND, -1)
                endOfPeriod = calendar.time
                // Menggunakan binding.buttonMonthPicker
                binding.buttonMonthPicker.text = SimpleDateFormat("yyyy", Locale("id", "ID")).format(selectedDate)
            }
            "month", else -> { // Default ke bulan
            calendar.set(Calendar.DAY_OF_MONTH, 1); calendar.set(Calendar.HOUR_OF_DAY, 0); calendar.set(Calendar.MINUTE, 0); calendar.set(Calendar.SECOND, 0); calendar.set(Calendar.MILLISECOND, 0)
            startOfPeriod = calendar.time
            calendar.add(Calendar.MONTH, 1); calendar.add(Calendar.MILLISECOND, -1)
            endOfPeriod = calendar.time
            // Menggunakan binding.buttonMonthPicker
            binding.buttonMonthPicker.text = SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).format(selectedDate)
        }
        }

        // 2. Ambil data order dari Firestore
        db.collection("orders")
            .whereGreaterThanOrEqualTo("orderDate", startOfPeriod)
            .whereLessThanOrEqualTo("orderDate", endOfPeriod)
            .orderBy("orderDate", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                var totalOrders = 0
                var totalRevenue = 0L

                val dailyRevenueMap = mutableMapOf<Int, Long>()

                for (document in documents) {
                    val amount = document.getLong("totalPrice") ?: 0L
                    val timestamp = document.getDate("orderDate")

                    if (timestamp != null) {
                        val tempCalendar = Calendar.getInstance().apply { time = timestamp }

                        val timeUnit = when (currentRecapPeriod) {
                            "year" -> tempCalendar.get(Calendar.MONTH) + 1
                            else -> tempCalendar.get(Calendar.DAY_OF_MONTH)
                        }

                        dailyRevenueMap[timeUnit] = (dailyRevenueMap[timeUnit] ?: 0L) + amount
                    }

                    totalOrders++
                    totalRevenue += amount
                }

                updateRecapUI(totalOrders, totalRevenue)
                setupChartData(dailyRevenueMap)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error loading recap data", exception)
                Toast.makeText(this, "Gagal memuat data rekap: ${exception.message}", Toast.LENGTH_LONG).show()
                updateRecapUI(0, 0L)
                setupChartData(emptyMap())
            }
    }

    private fun updateRecapUI(orders: Int, revenue: Long) {
        val formattedRevenue = formatRupiah(revenue.toDouble())

        // Menggunakan binding.tvTotalOrders dan binding.tvTotalRevenue
        binding.tvTotalOrders.text = "Total Order : $orders Order"
        binding.tvTotalRevenue.text = "Total Pendapatan : $formattedRevenue"
    }

    private fun formatRupiah(number: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0
        return format.format(number).replace("Rp", "Rp ").trim()
    }


    private fun configureChartBasicStyling() {
        // Menggunakan binding.lineChart (Asumsi ID chartView)
        binding.lineChart.xAxis.setDrawGridLines(false)
        binding.lineChart.xAxis.setDrawAxisLine(true)
        binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        val axisColor = android.graphics.Color.parseColor("#3A3135")
        binding.lineChart.xAxis.textColor = axisColor
        binding.lineChart.axisLeft.textColor = axisColor

        binding.lineChart.axisLeft.setDrawGridLines(true)
        binding.lineChart.axisLeft.gridColor = android.graphics.Color.parseColor("#6E6266")
        binding.lineChart.axisRight.isEnabled = false
        binding.lineChart.axisLeft.axisMinimum = 0f

        binding.lineChart.description.isEnabled = false
        binding.lineChart.setTouchEnabled(true)
        binding.lineChart.isDragEnabled = true
        binding.lineChart.setScaleEnabled(true)
        binding.lineChart.setPinchZoom(true)

        binding.lineChart.legend.isEnabled = true
        binding.lineChart.legend.textSize = 12f
        binding.lineChart.legend.textColor = axisColor
    }

    private fun setupChartData(dailyRevenueMap: Map<Int, Long>) {
        val lineEntries = ArrayList<Entry>()
        val calendar = Calendar.getInstance().apply { time = currentRecapDate }
        val maxUnits = when (currentRecapPeriod) {
            "year" -> 12
            else -> calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        val xAxisLabelFormatter = when (currentRecapPeriod) {
            "year" -> MonthValueFormatter(Locale("id", "ID"))
            else -> DayValueFormatter()
        }

        for (unit in 1..maxUnits) {
            val revenue = dailyRevenueMap[unit]?.toFloat() ?: 0f
            lineEntries.add(Entry(unit.toFloat(), revenue))
        }

        if (lineEntries.isEmpty() || lineEntries.all { it.y == 0f }) {
            binding.lineChart.clear()
            binding.lineChart.setNoDataText("Tidak ada data transaksi di periode ini.")
            binding.lineChart.setNoDataTextColor(android.graphics.Color.parseColor("#3A3135"))
            binding.lineChart.invalidate()
            return
        }

        val primaryColor = android.graphics.Color.parseColor("#982B15")
        val accentColor = android.graphics.Color.parseColor("#3A3135")

        val dataSet = LineDataSet(lineEntries, "Pendapatan (${if (currentRecapPeriod == "year") "Bulanan" else "Harian"})")
        dataSet.setDrawValues(false)
        dataSet.lineWidth = 2f
        dataSet.color = primaryColor
        dataSet.setCircleColor(accentColor)
        dataSet.setCircleRadius(4f)
        dataSet.setDrawFilled(true)
        dataSet.fillColor = primaryColor
        dataSet.fillAlpha = 50

        val lineData = LineData(dataSet)
        binding.lineChart.data = lineData

        binding.lineChart.xAxis.apply {
            valueFormatter = xAxisLabelFormatter
            granularity = 1f
            axisMinimum = 1f
            axisMaximum = maxUnits.toFloat()
        }

        binding.lineChart.animateX(800)
        binding.lineChart.invalidate()
    }

    class DayValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString()
        }
    }

    class MonthValueFormatter(private val locale: Locale) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val monthIndex = value.toInt() - 1
            if (monthIndex < 0 || monthIndex >= 12) return ""

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, monthIndex)
            val dateFormat = SimpleDateFormat("MMM", locale)
            return dateFormat.format(calendar.time)
        }
    }

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