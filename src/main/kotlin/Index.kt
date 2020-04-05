import org.slf4j.LoggerFactory
import java.io.File

class Index(
    storageDir: File,
    private val corpusDirectory: File,
    private val blocksSize: Int = 400
) {
    private val LOG = LoggerFactory.getLogger(Index::class.java)

    private val termsDictionary = IdentifierCompressor(storageDir.resolve("terms"))
    private val docsDictionary = IdentifierCompressor(storageDir.resolve("docs"))
    private val blockManager = BlockManager(storageDir)

    init {
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
    }

    private fun createBlock(stemmatizedDocuments: List<StemmatizedDocument>) {
        val processedBlock = blockManager.processBlock(stemmatizedDocuments, termsDictionary, docsDictionary)
        blockManager.dumpBlock(processedBlock)
    }

    private fun createBlocks() {
        LOG.info("Start creating index")

        val blocksSequence = corpusDirectory
            .walk()
            .filter { it.isFile }
            .asSequence()
            .map { TokenizedDocument(it) }
            .map { StemmatizedDocument(it) }
            .chunked(blocksSize)

        blocksSequence.withIndex().forEach { (index, block) ->
            createBlock(block)
            LOG.info("Block $index has been created")
        }
        termsDictionary.dump()
        docsDictionary.dump()
        LOG.info("Dictionaries has been dumped")
    }

    private fun mergeBlocks() {
        LOG.info("Start merging")
        blockManager.merge()
    }

    fun buildIndex() {
        blockManager.clearIndex()
        createBlocks()
        mergeBlocks()
    }

    fun isIndexExists() = docsDictionary.data.isNotEmpty()

    fun ensureIndex() {
        if (isIndexExists()) {
            LOG.info("Index is already exists")
        } else {
            buildIndex()
        }
    }

    private fun idByWord(word: String) = termsDictionary[Stemmer.stem(word)].toString()

    fun getRelatedDocIds(word: String): Set<Int> {
        return blockManager.indexStorage[idByWord(word)]
            ?.split(",")
            ?.map { it.toInt() }
            ?.toSet()
            .orEmpty()
    }

    fun getUnrelatedDocIds(word: String): Set<Int> {
        return getALlDocIds().subtract(getRelatedDocIds(word))
    }

    fun getALlDocIds(): Set<Int> = docsDictionary.data.values.toSet()

    fun translateToDocNames(docIds: Iterable<Int>): List<String> {
        return docIds.mapNotNull { docsDictionary.reverseGet(it) }
    }
}
