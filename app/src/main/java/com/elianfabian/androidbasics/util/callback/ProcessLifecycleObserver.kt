package com.elianfabian.androidbasics.util.callback

interface ProcessLifecycleObserver {
	fun onForeground() = Unit
	fun onBackground() = Unit
}
