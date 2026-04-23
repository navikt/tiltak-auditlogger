package no.nav.team_tiltak.tiltak_auditlogger

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LokalAuditloggerApplication

fun main(args: Array<String>) {
    runApplication<LokalAuditloggerApplication>(*args) {
        setAdditionalProfiles("lokal")
    }
}
