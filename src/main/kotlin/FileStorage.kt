import java.io.File

private const val PrefixLength = 2


class FileStorageException(override val message: String): Exception(message)


class FileStorage(
    private val storageDir: File
) {
    init {
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
    }

    private fun getPrefixAndName(key: String): Pair<String, String> {
        if (key.isEmpty()) {
            throw FileStorageException("Can not get prefix and name for empty key")
        }
        if (key.length < PrefixLength + 1) {
            return "short" to key
        }
        return key.substring(0, PrefixLength) to key.substring(PrefixLength)
    }

    operator fun get(key: String): String? {
        val (prefix, name) = getPrefixAndName(key)

        val prefixDir = storageDir.resolve(prefix)
        if (!prefixDir.exists()) {
            return null
        }
        val valueFile = prefixDir.resolve(name)
        if (valueFile.exists()) {
            return valueFile.readText()
        }
        return null
    }

    operator fun set(key: String, value: String) {
        val (prefix, name) = getPrefixAndName(key)

        val prefixDir = storageDir.resolve(prefix)
        if (!prefixDir.exists()) {
            prefixDir.mkdir()
        }
        prefixDir.resolve(name).writeText(value)
    }
}
