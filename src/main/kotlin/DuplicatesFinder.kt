import java.io.File

class DuplicatesFinder(
    storageDir: File,
    private val embeddingsManager: EmbeddingsManager
) {
    private val duplicatesStorage = FileStorage(storageDir.resolve("duplicates"))
    private val clustersStorage = FileStorage(storageDir.resolve("clusters"))

    fun process(embedding: Embedding, docName: String) {
        val embeddingHash = getClusterHash(embedding)
        for (hash in embeddingHash - 1 .. embeddingHash + 1) {
            clustersStorage[hash.toString()]?.let { rawDocNames ->
                for (doc in rawDocNames.split(";").drop(1)) {
                    if (embeddingsManager.load(doc) similar embedding) {
                        duplicatesStorage[doc] = (duplicatesStorage[doc] ?: "") + ";" + docName
                        return
                    }
                }
            }
        }
        val rawHash = embeddingHash.toString()
        clustersStorage[rawHash] = (clustersStorage[rawHash] ?: "") + ";" + docName
    }


    private fun getClusterHash(embedding: Embedding): Int {
        return embedding.sumByDouble { it.sum().toDouble() }.toInt()
    }
}
