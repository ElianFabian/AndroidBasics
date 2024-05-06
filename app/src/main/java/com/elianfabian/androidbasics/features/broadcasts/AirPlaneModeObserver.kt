package com.elianfabian.androidbasics.features.broadcasts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import com.elianfabian.androidbasics.core.MainActivity
import com.elianfabian.androidbasics.util.callback.MainActivityCallbacks
import com.elianfabian.androidbasics.util.isAirPlaneModeOn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface AirPlaneModeObserver {
	fun isTurnedOn(): Boolean
	fun isTurnedOnFlow(): Flow<Boolean>
}

class AirPlaneModeObserverImpl(
	context: Context,
) : AirPlaneModeObserver,
	MainActivityCallbacks {

	private var _airPlaneModeReceiver: AirPlaneModeReceiver? = AirPlaneModeReceiver()
	private val _isTurnedOn = MutableStateFlow(context.isAirPlaneModeOn())


	override fun isTurnedOnFlow(): Flow<Boolean> = _isTurnedOn

	override fun isTurnedOn(): Boolean = _isTurnedOn.value


	private fun setupReceiver(activity: Activity) {
		if (activity !is MainActivity) {
			return
		}

		_airPlaneModeReceiver?.setOnAirPlaneModeChangeListener { isTurnedOn ->
			_isTurnedOn.value = isTurnedOn
		}

		activity.registerReceiver(
			_airPlaneModeReceiver,
			IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED),
		)
	}


	override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
		_airPlaneModeReceiver = _airPlaneModeReceiver ?: AirPlaneModeReceiver()
	}

	override fun onActivityStarted(activity: Activity) {
		setupReceiver(activity)
	}

	override fun onActivityStopped(activity: Activity) {
		activity.unregisterReceiver(_airPlaneModeReceiver)
	}

	override fun onActivityDestroyed(activity: Activity) {
		_airPlaneModeReceiver = null
	}
}
