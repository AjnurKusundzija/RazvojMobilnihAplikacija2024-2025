package etf.ri.rma.newsfeedapp.util

object CategoryMapper {
    fun toApiCategory(category: String): String {
        return when (category) {
            "Politika" -> "politics"

            "Sport" -> "sports"

            "Nauka" -> "science"

            "Tehnologija" -> "tech"

            "Nauka/tehnologija" -> "tech"
            "Zdravlje" -> "health"

            "Zabava" -> "entertainment"

            "Posao" -> "business"

            "Hrana" -> "food"

            "Putovanja" -> "travel"

            "Sve", "All", "general" -> "general"
            else -> category.lowercase()
        }
    }
}
