import java.io.File


class EmbeddingsManagerException(override val message: String?) : Exception(message)


class EmbeddingsManager(
    storageDir: File,
    private val embeddingComputer: EmbeddingComputer
) {
    private val fileStorage = FileStorage(storageDir)

    init {
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
    }

    fun processFile(file: File): Embedding {
        val embedding = embeddingComputer.embed(file.readText())

        fileStorage[file.name] =
            embedding
                .joinToString(";") { singleEmbedding ->
                    singleEmbedding.joinToString(",")
                }
        return embedding
    }

    fun load(docName: String): Embedding {
        return fileStorage[docName]?.let { rawEmbedding ->
            rawEmbedding.split(";").map { rawSingleEmbedding ->
                val singleEmbedding = rawSingleEmbedding.split(",").map { it.toFloat() }
                FloatArray(singleEmbedding.size) { singleEmbedding[it] }
            }
        } ?: throw EmbeddingsManagerException("No embedding for document $docName")
    }
}
