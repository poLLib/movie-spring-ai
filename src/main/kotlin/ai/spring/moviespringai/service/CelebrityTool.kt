package ai.spring.moviespringai.service

import ai.spring.moviespringai.config.NinjasApiConfig.NinjasApiProperties
import ai.spring.moviespringai.service.model.Answer
import ai.spring.moviespringai.service.model.GetCelebrityInfoResponse
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class CelebrityTool(
        private val ninjasApi : NinjasApiProperties,
        private val objectMapper : ObjectMapper,
        private val celebrityFavoriteList : MutableSet<String>, // TODO: should be persistent
) {

    private val logger : KLogger = KotlinLogging.logger {}

    @Tool(
            name = "GetCelebrityInfoTool",
            description = "Get the information about the celebrity"
    )
    fun getInfo(
            @Parameter(description = "Name of the celebrity")
            name : String,
    ) : List<GetCelebrityInfoResponse> =
            RestClient
                    .builder()
                    .baseUrl("https://api.api-ninjas.com/v1/celebrity")
                    .defaultHeaders { headers ->
                        headers.set(
                                "X-Api-Key",
                                ninjasApi.apiNinjasKey
                        )
                        headers.set(
                                "Accept",
                                "application/json"
                        )
                        headers.set(
                                "Content-Type",
                                "application/json"
                        )
                    }
                    .build()
                    .let { client ->
                        client
                                .get()
                                .uri { uriBuilder ->
                                    uriBuilder
                                            .queryParam(
                                                    "name",
                                                    name
                                            )
                                            .build()
                                            .also { logger.debug { "Full request URI: $it" } }
                                }
                                .also {
                                    logger.debug { "Requesting celebrity data for: $name" }
                                }
                                .retrieve()
                                .body(JsonNode::class.java)
                                ?.let { jsonNode ->
                                    logger.info { "API response: $jsonNode" }
                                    takeIf { !jsonNode.isEmpty }
                                            ?.let {
                                                objectMapper.convertValue(
                                                        jsonNode,
                                                        object : TypeReference<List<GetCelebrityInfoResponse>>() {}
                                                )
                                            }
                                }
                                ?: emptyList()
                    }

    @Tool(
            name = "AddCelebrityFavoriteList",
            description = "Add a celebrity to your personal favorite list"
    )
    fun addToFavoriteList(
            @Parameter(description = "Name of the celebrity to add to favorite list")
            celebrityName : String,
            @Parameter(description = "Personal rating of the celebrity")
            rating : Double? = null,
    ) : Answer = try {
        logger.info { "Adding $celebrityName to favorite list with reason: $rating" }
        celebrityFavoriteList.add(
                celebrityName
                        .lowercase()
                        .trim()
        )
        Answer(
                answer = "Celebrity $celebrityName was added to your favorite list"
        )
    } catch (e : Exception) {
        logger.error(e) { "Failed to add $celebrityName to favorite list" }
        Answer(
                answer = "Unfortunately, an error occurred while adding the celebrity into the list"
        )
    }

    @Tool(
            name = "GetCelebrityFavoriteList",
            description = "Get your personal favorite list"
    )
    fun getFavoriteList() : Answer = try {
        logger.info { "Retrieving favorite celebrity list" }
        takeIf { !celebrityFavoriteList.isEmpty() }
                ?.let { Answer(answer = "Your favorite celebrities: ${celebrityFavoriteList.joinToString(", ")}") }
                ?: Answer(answer = "No favorite celebrities")
    } catch (e : Exception) {
        logger.error(e) { "Failed to retrieve favorite celebrity list" }
        Answer(answer = "Unfortunately, an error occurred while retrieving your favorite list")
    }
}