package com.elianfabian.androidbasics.features.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.elianfabian.androidbasics.util.isAirPlaneModeOn
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged

interface AirPlaneModeObserver {
	val isTurnedOn: Boolean
	val isTurnedOnFlow: Flow<Boolean>
}

class AirPlaneModeObserverImpl(
	context: Context,
) : AirPlaneModeObserver {

	override var isTurnedOn: Boolean = context.isAirPlaneModeOn()
		private set

	override val isTurnedOnFlow: Flow<Boolean> = callbackFlow {
		trySend(context.isAirPlaneModeOn())

		val receiver = object : BroadcastReceiver() {
			override fun onReceive(context: Context, intent: Intent) {
				if (intent.action != Intent.ACTION_AIRPLANE_MODE_CHANGED) {
					return
				}
				val isAirPlaneModeOn = intent.getBooleanExtra("state", false)
				isTurnedOn = isAirPlaneModeOn
				trySend(isAirPlaneModeOn)
			}
		}

		context.registerReceiver(
			receiver,
			IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED),
		)

		awaitClose {
			context.unregisterReceiver(receiver)
		}
	}.distinctUntilChanged()
		.conflate()
}
