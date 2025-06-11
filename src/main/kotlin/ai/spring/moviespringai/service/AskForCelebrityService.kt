package ai.spring.moviespringai.service

import ai.spring.moviespringai.service.model.AnswerResponse
import ai.spring.moviespringai.service.model.QuestionRequest
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

@Service
class AskForCelebrityService(
        private val chatClientBuilder : ChatClient.Builder,
        private val celebrityTool : CelebrityTool,
) {

    private val logger : KLogger = KotlinLogging.logger {}

    fun ask(
            request : QuestionRequest,
    ) : AnswerResponse = try {
        AnswerResponse(
                chatClientBuilder
                        .build()
                        .prompt(request.question)
                        .tools(celebrityTool)
                        .call()
                        .content()!!
        )
    } catch (e : Exception) {
        logger.error { "Error occurred when parsing response: ${e.message}" }
        AnswerResponse(answer = "")
    }
}