typealias Embedding = FloatArray

const val EPS = 10e-3


infix fun Embedding.similar(other: Embedding): Boolean {
    return EmbeddingComputer.distance(this, other) < EPS
}
