package com.example.threenitas_mobile_evaluation_project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.threenitas_mobile_evaluation_project.ui.theme.Onyx
import com.example.threenitas_mobile_evaluation_project.ui.theme.OuterSpace

@Composable
fun DrawTopAppBar(header: String){
	TopAppBar (
		modifier = Modifier.height(60.dp).fillMaxWidth(),
		backgroundColor = Onyx
	) {
		Row(
			Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Center
		) {
			Text(
				text = header,
				color = Color.White
			)
		}
	}
}

@Composable
fun DrawAlertDialog(showDialog: MutableState<Boolean>, title: String, text: String) {
	if(showDialog.value) {
		AlertDialog(
			shape = RoundedCornerShape(10),
			backgroundColor = OuterSpace,
			onDismissRequest = {
				showDialog.value = false
			},
			buttons = { },
			title = {
				Text(
					text = title,
					color = Color.White
				)
			},
			text = {
				Text(
					text = text,
					color = Color.White
				)
			}
		)
	}
}