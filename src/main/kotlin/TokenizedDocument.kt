import java.io.File

class TokenizedDocument(file: File) {
    val tokens = Tokenizer.tokenize(file.reader())
    val name: String = file.name
}
