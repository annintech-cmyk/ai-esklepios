package lu.esklepios.app.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow

/** Returned by [watch] — call [close] in Swift `deinit` to cancel the collecting scope. */
class FlowWatcher(private val scope: CoroutineScope) {
    fun close() = scope.cancel()
}

fun <T> StateFlow<T>.watch(onChange: (T) -> Unit): FlowWatcher {
    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    scope.launch { collect { onChange(it) } }
    return FlowWatcher(scope)
}
