package lu.esklepios.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.ui.navigation.NavDestination
import lu.esklepios.app.ui.theme.*

private val LandingTeal = Color(0xFF4DD0E1)
private val FieldBg     = Color(0xFFF3F4F6)

@Composable
fun LandingScreen(navController: NavController) {
    var searchQuery   by remember { mutableStateOf("") }
    var locationQuery by remember { mutableStateOf("Luxembourg") }

    val gradientBrush = Brush.verticalGradient(listOf(GradientStart, GradientEnd))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradientBrush)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 36.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { navController.navigate(NavDestination.Login.route) },
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.18f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp,
                                pressedElevation  = 0.dp
                            ),
                            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.landing_connect),
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = stringResource(R.string.app_name),
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = ".",
                            color = LandingTeal,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color      = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize   = 30.sp
                                )
                            ) { append("${stringResource(R.string.landing_hero_prefix)} ") }
                            withStyle(
                                SpanStyle(
                                    color      = LandingTeal,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize   = 30.sp
                                )
                            ) { append(stringResource(R.string.landing_hero_accent)) }
                        },
                        lineHeight = 38.sp
                    )

                    Spacer(Modifier.height(48.dp))
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-28).dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White,
                shadowElevation = 0.dp,
                tonalElevation  = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)
                ) {

                    LandingInputField(
                        value         = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder   = stringResource(R.string.landing_search_research),
                        leadingIcon   = Icons.Outlined.MedicalServices,
                        iconTint      = TextHint
                    )

                    Spacer(Modifier.height(12.dp))

                    LandingInputField(
                        value         = locationQuery,
                        onValueChange = { locationQuery = it },
                        placeholder   = stringResource(R.string.landing_location_hint),
                        leadingIcon   = Icons.Outlined.LocationOn,
                        iconTint      = PrimaryMid
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate(NavDestination.Home.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape  = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text       = stringResource(R.string.landing_search_button),
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Filled.ArrowForward,
                            contentDescription = null,
                            tint     = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(color = BorderColor)
                    Spacer(Modifier.height(20.dp))

                    LandingTrustRow(Icons.Outlined.Shield,   stringResource(R.string.landing_trust_verified))
                    Spacer(Modifier.height(12.dp))
                    LandingTrustRow(Icons.Outlined.NearMe,   stringResource(R.string.landing_trust_closest))
                    Spacer(Modifier.height(12.dp))
                    LandingTrustRow(Icons.Outlined.Lock,     stringResource(R.string.landing_trust_data_secured))

                    Spacer(Modifier.height(56.dp))
                }
            }
        }
    }
}

@Composable
private fun LandingInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    iconTint: Color
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        modifier      = Modifier.fillMaxWidth(),
        placeholder   = { Text(placeholder, color = TextHint, fontSize = 15.sp) },
        leadingIcon   = {
            Icon(
                leadingIcon,
                contentDescription = null,
                tint     = iconTint,
                modifier = Modifier.size(20.dp)
            )
        },
        singleLine = true,
        shape      = RoundedCornerShape(14.dp),
        colors     = OutlinedTextFieldDefaults.colors(
            focusedBorderColor     = Color.Transparent,
            unfocusedBorderColor   = Color.Transparent,
            focusedContainerColor   = FieldBg,
            unfocusedContainerColor = FieldBg,
            focusedTextColor       = TextPrimary,
            unfocusedTextColor     = TextPrimary
        )
    )
}

@Composable
private fun LandingTrustRow(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint     = TextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text     = label,
            color    = TextSecondary,
            fontSize = 14.sp
        )
    }
}
