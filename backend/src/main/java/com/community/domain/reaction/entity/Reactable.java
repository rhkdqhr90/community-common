package com.community.domain.reaction.entity;

import com.community.domain.user.entity.User;

public interface Reactable {

    Long getId();

    void incrementLikeCount();

    void decrementLikeCount();

    void incrementDislikeCount();

    void decrementDislikeCount();

    int getLikeCount();

    int getDislikeCount();

    boolean isDeleted();

    boolean isOwnedBy(User user);
}
