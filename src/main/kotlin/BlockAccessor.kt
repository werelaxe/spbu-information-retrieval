import java.io.File

class BlockAccessor(
    private val storageDirectory: File,
    private val blockIndex: Int,
    private val blockFilenamePrefix: String = "block_"
) {
    private fun getBlockFile(storageDirectory: File, blockIndex: Int) =
        storageDirectory.resolve(File("$blockFilenamePrefix$blockIndex"))

    fun rows() = sequence {
        val result = getBlockFile(storageDirectory, blockIndex)
            .bufferedReader()
            .lineSequence()
            .map { BlockFileRow.fromRaw(it, blockIndex) }
        yieldAll(result)
    }.iterator()
}
