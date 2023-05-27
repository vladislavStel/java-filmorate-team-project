package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;
import java.util.List;

public interface ReviewService {

    Review addReview(Review review);

    Review updateReview(Review review);

    void removeReview(long reviewId);

    Review findReviewById(long id);

    void addLikeToReview(long reviewId, long userId);

    void  addDislikeToReview(long reviewId, long userId);

    void  removeLike(long reviewId, long userId);

    void  removeDislike(long reviewId, long userId);

    List<Review> getAllReviewsByFilmId(Long filmId, Integer count);

}