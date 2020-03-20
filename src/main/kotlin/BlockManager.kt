import java.io.File
import java.util.*

class BlockManager(
    private val storageDirectory: File,
    private val blockFilenamePrefix: String = "block_",
    indexFilename: String = "index"
) {
    private val indexStorageDir = storageDirectory.resolve(indexFilename)
    val indexStorage = FileStorage(indexStorageDir)

    init {
        if (!storageDirectory.exists()) {
            storageDirectory.mkdir()
        }
        clearIndex()
    }

    var blocksCount = 0
        private set

    fun processBlock(
        stemmatizedDocuments: List<StemmatizedDocument>,
        termsVocabulary: IdentifierCompressor,
        docsVocabulary: IdentifierCompressor
    ): SortedMap<Int, Set<Int>> {
        val result = mutableMapOf<Int, MutableSet<Int>>()

        stemmatizedDocuments.forEach { stemmatizedDocument ->
            stemmatizedDocument.stemmata.forEach { stemma ->
                val termId = termsVocabulary[stemma]
                val docId = docsVocabulary[stemmatizedDocument.name]
                if (termId !in result) {
                    result[termId] = mutableSetOf()
                }
                result[termId]?.add(docId)
            }
        }
        return result.toSortedMap()
    }

    private fun dump(termId: Int, docIds: Set<Int>) = "$termId ${docIds.joinToString(",")}\n"

    fun dumpBlock(block: SortedMap<Int, Set<Int>>) {
        val blockFile = storageDirectory.resolve(File("$blockFilenamePrefix$blocksCount"))
        blockFile.writer().use { writer ->
            block.forEach { (termId, docIds) ->
                writer.write(dump(termId, docIds))
            }
        }
        blocksCount++
    }

    fun merge(): FileStorage {
        val blockAccessors = (0 until blocksCount).map {
            BlockAccessor(storageDirectory, it).rows()
        }

        val queue = PriorityQueue(BlockFileRowComparator())

        blockAccessors.forEach {
            queue.add(it.next())
        }

        val topValue = queue.poll()
        var currentValue = topValue.termId
        val currentBuffer = mutableSetOf<Int>().apply { addAll(topValue.docIds) }

        while (queue.isNotEmpty()) {
            val buff = queue.poll()
            if (buff.termId != currentValue) {
                writeToIndex(currentValue, currentBuffer)

                currentBuffer.clear()
                currentValue = buff.termId
            } else {
                currentBuffer.addAll(buff.docIds)
            }
            with(blockAccessors[buff.blockId])  {
                if (hasNext()) {
                    queue.add(next())
                }
            }
        }

        return indexStorage
    }

    private fun writeToIndex(termId: Int, docIds: Set<Int>) {
        indexStorage[termId.toString()] = docIds.joinToString(",")
    }

    fun clearIndex() {
        if (indexStorageDir.exists()) {
            indexStorageDir.delete()
        }
        indexStorageDir.mkdir()
    }
}
