package ai.spring.demospringai.controller

import ai.spring.demospringai.service.AskForMoviesService
import ai.spring.demospringai.service.model.AskForMoviesRequest
import ai.spring.demospringai.service.model.AskForMoviesResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MovieController(
        private val askForMoviesService : AskForMoviesService,
) {

    @PostMapping("/ask-for-movies")
    fun askForMovies(
            @RequestBody request : AskForMoviesRequest,
    ) : List<AskForMoviesResponse>? = askForMoviesService.ask(request)
}