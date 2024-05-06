package com.elianfabian.androidbasics.util

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

private fun <I, O, A> registerForActivityResultSuspendInternal(
	transform: (input: A) -> I,
	contract: ActivityResultContract<I, O>,
	registerForActivityResult: (
		contract: ActivityResultContract<I, O>,
		callback: ActivityResultCallback<O>,
	) -> ActivityResultLauncher<I>,
): suspend (
	input: A,
) -> O {
	val channel = Channel<O>()

	val resultLauncher = registerForActivityResult(contract) { result ->
		channel.trySend(result)
	}

	return { input: A ->
		println("$$$ input = ${if (input is Array<*>) input.contentToString() else input.toString()}")
		resultLauncher.launch(transform(input))

		channel.receive()
	}
}

private fun <I, O, A> registerForActivityResultCallbackInternal(
	transform: (input: A) -> I,
	contract: ActivityResultContract<I, O>,
	scope: CoroutineScope,
	registerForActivityResult: (
		contract: ActivityResultContract<I, O>,
		callback: ActivityResultCallback<O>,
	) -> ActivityResultLauncher<I>,
): (input: A, callback: (result: O) -> Unit) -> Unit {

	val channel = Channel<O>()

	val resultLauncher = registerForActivityResult(contract) { result ->
		channel.trySend(result)
	}

	return { input: A, callback: (result: O) -> Unit ->
		resultLauncher.launch(transform(input))

		scope.launch {
			val result = channel.receive()
			callback(result)
		}
	}
}

fun <I, O, A> ComponentActivity.registerForActivityResultSuspend(
	transform: (input: A) -> I,
	contract: ActivityResultContract<I, O>,
): suspend (input: A) -> O {
	return registerForActivityResultSuspendInternal(
		contract = contract,
		transform = transform,
		registerForActivityResult = ::registerForActivityResult,
	)
}

fun <I, O> ComponentActivity.registerForActivityResultSuspend(
	contract: ActivityResultContract<I, O>,
): suspend (input: I) -> O {
	return this.registerForActivityResultSuspend(
		contract = contract,
		transform = { it },
	)
}

fun <I, O> ComponentActivity.registerForActivityResultSuspend(
	contract: ActivityResultContract<I, O>,
	input: I,
): suspend () -> O {
	val function = registerForActivityResultSuspend(contract)

	return { function(input) }
}

fun <I, O, A> ComponentActivity.registerForActivityResultCallback(
	transform: (input: A) -> I,
	contract: ActivityResultContract<I, O>,
): (input: A, callback: (result: O) -> Unit) -> Unit {
	return registerForActivityResultCallbackInternal(
		contract = contract,
		registerForActivityResult = ::registerForActivityResult,
		transform = transform,
		scope = lifecycleScope,
	)
}

fun <I, O> ComponentActivity.registerForActivityResultCallback(
	contract: ActivityResultContract<I, O>,
): (input: I, callback: (result: O) -> Unit) -> Unit {
	return registerForActivityResultCallback(
		contract = contract,
		transform = { it },
	)
}

fun <I, O> ComponentActivity.registerForActivityResultCallback(
	contract: ActivityResultContract<I, O>,
	input: I,
): (callback: (result: O) -> Unit) -> Unit {
	val function = registerForActivityResultCallback(contract)

	return { callback ->
		function(input, callback)
	}
}

fun <I, O, A> Fragment.registerForActivityResultSuspend(
	transform: (A) -> I,
	contract: ActivityResultContract<I, O>,
): suspend (input: A) -> O {
	return registerForActivityResultSuspendInternal(
		contract = contract,
		transform = transform,
		registerForActivityResult = ::registerForActivityResult,
	)
}


fun <I, O> Fragment.registerForActivityResultSuspend(
	contract: ActivityResultContract<I, O>,
): suspend (input: I) -> O {
	return registerForActivityResultSuspend(
		contract = contract,
		transform = { it },
	)
}

fun <I, O> Fragment.registerForActivityResultSuspend(
	contract: ActivityResultContract<I, O>,
	input: I,
): suspend () -> O {
	val function = registerForActivityResultSuspend(contract)

	return { function(input) }
}

fun <I, O, A> Fragment.registerForActivityResultCallback(
	transform: (A) -> I,
	contract: ActivityResultContract<I, O>,
): (input: A, callback: (result: O) -> Unit) -> Unit {
	return registerForActivityResultCallbackInternal(
		contract = contract,
		registerForActivityResult = ::registerForActivityResult,
		transform = transform,
		scope = lifecycleScope,
	)
}

fun <I, O> Fragment.registerForActivityResultCallback(
	contract: ActivityResultContract<I, O>,
): (input: I, callback: (result: O) -> Unit) -> Unit {
	return registerForActivityResultCallback(
		contract = contract,
		transform = { it },
	)
}

fun <I, O> Fragment.registerForActivityResultCallback(
	contract: ActivityResultContract<I, O>,
	input: I,
): (callback: (result: O) -> Unit) -> Unit {
	val function = registerForActivityResultCallback(contract)

	return { callback ->
		function(input, callback)
	}
}
