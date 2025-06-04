package ai.spring.demospringai.service.model

import com.fasterxml.jackson.annotation.JsonPropertyDescription
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(
        "name",
        "description",
        "genres",
        "director",
        "released",
        "rating"
)
data class AskForMoviesResponse(
        @JsonPropertyDescription("The name of the movie")
        val name : String,

        @JsonPropertyDescription("The description of the movie")
        val description : String,

        @JsonPropertyDescription("The genres of the movie")
        val genres : List<String>,

        @JsonPropertyDescription("The director of the movie")
        val director : String,

        @JsonPropertyDescription("The released of the movie")
        val released : String,

        @JsonPropertyDescription("The rating of the movie")
        val rating : Double,
)