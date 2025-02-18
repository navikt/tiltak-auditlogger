package no.nav.tema_tiltak.tiltak_auditlogger

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit


@Configuration
class CacheConfig {

    @Bean
    fun auditEntryCache(): Cache<Audit, Boolean> = Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .maximumSize(1000)
        .build()
}

data class Audit(
    val utførtAv: String,
    val oppslagPåEntitet: String,
    val traceId: String
)
