package com.example.threenitas_mobile_evaluation_project

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.threenitas_mobile_evaluation_project.ui.theme.*
import kotlin.concurrent.thread


class MainActivity : ComponentActivity() {
	private lateinit var arr:Array<Book>
	private lateinit var thread: Thread
	private lateinit var dm: DownloadManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		initializeValues()
		initializeGUI()
	}

	private fun initializeValues(){
		//Get the download manager of the system
		dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

		//Get the array of the books asynchronously and sort it based on the date
		thread = thread {
			arr = UserInfo.getBookArray()
			arr = arr.sortedBy { it.date_released }.toTypedArray()
		}
	}

	private fun initializeGUI() {
		setContent {
			Threenitas_Mobile_Evaluation_ProjectTheme {
				DrawMainActivity()
			}
		}
	}

	@Composable
	fun DrawMainActivity() {
		Column (
			modifier = Modifier
				.fillMaxHeight()
				.background(OuterSpace),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			//Top bar with activity title
			DrawTopAppBar("Περιοδικά")

			//Wait for the thread to finish, so that there is
			// no data race between the two threads
			thread.join()

			//The view of the books
			DrawBooksLazyColumn()

			//Bottom utility bar
			DrawBottomAppBar()
		}
	}

	@Composable
	private fun DrawBottomAppBar() {
		BottomAppBar(
			modifier = Modifier.height(100.dp).fillMaxWidth(),
			backgroundColor = OuterSpace
		) {
			DrawBottomAppBarContents()
		}
	}

	@Composable
	private fun DrawBottomAppBarContents(){
		Box (
			contentAlignment = Alignment.Center
		){
			Image(
				modifier = Modifier.fillMaxSize(),
				painter = painterResource(R.drawable.tabs_bg),
				contentScale = ContentScale.FillBounds,
				contentDescription = null
			)
			Image(
				modifier = Modifier.fillMaxSize(),
				painter = painterResource(R.drawable.tabs_wave),
				contentScale = ContentScale.FillBounds,
				contentDescription = null
			)
			Row (
				verticalAlignment = Alignment.CenterVertically
			) {
				IconButton(
					modifier = Modifier.weight(1f),
					onClick = { }
				) {
					Image(
						painter = painterResource(R.drawable.ic_book_sel),
						contentDescription = null
					)
				}
				IconButton(
					modifier = Modifier.weight(1f),
					onClick = { }
				) {
					Image(
						painter = painterResource(R.drawable.ic_misc),
						contentDescription = null
					)
				}
				IconButton(
					modifier = Modifier.weight(1f),
					onClick = { }
				) {
					Image(
						painter = painterResource(R.drawable.btn_play),
						contentDescription = null
					)
				}
				IconButton(
					modifier = Modifier.weight(1f),
					onClick = { }
				) {
					Image(
						painter = painterResource(R.drawable.ic_link),
						contentDescription = null
					)
				}
				IconButton(
					modifier = Modifier.weight(1f),
					onClick = { }
				) {
					Image(
						painter = painterResource(R.drawable.ic_settings),
						contentDescription = null
					)
				}
			}
		}
	}

	//Drawing the book list
	@OptIn(ExperimentalFoundationApi::class)
	@Composable
	private fun ColumnScope.DrawBooksLazyColumn(){
		LazyColumn (
			modifier = Modifier.weight(1f).fillMaxWidth()
		) {
			val dates = arr.map { it.date_released.substring(0, 7) }.distinctBy { it }

			dates.forEach {date ->
				stickyHeader {
					Text(
						text = date.replace("-", "/"),
						fontSize = 24.sp,
						color = Color.White
					)
				}

				item {
					LazyRow {
						arr.filter { b -> b.date_released.substring(0, 7) == date }.forEach {
							item {
								DrawBookGUIEntity(it)
							}
						}
					}
				}
			}
		}
	}

	@Composable
	private fun DrawBookGUIEntity(it: Book) {
		Column(
			modifier = Modifier
				.width(200.dp)
				.height(200.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			DrawBookGUIEntityContents(it)
		}
	}

	@Composable
	private fun DrawBookGUIEntityContents(book: Book) {
		val downloading = remember { mutableStateOf(false) }
		val downloadState = remember { mutableStateOf(0f) }
		val downloaded = remember { mutableStateOf(book.exists()) }
		val downloadID = remember { mutableStateOf<Long>(-1) }

		Column {
			Box(
				contentAlignment = Alignment.Center
			) {
				DrawBookIconButton(
					book,
					downloaded,
					downloadID,
					downloading,
					downloadState
				)
				DrawDownloadIcon(downloaded, downloading)
				DrawCheckIcon(downloaded)
			}
			DrawDownloadProgressBar(downloading, downloadState)
		}
		DrawBookTitleText(book.titleDisplayed)
	}

	@Composable
	private fun DrawBookIconButton(book: Book, downloaded: MutableState<Boolean>, downloadID: MutableState<Long>, downloading: MutableState<Boolean>, downloadState: MutableState<Float>) {
		IconButton(
			modifier = Modifier
				.width(150.dp)
				.height(150.dp),
			onClick = {
				bookIconButtonOcClickThread1(book, book.titleDisplayed, downloading, downloadID, downloadState, downloaded)
			}
		) {
			DrawBookCoverGlideImage(book.img_url)
		}
	}

	@OptIn(ExperimentalGlideComposeApi::class)
	@Composable
	private fun DrawBookCoverGlideImage(url: String){
		GlideImage(
			modifier = Modifier
				.width(150.dp)
				.height(150.dp),
			model = url,
			contentDescription = "Book cover"
		)
	}

	@Composable
	private fun DrawDownloadIcon(downloaded: MutableState<Boolean>, downloading: MutableState<Boolean>){
		if(!downloaded.value && !downloading.value){
			Image(
				painter = painterResource(id = R.drawable.ic_download),
				contentDescription = ""
			)
		}
	}

	@Composable
	private fun DrawCheckIcon(downloaded: MutableState<Boolean>){
		if(downloaded.value){
			Image(
				modifier = Modifier.width(16.dp).height(16.dp),
				painter = painterResource(id = R.drawable.ic_check_w),
				contentDescription = ""
			)
		}
	}

	@Composable
	private fun DrawDownloadProgressBar(downloading: MutableState<Boolean>, downloadState: MutableState<Float>){
		if(downloading.value){
			LinearProgressIndicator(
				modifier = Modifier.background(Color.White).height(12.dp).width(150.dp),
				progress = downloadState.value,
				color = TuftsBlue
			)
		}
	}

	@Composable
	private fun DrawBookTitleText(title: String){
		Text(
			text = title,
			color = Color.White,
			fontSize = 16.sp
		)
	}

	//Functionality for the click on a book cover
	private fun bookIconButtonOcClickThread1(book: Book, titleDisplayed: String, downloading: MutableState<Boolean>, downloadID: MutableState<Long>, downloadState: MutableState<Float>, downloaded: MutableState<Boolean>){
		if (!downloaded.value) {
			downloadID.value = downloadPDF(book.pdf_url, titleDisplayed, dm)
			downloading.value = true

			thread {
				bookIconButtonOnClickThread2(downloading, downloadID, downloadState, downloaded)
			}

		} else {
			openPDF("$titleDisplayed.pdf", window.context)
		}
	}

	@SuppressLint("Range")
	private fun bookIconButtonOnClickThread2(downloading: MutableState<Boolean>, downloadID: MutableState<Long>, downloadState: MutableState<Float>, downloaded: MutableState<Boolean>) {
		while (downloading.value) {
			try {
				val q = DownloadManager.Query().setFilterById(downloadID.value)
				val cursor = dm.query(q)

				if (cursor.moveToFirst()) {
					when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
						DownloadManager.STATUS_RUNNING -> {
							val totalBytes = cursor.getLong(
								cursor.getColumnIndex(
									DownloadManager.COLUMN_TOTAL_SIZE_BYTES
								)
							)

							if (totalBytes > 0) {
								val downloadedBytes = cursor.getLong(
									cursor.getColumnIndex(
										DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR
									)
								)
								downloadState.value =
									(downloadedBytes.toFloat() / totalBytes)
							}
						}
						DownloadManager.STATUS_SUCCESSFUL -> {
							downloadState.value = 1f
							downloading.value = false
						}
						DownloadManager.STATUS_PAUSED -> {
						}
						DownloadManager.STATUS_PENDING -> {
						}
						else -> {
							downloadState.value = 0f
							downloading.value = false
							return
						}
					}
				}
				cursor.close()
			} catch (e: Exception) {
				Log.e(
					"Download status error",
					"Encountered an unexpected error while trying to get the download status"
				)
				downloading.value = false
			}
		}
		downloaded.value = true
		downloading.value = false

		if(downloadState.value > 100f){
			downloadState.value = 100f
		}
	}
}