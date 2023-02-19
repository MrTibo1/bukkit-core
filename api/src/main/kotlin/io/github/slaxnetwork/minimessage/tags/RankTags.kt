package io.github.slaxnetwork.minimessage.tags

import io.github.slaxnetwork.kyouko.models.rank.Rank
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

object RankTags {
    fun icon(rank: Rank): TagResolver {
        return Placeholder.parsed(
            "rank_icon",
            "<icon:${rank.prefixId}>"
        )
    }
}