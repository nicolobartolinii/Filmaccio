package it.univpm.filmaccio.main.adapters

// Questa classe rappresenta un item (cioè un elemento, una singola notifica) del feed dell'utente. Ogni item ha un tipo, che può essere
// FOLLOW_TYPE, REVIEW_TYPE, ecc. e un timestamp che indica quando è stato creato l'item.
// Era solo un abbozzo di come potrebbe essere implementato il feed, ma non è stato implementato
// per ora perché è una cosa molto complessa da fare.
sealed class ActivityItem {
    abstract val type: Int
    abstract val timestamp: Long
    // C'è posto per eventuali altri campi comuni a tutti gli item, tocca controllare successivamente

    data class FollowActivityItem(
        val followerName: String,
        val followerProfileImage: String,
        override val timestamp: Long,
        val isFollowing: Boolean
    ) : ActivityItem() {
        override val type: Int = FOLLOW_TYPE
    }

    data class ReviewActivityItem(
        val userName: String,
        val userProfileImage: String,
        override val timestamp: Long,
        val productTitle: String,
        val productPoster: String
    ) : ActivityItem() {
        override val type: Int = REVIEW_TYPE
    }

    // Aggiungere altre classi per gli altri tipi di feed

    companion object {
        const val FOLLOW_TYPE = 0
        const val REVIEW_TYPE = 1
        // Aggiungere altri tipi di feed
    }
}
