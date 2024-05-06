package com.elianfabian.androidbasics.util.simplestack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestack.ScopeLookupMode

/**
 * Composition local to access the Backstack within screens.
 */
val LocalBackstack =
	staticCompositionLocalOf<Backstack> { throw IllegalStateException("You must ensure that the BackstackProvider provides the backstack, but it currently doesn't exist.") }

/**
 * Provider for the backstack composition local.
 */
@Composable
fun BackstackProvider(backstack: Backstack, content: @Composable () -> Unit) {
	CompositionLocalProvider(LocalBackstack provides (backstack)) {
		content()
	}
}

/**
 * Helper function to remember a service looked up from the backstack.
 */
@Composable
inline fun <reified T> rememberService(serviceTag: String = T::class.java.name): T {
	val backstack = LocalBackstack.current

	return remember { backstack.lookupService(serviceTag) }
}

/**
 * Helper function to remember a service looked up from the backstack from a specific scope with the given scope lookup mode.
 */
@Composable
inline fun <reified T> rememberServiceFrom(scopeTag: String, serviceTag: String = T::class.java.name, scopeLookupMode: ScopeLookupMode = ScopeLookupMode.ALL): T {
	val backstack = LocalBackstack.current

	return remember { backstack.lookupFromScope(scopeTag, serviceTag, scopeLookupMode) }
}

/**
 * Helper function to remember a service looked up from the backstack from a specific scope with the given scope lookup mode.
 */
@Composable
inline fun <reified T> rememberServiceFrom(scopeKey: ScopeKey, serviceTag: String = T::class.java.name, scopeLookupMode: ScopeLookupMode = ScopeLookupMode.ALL): T = rememberServiceFrom(
	scopeTag = scopeKey.scopeTag,
	serviceTag = serviceTag,
	scopeLookupMode = scopeLookupMode,
)
