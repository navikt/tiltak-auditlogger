package no.nav.tema_tiltak.tiltak_auditlogger.kafka

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.tema_tiltak.tiltak_auditlogger.AppConfig
import org.junit.jupiter.api.Test
import java.net.URI
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class JsonDeserialiseringTest {
    val mapper = AppConfig().mapper()

    @Test
    fun `test vellykket deserialsering av kafkamelding med timestamp`() {
        val jsonMelding = """
            {"appNavn":"tiltak-refusjon-api",
             "utførtAv":"Z992785",
             "oppslagPå":"24070178547",
             "eventType":"READ",
             "forespørselTillatt":true,
             "oppslagUtførtTid":1693222027.076186081,
             "beskrivelse":"Oppslag på korreksjoner",
             "requestUrl":"/api/saksbehandler/korreksjon/01H8XVHYJWKBX71R87VDT80GGQ",
             "requestMethod":"GET",
             "correlationId":"b9594bd4-58cb-4ec0-99ec-a261261b86d8"
            }
        """
        val auditEntry = AuditEntry(
            "tiltak-refusjon-api",
            "Z992785",
            "24070178547",
            EventType.READ,
            true,
            Instant.ofEpochSecond(1693222027, 76186081),
            "Oppslag på korreksjoner",
            URI.create("/api/saksbehandler/korreksjon/01H8XVHYJWKBX71R87VDT80GGQ"),
            "GET",
            "b9594bd4-58cb-4ec0-99ec-a261261b86d8",
            null
        )

        assertEquals(auditEntry, mapper.readValue<AuditEntry>(jsonMelding))
    }

    @Test
    fun `test vellykket deserialsering av kafkamelding med datostring`() {
        val jsonMelding = """
            {"appNavn":"tiltak-refusjon-api",
             "utførtAv":"Z992785",
             "oppslagPå":"24070178547",
             "eventType":"READ",
             "forespørselTillatt":true,
             "oppslagUtførtTid":"2023-08-28T10:21:29.865897389Z",
             "beskrivelse":"Oppslag på korreksjoner",
             "requestUrl":"/api/saksbehandler/korreksjon/01H8XVHYJWKBX71R87VDT80GGQ",
             "requestMethod":"GET",
             "correlationId":"b9594bd4-58cb-4ec0-99ec-a261261b86d8"
            }
        """
        val auditEntry = AuditEntry(
            "tiltak-refusjon-api",
            "Z992785",
            "24070178547",
            EventType.READ,
            true,
            // "2023-08-28T10:21:29.865897389Z"
            ZonedDateTime.of(2023, 8, 28, 10, 21, 29, 865897389, ZoneOffset.UTC).toInstant(),
            "Oppslag på korreksjoner",
            URI.create("/api/saksbehandler/korreksjon/01H8XVHYJWKBX71R87VDT80GGQ"),
            "GET",
            "b9594bd4-58cb-4ec0-99ec-a261261b86d8",
            null
        )

        assertEquals(auditEntry, mapper.readValue<AuditEntry>(jsonMelding))
    }

    @Test
    fun `test vask av fnr i feilmeldinger`() {
        val testFnr = "16120101181"
        try {
            mapper.readValue<AuditEntry>(
                """ {"deltakerFnr": "${testFnr}"} """
            )
            assertFalse("Deserialisering burde feile") { true }
        } catch (e: Exception) {
            assertFalse("Vasket feilmelding burde ikke inneholde fnr") { e.message!!.contains(testFnr) }
        }
    }
}
