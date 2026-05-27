package lu.esklepios.app.view.dashboard.home.practitionerData

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.components.AppBodyText
import lu.esklepios.app.core.ui.components.AppCaptionText
import lu.esklepios.app.core.ui.components.AppIcon
import lu.esklepios.app.core.ui.components.AppIconButton
import lu.esklepios.app.core.ui.components.AppSubtitleText
import lu.esklepios.app.core.ui.components.AvatarCircle
import lu.esklepios.app.core.ui.components.GradientHeader
import lu.esklepios.app.core.ui.sharedElementModifier
import lu.esklepios.app.core.ui.theme.Background
import lu.esklepios.app.core.ui.theme.BorderColor
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.PrimaryLight
import lu.esklepios.app.core.ui.theme.PrimaryMid
import lu.esklepios.app.core.ui.theme.Surface
import lu.esklepios.app.core.ui.theme.TextHint
import lu.esklepios.app.core.ui.theme.TextPrimary
import lu.esklepios.app.core.ui.theme.TextSecondary
import lu.esklepios.app.debug.DummyPractitioners
import lu.esklepios.app.util.AppUrls

@Composable
fun PractitionerDetailScreen(
    navController: NavController,
    practitionerId: String,
) {
    val doctor = DummyPractitioners.all.find { it.id == practitionerId }
    val context = LocalContext.current

    if (doctor == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Background),
            contentAlignment = Alignment.Center,
        ) {
            AppBodyText(
                text = stringResource(R.string.detail_not_found),
                color = TextSecondary,
            )
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GradientHeader(
            roundedBottom = false,
            topPadding = Dimens.paddingNone,
            bottomPadding = Dimens.paddingNone,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    onClick = { navController.popBackStack() },
                    tint = Color.White,
                )
                Spacer(Modifier.weight(1f))
            }
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = Dimens.paddingL, end = Dimens.paddingL, bottom = Dimens.paddingL),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AvatarCircle(
                    initials = doctor.initials,
                    size = Dimens.detailAvatarSize,
                    modifier = sharedElementModifier("practitioner-avatar-$practitionerId"),
                )
                Spacer(Modifier.width(Dimens.paddingPlus))
                Column {
                    AppSubtitleText(
                        text = doctor.fullName,
                        color = Color.White,
                        modifier = sharedElementModifier("practitioner-name-$practitionerId"),
                    )
                    AppCaptionText(
                        text = doctor.specialty,
                        color = Color.White.copy(alpha = 0.75f),
                    )
                }
            }
        }

        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Background),
            contentPadding = PaddingValues(top = Dimens.paddingM),
        ) {
            // ── Card 1 — Contact details ─────────────────────────────────────
            item {
                DetailCard {
                    AppSubtitleText(
                        text = stringResource(R.string.detail_contact_details),
                        color = TextPrimary,
                        modifier =
                            Modifier.padding(
                                top = Dimens.paddingL,
                                start = Dimens.paddingL,
                                end = Dimens.paddingL,
                            ),
                    )
                    Row(
                        modifier =
                            Modifier.padding(
                                start = Dimens.paddingL,
                                end = Dimens.paddingL,
                                top = Dimens.paddingS,
                                bottom = Dimens.paddingL,
                            ),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            AppCaptionText(
                                text = stringResource(R.string.label_clinic),
                                color = TextPrimary,
                            )
                            Spacer(Modifier.height(Dimens.paddingXS))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AppIcon(
                                    imageVector = Icons.Filled.MedicalServices,
                                    // a11y: decorative — labelled by adjacent Text
                                    contentDescription = null,
                                    tint = PrimaryMid,
                                    size = Dimens.iconSizeMicro,
                                )
                                Spacer(Modifier.width(Dimens.paddingTiny))
                                AppCaptionText(text = doctor.clinicName, color = TextSecondary)
                            }
                            Spacer(Modifier.height(Dimens.paddingM))
                            AppCaptionText(
                                text = stringResource(R.string.detail_address),
                                color = TextPrimary,
                            )
                            Spacer(Modifier.height(Dimens.paddingXS))
                            Row(verticalAlignment = Alignment.Top) {
                                AppIcon(
                                    imageVector = Icons.Filled.LocationOn,
                                    // a11y: decorative — labelled by adjacent Text
                                    contentDescription = null,
                                    tint = PrimaryMid,
                                    size = Dimens.iconSizeMicro,
                                    modifier = Modifier.padding(top = Dimens.paddingXXS),
                                )
                                Spacer(Modifier.width(Dimens.paddingTiny))
                                AppCaptionText(
                                    text = "${doctor.address}, ${doctor.city}, ${doctor.postalCode}",
                                    color = TextSecondary,
                                )
                            }
                            Spacer(Modifier.height(Dimens.paddingM))
                            AppCaptionText(
                                text = stringResource(R.string.detail_contact),
                                color = TextPrimary,
                            )
                            Spacer(Modifier.height(Dimens.paddingXS))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier =
                                    Modifier.clickable {
                                        context.startActivity(
                                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:${doctor.phone}")),
                                        )
                                    },
                            ) {
                                AppIcon(
                                    imageVector = Icons.Filled.Phone,
                                    // a11y: decorative — labelled by adjacent Text
                                    contentDescription = null,
                                    tint = PrimaryMid,
                                    size = Dimens.iconSizeMicro,
                                )
                                Spacer(Modifier.width(Dimens.paddingTiny))
                                AppCaptionText(text = doctor.phone, color = Primary)
                            }
                            Spacer(Modifier.height(Dimens.paddingS))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier =
                                    Modifier.clickable {
                                        context.startActivity(
                                            Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("mailto:${doctor.email}")
                                            },
                                        )
                                    },
                            ) {
                                AppIcon(
                                    imageVector = Icons.Filled.Email,
                                    // a11y: decorative — labelled by adjacent Text
                                    contentDescription = null,
                                    tint = PrimaryMid,
                                    size = Dimens.iconSizeMicro,
                                )
                                Spacer(Modifier.width(Dimens.paddingTiny))
                                AppCaptionText(text = doctor.email, color = Primary)
                            }
                        }

                        Spacer(Modifier.width(Dimens.paddingM))

                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(Dimens.detailMapHeight)
                                        .clip(RoundedCornerShape(Dimens.radiusSm))
                                        .background(PrimaryLight),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    AppIcon(
                                        imageVector = Icons.Filled.LocationOn,
                                        contentDescription = stringResource(R.string.cd_map_placeholder),
                                        tint = PrimaryMid,
                                        size = Dimens.iconSizeLg,
                                    )
                                    Spacer(Modifier.height(Dimens.paddingTiny))
                                    AppCaptionText(
                                        text = doctor.city,
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Card 2 — Schedules ───────────────────────────────────────────
            item {
                DetailCard {
                    AppCaptionText(
                        text = stringResource(R.string.detail_schedules_header),
                        color = TextPrimary,
                        modifier =
                            Modifier.padding(
                                top = Dimens.paddingL,
                                start = Dimens.paddingL,
                                end = Dimens.paddingL,
                            ),
                    )
                    Row(
                        modifier =
                            Modifier.padding(
                                start = Dimens.paddingL,
                                end = Dimens.paddingL,
                                top = Dimens.paddingS,
                                bottom = Dimens.paddingL,
                            ),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            if (doctor.schedule.isEmpty()) {
                                AppCaptionText(
                                    text = stringResource(R.string.detail_no_schedule),
                                    color = TextHint,
                                )
                            } else {
                                doctor.schedule.forEach { entry ->
                                    Row(modifier = Modifier.padding(bottom = Dimens.paddingXS)) {
                                        AppCaptionText(
                                            text = entry.day,
                                            color = TextSecondary,
                                            modifier = Modifier.width(Dimens.scheduleDayLabelWidth),
                                        )
                                        Spacer(Modifier.width(Dimens.paddingXS))
                                        AppCaptionText(
                                            text = entry.hours,
                                            color = TextPrimary,
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.width(Dimens.paddingL))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.Top) {
                                AppIcon(
                                    imageVector = Icons.Filled.Phone,
                                    // a11y: decorative — labelled by adjacent Text
                                    contentDescription = null,
                                    tint = TextPrimary,
                                    size = Dimens.iconSizeMicro,
                                    modifier = Modifier.padding(top = Dimens.paddingXXS),
                                )
                                Spacer(Modifier.width(Dimens.paddingXS))
                                // UI-14 exemption: buildAnnotatedString requires raw `Text(annotated)` —
                                // annotated strings cannot be wrapped in AppText components.
                                Text(
                                    text =
                                        buildAnnotatedString {
                                            append(stringResource(R.string.detail_emergency_prefix))
                                            append(" ")
                                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append(stringResource(R.string.detail_emergency_number))
                                            }
                                        },
                                    fontSize = Dimens.fontSizeXxs,
                                    color = TextPrimary,
                                )
                            }
                            Spacer(Modifier.height(Dimens.paddingTiny))
                            AppCaptionText(
                                text = stringResource(R.string.detail_emergency_subtitle),
                                color = Primary,
                                modifier =
                                    Modifier.clickable {
                                        context.startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(AppUrls.HEALTH_PORTAL),
                                            ),
                                        )
                                    },
                            )
                        }
                    }
                }
            }

            // ── Card 3 — Payments ────────────────────────────────────────────
            item {
                DetailCard {
                    AppSubtitleText(
                        text = stringResource(R.string.detail_payments),
                        color = TextPrimary,
                        modifier =
                            Modifier.padding(
                                top = Dimens.paddingL,
                                start = Dimens.paddingL,
                                end = Dimens.paddingL,
                            ),
                    )
                    AppCaptionText(
                        text = stringResource(R.string.detail_means_of_payment),
                        color = TextPrimary,
                        modifier =
                            Modifier.padding(
                                top = Dimens.paddingS,
                                start = Dimens.paddingL,
                                end = Dimens.paddingL,
                            ),
                    )
                    if (doctor.paymentMethods.isEmpty()) {
                        AppBodyText(
                            text = stringResource(R.string.detail_no_payment_info),
                            color = TextHint,
                            modifier = Modifier.padding(Dimens.paddingL),
                        )
                    } else {
                        Column(
                            modifier =
                                Modifier.padding(
                                    start = Dimens.paddingL,
                                    end = Dimens.paddingL,
                                    top = Dimens.paddingS,
                                    bottom = Dimens.paddingL,
                                ),
                        ) {
                            doctor.paymentMethods.forEach { method ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = Dimens.paddingTiny),
                                ) {
                                    Box(
                                        modifier =
                                            Modifier
                                                .size(Dimens.paddingTiny)
                                                .background(TextSecondary, CircleShape),
                                    )
                                    Spacer(Modifier.width(Dimens.paddingS))
                                    AppCaptionText(
                                        text = method,
                                        color = TextSecondary,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Card 4 — Presentation ────────────────────────────────────────
            item {
                DetailCard {
                    AppSubtitleText(
                        text = stringResource(R.string.detail_presentation),
                        color = TextPrimary,
                        modifier =
                            Modifier.padding(
                                top = Dimens.paddingL,
                                start = Dimens.paddingL,
                                end = Dimens.paddingL,
                            ),
                    )
                    if (doctor.diplomas.isEmpty() && doctor.presentation.isBlank()) {
                        AppBodyText(
                            text = stringResource(R.string.detail_no_presentation),
                            color = TextHint,
                            modifier = Modifier.padding(Dimens.paddingL),
                        )
                    } else {
                        if (doctor.diplomas.isNotEmpty()) {
                            AppCaptionText(
                                text = stringResource(R.string.detail_diplomas_label),
                                color = TextPrimary,
                                modifier =
                                    Modifier.padding(
                                        horizontal = Dimens.paddingL,
                                        vertical = Dimens.paddingS,
                                    ),
                            )
                            Column(modifier = Modifier.padding(horizontal = Dimens.paddingL)) {
                                doctor.diplomas.forEach { diploma ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = Dimens.paddingTiny),
                                    ) {
                                        Box(
                                            modifier =
                                                Modifier
                                                    .size(Dimens.paddingTiny)
                                                    .background(TextSecondary, CircleShape),
                                        )
                                        Spacer(Modifier.width(Dimens.paddingS))
                                        AppCaptionText(
                                            text = diploma,
                                            color = TextSecondary,
                                        )
                                    }
                                }
                            }
                        }
                        if (doctor.presentation.isNotBlank()) {
                            Spacer(Modifier.height(Dimens.paddingS))
                            AppCaptionText(
                                text = doctor.presentation,
                                color = TextSecondary,
                                modifier =
                                    Modifier.padding(
                                        start = Dimens.paddingL,
                                        end = Dimens.paddingL,
                                        bottom = Dimens.paddingL,
                                    ),
                            )
                        } else {
                            Spacer(Modifier.height(Dimens.paddingM))
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(Dimens.paddingXXL)) }
        }
    }
}

@Composable
private fun DetailCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(Dimens.radiusCard),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(Dimens.borderHairline, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.paddingM)
                .padding(bottom = Dimens.paddingM),
    ) {
        Column(content = content)
    }
}
