package com.elianfabian.androidbasics.util

import android.content.Context
import android.os.Parcelable
import androidx.annotation.BoolRes
import androidx.annotation.IntegerRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import java.io.Serializable

sealed interface UiText : Parcelable {
	fun asString(context: Context): String
}

sealed interface UiTextArg : Parcelable

sealed interface UiTextArgList


private class UiTextArgListImpl(
	val sourceList: List<UiTextArg?>,
) : UiTextArgList, List<UiTextArg?> by sourceList {

	override fun toString(): String = sourceList.toString()

	override fun hashCode(): Int = sourceList.hashCode()

	override fun equals(other: Any?): Boolean {
		if (this === other) {
			return true
		}
		if (other is UiTextArgListImpl) {
			return sourceList == other.sourceList
		}
		return false
	}
}

private val UiTextArgList.rawList: List<UiTextArg?>
	get() {
		this as UiTextArgListImpl
		return sourceList
	}

private val EmptyUiArgs: UiTextArgList = UiTextArgListImpl(emptyList())

@Parcelize
private data class DynamicString(val value: String) : UiText {
	override fun asString(context: Context) = value
}

@Parcelize
private data class StringResource(
	@StringRes
	val resId: Int,
	val args: List<UiTextArg?>,
) : UiText {
	override fun asString(context: Context): String {
		val arguments = args.map { arg ->
			arg?.getValue(context)
		}.toTypedArray()

		return String.format(
			format = context.getString(resId),
			args = arguments,
		)
	}
}

@Parcelize
private data class PluralsResource(
	@PluralsRes
	val resId: Int,
	val quantity: Int,
	val args: List<UiTextArg?>,
) : UiText {
	override fun asString(context: Context): String {
		val arguments = args.map { arg ->
			arg?.getValue(context)
		}.toTypedArray()

		return String.format(
			format = context.resources.getQuantityString(resId, quantity),
			args = arguments,
		)
	}
}


private val EmptyUiText: UiText = DynamicString("")

fun UiText(value: String): UiText = when {
	value.isEmpty() -> EmptyUiText
	else -> DynamicString(value)
}

fun UiText(
	@StringRes
	resId: Int,
	args: UiTextArgList = EmptyUiArgs,
): UiText = StringResource(
	resId = resId,
	args = args.rawList,
)

fun UiText(
	@PluralsRes
	resId: Int,
	quantity: Int,
	args: UiTextArgList = EmptyUiArgs,
): UiText = PluralsResource(
	resId = resId,
	quantity = quantity,
	args = args.rawList,
)


@Parcelize
private data class SerializableArg(val value: Serializable) : UiTextArg

@Parcelize
private data class ParcelableArg(val value: Parcelable) : UiTextArg

@Parcelize
private data class BooleanResourceArg(@BoolRes val resId: Int) : UiTextArg

@Parcelize
private data class IntegerResourceArg(@IntegerRes val resId: Int) : UiTextArg

@Parcelize
private data class StringResourceArg(
	@StringRes
	val resId: Int,
	val args: List<UiTextArg?>,
) : UiTextArg

@Parcelize
private data class PluralsResourceArg(
	@PluralsRes
	val resId: Int,
	val quantity: Int,
	val args: List<UiTextArg?>,
) : UiTextArg

private fun UiTextArg.getValue(context: Context): Any {
	val resources = context.resources

	return when (this) {
		is SerializableArg -> value
		is ParcelableArg -> value
		is BooleanResourceArg -> resources.getBoolean(resId)
		is IntegerResourceArg -> resources.getInteger(resId)
		is StringResourceArg -> {
			String.format(
				format = resources.getString(resId),
				args = args.map { arg -> arg?.getValue(context) }.toTypedArray(),
			)
		}
		is PluralsResourceArg -> {
			String.format(
				format = resources.getQuantityString(resId, quantity),
				args = args.map { arg -> arg?.getValue(context) }.toTypedArray(),
			)
		}
	}
}

fun stringResArg(
	@StringRes
	resId: Int,
	args: UiTextArgList = EmptyUiArgs,
): UiTextArg = StringResourceArg(resId, args.rawList)

fun pluralsResArg(
	@PluralsRes
	resId: Int,
	quantity: Int,
	args: UiTextArgList = EmptyUiArgs,
): UiTextArg = PluralsResourceArg(resId, quantity, args.rawList)

fun integerResArg(@IntegerRes resId: Int): UiTextArg = IntegerResourceArg(resId)

fun booleanResArg(@BoolRes resId: Int): UiTextArg = BooleanResourceArg(resId)


private fun Any.asUiArg(): UiTextArg = when (this) {
	is UiTextArg -> this
	is Parcelable -> ParcelableArg(this)
	is Serializable -> SerializableArg(this)
	else -> throw IllegalArgumentException("Unsupported type: ${this::class}. Only Serializable, Parcelable and ${UiTextArg::class} types are supported.")
}

fun uiTextArgsOf(arg0: Any?, vararg args: Any?): UiTextArgList {
	val uiArgs = buildList {
		add(arg0?.asUiArg())
		for (arg in args) {
			add(arg?.asUiArg())
		}
	}
	return UiTextArgListImpl(uiArgs)
}
