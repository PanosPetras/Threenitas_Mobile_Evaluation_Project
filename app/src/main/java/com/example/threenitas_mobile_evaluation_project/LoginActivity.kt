package com.example.threenitas_mobile_evaluation_project

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.threenitas_mobile_evaluation_project.ui.theme.ForestGreen
import com.example.threenitas_mobile_evaluation_project.ui.theme.Green
import com.example.threenitas_mobile_evaluation_project.ui.theme.OuterSpace
import com.example.threenitas_mobile_evaluation_project.ui.theme.Threenitas_Mobile_Evaluation_ProjectTheme
import kotlin.concurrent.thread

class LoginActivity : ComponentActivity() {
	private var username = ""
	private var password = ""
	private val usernameMaxCharacters = 6
	private val passwordMaxCharacters = 8

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		initializeGUI()
	}

	private fun initializeGUI(){
		setContent {
			Threenitas_Mobile_Evaluation_ProjectTheme {
				DrawLoginActivity()
			}
		}
	}

	@Preview(showBackground = true, showSystemUi = true)
	@Composable
	fun DrawLoginActivity() {
		val showFailedLoginDialog = remember { mutableStateOf(false) }
		val showInvalidLoginDialog = remember { mutableStateOf(false) }
		val showUsernameInfoDialog = remember { mutableStateOf(false) }
		val showPasswordInfoDialog = remember { mutableStateOf(false) }

		Column (
			modifier = Modifier
				.fillMaxHeight()
				.background(OuterSpace),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(40.dp)

		) {
			DrawTopAppBar("Σύνδεση")
			Spacer(modifier = Modifier.height(20.dp))
			DrawUsernameTextField(showUsernameInfoDialog)
			DrawPasswordTextField(showPasswordInfoDialog)
			Spacer(modifier = Modifier.height(200.dp))
			DrawConnectButton(showFailedLoginDialog, showInvalidLoginDialog)

			DrawAlertDialogs(
				showFailedLoginDialog,
				showInvalidLoginDialog,
				showUsernameInfoDialog,
				showPasswordInfoDialog
			)
		}
	}

	@Composable
	fun DrawUsernameTextField(showDialog: MutableState<Boolean>){
		val textState = remember { mutableStateOf("") }
		TextField(
			modifier = Modifier
				.height(62.dp)
				.width(300.dp),
			value = textState.value,
			singleLine = true,
			colors = TextFieldDefaults.textFieldColors(
				backgroundColor = OuterSpace,
				focusedIndicatorColor = Green,
				unfocusedIndicatorColor = Green,
				textColor = Color.White,
				cursorColor = Color.White
			),
			onValueChange = {
				if(it.length <= usernameMaxCharacters) {
					username = it
					textState.value = it
				}
			},
			label = {
				Row (
					horizontalArrangement = Arrangement.Start,
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						modifier = Modifier.weight(15f),
						text = "UserID",
						color = Color.White,
						fontSize = 18.sp
					)
					Spacer(modifier = Modifier.weight(1f))
					IconButton(
						modifier = Modifier
							.weight(10f)
							.width(20.dp)
							.height(20.dp),
						onClick = {
							showDialog.value = true
						}
					) {
						Image(
							painter = painterResource(R.drawable.ic_info),
							contentDescription = "Information icon"
						)
					}
					Spacer(modifier = Modifier
						.weight(40f)
						.height(20.dp))
				}
			},
		)
	}

	@Composable
	fun DrawPasswordTextField(showDialog: MutableState<Boolean>){
		val textState = remember { mutableStateOf("") }
		val passwordVisible = remember { mutableStateOf(false) }

		TextField(
			modifier = Modifier
				.height(62.dp)
				.width(300.dp),
			value = textState.value,
			singleLine = true,
			colors = TextFieldDefaults.textFieldColors(
				backgroundColor = OuterSpace,
				focusedIndicatorColor = Green,
				unfocusedIndicatorColor = Green,
				textColor = Color.White,
				cursorColor = Color.White
			),
			onValueChange = {
				if(it.length <= passwordMaxCharacters) {
					password = it
					textState.value = it
				}
			},
			visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
			label = {
				DrawPasswordTextFieldLabel(passwordVisible, showDialog)
			}
		)
	}

	@Composable
	fun DrawPasswordTextFieldLabel(passwordVisible: MutableState<Boolean>, showDialog: MutableState<Boolean>){
		Row (
			horizontalArrangement = Arrangement.Start,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				modifier = Modifier.weight(20f),
				text = "Κωδικός",
				color = Color.White,
				fontSize = 18.sp
			)
			Spacer(modifier = Modifier
				.weight(1f)
			)
			IconButton(
				modifier = Modifier
					.weight(10f)
					.width(20.dp)
					.height(20.dp),
				onClick = {
					showDialog.value = true
				}
			) {
				Image(
					painter = painterResource(R.drawable.ic_info),
					contentDescription = "Information icon"
				)
			}
			Spacer(modifier = Modifier.weight(10f))
			Button(
				modifier = Modifier
					.weight(25f)
					.height(20.dp),
				contentPadding = PaddingValues(0.dp),
				elevation = ButtonDefaults.elevation(0.dp),
				colors = ButtonDefaults.buttonColors(
					backgroundColor = OuterSpace,
					contentColor = ForestGreen
				),
				onClick = {
					passwordVisible.value = !passwordVisible.value
				}
			) {
				Text(
					text = if(!passwordVisible.value) "Προβολή" else "Απόκρυψη",
					fontSize = 13.sp,
				)
			}
		}
	}

	@Composable
	fun DrawConnectButton(showFailedLoginDialog: MutableState<Boolean>, showInvalidLoginDialog: MutableState<Boolean>){
		Button(
			modifier = Modifier
				.width(188.dp)
				.height(49.dp),
			shape = RoundedCornerShape(50),
			colors = ButtonDefaults.buttonColors(
				backgroundColor = OuterSpace,
				contentColor = Green,
			),
			border = BorderStroke(2.dp, Green),
			onClick = {
				connectButtonClicked(showFailedLoginDialog, showInvalidLoginDialog)
			}
		) {
			Text(
				text = "Σύνδεση",
				fontSize = 17.sp
			)
		}
	}

	@Composable
	private fun DrawAlertDialogs(showFailedLoginDialog: MutableState<Boolean>, showInvalidLoginDialog: MutableState<Boolean>, showUsernameInfoDialog: MutableState<Boolean>, showPasswordInfoDialog: MutableState<Boolean>) {
		DrawFailedLoginAlertDialog(showFailedLoginDialog)
		DrawAlertDialog(
			showInvalidLoginDialog,
			"Λανθασμένα στοιχεία",
			"Το USERID και/ή ο κωδικός έχουν λανθασμένη μορφή. (Συμβουλευτείτε τις πληροφορίες για την μορφή τους)"
		)
		DrawAlertDialog(
			showUsernameInfoDialog,
			"Πληροφορίες",
			"Το USERID πρέπει να αποτελείται απο 6 χαρακτήρες(2 κεφαλαία και 4 νούμερα με την σειρά που είναι γραμμένα)."
		)
		DrawAlertDialog(
			showPasswordInfoDialog,
			"Πληροφορίες",
			"Ο κωδικός πρέπει να αποτελείται απο 8 χαρακτήρες(2 κεφαλαία, 3 πεζά, 1 ειδικό χαρακτήρα και 2 νούμερα)."
		)
	}

	@Composable
	fun DrawFailedLoginAlertDialog(showDialog: MutableState<Boolean>) {
		if(showDialog.value) {
			AlertDialog(
				backgroundColor = OuterSpace,
				shape = RoundedCornerShape(10),
				onDismissRequest = {
					showDialog.value = false
				},
				buttons = {
					OutlinedButton(
						modifier = Modifier.fillMaxWidth(),
						colors = ButtonDefaults.buttonColors(backgroundColor = OuterSpace),
						onClick = { showDialog.value = false }
					) {
						Text(
							text = "Επιστροφή",
							color = Green
						)
					}
				},
				title = {
					Text(
						text = "Λανθασμένα στοιχεία",
						color = Color.White
					)
				},
				text = {
					Text(
						text = "Έχετε υποβάλει λάθος στοιχεία.",
						color = Color.White
					)
				}
			)
		}
	}

	private fun connectButtonClicked(showFailedLoginDialog: MutableState<Boolean>, showInvalidLoginDialog: MutableState<Boolean>){
		if(!UserInfo.checkIfUsernameIsValid(username) ||
			!UserInfo.checkIfPasswordIsValid(password)){
			showFailedLoginDialog.value = true
			return
		}

		thread {
			if(UserInfo.login(username, password)){
				startMainActivity()
			} else {
				showInvalidLoginDialog.value = true
			}
		}
	}

	private fun startMainActivity() {
		val intent = Intent(this, MainActivity::class.java)
		startActivity(intent)
		finish()
	}
}