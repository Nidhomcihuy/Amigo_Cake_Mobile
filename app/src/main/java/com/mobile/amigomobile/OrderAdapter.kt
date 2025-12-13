package com.mobile.amigomobile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast // DITAMBAHKAN: Perbaikan Kritis
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(
    private val context: Context,
    private val orderList: List<OrderModel>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    // Helper untuk format Rupiah
    private val rupiahFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
        maximumFractionDigits = 0 // Tidak menampilkan desimal
    }

    // Helper untuk format Tanggal
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    // Class untuk menahan View dari item_order_card.xml
    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.tv_item_name)
        val customerName: TextView = itemView.findViewById(R.id.tv_customer_name)
        // Tambahan: Harga dan Kuantitas (Asumsi ID di item_order_card.xml)
        val amount: TextView = itemView.findViewById(R.id.tv_order_amount)
        val quantity: TextView = itemView.findViewById(R.id.tv_order_quantity)

        val status: TextView = itemView.findViewById(R.id.tv_order_status)
        val date: TextView = itemView.findViewById(R.id.tv_order_date)

        init {
            itemView.setOnClickListener {
                // TODO: Ganti ini dengan Intent yang membuka Order Detail Activity
                val orderId = orderList[adapterPosition].id
                // TOAST BERHASIL DIKOMPILASI KARENA IMPORT SUDAH DITAMBAHKAN
                Toast.makeText(context, "Membuka detail order ID: $orderId", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Dipanggil saat RecyclerView membutuhkan ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        // Inflate layout item_order_card.xml
        // Pastikan Anda memiliki layout bernama item_order_card.xml
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_card, parent, false)
        return OrderViewHolder(view)
    }

    // Dipanggil untuk menampilkan data pada posisi tertentu
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = orderList[position]

        holder.itemName.text = currentOrder.itemName
        holder.customerName.text = currentOrder.customerName
        holder.status.text = currentOrder.status.uppercase(Locale.getDefault())

        // --- TAMBAHAN: Binding Data Numerik ---

        // Harga (Amount)
        // Menggunakan formatRupiah() untuk tampilan yang rapi
        val formattedAmount = rupiahFormatter.format(currentOrder.amount).replace("Rp", "Rp ").trim()
        holder.amount.text = formattedAmount

        // Kuantitas
        holder.quantity.text = "x${currentOrder.quantity}"

        // --- Akhir Tambahan ---

        // Format Tanggal
        holder.date.text = if (currentOrder.deadlineDate != null) {
            dateFormat.format(currentOrder.deadlineDate)
        } else {
            "No Deadline"
        }

        // Kustomisasi Warna Status
        when (currentOrder.status) {
            "completed" -> {
                holder.status.setBackgroundResource(R.drawable.bg_status_completed)
                // Menggunakan android.R.color.white
                holder.status.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            }
            "pending" -> {
                holder.status.setBackgroundResource(R.drawable.bg_status_pending)
                holder.status.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            }
            "processing" -> {
                // Perlu file drawable: bg_status_processing.xml
                holder.status.setBackgroundResource(R.drawable.bg_status_processing)
                holder.status.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            }
            "cancelled" -> {
                // Perlu file drawable: bg_status_canceled.xml
                holder.status.setBackgroundResource(R.drawable.bg_status_canceled)
                holder.status.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            }
            else -> {
                // Default style jika status tidak terdefinisi
                holder.status.setBackgroundResource(R.drawable.bg_status_default)
                holder.status.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            }
        }
    }

    override fun getItemCount(): Int = orderList.size
}