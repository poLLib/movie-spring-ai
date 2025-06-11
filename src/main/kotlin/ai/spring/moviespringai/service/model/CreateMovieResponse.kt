package ai.spring.moviespringai.service.model

import com.fasterxml.jackson.annotation.JsonClassDescription
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(
        "name",
        "description",
        "genres",
        "director",
)
@JsonClassDescription("Response of movie creation with its informations")
data class CreateMovieResponse(
        @JsonPropertyDescription("The name of the movie")
        val name : String,

        @JsonPropertyDescription("The description of the movie")
        val description : String?,

        @JsonPropertyDescription("The genres of the movie")
        val genres : List<String>,

        @JsonPropertyDescription("The director of the movie")
        val director : String?,
)