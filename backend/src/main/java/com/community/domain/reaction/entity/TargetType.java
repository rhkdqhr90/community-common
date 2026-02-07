package com.community.domain.reaction.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TargetType {
    POST("게시글"),
    COMMENT("댓글");

    private final String description;
}
