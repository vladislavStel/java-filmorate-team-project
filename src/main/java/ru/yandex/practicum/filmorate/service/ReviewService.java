package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewService {

    Review addReview(Review review);
    Review upadateReview(Review review);
    void removeReview(long reviewId);

    Review findReviewById(long id);
    void addLikeToReview (long reviewId, long userId);
    void  addDislikeToReview(long reviewId, long userId);
    void  removeLike(long reviewId, long userId);
    void  removeDislike(long reviewId, long userId);
    Collection<Review> getAllReviewsByFilmId(Long filmId, Integer count);
}
