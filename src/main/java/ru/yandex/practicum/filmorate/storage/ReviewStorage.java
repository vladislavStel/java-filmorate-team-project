package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {

    Review saveReview(Review review);

    Review update(Review review);

    void delete(long reviewId);

    Review findById(long reviewId);

    Collection<Review> getAllReviewsByFilmId(Long reviewId, Integer count);

   void addLikeToReview(long reviewId, long userId);

    void addDislikeToReview(long reviewId, long userId);

    void removeLike(long reviewId, long userId);

    void removeDislike(long reviewId, long userId);

}