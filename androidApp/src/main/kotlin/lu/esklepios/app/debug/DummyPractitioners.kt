package lu.esklepios.app.debug

import lu.esklepios.app.domain.model.AppointmentSlot
import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.domain.model.ScheduleEntry
import lu.esklepios.app.utils.DateUtil

/**
 * Dummy practitioner data for development / preview purposes.
 *
 * Today is 2026-05-24 (Sunday).
 * Next-week dates:
 *   Mon = 2026-05-25, Tue = 2026-05-26, Wed = 2026-05-27,
 *   Thu = 2026-05-28, Fri = 2026-05-29, Sat = 2026-05-30, Sun = 2026-05-31
 */
object DummyPractitioners {
    // ── Dr. Fabien Cipriani ───────────────────────────────────────────────────
    private val cipriani =
        Practitioner(
            id = "d1",
            firstName = "Fabien",
            lastName = "Cipriani",
            specialty = "General Practitioner",
            clinicName = "Al Esch Medical Center",
            address = "4 Rue de l'Alzette",
            city = "Esch-sur-Alzette",
            postalCode = "4067",
            phone = "(+352) 27 12 34 56",
            email = "contact@alesch.lu",
            latitude = 49.4955,
            longitude = 5.9818,
            acceptingNewPatients = true,
            availableSlots =
                listOf(
                    // Tuesday 2026-05-26
                    slot("d1", "20260526", "08:00"),
                    slot("d1", "20260526", "08:30"),
                    slot("d1", "20260526", "09:30"),
                    slot("d1", "20260526", "11:30"),
                    slot("d1", "20260526", "14:00"),
                    slot("d1", "20260526", "17:00"),
                    // Wednesday 2026-05-27
                    slot("d1", "20260527", "08:00"),
                    slot("d1", "20260527", "08:30"),
                    slot("d1", "20260527", "09:30"),
                    slot("d1", "20260527", "11:30"),
                    slot("d1", "20260527", "14:00"),
                    slot("d1", "20260527", "15:30"),
                    slot("d1", "20260527", "17:00"),
                    // Saturday 2026-05-30
                    slot("d1", "20260530", "08:00"),
                    slot("d1", "20260530", "08:30"),
                    slot("d1", "20260530", "09:30"),
                    slot("d1", "20260530", "11:30"),
                    slot("d1", "20260530", "14:00"),
                    slot("d1", "20260530", "15:30"),
                    slot("d1", "20260530", "17:00"),
                    slot("d1", "20260530", "18:00"),
                ),
            schedule =
                listOf(
                    ScheduleEntry("Lundi", "8-12h00 et 14-18h00"),
                    ScheduleEntry("Mercredi", "8-12h00"),
                    ScheduleEntry("Jeudi", "8-12h00 et 14-17h00"),
                    ScheduleEntry("Vendredi", "8-12h00 et 14-17h00"),
                ),
            paymentMethods = listOf("Espèces", "Carte bancaire", "PID"),
            diplomas =
                listOf(
                    "Médecin généraliste",
                    "DIU d'échographie générale",
                ),
            presentation = "",
            isFavorite = false,
        )

    // ── Dr. Marie Hoffmann ────────────────────────────────────────────────────
    private val hoffmann =
        Practitioner(
            id = "d2",
            firstName = "Marie",
            lastName = "Hoffmann",
            specialty = "Cardiologist",
            clinicName = "CHL Luxembourg",
            address = "4 Rue Barblé",
            city = "Luxembourg",
            postalCode = "1210",
            phone = "(+352) 27 12 34 56",
            email = "contact@chl.lu",
            latitude = 49.6116,
            longitude = 6.1319,
            acceptingNewPatients = false,
            availableSlots =
                listOf(
                    // Tuesday 2026-05-26
                    slot("d2", "20260526", "09:00"),
                    slot("d2", "20260526", "09:30"),
                    slot("d2", "20260526", "11:00"),
                    // Wednesday 2026-05-27
                    slot("d2", "20260527", "10:00"),
                    slot("d2", "20260527", "11:30"),
                    // Friday 2026-05-29
                    slot("d2", "20260529", "14:00"),
                    slot("d2", "20260529", "15:30"),
                    // Monday next-next week (2026-06-01)
                    slot("d2", "20260601", "09:00"),
                ),
            schedule =
                listOf(
                    ScheduleEntry("Lundi", "9-12h00"),
                    ScheduleEntry("Mardi", "9-12h00 et 14-17h00"),
                    ScheduleEntry("Jeudi", "14-17h00"),
                ),
            paymentMethods = listOf("Carte bancaire", "CNS", "Virement"),
            diplomas =
                listOf(
                    "Spécialiste en cardiologie",
                    "Fellow ESC",
                ),
            presentation = "",
            isFavorite = false,
        )

    // ── Dr. Pierre Lecomte ────────────────────────────────────────────────────
    private val lecomte =
        Practitioner(
            id = "d3",
            firstName = "Pierre",
            lastName = "Lecomte",
            specialty = "Dermatologist",
            clinicName = "Clinique Bohler",
            address = "5 Rue Edward Steichen",
            city = "Luxembourg",
            postalCode = "1537",
            phone = "(+352) 27 12 34 56",
            email = "contact@bohler.lu",
            latitude = 49.6200,
            longitude = 6.1450,
            acceptingNewPatients = true,
            availableSlots =
                listOf(
                    // Wednesday 2026-05-27
                    slot("d3", "20260527", "11:00"),
                    slot("d3", "20260527", "11:30"),
                    // Thursday 2026-05-28
                    slot("d3", "20260528", "09:00"),
                    // Friday 2026-05-29
                    slot("d3", "20260529", "10:00"),
                ),
            schedule =
                listOf(
                    ScheduleEntry("Mardi", "9-12h00"),
                    ScheduleEntry("Mercredi", "9-12h00 et 14-17h00"),
                    ScheduleEntry("Vendredi", "9-12h00"),
                ),
            paymentMethods = listOf("Espèces", "Carte bancaire"),
            diplomas =
                listOf(
                    "Dermatologue certifié",
                    "Laser & esthétique médicale",
                ),
            presentation = "",
            isFavorite = false,
        )

    // ── Dr. Olivia Wang ───────────────────────────────────────────────────────
    private val wang =
        Practitioner(
            id = "d4",
            firstName = "Olivia",
            lastName = "Wang",
            specialty = "Physiotherapist",
            clinicName = "Kinésio Esch",
            address = "12 Boulevard Prince Henri",
            city = "Esch-sur-Alzette",
            postalCode = "4020",
            phone = "(+352) 27 12 34 56",
            email = "contact@kinesio-esch.lu",
            latitude = 49.4970,
            longitude = 5.9850,
            acceptingNewPatients = true,
            availableSlots =
                listOf(
                    // Tuesday 2026-05-26
                    slot("d4", "20260526", "09:00"),
                    slot("d4", "20260526", "10:00"),
                    // Wednesday 2026-05-27
                    slot("d4", "20260527", "10:30"),
                    slot("d4", "20260527", "11:00"),
                    slot("d4", "20260527", "14:00"),
                    // Friday 2026-05-29
                    slot("d4", "20260529", "08:00"),
                    slot("d4", "20260529", "09:30"),
                    // Saturday 2026-05-30
                    slot("d4", "20260530", "09:30"),
                    // Monday 2026-06-01
                    slot("d4", "20260601", "10:00"),
                ),
            schedule =
                listOf(
                    ScheduleEntry("Lundi", "8-12h00 et 14-18h00"),
                    ScheduleEntry("Mercredi", "8-12h00"),
                    ScheduleEntry("Jeudi", "8-12h00 et 14-17h00"),
                    ScheduleEntry("Samedi", "9-12h00"),
                ),
            paymentMethods = listOf("Espèces", "Carte bancaire", "CNS"),
            diplomas =
                listOf(
                    "Kinésithérapeute diplômée d'État",
                    "Certificat en thérapie manuelle",
                ),
            presentation = "",
            isFavorite = false,
        )

    // ── Dr. Jean Dupont ───────────────────────────────────────────────────────
    private val dupont =
        Practitioner(
            id = "d5",
            firstName = "Jean",
            lastName = "Dupont",
            specialty = "Pediatrician",
            clinicName = "Maison Médicale Kayl",
            address = "3 Rue de la Mairie",
            city = "Kayl",
            postalCode = "3895",
            phone = "(+352) 27 12 34 56",
            email = "contact@mmkayl.lu",
            latitude = 49.4850,
            longitude = 6.0340,
            acceptingNewPatients = true,
            availableSlots =
                listOf(
                    // Tuesday 2026-05-26
                    slot("d5", "20260526", "10:00"),
                    slot("d5", "20260526", "11:30"),
                    // Thursday 2026-05-28
                    slot("d5", "20260528", "09:00"),
                    slot("d5", "20260528", "09:30"),
                    // Saturday 2026-05-30
                    slot("d5", "20260530", "11:00"),
                    // Monday 2026-06-01
                    slot("d5", "20260601", "10:30"),
                    slot("d5", "20260601", "14:00"),
                ),
            schedule =
                listOf(
                    ScheduleEntry("Lundi", "9-12h00"),
                    ScheduleEntry("Mardi", "9-12h00 et 14-17h00"),
                    ScheduleEntry("Mercredi", "9-12h00"),
                    ScheduleEntry("Vendredi", "14-17h00"),
                    ScheduleEntry("Samedi", "9-12h00"),
                ),
            paymentMethods = listOf("Espèces", "Carte bancaire", "PID", "CNS"),
            diplomas =
                listOf(
                    "Pédiatre certifié",
                    "DU Néonatologie",
                ),
            presentation = "",
            isFavorite = false,
        )

    val all: List<Practitioner> = listOf(cipriani, hoffmann, lecomte, wang, dupont)

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun slot(
        doctorId: String,
        compactDate: String,
        time: String,
    ): AppointmentSlot {
        val hhmm = time.replace(":", "")
        val isoDate = DateUtil.compactToIso(compactDate)
        return AppointmentSlot(
            id = "slot_${doctorId}_${compactDate}_$hhmm",
            practitionerId = doctorId,
            dateTime = "${isoDate}T$time",
            available = true,
            durationMinutes = 30,
        )
    }
}
