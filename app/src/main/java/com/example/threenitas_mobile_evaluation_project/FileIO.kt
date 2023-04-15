package com.example.threenitas_mobile_evaluation_project

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File


fun downloadPDF(url: String, pdfTitle: String, dm: DownloadManager): Long {
	val uri = Uri.parse(url)

	val request = DownloadManager.Request(uri)
	request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)  // Tell on which network you want to download file.
	request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)  // This will show notification on top when downloading the file.
	request.setTitle(pdfTitle) // Title for notification.
	request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${pdfTitle}.pdf")

	return dm.enqueue(request)
}

fun openPDF(fName: String, context: Context){
	val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath, fName)
	val uri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file)
	Log.i("Loading file", uri.toString())

	val target = Intent(Intent.ACTION_VIEW)
	target.setDataAndType(uri, "application/pdf")
	target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
	target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

	val intent = Intent.createChooser(target, "Open File")

	try {
		context.startActivity(intent)
	} catch (e: ActivityNotFoundException) {
		Log.e("Error opening pdf file", "The device does not have a pdf reader")
	}
}