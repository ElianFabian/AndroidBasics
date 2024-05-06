package com.elianfabian.androidbasics.core

import com.elianfabian.androidbasics.features.broadcasts.AirPlaneModeObserver
import com.elianfabian.androidbasics.features.services.TimerNotificationHandler
import com.elianfabian.androidbasics.util.simplestack.ServiceScope
import com.zhuinden.simplestack.ScopedServices
import kotlinx.coroutines.launch

class MainViewModel(
	private val airPlaneModeObserver: AirPlaneModeObserver,
	private val timerNotificationHandler: TimerNotificationHandler,
	private val serviceScope: ServiceScope = ServiceScope(),
) : ScopedServices.Registered by serviceScope {

	val isInternetConnectionTurnedOnFlow = airPlaneModeObserver.isTurnedOnFlow()


	fun startTimer() {
		timerNotificationHandler.start()
	}

	fun stopTimer() {
		timerNotificationHandler.stop()
	}
}
