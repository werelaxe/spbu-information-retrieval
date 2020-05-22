import java.io.File
import kotlin.math.min


fun main() {
    val storageDirectory = File("storage")
    val corpusDirectory = File("src/corpus-generator/corpus")
    val model = Bert.load(File("src/bert-model-fetcher/bert-model"))

    val embeddingComputer = EmbeddingComputer(model)
    val embeddingsManager = EmbeddingsManager(storageDirectory.resolve("embeddings"), embeddingComputer)

    val index = Index(embeddingsManager, storageDirectory, corpusDirectory)
    index.ensureIndex()
    val queryProcessor = QueryProcessor(index, BertRanker(embeddingComputer, embeddingsManager))

    println("Type 'exit' for exit")
    var query: String?
    while (true) {
        try {
            print("Input query: ")
            query = readLine()
            if (query == null) {
                println("exit")
                break
            }
            if (query.isEmpty()) {
                println("Query can not be empty")
                continue
            }
            if (query == "exit") {
                break
            }
            println("Query: '${query}'")
            val result = querier.query(query)
            println("Related documents count: ${result.size}")
            println("Top documents: ${result.subList(0, min(10, result.size))}")
        } catch (e: Throwable) {
            println(e)
        }
    }
}
