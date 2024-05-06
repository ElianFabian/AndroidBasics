package com.elianfabian.androidbasics.core

import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.elianfabian.androidbasics.util.simplestack.BasePreview

@Composable
fun RootContainer(
	@IdRes
	containerId: Int,
) {
	Scaffold(
		modifier = Modifier
			.fillMaxSize()
	) { innerPadding ->
		AndroidView(
			factory = { context ->
				FrameLayout(context).apply {
					id = containerId
				}
			},
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
		)
	}
}


@Preview(showBackground = true)
@Composable
private fun Preview() = BasePreview {
	RootContainer(
		containerId = 0,
	)
}
