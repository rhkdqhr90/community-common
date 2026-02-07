package com.community.domain.reaction.dto.request;

import com.community.domain.reaction.entity.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReactionRequest {

    @NotNull(message = "반응 타입은 필수 입니다.")
    private ReactionType reactionType;
}
