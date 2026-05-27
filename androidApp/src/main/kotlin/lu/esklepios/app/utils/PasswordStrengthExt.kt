package lu.esklepios.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.theme.Danger
import lu.esklepios.app.core.ui.theme.StrengthGood
import lu.esklepios.app.core.ui.theme.Success
import lu.esklepios.app.core.ui.theme.Warning
import lu.esklepios.app.util.PasswordStrength

@Composable
fun PasswordStrength.strengthLabel(): String = when (this) {
    PasswordStrength.STRONG -> stringResource(R.string.change_password_strength_strong)
    PasswordStrength.GOOD   -> stringResource(R.string.change_password_strength_good)
    PasswordStrength.FAIR   -> stringResource(R.string.change_password_strength_fair)
    else                    -> stringResource(R.string.change_password_strength_weak)
}

fun PasswordStrength.strengthColor(): Color = when (this) {
    PasswordStrength.STRONG -> Success
    PasswordStrength.GOOD   -> StrengthGood
    PasswordStrength.FAIR   -> Warning
    else                    -> Danger
}