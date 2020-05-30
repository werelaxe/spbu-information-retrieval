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
        fileStorage[file.name] = embedding.joinToString(",")
        return embedding
    }

    fun load(docName: String): Embedding {
        return fileStorage[docName]?.let { rawEmbedding ->
            val floatList = rawEmbedding.split(",").map {
                it.toFloat()
            }
            FloatArray(floatList.size) { floatList[it] }
        } ?: throw EmbeddingsManagerException("No embedding for document $docName")
    }
}
