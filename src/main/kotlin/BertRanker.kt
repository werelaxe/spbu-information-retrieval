class BertRanker(
    private val embeddingsComputer: EmbeddingComputer,
    private val embeddingsManager: EmbeddingsManager
) {
    fun rank(query: String, docs: List<String>): List<Pair<String, Double>> {
        val queryEmbedding = embeddingsComputer.embed(query)

        return docs.map { doc ->
            doc to EmbeddingComputer.distance(queryEmbedding, embeddingsManager.load(doc))
        }
            .sortedByDescending { it.second }
    }
}
