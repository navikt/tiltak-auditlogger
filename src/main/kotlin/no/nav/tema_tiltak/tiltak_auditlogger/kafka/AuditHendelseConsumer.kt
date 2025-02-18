package no.nav.tema_tiltak.tiltak_auditlogger.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.benmanes.caffeine.cache.Cache
import no.nav.common.audit_log.cef.AuthorizationDecision
import no.nav.common.audit_log.cef.CefMessage
import no.nav.common.audit_log.cef.CefMessageEvent
import no.nav.common.audit_log.cef.CefMessageSeverity
import no.nav.common.audit_log.log.AuditLogger
import no.nav.common.audit_log.log.AuditLoggerImpl
import no.nav.tema_tiltak.tiltak_auditlogger.Audit
import no.nav.tema_tiltak.tiltak_auditlogger.utils.log
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Profile("kafka")
@Component
class AuditHendelseConsumer(
    private val mapper: ObjectMapper,
    private val auditEntryCache: Cache<Audit, Boolean>
) {
    val auditLogger: AuditLogger = AuditLoggerImpl()

    @KafkaListener(topics = [Topics.AUDIT_HENDELSE])
    fun meldingLytter(message: String) {
        try {
            val melding: AuditEntry = mapper.readValue(message)
            val cacheNøkkel = melding.cacheNøkkel()

            val skalLogges = cacheNøkkel?.let {
                auditEntryCache.contains(it)
            } ?: true

            if (skalLogges) {
                val cefMessage = CefMessage.builder()
                    .applicationName("Tiltaksgjennomforing")
                    .loggerName(melding.appNavn)
                    .event(cefEvent(melding.eventType))
                    .name("Sporingslogg")
                    .severity(CefMessageSeverity.INFO)
                    .authorizationDecision(if (melding.forespørselTillatt) AuthorizationDecision.PERMIT else AuthorizationDecision.DENY) // Bruk AuthorizationDecision.DENY hvis Nav-ansatt ikke fikk tilgang til å gjøre oppslag
                    .sourceUserId(melding.utførtAv)
                    .destinationUserId(melding.oppslagPå)
                    .timeEnded(melding.oppslagUtførtTid.toEpochMilli())
                    .extension("msg", melding.beskrivelse)
                    .extension("request", melding.requestUrl.toString())
                    .extension("requestMethod", melding.requestMethod)
                    .extension("dproc", melding.correlationId)
                    .build()

                if (cacheNøkkel != null) {
                    auditEntryCache.add(cacheNøkkel)
                }

                auditLogger.log(cefMessage)
            } else {
                log.info(
                    "Melding skulle ikke auditlogges fordi det er duplikat (oppslag av {} på {})",
                    melding.utførtAv,
                    melding.entitetId
                )
            }
        } catch (ex: Exception) {
            log.error("Kunne ikke logge audit-hendelse: {}", vaskFnr(ex.message))
        }
    }
}

private fun <K> Cache<K, Boolean>.contains(it: K) = this.getIfPresent(it) ?: false
private fun <K> Cache<K, Boolean>.add(it: K) = this.put(it, true)

private fun cefEvent(e: EventType) = when (e) {
    EventType.CREATE -> CefMessageEvent.CREATE
    EventType.READ -> CefMessageEvent.ACCESS
    EventType.UPDATE -> CefMessageEvent.UPDATE
    EventType.DELETE -> CefMessageEvent.DELETE
}

private val fnrStringRegex = Regex("\"(\\d{4})\\d{7}\"")
fun vaskFnr(message: String?) =
    message?.replace(fnrStringRegex) { transform: MatchResult ->
        "\"${transform.groups.get(1)?.value ?: "****"}*******\""
    } ?: ""
