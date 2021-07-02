package ru.geekbrains.nasaapp.utils

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewBindingDelegate<V : ViewBinding>(private val bind: (view: View) -> V) :
    ReadOnlyProperty<Fragment, V> {

    private var viewBinding: V? = null

    override fun getValue(
        thisRef: Fragment,
        property: KProperty<*>
    ): V {
        return viewBinding ?: run {
            val view = thisRef.requireView()
            bind.invoke(view).also {
                viewBinding = it
            }
        }
    }
}