package no.nav.tema_tiltak.tiltak_auditlogger

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import kotlin.system.exitProcess

@SpringBootApplication
@EnableKafka
class AuditloggerApplication

fun main(args: Array<String>) {
    runApplication<AuditloggerApplication>(*args) {
        if (System.getenv("MILJO") == null) {
            println("Kan ikke startes uten miljøvariabel MILJO. Lokalt kan LocalApplication kjøres.")
            exitProcess(1)
        }
        setAdditionalProfiles(System.getenv("MILJO"), "kafka")
    }
}
