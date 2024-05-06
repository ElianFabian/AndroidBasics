package com.elianfabian.androidbasics.util.simplestack

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.elianfabian.androidbasics.util.callback.SimpleActivityLifecycleCallbacks
import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

class ActivityServiceProvider(
	activity: ComponentActivity,
) : ScopedServices {

	private var _activity: ComponentActivity? = activity
	private var _activityClass = activity::class.java


	init {
		activity.application.registerActivityLifecycleCallbacks(object : SimpleActivityLifecycleCallbacks {
			override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
				if (activity::class.java == _activityClass && activity is ComponentActivity) {
					_activity = activity
				}
			}

			override fun onActivityDestroyed(activity: Activity) {
				if (activity::class.java == _activityClass) {
					_activity = null
				}
			}
		})
	}


	override fun bindServices(serviceBinder: ServiceBinder) {
		val key = serviceBinder.getKey<Any>()

		if (key is HasServices && serviceBinder.scopeTag == key.scopeTag) {
			key.bindServices(
				serviceBinder = serviceBinder,
				activity = _activity ?: error("For some reason activity was null."),
			)
			return
		}
		if (key is DefaultServiceProvider.HasServices && serviceBinder.scopeTag == key.scopeTag) {
			key.bindServices(serviceBinder)
		}
	}

	interface HasServices : ScopeKey {
		fun bindServices(serviceBinder: ServiceBinder, activity: ComponentActivity)
	}
}
