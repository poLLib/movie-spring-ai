package ai.spring.moviespringai.service

import ai.spring.moviespringai.service.model.AskForMoviesResponse
import ai.spring.moviespringai.service.model.QuestionRequest
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.chat.prompt.SystemPromptTemplate
import org.springframework.ai.converter.BeanOutputConverter
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class AskForMoviesService(
        private val chatClientBuilder : ChatClient.Builder,
        private val vectorStore : VectorStore,
) {

    @Value("classpath:/templates/ask-for-movie-rag-prompt-template.st")
    private val askForMoviesRagPromptTemplate : Resource? = null

    @Value("classpath:/templates/system-prompt-template.st")
    private val systemPromptTemplate : Resource? = null

    private val logger : KLogger = KotlinLogging.logger {}

    private val chatClient = chatClientBuilder.build()

    fun ask(
            request : QuestionRequest,
    ) : List<AskForMoviesResponse>? =
            BeanOutputConverter(object : ParameterizedTypeReference<List<AskForMoviesResponse>>() {})
                    .let { converter ->
                        vectorStore
                                .similaritySearch(
                                        SearchRequest
                                                .builder()
                                                .query(request.question)
                                                .topK(15)
                                                .build()
                                )
                                .let { documents ->
                                    documents!!
                                            .stream()
                                            .map(Document::getFormattedContent)
                                            .toList()
                                            .also {
                                                logDocuments(
                                                        request = request,
                                                        documents = documents,
                                                        contentList = it
                                                )
                                            }

                                }
                                .let { contentList ->
                                    val systemMessage = SystemPromptTemplate(systemPromptTemplate).createMessage()
                                    val userMessage = PromptTemplate(askForMoviesRagPromptTemplate).createMessage(
                                            mapOf(
                                                    "question" to request.question,
                                                    "documents" to contentList.joinToString(separator = "\n"),
                                                    "format" to converter.format,
                                            )
                                    )
                                    val prompt = Prompt(
                                            listOf(
                                                    systemMessage,
                                                    userMessage,
                                            )
                                    )

                                    try {
                                        converter.convert(
                                                chatClient
                                                        .prompt(prompt)
                                                        .call()
                                                        .content()!!
                                        )
                                    } catch (e : Exception) {
                                        logger.error { "Error occurred when parsing response: ${e.message}" }
                                        emptyList()
                                    }
                                }
                    }

    private fun logDocuments(
            request : QuestionRequest,
            documents : List<Document>,
            contentList : List<String>?,
    ) {
        logger.info { "=== DOCUMENTS FOUND ===" }
        logger.info { "Query: ${request.question}" }
        logger.info { "Documents count: ${documents.size}" }
        contentList
                ?.forEach { content ->
                    logger.debug { "Document content: ${content.take(500)}" }
                }
    }
}