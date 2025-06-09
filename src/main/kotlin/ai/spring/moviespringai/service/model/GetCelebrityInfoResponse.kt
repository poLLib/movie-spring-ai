package ai.spring.moviespringai.service.model

import com.fasterxml.jackson.annotation.JsonProperty

data class GetCelebrityInfoResponse(
        @JsonProperty("name")
        val name : String,

        @JsonProperty("net_worth")
        val netWorth : Long?,

        @JsonProperty("gender")
        val gender : String?,

        @JsonProperty("nationality")
        val nationality : String?,

        @JsonProperty("occupation")
        val occupation : List<String>?,

        @JsonProperty("height")
        val height : Double?,

        @JsonProperty("birthday")
        val birthday : String?,

        @JsonProperty("age")
        val age : Int?,
)
