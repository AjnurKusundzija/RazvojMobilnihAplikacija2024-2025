package etf.ri.rma.newsfeedapp.util

object CategoryMapper {
    private val displayToApi = mapOf(
        "General" to "general",
        "Nauka" to "science",
        "Sport" to "sports",
        "Biznis" to "business",
        "Zdravlje" to "health",
        "Zabava" to "entertainment",
        "Tehnologija" to "tech",
        "Politika" to "politics",
        "Hrana" to "food",
        "Putovanja" to "travel"
    )

    fun toApiCategory(displayCategory: String): String =
        displayToApi[displayCategory] ?: "general"
}

