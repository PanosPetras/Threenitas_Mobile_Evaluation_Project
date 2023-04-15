package com.example.threenitas_mobile_evaluation_project

import android.util.Log

object UserInfo {
	private var accessToken = ""

	fun checkIfUsernameIsValid(username: String): Boolean {
		//Check if the username has the correct size
		if(username.length != 6){
			return false
		}

		//Count the amount of Capital letters in the password
		val regex = Regex("^[A-Z]{2}\\d{4}")

		return regex.containsMatchIn(username)
	}

	fun checkIfPasswordIsValid(password: String): Boolean {
		//Check if the password has the correct size
		if(password.length != 8){
			return false
		}

		//Count the amount of Capital letters in the password
		var regex = Regex("[A-Z]")
		if(regex.findAll(password).count() != 2){
			return false
		}

		//Count the amount of Lowercase letters in the password
		regex = Regex("[a-z]")
		if(regex.findAll(password).count() != 3){
			return false
		}

		//Count the amount of numerals in the password
		regex = Regex("\\d")
		if(regex.findAll(password).count() != 2){
			return false
		}

		//Count the amount of special characters in the password
		regex = Regex("[!@#\$%&*()_+=|<>?{}\\\\~-]")
		if(regex.findAll(password).count() != 1){
			return false
		}

		return true
	}

	fun login(username: String, password: String): Boolean {
		val res = sendPostRequest(username, password)

		if(res.code == 200){
			Log.e("Wow, it got through", "Request answer was 200(OK), token: ${res.token}")
			accessToken = res.token
			return true
		} else {
			Log.e("HTTP POST REQUEST FAILED", "ERROR CODE " + res.code)
		}

		return false
	}

	fun getBookArray(): Array<Book>{
		val json = sendGetRequest(accessToken)
		return extractBookArray(json)
	}
}