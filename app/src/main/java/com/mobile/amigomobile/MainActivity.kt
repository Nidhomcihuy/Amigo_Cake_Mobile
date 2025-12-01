import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val chart = findViewById<LineChart>(R.id.lineChart)
        setupLineChart(chart)
    }

    private fun setupLineChart(chart: LineChart) {
        // --- 1. DATA ENTRY DARI GAMBAR ---
        // Garis Kuning (Line 1)
        val entries1 = arrayListOf(
            Entry(0f, 500f), // Asumsi data dari gambar (0, 500)
            Entry(1f, 800f),
            Entry(2f, 500f),
            Entry(3f, 550f),
            Entry(4f, 150f)
        )
        // Garis Merah (Line 2)
        val entries2 = arrayListOf(
            Entry(0f, 500f),
            Entry(1f, 550f),
            Entry(2f, 250f),
            Entry(3f, 550f),
            Entry(4f, 350f)
        )

        // --- 2. SETUP DATASET GARIS KUNING (Line 1) ---
        val dataSet1 = LineDataSet(entries1, "Data Set 1").apply {
            color = Color.rgb(200, 255, 0) // Warna Kuning/Hijau
            lineWidth = 2f
            setDrawCircles(false) // Tidak menggambar lingkaran pada titik data
            setDrawValues(false) // Tidak menampilkan nilai di atas garis
            mode = LineDataSet.Mode.LINEAR
        }

        // --- 3. SETUP DATASET GARIS MERAH (Line 2) ---
        val dataSet2 = LineDataSet(entries2, "Data Set 2").apply {
            color = Color.rgb(180, 80, 50) // Warna Merah/Cokelat
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.LINEAR
        }

        // --- 4. GABUNGKAN DATASET DAN SETUP CHART ---
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(dataSet1)
        dataSets.add(dataSet2)

        val lineData = LineData(dataSets)
        chart.data = lineData

        // --- 5. STYLING CHART ---
        chart.description.isEnabled = false // Hapus deskripsi di pojok kanan bawah
        chart.legend.isEnabled = false // Hapus keterangan legend

        // Axis Y Kiri
        chart.axisLeft.apply {
            axisMinimum = 0f
            axisMaximum = 1000f // Sesuaikan dengan skala vertikal (0-1000)
            setDrawGridLines(true)
            gridColor = Color.LTGRAY
        }

        // Axis X Bawah
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            axisMinimum = 0f
            axisMaximum = 4f // Sesuaikan dengan skala horizontal (0-4)
            setDrawGridLines(false) // Hapus garis grid vertikal
        }

        // Axis Y Kanan (Dihapus/Disable)
        chart.axisRight.isEnabled = false

        // Refresh Chart
        chart.invalidate()
    }
}