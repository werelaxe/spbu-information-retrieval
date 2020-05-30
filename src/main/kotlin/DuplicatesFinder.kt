import java.io.File

class DuplicatesFinder(
    storageDir: File,
    private val embeddingsManager: EmbeddingsManager
) {
    private val duplicatesStorage = FileStorage(storageDir.resolve("duplicates"))
    private val clustersStorage = FileStorage(storageDir.resolve("clusters"))

    fun processAndGetOriginal(embedding: Embedding, docName: String): String? {
        val embeddingHash = getClusterHash(embedding)
        for (hash in embeddingHash - 1 .. embeddingHash + 1) {
            clustersStorage[hash.toString()]?.let { rawDocNames ->
                for (doc in rawDocNames.split(";").drop(1)) {
                    if (embeddingsManager.load(doc) similar embedding) {
                        duplicatesStorage[doc] = (duplicatesStorage[doc] ?: "") + ";" + docName
                        return doc
                    }
                }
            }
        }
        val rawHash = embeddingHash.toString()
        clustersStorage[rawHash] = (clustersStorage[rawHash] ?: "") + ";" + docName
        return null
    }

    fun findDuplicates(docName: String): List<String> {
        duplicatesStorage[docName]?.let { rawDups ->
            return rawDups.split(";").drop(1)
        } ?: return emptyList()
    }

    private fun getClusterHash(embedding: Embedding): Int {
        return embedding.sum().toInt()
    }
}
