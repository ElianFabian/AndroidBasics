package com.elianfabian.androidbasics.di

import android.app.Application
import android.content.Context
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup

class GlobalServicesFactory(
	private val application: Application,
) : GlobalServices.Factory {

	override fun create(backstack: Backstack): GlobalServices {

		val applicationContext: Context = application

		val globalServices = GlobalServices.builder()
			//.add(mainViewModel)
			.add(applicationContext, TAG_APPLICATION_CONTEXT)
			.build()

		return globalServices
	}
}


private const val TAG_APPLICATION_CONTEXT = "TAG_APPLICATION_CONTEXT"

fun ServiceBinder.lookupApplicationContext(): Context {
	return lookup(TAG_APPLICATION_CONTEXT)
}
