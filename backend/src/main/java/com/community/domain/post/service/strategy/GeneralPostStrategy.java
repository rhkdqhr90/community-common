package com.community.domain.post.service.strategy;

import com.community.domain.post.dto.request.PostCreateRequest;
import com.community.domain.post.dto.request.PostUpdateRequest;
import com.community.domain.post.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class GeneralPostStrategy implements PostStrategy{
    @Override
    public void validateCreate(PostCreateRequest request) {
        
    }

    @Override
    public void validateUpdate(PostUpdateRequest request) {

    }

    @Override
    public void beforeCreate(Post post, PostCreateRequest request) {

    }

    @Override
    public void afterCreate(Post post) {

    }

    @Override
    public void beforeUpdate(Post post, PostUpdateRequest request) {

    }

    @Override
    public void afterUpdate(Post post) {

    }
}
