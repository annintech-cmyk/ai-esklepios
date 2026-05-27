package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/** Vertical gap using a Dimens token. Prefer Arrangement.spacedBy() in Columns when possible. */
@Composable
fun VSpace(height: Dp) { Spacer(Modifier.height(height)) }

/** Horizontal gap using a Dimens token. */
@Composable
fun HSpace(width: Dp) { Spacer(Modifier.width(width)) }