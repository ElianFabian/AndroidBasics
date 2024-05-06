package com.elianfabian.androidbasics.core

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.elianfabian.androidbasics.R
import com.elianfabian.androidbasics.di.GlobalServicesFactory
import com.elianfabian.androidbasics.features.work_manager.PhotoCompressorKey
import com.elianfabian.androidbasics.util.callback.MainActivityCallbacks
import com.elianfabian.androidbasics.util.callback.OnMainBackstackIsInitializedCallback
import com.elianfabian.androidbasics.util.callback.OnReceiveIntentListener
import com.elianfabian.androidbasics.util.simplestack.forEachServiceOfType
import com.zhuinden.simplestack.BackHandlingModel
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestackextensions.lifecyclektx.observeAheadOfTimeWillHandleBackChanged
import com.zhuinden.simplestackextensions.navigatorktx.androidContentFrame
import com.zhuinden.simplestackextensions.navigatorktx.backstack
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

class MainActivity : AppCompatActivity() {

	private val backPressedCallback = object : OnBackPressedCallback(false) {
		override fun handleOnBackPressed() {
			backstack.goBack()
		}
	}

//	@SuppressLint("InlinedApi")
//	private val requestPostNotificationsPermission = registerForActivityResult(
//		contract = ActivityResultContracts.RequestPermission(),
//		input = Manifest.permission.POST_NOTIFICATIONS,
//	)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		requestPostNotificationsPermission()

		onBackPressedDispatcher.addCallback(backPressedCallback)

		val containerId = R.id.MainFragmentContainer

		setContent {
			RootContainer(
				containerId = containerId,
			)
		}

		val container = androidContentFrame.apply {
			id = containerId
		}

		val fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, containerId)

		Navigator.configure()
			.setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME)
			.setStateChanger(
				SimpleStateChanger { stateChange ->
					fragmentStateChanger.handleStateChange(stateChange)
				}
			)
			.setScopedServices(DefaultServiceProvider())
			.setGlobalServices(GlobalServicesFactory(application))
			.install(
				this,
				container,
				History.single(PhotoCompressorKey),
			)

		backPressedCallback.isEnabled = backstack.willHandleAheadOfTimeBack()
		backstack.observeAheadOfTimeWillHandleBackChanged(this) { willHandleBack ->
			backPressedCallback.isEnabled = willHandleBack
		}

		backstack.forEachServiceOfType<MainActivityCallbacks> { service ->
			service.onActivityCreated(this, savedInstanceState)
		}

		if (savedInstanceState == null) {
			backstack.forEachServiceOfType<OnMainBackstackIsInitializedCallback> { service ->
				service.onMainBackstackIsInitialized(backstack)
			}
			// Fix: onNewIntent is not called when the application is closed
			backstack.forEachServiceOfType<OnReceiveIntentListener> { service ->
				service.onReceiveIntent(intent)
			}
		}
	}

	override fun onStart() {
		super.onStart()
		backstack.forEachServiceOfType<MainActivityCallbacks> { service ->
			service.onActivityStarted(this)
		}
	}

	override fun onResume() {
		super.onResume()

		backstack.forEachServiceOfType<MainActivityCallbacks> { service ->
			service.onActivityResumed(this)
		}
	}

	override fun onPause() {
		super.onPause()

		backstack.forEachServiceOfType<MainActivityCallbacks> { service ->
			service.onActivityPaused(this)
		}
	}

	override fun onStop() {
		super.onStop()

		backstack.forEachServiceOfType<MainActivityCallbacks> { service ->
			service.onActivityStopped(this)
		}
	}

	override fun onDestroy() {
		super.onDestroy()

		backstack.forEachServiceOfType<MainActivityCallbacks> { service ->
			service.onActivityDestroyed(this)
		}
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)

		backstack.forEachServiceOfType<MainActivityCallbacks> { service ->
			service.onActivitySaveInstanceState(this, outState)
		}
	}

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)

		backstack.forEachServiceOfType<OnReceiveIntentListener> { service ->
			service.onReceiveIntent(intent)
		}
	}

	private fun requestPostNotificationsPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			ActivityCompat.requestPermissions(
				this,
				arrayOf(Manifest.permission.POST_NOTIFICATIONS),
				0,
			)
		}
	}
}
