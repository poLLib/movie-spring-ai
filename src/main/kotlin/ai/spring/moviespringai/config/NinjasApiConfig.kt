package ai.spring.moviespringai.config

import ai.spring.moviespringai.config.NinjasApiConfig.NinjasApiProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
        value = [
            NinjasApiProperties::class
        ]
)
class NinjasApiConfig {

    @ConfigurationProperties(prefix = "spring.ninjas-api")
    data class NinjasApiProperties(
            val apiNinjasKey : String,
    )
}