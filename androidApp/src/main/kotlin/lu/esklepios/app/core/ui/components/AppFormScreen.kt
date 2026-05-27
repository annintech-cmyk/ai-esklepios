package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import lu.esklepios.app.core.ui.theme.Background
import lu.esklepios.app.core.ui.theme.Dimens

@Composable
fun AppFormScreen(
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    error: String? = null,
    onErrorDismissed: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    horizontalPadding: Dp = Dimens.paddingXL,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            onErrorDismissed()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background,
        bottomBar = bottomBar
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            AppToolbar(title = title, onNavigateBack = onNavigateBack)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding)
                    .padding(bottom = innerPadding.calculateBottomPadding()),
                horizontalAlignment = horizontalAlignment,
                content = content
            )
        }
    }
}