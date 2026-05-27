package lu.esklepios.app.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow

fun <T> StateFlow<T>.watch(onChange: (T) -> Unit) {
    CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {
        collect { onChange(it) }
    }
}
