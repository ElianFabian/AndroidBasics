package com.elianfabian.androidbasics.util.simplestack

import com.zhuinden.simplestack.ScopedServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

class ServiceScope(
	context: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate,
) : CoroutineScope, ScopedServices.Registered {

	override val coroutineContext: CoroutineContext = context

	override fun onServiceRegistered() {

	}

	override fun onServiceUnregistered() {
		this.cancel()
	}
}
