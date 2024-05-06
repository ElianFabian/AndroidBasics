package com.elianfabian.androidbasics.features.work_manager

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.elianfabian.androidbasics.di.lookupApplicationContext
import com.elianfabian.androidbasics.util.getSize
import com.elianfabian.androidbasics.util.simplestack.ComposeKeyedFragment
import com.elianfabian.androidbasics.util.simplestack.FragmentKey
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.fragmentsktx.backstack
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
object PhotoCompressorKey : FragmentKey() {
	override fun instantiateFragment() = PhotoCompressorFragment()

	override fun bindServices(serviceBinder: ServiceBinder) {
		val context = serviceBinder.lookupApplicationContext()

		val asyncImageCompressor: AsyncImageCompressor = AsyncImageCompressorImpl(context)
		val viewModel = PhotoCompressorViewModel(
			asyncImageCompressor = asyncImageCompressor,
		)

		serviceBinder.apply {
			add(asyncImageCompressor)
			add(viewModel)
		}
	}
}

class PhotoCompressorFragment : ComposeKeyedFragment() {

	private val viewModel: PhotoCompressorViewModel by lazy { backstack.lookup() }


	@Composable
	override fun Content() {
		val uncompressedImageUri by viewModel.uncompressedImageUri.collectAsState()
		val compressedImagePath by viewModel.compressedImagePath.collectAsState()
		val thresholdInBytes by viewModel.thresholdInBytes.collectAsState()
		val context = LocalContext.current

		Column(
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.fillMaxSize()
		) {
			TextField(
				value = thresholdInBytes.toString(),
				onValueChange = { value ->
					viewModel.enterThreshold(value.toLong())
				},
				keyboardOptions = KeyboardOptions.Default.copy(
					keyboardType = KeyboardType.Number,
				)
			)
			uncompressedImageUri?.also { uri ->
				Text(
					text = "Uncompressed photo: Size: ${uri.getSize(context)}",
				)
				AsyncImage(
					model = uncompressedImageUri,
					contentDescription = null,
				)
				Spacer(Modifier.height(16.dp))
			}
			compressedImagePath?.also { path ->
				Text(
					text = "Compressed photo. Size: ${File(path).length()}",
				)
				val bitmap = BitmapFactory.decodeFile(path).asImageBitmap()
				Image(
					bitmap = bitmap,
					contentDescription = null,
				)
			}
		}
	}
}
