package com.elianfabian.androidbasics.util.simplestack

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

abstract class FragmentKey(
	private val serviceModule: Module? = null,
) : DefaultFragmentKey(), DefaultServiceProvider.HasServices {

	override fun getScopeTag(): String = toString()

	override fun bindServices(serviceBinder: ServiceBinder) {
		if (serviceModule is RegularServiceModule) {
			serviceModule.bindServices(serviceBinder)
		}
	}
}

sealed interface Module

interface RegularServiceModule : Module {
	fun bindServices(serviceBinder: ServiceBinder)
}
