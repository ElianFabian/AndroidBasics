@file:Suppress("NOTHING_TO_INLINE")

package com.elianfabian.androidbasics.util

import android.content.Context
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.isAirPlaneModeOn(): Boolean {
	return Settings.Global.getInt(
		contentResolver,
		Settings.Global.AIRPLANE_MODE_ON,
	) != 0
}

inline fun Context.showToast(text: String) {
	Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

inline fun Context.showLongToast(text: String) {
	Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

inline fun Context.showToast(@StringRes resId: Int) {
	Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

inline fun Context.showLongToast(@StringRes resId: Int) {
	Toast.makeText(this, resId, Toast.LENGTH_LONG).show()
}
