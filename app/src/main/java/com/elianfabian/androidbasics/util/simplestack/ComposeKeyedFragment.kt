package com.elianfabian.androidbasics.util.simplestack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.elianfabian.androidbasics.ui.theme.AndroidBasicsTheme
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.navigatorktx.backstack

object Factory : FragmentFactory() {
	override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
		val clazz = loadFragmentClass(classLoader, className)


		return super.instantiate(classLoader, className)
	}
}

abstract class ComposeKeyedFragment : KeyedFragment() {

	@Composable
	abstract fun Content()


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		requireActivity().window.decorView.rootView.post {
			onPostCreate()
		}
	}

	final override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?,
	): View {
		return ComposeView(requireContext()).apply {
			setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
			setContent {
				BackstackProvider(
					backstack = backstack,
				) {
					AndroidBasicsTheme {
						this@ComposeKeyedFragment.Content()
					}
				}
			}
		}
	}

	/**
	 * After process death it's not possible to get the backstack in onCreate().
	 *
	 * Issue's source: https://github.com/Zhuinden/simple-stack/issues/275
	 */
	protected open fun onPostCreate() {

	}
}
