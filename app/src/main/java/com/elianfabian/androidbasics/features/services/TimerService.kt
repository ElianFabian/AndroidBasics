package com.elianfabian.androidbasics.features.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.elianfabian.androidbasics.R
import com.elianfabian.androidbasics.util.getSerializableExtraCompat
import com.elianfabian.androidbasics.util.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerService : Service() {

	private val scope = CoroutineScope(Dispatchers.Main.immediate)

	private var currentTime = 0

	override fun onBind(intent: Intent): IBinder? = null


	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		val action: Action = intent?.getSerializableExtraCompat(ExtraAction) ?: return START_NOT_STICKY
		val initialTime = intent.getIntExtra(ExtraInitialTime, currentTime)

		currentTime = initialTime

		when (action) {
			Action.Start -> {
				start()
			}
			Action.Stop -> {
				stopSelf(startId)
			}
		}

		return START_NOT_STICKY
	}


	private fun start() {
		val notificationId = 1
		val channelId = "running_channel"

		scope.launch {
			showToast("Service started")
			while (true) {
				val notification = NotificationCompat.Builder(this@TimerService, channelId)
					.setSmallIcon(R.drawable.ic_launcher_foreground)
					.setContentTitle("Run is active")
					.setContentText("Elapsed time: $currentTime")
					.build()
				startForeground(notificationId, notification)
				delay(1000)
				currentTime++
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		scope.cancel()
	}


	enum class Action {
		Start,
		Stop,
	}


	companion object {
		private val ExtraAction = "${this::class.qualifiedName}.Action"
		private val ExtraInitialTime = "${this::class.qualifiedName}.InitialTime"

		fun newIntent(
			context: Context,
			action: Action,
			initialTime: Int? = null,
		): Intent {
			return Intent(context, TimerService::class.java).apply {
				putExtra(ExtraAction, action)
				putExtra(ExtraInitialTime, initialTime)
			}
		}
	}
}
