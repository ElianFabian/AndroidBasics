@file:Suppress("NOTHING_TO_INLINE")

package com.elianfabian.androidbasics.util

import android.os.Binder
import android.os.Bundle
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import android.util.SparseArray
import androidx.annotation.RequiresApi
import java.io.Serializable

@Suppress("DEPRECATION")
fun Bundle.contentToString(): String {
	return buildString {
		var separator = ""

		append("Bundle[")
		for (key in keySet()) {
			val value = get(key)

			append(separator)
			append("$key=")

			when (value) {
				is String -> append("'$value'")
				is Bundle -> append(value.contentToString())
				else -> append(value)
			}

			separator = ", "
		}
		append("]")
	}
}

inline fun Bundle?.orEmpty(): Bundle = this ?: Bundle.EMPTY

inline fun buildBundle(builder: Bundle.() -> Unit): Bundle {
	return Bundle().apply(builder)
}

inline operator fun Bundle.contains(key: String) = containsKey(key)

@Suppress("DEPRECATION")
fun Bundle.asSequence(): Sequence<Pair<String, Any?>> {
	return keySet().asSequence().map { key ->
		key to get(key)
	}
}

operator fun Bundle.iterator() = asSequence().iterator()

fun Bundle.toList(): List<Pair<String, Any?>> = asSequence().toList()
fun Bundle.toMap(): Map<String, Any?> {
	return asSequence().map { (key, element) ->
		key to when (element) {
			is Bundle -> element.internalToMap()
			else -> element
		}
	}.toMap()
}

private inline fun Bundle.internalToMap() = asSequence().toMap()

inline operator fun Bundle.set(key: String, value: Boolean) = putBoolean(key, value)
inline operator fun Bundle.set(key: String, value: Byte) = putByte(key, value)
inline operator fun Bundle.set(key: String, value: Short) = putShort(key, value)
inline operator fun Bundle.set(key: String, value: Int) = putInt(key, value)
inline operator fun Bundle.set(key: String, value: Long) = putLong(key, value)
inline operator fun Bundle.set(key: String, value: Float) = putFloat(key, value)
inline operator fun Bundle.set(key: String, value: Double) = putDouble(key, value)
inline operator fun Bundle.set(key: String, value: Char) = putChar(key, value)
inline operator fun Bundle.set(key: String, value: CharSequence) = putCharSequence(key, value)
inline operator fun Bundle.set(key: String, value: String) = putString(key, value)
inline operator fun Bundle.set(key: String, value: Bundle) = putBundle(key, value)
inline operator fun Bundle.set(key: String, value: Parcelable) = putParcelable(key, value)
inline operator fun Bundle.set(key: String, value: Serializable) = putSerializable(key, value)

inline operator fun Bundle.set(key: String, value: BooleanArray) = putBooleanArray(key, value)
inline operator fun Bundle.set(key: String, value: ByteArray) = putByteArray(key, value)
inline operator fun Bundle.set(key: String, value: ShortArray) = putShortArray(key, value)
inline operator fun Bundle.set(key: String, value: IntArray) = putIntArray(key, value)
inline operator fun Bundle.set(key: String, value: LongArray) = putLongArray(key, value)
inline operator fun Bundle.set(key: String, value: FloatArray) = putFloatArray(key, value)
inline operator fun Bundle.set(key: String, value: DoubleArray) = putDoubleArray(key, value)
inline operator fun Bundle.set(key: String, value: CharArray) = putCharArray(key, value)
inline operator fun Bundle.set(key: String, value: Array<out CharSequence>) = putCharSequenceArray(key, value)
inline operator fun Bundle.set(key: String, value: Array<out String>) = putStringArray(key, value)
inline operator fun Bundle.set(key: String, value: Array<out Parcelable>) = putParcelableArray(key, value)
inline operator fun Bundle.set(key: String, value: SparseArray<out Parcelable>) = putSparseParcelableArray(key, value)

@JvmName("setIntArrayList")
inline operator fun Bundle.set(key: String, value: ArrayList<Int>) = putIntegerArrayList(key, value)

@JvmName("setStringArrayList")
inline operator fun Bundle.set(key: String, value: ArrayList<String>) = putStringArrayList(key, value)

@JvmName("setCharSequenceArrayList")
inline operator fun Bundle.set(key: String, value: ArrayList<CharSequence>) = putCharSequenceArrayList(key, value)

@JvmName("setParcelableArrayList")
inline operator fun Bundle.set(key: String, value: ArrayList<out Parcelable>) = putParcelableArrayList(key, value)

@RequiresApi(18)
inline operator fun Bundle.set(key: String, value: Binder) = putBinder(key, value)

@RequiresApi(21)
inline operator fun Bundle.set(key: String, value: Size) = putSize(key, value)

@RequiresApi(21)
inline operator fun Bundle.set(key: String, value: SizeF) = putSizeF(key, value)

@Suppress("UNUSED_PARAMETER")
inline operator fun Bundle.set(key: String, value: Nothing?) = putString(key, null)
