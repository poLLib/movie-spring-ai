package ai.spring.moviespringai.controller

import ai.spring.moviespringai.service.AskForCelebrityService
import ai.spring.moviespringai.service.AskForMoviesService
import ai.spring.moviespringai.service.CreateMovieService
import ai.spring.moviespringai.service.model.Answer
import ai.spring.moviespringai.service.model.AskForMoviesResponse
import ai.spring.moviespringai.service.model.CreateMovieResponse
import ai.spring.moviespringai.service.model.QuestionRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MovieController(
        private val askForMoviesService : AskForMoviesService,
        private val createMovieService : CreateMovieService,
        private val askForCelebrityService : AskForCelebrityService,
) {

    @PostMapping("/ask-for-movies")
    fun askForMovies(
            @RequestBody request : QuestionRequest,
    ) : List<AskForMoviesResponse>? = askForMoviesService.ask(request)

    @PostMapping("/movie")
    fun createMovie(
            @RequestBody request : QuestionRequest,
    ) : CreateMovieResponse? = createMovieService.create(request)

    @PostMapping("/celebrity")
    fun askForCelebrity(
            @RequestBody request : QuestionRequest,
    ) : Answer? = askForCelebrityService.ask(request)
}