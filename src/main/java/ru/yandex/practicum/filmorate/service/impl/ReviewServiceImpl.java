package ru.yandex.practicum.filmorate.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectFieldReviewException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Override
    public Review addReview(Review review) {
        if (review.getUserId() == null || review.getFilmId() == null) {
            throw new ValidationException("Не заданно поле отзыва");

        }
        if (filmStorage.isNotExistsFilm(review.getFilmId()) || userStorage.isNotExistsUser(review.getUserId())) {
            throw new IncorrectFieldReviewException("Ошибка заполения поля USER_ID или FILM_ID");
        }
        return reviewStorage.saveReview(review);

    }

    @Override
    public Review upadateReview(Review review) {
        return reviewStorage.update(review);

    }

    @Override
    public void removeReview(long reviewId) {
        reviewStorage.delete(reviewId);
    }

    @Override
    public Collection<Review> getAllReviewsByFilmId(Long reviewId, Integer count) {
        return reviewStorage.getAllReviewsByFilmId(reviewId, count);

    }

    @Override
    public Review findReviewById(long id) {
        return reviewStorage.findById(id);


    }

    @Override
    public void addLikeToReview(long reviewId, long userId) {
        reviewStorage.addLikeToReview(reviewId, userId);
    }

    @Override
    public void addDislikeToReview(long reviewId, long userId) {
        reviewStorage.addDislikeToReview(reviewId, userId);
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        reviewStorage.removeLike(reviewId, userId);
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        reviewStorage.removeDislike(reviewId, userId);
    }
}
