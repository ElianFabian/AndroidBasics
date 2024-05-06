package com.elianfabian.androidbasics.util.callback

import android.content.res.Configuration

interface OnConfigurationChangedListener {
	fun onConfigurationChanged(newConfig: Configuration)
}
