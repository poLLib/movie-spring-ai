package ai.spring.moviespringai.controller

import ai.spring.moviespringai.service.AskForMoviesService
import ai.spring.moviespringai.service.model.AskForMoviesRequest
import ai.spring.moviespringai.service.model.AskForMoviesResponse
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MovieController(
        private val askForMoviesService : AskForMoviesService,
        private val vectorStore: VectorStore
) {

    @PostMapping("/ask-for-movies")
    fun askForMovies(
            @RequestBody request : AskForMoviesRequest,
    ) : List<AskForMoviesResponse>? = askForMoviesService.ask(request)

    @GetMapping("/debug/vector-store-test")
    fun vectorStoreTest(): Map<String, Any> {
        // Test s velmi nízkým threshold a vysokým topK
        val allDocuments = vectorStore.similaritySearch(
                SearchRequest
                        .builder()
                        .query("movie") // Nejobecnější dotaz
                        .topK(200)
                        .similarityThreshold(0.0) // Nejnižší možný
                        .build()
        )

        return mapOf(
                "totalDocuments" to allDocuments!!.size,
                "firstFewDocuments" to allDocuments.take(3).map {
                    mapOf(
                            "content" to it.formattedContent.take(200),
                            "metadata" to it.metadata
                    )
                }
        )
    }
}