package com.elianfabian.androidbasics.features.work_manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.asFlow
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.elianfabian.androidbasics.util.callback.OnReceiveIntentListener
import com.elianfabian.androidbasics.util.getParcelableExtraCompat
import com.elianfabian.androidbasics.util.simplestack.ServiceScope
import com.zhuinden.simplestack.ScopedServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

interface AsyncImageCompressor {
	val uncompressedImageUri: StateFlow<Uri?>
	val compressedImagePath: StateFlow<String?>
	var compressionThresholdInBytes: Long
}

@OptIn(ExperimentalCoroutinesApi::class)
class AsyncImageCompressorImpl(
	context: Context,
	serviceScope: ServiceScope = ServiceScope(),
) :
	AsyncImageCompressor,
	OnReceiveIntentListener,
	ScopedServices.Registered by serviceScope {

	private val _workManager = WorkManager.getInstance(context)
	private var _workId = MutableStateFlow<UUID?>(null)

	private val _uncompressedImageUri = MutableStateFlow<Uri?>(null)
	override var uncompressedImageUri = _uncompressedImageUri.asStateFlow()

	override var compressionThresholdInBytes: Long = 20 * 1024L
		set(value) {
			val workResult = _workResult.value
			if (workResult == null) {
				field = value
				return
			}
			val compressionResult = PhotoCompressionWorker.output(workResult)
			if (compressionResult == PhotoCompressionWorker.CompressionResult.NotReady) {
				throw IllegalStateException("Can't not set the compressionThresholdInBytes when the process is not finished.")
			}
			field = value
		}

	private val _workResult = _workId.flatMapLatest { id ->
		if (id == null) {
			return@flatMapLatest flowOf(null)
		}
		_workManager.getWorkInfoByIdLiveData(id).asFlow()
	}.stateIn(
		scope = serviceScope,
		started = SharingStarted.Eagerly,
		initialValue = null,
	)

	override val compressedImagePath = _workResult.map { workInfo ->
		if (workInfo == null) {
			return@map null
		}
		when (val result = PhotoCompressionWorker.output(workInfo)) {
			is PhotoCompressionWorker.CompressionResult.Success -> {
				result.imagePath
			}
			else -> null
		}
	}.stateIn(
		scope = serviceScope,
		started = SharingStarted.Eagerly,
		initialValue = null,
	)


	override fun onReceiveIntent(intent: Intent) {
		if (intent.action != Intent.ACTION_SEND || !intent.hasExtra(Intent.EXTRA_STREAM)) {
			return
		}

		val imageUri = intent.getParcelableExtraCompat<Uri>(Intent.EXTRA_STREAM)

		_uncompressedImageUri.value = imageUri

		val request = OneTimeWorkRequestBuilder<PhotoCompressionWorker>()
			.setInputData(
				PhotoCompressionWorker.input(
					imageUriString = imageUri.toString(),
					compressionThresholdInBytes = compressionThresholdInBytes,
				)
			)
			.setConstraints(
				Constraints(
					requiresStorageNotLow = true,
				)
			)
			.build()

		_workId.value = request.id

		_workManager.enqueue(request)
	}
}
