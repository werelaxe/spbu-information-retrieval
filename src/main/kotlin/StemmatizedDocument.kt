
class StemmatizedDocument(tokenizedDocument: TokenizedDocument) {
    val stemmata = tokenizedDocument.tokens.map { Stemmer.stem(it) }
    val name = tokenizedDocument.name
}
