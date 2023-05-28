package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review findById(long reviewId);

    List<Review> findAllReviewByFilmId(Long reviewId, Integer count);

    Review save(Review review);

    void addLikeToReview(long reviewId, long userId);

    void addDislikeToReview(long reviewId, long userId);

    Review update(Review review);

    void delete(long reviewId);

    void removeLike(long reviewId, long userId);

    void removeDislike(long reviewId, long userId);

    boolean isNotExistsReview(Long id);

}