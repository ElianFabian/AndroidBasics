package com.elianfabian.androidbasics.features.work_manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PhotoCompressorViewModel(
	private val asyncImageCompressor: AsyncImageCompressor,
) {
	val uncompressedImageUri = asyncImageCompressor.uncompressedImageUri
	val compressedImagePath = asyncImageCompressor.compressedImagePath

	private val _thresholdInBytes = MutableStateFlow(asyncImageCompressor.compressionThresholdInBytes)
	val thresholdInBytes = _thresholdInBytes.asStateFlow()


	fun enterThreshold(value: Long) {
		_thresholdInBytes.value = value
		asyncImageCompressor.compressionThresholdInBytes = value
	}
}
