package ai.spring.demospringai.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

@Configuration
@EnableConfigurationProperties(
        value = [
            VectorStoreProperties::class
        ]
)
class VectorStoreConfig

@ConfigurationProperties(prefix = "spring.vector-db")
data class VectorStoreProperties(
        val vectorStorePath : String,
        val documentsToLoad : List<Resource>,
)