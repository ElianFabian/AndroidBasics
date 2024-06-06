package com.elianfabian.androidbasics.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun UiText.asString(): String {
	val context = LocalContext.current
	return asString(context)
}
