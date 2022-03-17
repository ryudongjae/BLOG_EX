package com.blog.blogex.n_plus_1.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class PostService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public List<String> findAllComments() {
        return extractSubjectNames(postRepository.findAll());
    }

    private List<String> extractSubjectNames(List<Post> posts) {
        log.info(">>>>>>>>[모든 댓글을 추출한다]<<<<<<<<<");
        log.info("posts Size : {}", posts.size());


        return posts.stream()
                .map(a -> a.getComments().get(0).getContent())
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<String> findAllCommentsByJoinFetch() {
        return extractSubjectNames(postRepository.findAllFetchJoin());
    }

    @Transactional(readOnly = true)
    public List<String> findAllCommentsByEntityGraph() {
        return extractSubjectNames(postRepository.findAllEntityGraph());
    }

    @Transactional(readOnly = true)
    public List<String> findAllCommentsByJoinFetchDistinct() {
        return extractSubjectNames(postRepository.findAllJoinFetchDistinct());
    }

    @Transactional(readOnly = true)
    public List<String> findAllCommentsByEntityGraphDistinct() {
        return extractSubjectNames(postRepository.findAllEntityGraphDistinct());
    }
}
