package ai.spring.demospringai.service

import ai.spring.demospringai.service.model.GetMovieRequest
import ai.spring.demospringai.service.model.GetMovieResponse
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
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class GetMovieService(
        chatModel : ChatModel,
        private val vectorStore : VectorStore,
) {

    @Value("classpath:/templates/rag-prompt-template.st")
    private val ragPromptTemplate : Resource? = null

    @Value("classpath:/templates/system-prompt-template.st")
    private val systemPromptTemplate : Resource? = null

    private val chatClient = ChatClient.builder(chatModel).build()

    fun get(
            request : GetMovieRequest,
    ) : List<GetMovieResponse> {
        val converter = BeanOutputConverter(GetMovieResponse::class.java)
        val format = converter.format
        val documents = vectorStore.similaritySearch(
                SearchRequest
                        .builder()
                        .query(request.movieQuestion)
                        .topK(10)
                        .similarityThreshold(0.8)
                        .build()
        )
        val contentList = documents!!
                .stream()
                .map(Document::getFormattedContent)
                .toList()

        val systemMessage = SystemPromptTemplate(systemPromptTemplate).createMessage()
        val userMessage = PromptTemplate(ragPromptTemplate).createMessage(
                mapOf(
                        "input" to request.movieQuestion,
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
        return listOfNotNull(
                chatClient
                        .prompt(prompt)
                        .call()
                        .entity(GetMovieResponse::class.java)
        )
    }
}
