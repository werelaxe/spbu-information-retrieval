import org.tartarus.snowball.ext.EnglishStemmer


object Stemmer {
    private val englishStemmer = EnglishStemmer()

    fun stem(word: String): String {
        with(englishStemmer) {
            current = word
            stem()
            return current.toLowerCase()
        }
    }
}
