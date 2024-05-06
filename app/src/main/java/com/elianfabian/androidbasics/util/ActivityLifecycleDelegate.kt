package com.elianfabian.androidbasics.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


/**
 * Returns a delegate for obtaining the instance of an activity with its associated lifecycle.
 * This delegate ensures that the activity instance remains non-null during its lifecycle.
 * If the activity is destroyed, attempting to access it will throw an IllegalStateException.
 *
 * @param initialActivityInstance The initial instance of the activity to be associated with the delegate.
 * @return A delegate providing access to the activity instance with its associated lifecycle.
 */
@JvmName("activityWithLifecycleOf")
fun <T : Activity> activityWithLifecycle(initialActivityInstance: T): ReadOnlyProperty<Any?, T> {
	return NonNullActivityWithLifecycleDelegate(
		ActivityWithLifecycleDelegate(initialActivityInstance)
	)
}

/**
 * Returns a delegate for obtaining the instance of an activity with its associated lifecycle,
 * allowing null values. If the activity is destroyed, the delegate returns null.
 *
 * @param initialActivityInstance The initial instance of the activity to be associated with the delegate.
 * @return A delegate providing access to the activity instance with its associated lifecycle, allowing null values.
 */
@JvmName("nullableActivityWithLifecycleOf")
fun <T : Activity> nullableActivityWithLifecycle(initialActivityInstance: T): ReadOnlyProperty<Any?, T?> {
	return ActivityWithLifecycleDelegate(initialActivityInstance)
}

/**
 * Returns a delegate for obtaining the instance of an activity with its associated lifecycle.
 * This delegate ensures that the activity instance remains non-null during its lifecycle.
 * If the activity is destroyed, attempting to access it will throw an IllegalStateException.
 *
 * @param initialActivityInstance The initial instance of the activity to be associated with the delegate.
 * @return A delegate providing access to the activity instance with its associated lifecycle.
 */
fun activityWithLifecycle(initialActivityInstance: Activity): ReadOnlyProperty<Any?, Activity> {
	return NonNullActivityWithLifecycleDelegate(
		ActivityWithLifecycleDelegate(initialActivityInstance)
	)
}

/**
 * Returns a delegate for obtaining the instance of an activity with its associated lifecycle,
 * allowing null values. If the activity is destroyed, the delegate returns null.
 *
 * @param initialActivityInstance The initial instance of the activity to be associated with the delegate.
 * @return A delegate providing access to the activity instance with its associated lifecycle, allowing null values.
 */
fun nullableActivityWithLifecycle(initialActivityInstance: Activity): ReadOnlyProperty<Any?, Activity?> {
	return ActivityWithLifecycleDelegate(initialActivityInstance)
}

private class NonNullActivityWithLifecycleDelegate<T : Activity>(
	private val delegate: ActivityWithLifecycleDelegate<T>,
) : ReadOnlyProperty<Any?, T> {
	override fun getValue(thisRef: Any?, property: KProperty<*>): T {
		return delegate.getValue(thisRef, property) ?: throw IllegalStateException("Can't access activity because it was destroyed.")
	}
}

private class ActivityWithLifecycleDelegate<T : Activity>(
	initialActivityInstance: T,
) : ReadOnlyProperty<Any?, T?> {

	private var _activity: T? = initialActivityInstance
	private var _activityClass = initialActivityInstance::class.java


	init {
		// This callback will leak and there's no way to avoid it.
		initialActivityInstance.application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
			override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
				@Suppress("UNCHECKED_CAST")
				if (activity::class.java == _activityClass) {
					_activity = activity as T
				}
			}

			override fun onActivityStarted(activity: Activity) = Unit
			override fun onActivityResumed(activity: Activity) = Unit
			override fun onActivityPaused(activity: Activity) = Unit
			override fun onActivityStopped(activity: Activity) = Unit
			override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

			override fun onActivityDestroyed(activity: Activity) {
				if (activity::class.java == _activityClass) {
					_activity = null
				}
			}
		})
	}


	override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
		return _activity
	}
}
