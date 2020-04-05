import java.io.File


fun main() {
    val storageDirectory = File("storage")
    val corpusDirectory = File("src/corpus-generator/corpus")
    val index = Index(storageDirectory, corpusDirectory)
    index.ensureIndex()
    val querier = Queryier(index)

    println("Type 'exit' for exit")
    var query: String
    while (true) {
        try {
            print("Input query: ")
            query = readLine()!!
            if (query.isEmpty()) {
                println("Query can not be empty")
                continue
            }
            if (query == "exit") {
                break
            }
            println("Query: '${query}'")
            println("Related documents: ${querier.query(query)}")
        } catch (e: Throwable) {
            println(e)
        }
    }
}
