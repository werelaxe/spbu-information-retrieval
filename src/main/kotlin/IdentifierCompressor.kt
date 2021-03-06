import java.io.File

class IdentifierCompressorException(override val message: String): Exception(message)


class IdentifierCompressor(
    private val storageFile: File? = null
) {
    val data = mutableMapOf<String, Int>()
    val reverseData = mutableMapOf<Int, String>()

    init {
        storageFile?.let {
            if (storageFile.exists()) {
                load()
            }
        }
    }

    operator fun get(term: String): Int {
        return data.computeIfAbsent(term) {
            reverseData[data.size] = term
            data.size
        }
    }

    fun reverseGet(id: Int) = reverseData[id]

    fun dump() {
        storageFile?.let {
            data.forEach { (originalId, compressedId) ->
                storageFile.appendText("$originalId $compressedId\n")
            }
        } ?: throw IdentifierCompressorException("Can not dump $this, storageDir is null")
    }

    fun load() {
        storageFile?.let {
            storageFile.readLines().forEach {
                val (originalId, rawCompressedId) = it.split(" ")
                val compressedId = rawCompressedId.toInt()
                data[originalId] = compressedId
                reverseData[compressedId] = originalId
            }
        } ?: throw IdentifierCompressorException("Can not load $this, storageDir is null")
    }
}
