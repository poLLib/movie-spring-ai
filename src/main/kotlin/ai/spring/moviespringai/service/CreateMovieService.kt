package ai.spring.moviespringai.service

import ai.spring.moviespringai.service.model.CreateMovieResponse
import ai.spring.moviespringai.service.model.QuestionRequest
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.chat.prompt.SystemPromptTemplate
import org.springframework.ai.converter.BeanOutputConverter
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class CreateMovieService(
        private val chatClientBuilder : ChatClient.Builder,
        private val vectorStore : VectorStore,
) {

    @Value("classpath:/templates/create-movie-rag-prompt-template.st")
    private val createMovieRagPromptTemplate : Resource? = null

    @Value("classpath:/templates/system-prompt-template.st")
    private val systemPromptTemplate : Resource? = null

    private val logger : KLogger = KotlinLogging.logger {}

    private val chatClient = chatClientBuilder.build()

    fun create(
            request : QuestionRequest,
    ) : CreateMovieResponse =
            try {
                logger.info { "Creating movie from request: ${request.question.take(100)}..." }
                extractMovieDataFromText(request.question)
                        .let { movieData ->
                            vectorStore.add(
                                    listOf(
                                            createMovieDocument(
                                                    request.question,
                                                    movieData
                                            )
                                    )
                            )
                            logger.info { "Movie '${movieData.name}' successfully added to vector store" }
                            movieData
                        }
            } catch (e : Exception) {
                logger.error(e) { "Error creating movie: ${e.message}" }
                CreateMovieResponse(
                        name = "Error",
                        description = "ErrorMessage: ${e.message}",
                        genres = listOf("Error"),
                        director = null
                )
            }

    private fun extractMovieDataFromText(text : String) : CreateMovieResponse =
            BeanOutputConverter(CreateMovieResponse::class.java)
                    .let { converter ->
                        logger.debug { "Extracting movie data from text using AI..." }
                        val systemMessage = SystemPromptTemplate(systemPromptTemplate).createMessage()
                        val userMessage = PromptTemplate(createMovieRagPromptTemplate).createMessage(
                                mapOf(
                                        "movieText" to text,
                                        "format" to converter.format
                                )
                        )

                        val prompt = Prompt(
                                listOf(
                                        systemMessage,
                                        userMessage
                                )
                        )
                        chatClient
                                .prompt(prompt)
                                .call()
                                .content()
                                .let { response ->
                                    converter.convert(response!!)
                                            ?: run {
                                                logger.warn { "Failed to parse AI response, creating fallback movie data" }
                                                CreateMovieResponse(
                                                        name = "Unknown Movie",
                                                        description = text,
                                                        genres = listOf("Unknown"),
                                                        director = null
                                                )
                                            }
                                }
                    }
}

private fun createMovieDocument(originalText : String, movieData : CreateMovieResponse) =
        Document(
                """
                Movie: ${movieData.name}
                Director: ${movieData.director ?: "Unknown"}
                Genres: ${movieData.genres.joinToString(", ")}
                Description: ${movieData.description ?: "No description"}
                
                Original input: $originalText
            """.trimIndent()
        )