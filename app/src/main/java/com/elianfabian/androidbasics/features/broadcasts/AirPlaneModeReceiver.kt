package com.elianfabian.androidbasics.features.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.elianfabian.androidbasics.util.isAirPlaneModeOn

class AirPlaneModeReceiver : BroadcastReceiver() {

	private var _onAirPlaneModeChanged: ((isTurnedOn: Boolean) -> Unit)? = null


	override fun onReceive(context: Context, intent: Intent) {
		val isTurnedOn = context.isAirPlaneModeOn()

		_onAirPlaneModeChanged?.invoke(isTurnedOn)
	}


	fun setOnAirPlaneModeChangeListener(listener: ((isTurnedOn: Boolean) -> Unit)?) {
		_onAirPlaneModeChanged = listener
	}
}
