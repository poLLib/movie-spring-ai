package ai.spring.moviespringai.bootstrap

import ai.spring.moviespringai.config.VectorStoreProperties
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class LoadVectorStore(
        private val vectorStore : VectorStore,
        private val vectorStoreProperties : VectorStoreProperties,
) : CommandLineRunner {

    private val logger : KLogger = KotlinLogging.logger {}

    override fun run(vararg args : String?) {
        logger.debug { "Loading vector store..." }

        try {
            vectorStore
                    .similaritySearch("Movie") // TODO: solve if exists
                    ?.takeIf { it.isNotEmpty() }
                    ?.also { logger.debug { "Vector store exists, skipping loading..." } }
                    ?.run {
                        logger.debug { "Loading documents into vector store..." }
                        processDocuments()
                    }
        } catch (e : Exception) {
            logger.warn(e) { "Error occurred when loading documents into vector store: ${e.message}" }
        }
    }

    private fun processDocuments() {
        vectorStoreProperties.documentsToLoad
                .forEach { document ->
                    logger.debug { "Loading document: ${document.filename}" }

                    TikaDocumentReader(document)
                            .get()
                            .let { docs ->
                                TokenTextSplitter()
                                        .apply(docs)
                                        .let { splitDocs ->
                                            logger.debug { "Adding ${splitDocs.size} documents to vector store" }
                                            vectorStore.add(splitDocs)
                                        }
                            }
                }
        logger.debug { "Vector store loaded" }
    }
}