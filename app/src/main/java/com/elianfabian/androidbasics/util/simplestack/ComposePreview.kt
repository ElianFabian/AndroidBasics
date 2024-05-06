package com.elianfabian.androidbasics.util.simplestack

import androidx.compose.runtime.Composable
import com.elianfabian.androidbasics.ui.theme.AndroidBasicsTheme
import com.zhuinden.simplestack.Backstack

@Composable
fun BasePreview(
	content: @Composable () -> Unit,
) {
	BackstackProvider(backstack = Backstack()) {
		AndroidBasicsTheme {
			content()
		}
	}
}
