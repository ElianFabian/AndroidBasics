package com.elianfabian.androidbasics.util.callback

import android.app.Activity
import android.os.Bundle

interface MainActivityCallbacks {
	fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
	fun onActivityStarted(activity: Activity) = Unit
	fun onActivityResumed(activity: Activity) = Unit
	fun onActivityPaused(activity: Activity) = Unit
	fun onActivityStopped(activity: Activity) = Unit
	fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
	fun onActivityDestroyed(activity: Activity) = Unit
}
