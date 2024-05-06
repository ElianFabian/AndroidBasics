package com.elianfabian.androidbasics.features.services

import android.content.Context

interface TimerNotificationHandler {
	fun start()
	fun stop()
}

class TimerNotificationHandleImpl(
	private val context: Context,
) : TimerNotificationHandler {

	override fun start() {
		context.startService(
			TimerService.newIntent(
				context = context,
				action = TimerService.Action.Start,
			)
		)
	}

	override fun stop() {
		context.startService(
			TimerService.newIntent(
				context = context,
				action = TimerService.Action.Stop,
			)
		)
	}
}
