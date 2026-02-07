package com.community.domain.reaction.dto.response;

import com.community.domain.reaction.entity.Reactable;
import com.community.domain.reaction.entity.ReactionType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReactionResponse {

    private int likeCount;
    private int dislikeCount;
    private String myReaction;

    public static ReactionResponse of(Reactable target, ReactionType myReaction) {
        return ReactionResponse.builder()
                .likeCount(target.getLikeCount())
                .dislikeCount(target.getDislikeCount())
                .myReaction(myReaction != null ? myReaction.name() : null)
                .build();
    }
}
