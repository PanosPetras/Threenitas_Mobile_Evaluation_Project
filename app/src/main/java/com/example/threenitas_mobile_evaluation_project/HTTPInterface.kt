package com.example.threenitas_mobile_evaluation_project

import android.os.Environment
import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class Response(val code: Int, val token: String)
internal data class Res(val expires_in: Int, val token_type: String, val refresh_token: String, val access_token: String)
data class Book(val id: Int, val title: String, val img_url: String, val date_released: String, val pdf_url: String) {
	val titleDisplayed: String = title.substring(0, 28.coerceAtMost(title.length))

	fun exists(): Boolean {
		val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath, "$titleDisplayed.pdf")
		return file.exists()
	}
}

fun sendGetRequest(token: String): String {
	val url = URL("https://3nt-demo-backend.azurewebsites.net/Access/Books")
	var resBody = ""

	with(url.openConnection() as HttpURLConnection) {
		requestMethod = "GET"

		setRequestProperty("Authorization", "Bearer $token")

		if(responseCode == 200) {
			InputStreamReader(inputStream).use {
				resBody = it.readText()

				Log.i("Received response to GET request", "Response : $resBody")
			}
		}
	}

	return resBody
}

fun sendPostRequest(userName:String, password:String): Response {
	var reqParam = URLEncoder.encode("UserName", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8")
	reqParam += "&" + URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")
	val url = URL("https://3nt-demo-backend.azurewebsites.net/Access/Login")

	var resCode: Int
	var resBody = ""

	with(url.openConnection() as HttpURLConnection) {
		requestMethod = "POST"

		val wr = OutputStreamWriter(outputStream)
		wr.write(reqParam)
		wr.flush()

		resCode = responseCode

		if(resCode == 200) {
			InputStreamReader(inputStream).use {
				resBody = it.readText()

				Log.i("Received response to POST request", "Response : $resBody")
			}
		}
	}

	val token = if(resCode == 200){ extractToken(resBody) } else {""}

	return Response(resCode, token)
}

internal fun extractToken(json: String): String {
	val mapper = jacksonObjectMapper()
	val c: Res = mapper.readValue(json)

	return c.access_token
}

fun extractBookArray(json: String): Array<Book> {
	if(json.isEmpty() || json.isBlank()){
		return arrayOf()
	}
	val mapper = jacksonObjectMapper()
	return mapper.readValue(json)
}