package com.elianfabian.androidbasics.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.elianfabian.androidbasics.di.lookupApplicationContext
import com.elianfabian.androidbasics.features.broadcasts.AirPlaneModeObserver
import com.elianfabian.androidbasics.features.broadcasts.AirPlaneModeObserverImpl
import com.elianfabian.androidbasics.features.services.TimerNotificationHandleImpl
import com.elianfabian.androidbasics.features.services.TimerNotificationHandler
import com.elianfabian.androidbasics.util.simplestack.ComposeKeyedFragment
import com.elianfabian.androidbasics.util.simplestack.FragmentKey
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.fragmentsktx.backstack
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
data object MainKey : FragmentKey() {

	override fun instantiateFragment(): Fragment = MainFragment()

	override fun bindServices(serviceBinder: ServiceBinder) {
		val context = serviceBinder.lookupApplicationContext()
		val airPlaneModeObserver: AirPlaneModeObserver = AirPlaneModeObserverImpl(context)
		val timerNotificationHandler: TimerNotificationHandler = TimerNotificationHandleImpl(context)
		val viewModel = MainViewModel(
			airPlaneModeObserver = airPlaneModeObserver,
			timerNotificationHandler = timerNotificationHandler,
		)

		serviceBinder.apply {
			add(airPlaneModeObserver)
			add(timerNotificationHandler)
			add(viewModel)
		}
	}
}

class MainFragment : ComposeKeyedFragment() {

	private val viewModel: MainViewModel by lazy { backstack.lookup() }


	override fun onPostCreate() {
		lifecycleScope.launch {
			viewModel.isInternetConnectionTurnedOnFlow.collectLatest { isTurnedOn ->
				println("$$$ MainFragment: airPlaneMode.isTurnedOn = $isTurnedOn")
			}
		}
	}

	@Composable
	override fun Content() {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.SpaceAround,
			modifier = Modifier
				.fillMaxSize()
		) {
			Button(
				onClick = {
					viewModel.startTimer()
				}
			) {
				Text("Start")
			}
			Button(
				onClick = {
					viewModel.stopTimer()
				}
			) {
				Text("Stop")
			}
		}
	}
}
