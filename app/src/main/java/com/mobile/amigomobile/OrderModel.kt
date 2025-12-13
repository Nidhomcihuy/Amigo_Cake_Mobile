package com.mobile.amigomobile

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Model data untuk memetakan dokumen 'orders' dari Firebase Firestore.
 */
data class OrderModel(
    // ID Dokumen dari Firestore. Menggunakan @get:Exclude agar ID dokumen
    // dapat diakses di kode Kotlin (misalnya saat Delete) tanpa diunggah kembali ke Firestore.
    @get:Exclude var id: String = "", // Var agar bisa diubah setelah dokumen diambil

    // --- Detail Pelanggan ---
    val customerName: String = "", // Default value non-null
    val customerContact: String = "", // Default value non-null
    val address: String = "", // Default value non-null

    // --- Detail Produk & Harga ---
    val productName: String = "",
    val diameter: String = "",
    val quantity: Int = 1,
    val unitPrice: Long = 0,
    val totalPrice: Long = 0,
    val notes: String = "",

    // --- Detail Waktu & Status ---
    val status: String = "Pending",
    val pickupDate: Date? = null, // Boleh null

    // Waktu dibuat (diisi otomatis oleh Firestore saat dokumen dibuat)
    @ServerTimestamp
    val orderDate: Date? = null // Boleh null
)