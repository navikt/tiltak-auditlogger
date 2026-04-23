package no.nav.team_tiltak.tiltak_auditlogger

import no.nav.team_tiltak.tiltak_auditlogger.domene.AuditEntry
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Profile("lokal")
@RestController
internal class AuditHendelseController(private val loggAuditMelding: LoggAuditMelding) {
   @PostMapping("melding")
    fun meldingLytter(@RequestBody melding: AuditEntry) {
        loggAuditMelding.loggAuditMelding(melding)
    }
}
