package lu.esklepios.app.core.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

/**
 * Returns a [sharedElement] modifier when both composition locals are present,
 * otherwise [Modifier]. Call at the top level of any composable that participates
 * in a shared-element transition.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun sharedElementModifier(key: Any): Modifier {
    val sts = LocalSharedTransitionScope.current ?: return Modifier
    val avs = LocalNavAnimatedVisibilityScope.current ?: return Modifier
    return with(sts) {
        Modifier.sharedElement(
            state = rememberSharedContentState(key = key),
            animatedVisibilityScope = avs
        )
    }
}
