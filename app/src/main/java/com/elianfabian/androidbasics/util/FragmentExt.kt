@file:Suppress("NOTHING_TO_INLINE")

package com.elianfabian.androidbasics.util

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

inline fun Fragment.showToast(text: String) {
	Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

inline fun Fragment.showLongToast(text: String) {
	Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

inline fun Fragment.showToast(@StringRes resId: Int) {
	Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
}

inline fun Fragment.showLongToast(@StringRes resId: Int) {
	Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
}
