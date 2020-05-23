typealias Embedding = List<FloatArray>

const val EPS = 10e-2


infix fun Embedding.similar(other: Embedding): Boolean {
    return EmbeddingComputer.distance(this, other) < EPS
}
