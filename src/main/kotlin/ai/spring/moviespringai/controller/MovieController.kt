package ai.spring.moviespringai.controller

import ai.spring.moviespringai.service.AskForCelebrityService
import ai.spring.moviespringai.service.AskForMoviesService
import ai.spring.moviespringai.service.CreateMovieService
import ai.spring.moviespringai.service.model.AnswerResponse
import ai.spring.moviespringai.service.model.AskForMoviesResponse
import ai.spring.moviespringai.service.model.CreateMovieResponse
import ai.spring.moviespringai.service.model.QuestionRequest
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MovieController(
        private val askForMoviesService : AskForMoviesService,
        private val createMovieService : CreateMovieService,
        private val askForCelebrityService : AskForCelebrityService,
) {

    @PostMapping(
            value = ["/ask-for-movies"],
            consumes = [APPLICATION_JSON_VALUE],
            produces = [APPLICATION_JSON_VALUE]
    )
    fun askForMovies(
            @RequestBody request : QuestionRequest,
    ) : ResponseEntity<List<AskForMoviesResponse>> = ResponseEntity.ok(askForMoviesService.ask(request))

    @PostMapping(
            value = ["/movie"],
            consumes = [APPLICATION_JSON_VALUE],
            produces = [APPLICATION_JSON_VALUE]
    )
    fun createMovie(
            @RequestBody request : QuestionRequest,
    ) : ResponseEntity<CreateMovieResponse> = ResponseEntity.ok(createMovieService.create(request))

    @PostMapping(
            value = ["/celebrity"],
            consumes = [APPLICATION_JSON_VALUE],
            produces = [APPLICATION_JSON_VALUE]
    )
    fun askForCelebrity(
            @RequestBody request : QuestionRequest,
    ) : ResponseEntity<AnswerResponse> = ResponseEntity.ok(askForCelebrityService.ask(request))
}