package com.mobile.amigomobile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.amigomobile.models.TopicModel // ASUMSI: TopicModel ada di sini

// 1. PERBAIKAN: GANTI NAMA KELAS DARI OrderAdapter MENJADI TopicAdapter
class TopicAdapter(
    private val context: Context,
    private var topics: List<TopicModel>,
    // Asumsi: TopicAdapter mungkin juga memiliki fungsi delete atau interaksi klik
    private val onDeleteClicked: (String) -> Unit = {}
) : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    // 2. PERBAIKAN: GANTI NAMA VIEWHOLDER DARI OrderViewHolder MENJADI TopicViewHolder
    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ID Widget (ASUMSI ID WIDGET SAMA DENGAN LOG ERROR, TAPI MUNGKIN PERLU DISESUAIKAN DENGAN item_topic_card.xml)
        val tvOrderName: TextView = itemView.findViewById(R.id.tv_order_name) // Sebelumnya Unresolved: tv_order_name
        val tvOrderAmount: TextView = itemView.findViewById(R.id.tv_order_amount) // Sebelumnya Unresolved: tv_order_amount
        val tvOrderQuantity: TextView = itemView.findViewById(R.id.tv_order_quantity) // Sebelumnya Unresolved: tv_order_quantity
        val tvPickupDate: TextView = itemView.findViewById(R.id.tv_pickup_date) // Sebelumnya Unresolved: tv_pickup_date
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status) // Sebelumnya Unresolved: tv_status
        val ivDeleteOrder: ImageView = itemView.findViewById(R.id.iv_delete_order) // Sebelumnya Unresolved: iv_delete_order

        init {
            ivDeleteOrder.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val topicId = topics[position].id // ASUMSI TopicModel memiliki ID
                    if (topicId.isNotEmpty()) {
                        showDeleteConfirmationDialog(topicId, position)
                    }
                }
            }
        }
    }

    // 3. PERBAIKAN: Menggunakan layout item yang berbeda untuk Topik (ASUMSI: item_topic_card)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        // Ganti R.layout.item_order_card menjadi R.layout.item_topic_card (ATAU SESUAIKAN DENGAN XML ANDA)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_topic, parent, false)
        return TopicViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val topic = topics[position] // Mengambil TopicModel

        // ASUMSI: TopicModel memiliki properti yang sesuai
        holder.tvOrderName.text = topic.topicName // Contoh: topicName
        holder.tvOrderAmount.text = topic.totalOrders.toString() // Contoh: totalOrders
        holder.tvOrderQuantity.text = topic.status // Contoh: status
        holder.tvPickupDate.text = topic.lastUpdate.toString() // Contoh: lastUpdate

        // Atur Status dan warna/drawable
        setStatusStyle(holder.tvStatus, topic.status)
    }

    override fun getItemCount(): Int = topics.size

    fun updateData(newTopics: List<TopicModel>) {
        this.topics = newTopics
        notifyDataSetChanged()
    }

    private fun setStatusStyle(textView: TextView, status: String?) {
        // 4. PERBAIKAN: Menangani Unresolved reference: bg_status_canceled dan bg_status_default
        val drawableRes = when (status) {
            "Canceled" -> R.drawable.bg_status_canceled // ASUMSI resource ini ada
            "Default" -> R.drawable.bg_status_default // ASUMSI resource ini ada
            else -> R.drawable.bg_status_default // Default fallback
        }

        // Menggunakan ContextCompat untuk mendapatkan Drawable
        textView.background = ContextCompat.getDrawable(context, drawableRes)
    }

    private fun showDeleteConfirmationDialog(topicId: String, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Konfirmasi Hapus Topik")
            .setMessage("Anda yakin ingin menghapus topik ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                onDeleteClicked(topicId)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}