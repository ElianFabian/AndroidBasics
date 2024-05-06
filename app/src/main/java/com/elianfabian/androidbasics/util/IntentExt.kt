package com.elianfabian.androidbasics.util

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import java.io.Serializable

@Suppress("DEPRECATION")
inline fun <reified T : Serializable> Intent.getSerializableExtraCompat(name: String): T? {

	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		return getSerializableExtra(name, T::class.java)
	}
	return getSerializableExtra(name) as? T
}

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Intent.getParcelableExtraCompat(name: String): T? {

	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		return getParcelableExtra(name, T::class.java)
	}
	return getParcelableExtra(name) as? T
}
