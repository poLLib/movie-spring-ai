package ai.spring.demospringai.config

import ai.spring.demospringai.config.VectorStoreConfig.VectorStoreProperties
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

@Configuration
@EnableConfigurationProperties(
        value = [
            VectorStoreProperties::class
        ]
)
class VectorStoreConfig {

    private val logger : KLogger = KotlinLogging.logger {}

    @Bean
    fun embeddingModel() : EmbeddingModel =
            OpenAiEmbeddingModel(
                    OpenAiApi
                            .builder()
                            .apiKey(System.getenv("OPENAI_API_KEY"))
                            .build()
            )

    @ConfigurationProperties(prefix = "spring.vector-db")
    data class VectorStoreProperties(
            val vectorStorePath : String,
            val documentsToLoad : List<Resource>,
    )
}