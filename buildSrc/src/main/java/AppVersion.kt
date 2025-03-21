@Suppress("unused")
object AppVersion {
    private const val MAJOR = 0
    private const val MINOR = 5
    private const val BUILD = 0

    const val NAME: String = "$MAJOR.$MINOR.$BUILD"
    val CODE = "${MAJOR}${MINOR.format()}${BUILD.format()}".toInt()

    private fun Int.format() = "%03d".format(this)
}
