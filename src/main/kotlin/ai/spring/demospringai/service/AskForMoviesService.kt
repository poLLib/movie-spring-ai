package ai.spring.demospringai.service

import ai.spring.demospringai.service.model.AskForMoviesRequest
import ai.spring.demospringai.service.model.AskForMoviesResponse
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
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
        chatModel : ChatModel,
        private val vectorStore : VectorStore,
) {

    @Value("classpath:/templates/rag-prompt-template.st")
    private val ragPromptTemplate : Resource? = null

    @Value("classpath:/templates/system-prompt-template.st")
    private val systemPromptTemplate : Resource? = null

    private val logger : KLogger = KotlinLogging.logger {}

    private val chatClient = ChatClient
            .builder(chatModel)
            .build()

    fun ask(
            request : AskForMoviesRequest,
    ) : List<AskForMoviesResponse>? {
        val converter = BeanOutputConverter(AskForMoviesResponse::class.java)
        val format = converter.format
        val documents = vectorStore.similaritySearch(
                SearchRequest
                        .builder()
                        .query(request.movieQuestion)
                        .topK(getTopK(request))
                        .similarityThreshold(0.6)
                        .build()
        )

        val contentList = documents!!
                .stream()
                .map(Document::getFormattedContent)
                .toList()

        logger.info { "=== DOCUMENTS FOUND ===" }
        logger.info { "Query: ${request.movieQuestion}" }
        logger.info { "Documents count: ${documents.size}" }
        contentList.forEach { content ->
            logger.info { "Document content: ${content.take(500)}" }
        }

        val systemMessage = SystemPromptTemplate(systemPromptTemplate).createMessage()
        val userMessage = PromptTemplate(ragPromptTemplate).createMessage(
                mapOf(
                        "movieQuestion" to request.movieQuestion,
                        "documents" to contentList.joinToString { "\n" },
                        "format" to format,
                )
        )
        val prompt = Prompt(
                listOf(
                        systemMessage,
                        userMessage,
                )
        )
        return try {
            chatClient
                    .prompt(prompt)
                    .call()
                    .entity(object : ParameterizedTypeReference<List<AskForMoviesResponse>>() {})
        } catch (e : Exception) {
            listOfNotNull(
                    chatClient
                            .prompt(prompt)
                            .call()
                            .entity(AskForMoviesResponse::class.java)
            )
        }
    }

    private fun getTopK(
            request : AskForMoviesRequest,
    ) : Int = if (listOf(
                    "all",
                    "movies",
                    "films",
                    "every"
            ).any {
                request.movieQuestion.contains(
                        it,
                        ignoreCase = true
                )
            }) {
        100
    } else {
        10
    }
}
