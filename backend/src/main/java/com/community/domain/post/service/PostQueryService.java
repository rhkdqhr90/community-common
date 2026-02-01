package com.community.domain.post.service;

import com.community.core.common.dto.PageResponse;
import com.community.core.exception.ErrorCode;
import com.community.core.exception.custom.NotFoundException;
import com.community.domain.post.dto.condition.PostSearchCondition;
import com.community.domain.post.dto.response.PostDetailResponse;
import com.community.domain.post.dto.response.PostListResponse;
import com.community.domain.post.entity.Post;
import com.community.domain.post.repository.PostRepository;
import com.community.domain.user.entity.User;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 게시글 조회 서비스 (read 작업)
 *
 * <p>게시글 조회, 목록 조회, 검색을 담당합니다.</p>
 * <p>생성/수정/삭제는 {@link PostService}</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {

    private final PostRepository postRepository;

    /**
     * 게시글 상세 조회
     *
     * @param postId      게시글 ID
     * @param currentUser 사용자
     * @return 게시글 상제 응답
     */
    @Transactional
    public PostDetailResponse getPostDetail(Long postId, User currentUser) {
        Post post = postRepository.findByIdWithBoardAndUser(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));
        //조회수 증가
        post.incrementViewCount();

        //태그 목록 조회
        List<String> tags = post.getTags().stream()
                .map(postTag -> postTag.getTag().getName())
                .toList();
        //사용자별 좋아요 버튼
        String myReaction = null;

        //북마크
        boolean isBookmarked = false;

        PostDetailResponse response = PostDetailResponse.from(post, tags, currentUser, myReaction, isBookmarked);

        return response;
    }

    /**
     * 게시판별 게시글 목록 조회
     * @param slug 게시판 슬러그
     * @param pageable 페이지정보
     * @return 게시글 목록 응답(페이징)
     */
    public PageResponse<PostListResponse> getPostList(String slug, Pageable pageable) {
        Page<Post> postPage = postRepository.findByBoardSlug(slug, pageable);

        Page<PostListResponse> responsePage = postPage.map(post -> {
            List<String> tag = extractTagNames(post);
            return PostListResponse.from(post, tag);
        });

        return PageResponse.of(responsePage);
    }

    /**
     * 게시판별 게시글 목록 조회(공지 포함)
     * @param slug 게시판 슬러그
     * @param pageable 페이지 정보
     * @return 게시글 목록 응답(공지글 + 일반글)
     */
    public PageResponse<PostListResponse> getPostsWithNotices(String slug, Pageable pageable) {
        List<Post> notices = postRepository.findNoticesByBoardSlug(slug);

        Page<Post> postPage = postRepository.findByBoardSlug(slug, pageable);

        List<PostListResponse> noticeResponse = notices.stream()
                .map(post -> {
                    List<String> tag = extractTagNames(post);
                    return PostListResponse.from(post, tag);
                })
                .toList();

        Page<PostListResponse> responsePage = postPage.map(post -> {
            List<String> tag = extractTagNames(post);
            return PostListResponse.from(post, tag);
        });

        List<PostListResponse> allContent = noticeResponse;
        allContent.addAll(responsePage.getContent());

        return PageResponse.<PostListResponse>builder()
                .content(allContent)
                .page(responsePage.getNumber())
                .size(responsePage.getSize())
                .totalElements(responsePage.getTotalElements())
                .totalPages(responsePage.getTotalPages())
                .hasNext(responsePage.hasNext())
                .hasPrevious(responsePage.hasPrevious())
                .build();
    }

    /**
     * 게시글 검색
     * @param condition 검색 조건(keyword, boardId, tag 등)
     * @param pageable 페이지 정보
     * @return 검색 결과
     */
    public PageResponse<PostListResponse> searchPost(PostSearchCondition condition, Pageable pageable) {
        // QueryDsl 기반 동적 쿼리 검색
        Page<Post> postPage = postRepository.searchPosts(condition, pageable);

        Page<PostListResponse> responsesPage = postPage.map(post -> {
            List<String> tags = extractTagNames(post);
            return PostListResponse.from(post, tags);
        });

        return PageResponse.of(responsesPage);
    }


    /**
     * 사용자가 작성한 게시글 목록 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이지 정보
     * @return 게시글 목록 응답
     */
    public PageResponse<PostListResponse> getPostsByUser(Long userId, Pageable pageable) {
        // 1. 사용자별 게시글 조회 (최신순)
        Page<Post> postPage = postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        // 2. Post -> PostListResponse 변환 (Page.map 사용)
        Page<PostListResponse> responsePage = postPage.map(post -> {
            List<String> tags = extractTagNames(post);
            return PostListResponse.from(post, tags);
        });

        // 3. PageResponse 생성
        return PageResponse.of(responsePage);
    }

    /**
     * 태그명 추출 (private helper)
     */
    private List<String> extractTagNames(Post post) {
        return post.getTags().stream()
                .map(postTag -> postTag.getTag().getName())
                .toList();
    }

}
