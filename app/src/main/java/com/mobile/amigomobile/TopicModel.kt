// File: TopicModel.kt
package com.mobile.amigomobile

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Model data untuk Kategori atau Topik produk.
 */
data class TopicModel(
    // ID Dokumen dari Firestore. Menggunakan @get:Exclude agar ID dokumen
    // dapat diakses di kode Kotlin (misalnya saat Delete) tanpa diunggah kembali ke Firestore.
    @get:Exclude var id: String = "",

    // Nama topik/kategori. Ini adalah field wajib.
    val name: String = "",

    // Deskripsi topik (dibuat nullable, opsional untuk diisi).
    val description: String? = null,

    // Timestamp Waktu Topik Dibuat.
    // @ServerTimestamp memastikan waktu diisi secara akurat oleh server Firestore.
    @ServerTimestamp
    val createdAt: Date? = null
)