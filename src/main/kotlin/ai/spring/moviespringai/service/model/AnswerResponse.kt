package ai.spring.moviespringai.service.model

import com.fasterxml.jackson.annotation.JsonClassDescription

@JsonClassDescription("Response text from for the user")
data class AnswerResponse(
        val answer: String,
)
