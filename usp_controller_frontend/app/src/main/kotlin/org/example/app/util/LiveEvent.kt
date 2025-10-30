package org.example.app.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * Single-consumption live event.
 * PUBLIC_INTERFACE
 */
class LiveEvent<T>: MutableLiveData<T>() {
    private val observers = mutableMapOf<Observer<in T>, Observer<in T>>()

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val wrapper = Observer<T> { t ->
            if (t == null) return@Observer
            value = null
            observer.onChanged(t)
        }
        observers[observer] = wrapper
        super.observe(owner, wrapper)
    }

    override fun removeObserver(observer: Observer<in T>) {
        val wrapper = observers.remove(observer)
        if (wrapper != null) {
            super.removeObserver(wrapper)
        } else {
            super.removeObserver(observer)
        }
    }
}
