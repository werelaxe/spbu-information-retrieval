import com.robrua.nlp.bert.Bert
import java.io.File


fun main() {
    val storageDirectory = File("storage")
    if (!storageDirectory.exists()) {
        storageDirectory.mkdir()
    }
    val corpusDirectory = File("src/corpus-generator/corpus")
    val model = Bert.load(File("src/bert-model-fetcher/bert-model"))

    val embeddingComputer = EmbeddingComputer(model)
    val embeddingsManager = EmbeddingsManager(storageDirectory.resolve("embeddings"), embeddingComputer)
    val duplicatesFinder = DuplicatesFinder(storageDirectory.resolve("dupfinder"), embeddingsManager)

    val index = Index(embeddingsManager, duplicatesFinder, storageDirectory, corpusDirectory)
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
            val result = queryProcessor.query(query)
            println("Related documents count: ${result.size}")
            if (result.isEmpty()) {
                continue
            }
            println("Top documents:")
            val maxDocNameLen = result.map { it.first.length }.max()!!
            result.forEach { (doc, rank) ->
                val duplicates = duplicatesFinder.findDuplicates(doc)
                val dupMessage =
                    if (duplicates.isEmpty()) ""
                    else ", duplicates: ${duplicates.joinToString(", ")}"
                println("${doc}: ${" ".repeat(maxDocNameLen - doc.length)}$rank${dupMessage}")
            }
        } catch (e: Throwable) {
            println(e)
        }
    }
}
