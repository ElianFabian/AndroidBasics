package com.elianfabian.androidbasics.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns


fun Uri.getSize(context: Context): String? {
	var fileSize: String? = null
	val cursor = context.contentResolver
		.query(this, null, null, null, null, null)
	try {
		if (cursor != null && cursor.moveToFirst()) {
			val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
			if (!cursor.isNull(sizeIndex)) {
				fileSize = cursor.getString(sizeIndex)
			}
		}
	}
	finally {
		cursor!!.close()
	}
	return fileSize
}
