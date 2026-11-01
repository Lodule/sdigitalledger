package com.sdigitalledger.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

class WhatsAppHelper(private val context: Context) {
    fun sendWhatsAppMessage(phoneNumber: String, message: String) {
        try {
            val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp is not installed or available.", Toast.LENGTH_SHORT).show()
        }
    }
}
