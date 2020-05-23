import com.robrua.nlp.bert.Bert
import edu.stanford.nlp.ling.SentenceUtils
import edu.stanford.nlp.process.DocumentPreprocessor
import kotlin.math.abs
import kotlin.math.min


class EmbeddingComputerException(override val message: String) : Exception(message)


class EmbeddingComputer(
    private val model: Bert,
    private val sentencesLimit: Int = 20
) {
    fun embed(text: String): Embedding {
        return model.embedSequences(explodeToSentences(text).take(sentencesLimit)).toList()
    }

    private fun explodeToSentences(text: String): List<String> {
        return DocumentPreprocessor(text.reader()).map { SentenceUtils.listToString(it); }
    }

    companion object {
        fun l2distance(first: FloatArray, second: FloatArray): Double {
            if (first.size != second.size) {
                throw EmbeddingComputerException(
                    "Can not compute l2 distance" +
                            " with different size arrays: ${first.size} and ${second.size}"
                )
            }
            var l2distance = 0.0
            for (index in first.indices) {
                l2distance += abs(first[index] - second[index])
            }
            return l2distance / first.size
        }

        fun distance(first: Embedding, second: Embedding): Double {
            val len = min(first.size, second.size)
            var distance = 0.0
            for (index in 0 until len) {
                distance += l2distance(first[index], second[index])
            }
            return distance / len
        }
    }
}
