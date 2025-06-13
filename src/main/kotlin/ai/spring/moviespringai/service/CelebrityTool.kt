package ai.spring.moviespringai.service

import ai.spring.moviespringai.config.NinjasApiConfig.NinjasApiProperties
import ai.spring.moviespringai.service.model.AnswerResponse
import ai.spring.moviespringai.service.model.GetCelebrityInfoResponse
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import javax.annotation.Nullable

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
            @ToolParam(description = "Name of the celebrity")
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
            @ToolParam(description = "Name of the celebrity to add to favorite list")
            celebrityName : String,
            @Nullable
            @ToolParam(
                    description = "Personal rating of the celebrity",
                    required = false
            )
            rating : Double? = null,
    ) : AnswerResponse = try {
        logger.info { "Adding $celebrityName to favorite list with reason: $rating" }
        celebrityFavoriteList.add(
                celebrityName
                        .lowercase()
                        .trim()
        )
        AnswerResponse(
                answer = "Celebrity $celebrityName was added to your favorite list"
        )
    } catch (e : Exception) {
        logger.error(e) { "Failed to add $celebrityName to favorite list" }
        AnswerResponse(
                answer = "Unfortunately, an error occurred while adding the celebrity into the list"
        )
    }

    @Tool(
            name = "GetCelebrityFavoriteList",
            description = "Get your personal favorite list"
    )
    fun getFavoriteList() : AnswerResponse = try {
        logger.info { "Retrieving favorite celebrity list" }
        takeIf { !celebrityFavoriteList.isEmpty() }
                ?.let { AnswerResponse(answer = "Your favorite celebrities: ${celebrityFavoriteList.joinToString(", ")}") }
                ?: AnswerResponse(answer = "No favorite celebrities")
    } catch (e : Exception) {
        logger.error(e) { "Failed to retrieve favorite celebrity list" }
        AnswerResponse(answer = "Unfortunately, an error occurred while retrieving your favorite list")
    }
}