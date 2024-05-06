package com.elianfabian.androidbasics.features.work_manager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.roundToInt

class PhotoCompressionWorker(
	appContext: Context,
	params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

	override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
		val imageUriString = inputData.getString(INPUT_KEY_IMAGE_URI) ?: return@withContext failure(Error.ImageUriIsNull)
		val compressionThresholdInBytes = inputData.getLong(INPUT_KEY_COMPRESSION_THRESHOLD, 20 * 1024)

		val imageUri = Uri.parse(imageUriString)
		val imageBytes = applicationContext.contentResolver.openInputStream(imageUri)?.use { stream ->
			stream.readBytes()
		} ?: return@withContext failure(Error.CouldNotResolveImageUri)

		val imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

		var compressedImageBytes: ByteArray
		var currentQuality = 100

		do {
			ByteArrayOutputStream().use { stream ->
				imageBitmap.compress(Bitmap.CompressFormat.JPEG, currentQuality, stream)
				compressedImageBytes = stream.toByteArray()
				currentQuality -= (currentQuality * 0.1).roundToInt()
			}
		}
		while (compressedImageBytes.size > compressionThresholdInBytes && currentQuality > 5)

		val compressImageOutputFile = File(applicationContext.cacheDir, "$id.jpg").apply {
			writeBytes(compressedImageBytes)
		}

		return@withContext success(compressImageOutputFile.absolutePath)
	}


	enum class Error {
		ImageUriIsNull,
		CouldNotResolveImageUri,
		Unknown,
	}


	companion object {
		private const val INPUT_KEY_IMAGE_URI = "INPUT_KEY_IMAGE_URI"
		private const val INPUT_KEY_COMPRESSION_THRESHOLD = "INPUT_KEY_COMPRESSION_THRESHOLD"

		private const val OUTPUT_KEY_COMPRESSION_IMAGE_PATH = "OUTPUT_KEY_COMPRESSION_IMAGE_PATH"
		private const val OUTPUT_KEY_ERROR = "OUTPUT_KEY_ERROR"


		fun input(
			imageUriString: String,
			compressionThresholdInBytes: Long,
		) = workDataOf(
			INPUT_KEY_IMAGE_URI to imageUriString,
			INPUT_KEY_COMPRESSION_THRESHOLD to compressionThresholdInBytes,
		)

		fun output(workInfo: WorkInfo): CompressionResult {
			return when (workInfo.state) {
				WorkInfo.State.SUCCEEDED -> {
					val imagePath = workInfo.outputData.getString(OUTPUT_KEY_COMPRESSION_IMAGE_PATH) ?: throw IllegalStateException("Output data is expected to contain $OUTPUT_KEY_COMPRESSION_IMAGE_PATH")
					CompressionResult.Success(imagePath)
				}
				WorkInfo.State.FAILED -> {
					val error = enumValueOf<Error>(
						workInfo.outputData.getString(OUTPUT_KEY_ERROR) ?: return CompressionResult.Failure(Error.Unknown)
					)
					CompressionResult.Failure(error)
				}
				WorkInfo.State.CANCELLED -> CompressionResult.NotReady
				else -> CompressionResult.Cancelled
			}
		}

		private fun failure(error: Error): Result {
			return Result.failure(
				workDataOf(
					OUTPUT_KEY_ERROR to error.toString(),
				)
			)
		}

		private fun success(
			compressImageOutputPath: String,
		): Result {
			return Result.success(
				workDataOf(
					OUTPUT_KEY_COMPRESSION_IMAGE_PATH to compressImageOutputPath,
				)
			)
		}
	}

	sealed interface CompressionResult {
		data class Success(val imagePath: String) : CompressionResult
		data class Failure(val error: Error) : CompressionResult
		data object NotReady : CompressionResult
		data object Cancelled : CompressionResult
	}
}
