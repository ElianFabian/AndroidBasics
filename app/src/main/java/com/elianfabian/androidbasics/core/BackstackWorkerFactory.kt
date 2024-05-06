package com.elianfabian.androidbasics.core

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.zhuinden.simplestack.Backstack

object BackstackWorkerFactory : WorkerFactory() {
	override fun createWorker(
		appContext: Context,
		workerClassName: String,
		workerParameters: WorkerParameters,
	): ListenableWorker? {
		try {
			val workerClass = Class.forName(workerClassName)

			val constructor = workerClass.getDeclaredConstructor(
				Context::class.java,
				WorkerParameters::class.java,
				Backstack::class.java,
			)
			workerClass.declaredConstructors.first()
			return constructor.newInstance(
				appContext,
				workerParameters,
				appContext.mainBackstack,
			) as ListenableWorker
		}
		catch (e: Throwable) {
			e.printStackTrace()
			return null
		}
	}
}
