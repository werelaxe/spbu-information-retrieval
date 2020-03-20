import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.process.CoreLabelTokenFactory
import edu.stanford.nlp.process.PTBTokenizer
import java.io.Reader


object Tokenizer {
    private val punctuationRegex = "\\p{Punct}+".toRegex()

    fun tokenize(source: Reader): Sequence<String> {
        return PTBTokenizer(source, CoreLabelTokenFactory(), "untokenizable=noneDelete")
            .asSequence()
            .map { it.value() }
            .filterNot { it.matches(punctuationRegex) }
    }
}
