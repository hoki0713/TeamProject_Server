package com.mobeom.local_currency.post;


import com.mobeom.local_currency.board.Board;
import com.mobeom.local_currency.board.BoardRepository;
import com.mobeom.local_currency.rating.Rating;
import com.mobeom.local_currency.rating.RatingRepository;
import com.mobeom.local_currency.store.Store;
import com.mobeom.local_currency.store.StoreRepository;
import com.mobeom.local_currency.user.User;
import com.mobeom.local_currency.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
interface PostService {
    List<Post>  postList();
    Optional<Post> onePost(long postId);
    Post insertNotice(Post notice);
    Post updatePost(Post notice);
    void deleteNotice(Post notice);
    List<Post> inquiryList();
    Post createReview(String storeId, ReviewVO review);

    Map<Long, ReviewVO> getAllReviewsByUserId(long userId);
}

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final RatingRepository ratingRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public PostServiceImpl(PostRepository postRepository,
                           RatingRepository ratingRepository,
                           StoreRepository storeRepository,
                           UserRepository userRepository,
                           BoardRepository boardRepository) {
        this.postRepository = postRepository;
        this.ratingRepository = ratingRepository;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
    }

    @Override
    public List<Post> postList() {
        return postRepository.findAll();
    }

    @Override
    public Optional<Post> onePost(long postId) {
        Optional<Post> onePost = postRepository.findById(postId);
        return onePost;
    }

    @Override
    public Post insertNotice(Post notice) {
        return postRepository.save(notice);
    }

    @Override
    public Post updatePost(Post notice) {
        return postRepository.save(notice);
    }

    @Override
    public void deleteNotice(Post notice) {
        postRepository.delete(notice);

    }

    @Override
    public List<Post> inquiryList() {
        return postRepository.inquiryList();
    }

    @Override
    public Post createReview(String storeId, ReviewVO review) {
        Optional<Store> store = storeRepository.findById(Long.parseLong(storeId));
        Optional<User> user = userRepository.findById(review.getUserId());
        Optional<Board> board = boardRepository.findById((long) 3);

        Rating newRating = new Rating();
        newRating.setUser(user.get());
        newRating.setStore(store.get());
        newRating.setStarRating(review.getStarRating());
        Rating savedRating = ratingRepository.save(newRating);


        Post newPost = new Post();
        newPost.setBoard(board.get());
        newPost.setContents(review.getContents());
        newPost.setDeleteYn(false);
        newPost.setNoticeYn(false);
        newPost.setRating(savedRating);
        newPost.setUser(user.get());
        newPost.setRegDate(newPost.getRegDate());
        return postRepository.save(newPost);
    }

    @Override
    public Map<Long, ReviewVO> getAllReviewsByUserId(long userId) {
        Map<Long, ReviewVO> resultMap = new HashMap<>();
        List<Post> usersReviews = postRepository.findAllReviewsByUserIdAndBoardId(userId, (long)3);
        usersReviews.forEach(review -> {
           ReviewVO oneReview = new ReviewVO();
           oneReview.setStoreId(review.getRating().getStore().getId());
           oneReview.setStoreName(review.getRating().getStore().getStoreName());
           oneReview.setContents(review.getContents());
           oneReview.setStarRating(review.getRating().getStarRating());
           resultMap.put(review.getPostId(), oneReview);
        });
        return resultMap;
    }

}
