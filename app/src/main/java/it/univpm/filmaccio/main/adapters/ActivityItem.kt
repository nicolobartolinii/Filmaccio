package it.univpm.filmaccio.main.adapters

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
