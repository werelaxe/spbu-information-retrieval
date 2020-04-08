data class BlockFileRow(
    val termId: Int,
    val blockId: Int,
    val docIds: Set<Int>
) {
    companion object {
        fun fromRaw(blockFileRow: String, blockId: Int): BlockFileRow {
            val (rawTermId, rawDocIds) = blockFileRow.split(" ")
            val termId = rawTermId.toInt()
            val docIds = rawDocIds
                .split(",")
                .map { it.toInt() }
                .toSet()
            return BlockFileRow(termId, blockId, docIds)
        }
    }
}


class BlockFileRowComparator: Comparator<BlockFileRow> {
    override fun compare(first: BlockFileRow, second: BlockFileRow): Int {
        return if (first.termId != second.termId) {
            first.termId.compareTo(second.termId)
        } else {
            first.blockId.compareTo(second.blockId)
        }
    }
}
