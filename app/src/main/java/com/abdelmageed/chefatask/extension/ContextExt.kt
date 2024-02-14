package com.abdelmageed.chefatask.extension

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.net.MalformedURLException
import java.net.URL


fun Context.showToast(message: String, length: Int? = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length!!).show()
}


fun Context.isOnline(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            }

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            }

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
    }
    return false
}

@SuppressLint("Range")
fun Context.downloadImage(imageUrl: String) {
    var url: URL? = null
    try {
        url = URL(imageUrl)
    } catch (e: MalformedURLException) {
        e.printStackTrace()
    }
    var fileName = url?.path.toString()
    fileName = fileName.substring(fileName.lastIndexOf('/') + 1)
    val request = DownloadManager.Request(Uri.parse(url.toString()))
    request.setTitle(fileName)
    request.setMimeType("image/*")
    request.setAllowedOverMetered(true)
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
    val dm =
        ContextCompat.getSystemService(
            this,
            DownloadManager::class.java
        ) as DownloadManager
    val downloadId = dm.enqueue(request)
}