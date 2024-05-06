package com.elianfabian.androidbasics.util.simplestack

import androidx.activity.ComponentActivity
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.GlobalServices

abstract class ActivityGlobalServicesFactory(
	activity: ComponentActivity,
) : GlobalServices.Factory {

	private var _activity: ComponentActivity? = activity

	protected val activity: ComponentActivity
		get() {
			return _activity
				?: throw IllegalStateException("Can't access activity property after the createGlobalServices() returns.")
		}


	final override fun create(backstack: Backstack): GlobalServices {
		val globalServices = createGlobalServices(backstack)

		// avoid memory leak
		_activity = null

		return globalServices
	}

	abstract fun createGlobalServices(backstack: Backstack): GlobalServices
}

class ExtendedGlobal


class GlobalServicesBuilderScope {

	private val _builder = GlobalServices.builder()

	private val _services = mutableMapOf<String, Any>()
	val services: Map<String, Any> = _services


	fun <T : Any> addService(
		tag: String,
		service: T,
	) {
		_builder.addService(tag, service)
		_services[tag] = service
	}

	fun <T : Any> addAlias(
		tag: String,
		service: T,
	) {
		_builder.addAlias(tag, service)
	}

	inline fun <reified T : Any> add(
		service: T,
		tag: String = T::class.java.name,
	) {
		addService(tag, service)
	}

	inline fun <reified T : Any> rebind(
		service: T,
		tag: String = T::class.java.name,
	) {
		addAlias(tag, service)
	}
}