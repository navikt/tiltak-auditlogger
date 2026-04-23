package no.nav.team_tiltak.tiltak_auditlogger.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.team_tiltak.tiltak_auditlogger.LoggAuditMelding
import no.nav.team_tiltak.tiltak_auditlogger.domene.AuditEntry
import no.nav.team_tiltak.tiltak_auditlogger.utils.log
import no.nav.team_tiltak.tiltak_auditlogger.vaskFnr
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Profile("kafka")
@Component
internal class AuditHendelseConsumer(private val mapper: ObjectMapper, private val loggAuditMelding: LoggAuditMelding) {

    @KafkaListener(topics = [Topics.AUDIT_HENDELSE])
    fun meldingLytter(message: String) {
        try {
            val melding: AuditEntry = mapper.readValue(message)

            loggAuditMelding.loggAuditMelding(melding)
        } catch (ex: Exception) {
            log.error("Behandling av kafkamelding feilet {}", vaskFnr(ex.message))
        }
    }
}
