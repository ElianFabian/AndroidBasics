package com.elianfabian.androidbasics.core

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import androidx.work.Configuration as WorkerConfiguration
import android.os.Build
import androidx.core.content.getSystemService
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.elianfabian.androidbasics.util.callback.OnMainBackstackIsInitializedCallback
import com.elianfabian.androidbasics.util.callback.OnConfigurationChangedListener
import com.elianfabian.androidbasics.util.callback.ProcessLifecycleObserver
import com.elianfabian.androidbasics.util.simplestack.forEachServiceOfType
import com.zhuinden.simplestack.Backstack

class AndroidBasicsApplication : Application(),
	OnMainBackstackIsInitializedCallback,
	WorkerConfiguration.Provider {

//	private var _mainActivity: Activity? = null
//	val mainActivity get() = _mainActivity ?: throw IllegalStateException("Attempt to access MainActivity when is null")

	private var _mainBackstack: Backstack? = null
	val mainBackstack get() = _mainBackstack ?: throw IllegalStateException("Main Backstack is not yet initialized")


	override fun onCreate() {
		super.onCreate()

//		observeMainActivity()

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				"running_channel",
				"Running Notifications",
				NotificationManager.IMPORTANCE_HIGH,
			)

			val notificationManager = getSystemService<NotificationManager>()!!

			notificationManager.createNotificationChannel(channel)
		}
	}

	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)

		mainBackstack.forEachServiceOfType<OnConfigurationChangedListener> { service ->
			service.onConfigurationChanged(newConfig)
		}
	}

	override fun onMainBackstackIsInitialized(backstack: Backstack) {
		_mainBackstack = backstack
		registerProcessLifecycleObserver()
	}

	private fun registerProcessLifecycleObserver() {
		ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
			override fun onStart(owner: LifecycleOwner) {
				mainBackstack.forEachServiceOfType<ProcessLifecycleObserver> { service ->
					service.onForeground()
				}
			}

			override fun onStop(owner: LifecycleOwner) {
				mainBackstack.forEachServiceOfType<ProcessLifecycleObserver> { service ->
					service.onBackground()
				}
			}
		})
	}

	override val workManagerConfiguration
		get() = WorkerConfiguration.Builder()
			.setWorkerFactory(BackstackWorkerFactory)
			.build()

	//	private fun observeMainActivity() {
//		registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
//			override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
//				if (activity is MainActivity) {
//					_mainActivity = activity
//				}
//			}
//
//			override fun onActivityStarted(activity: Activity) = Unit
//			override fun onActivityResumed(activity: Activity) = Unit
//			override fun onActivityPaused(activity: Activity) = Unit
//			override fun onActivityStopped(activity: Activity) = Unit
//			override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
//
//			override fun onActivityDestroyed(activity: Activity) {
//				if (activity is MainActivity) {
//					_mainActivity = null
//				}
//			}
//		})
//	}
}


val Context.mainBackstack: Backstack get() = (applicationContext as AndroidBasicsApplication).mainBackstack
