package ru.yandex.practicum.filmorate.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Override
    public List<Review> getAllReviewsByFilmId(Long reviewId, Integer count) {
        return reviewStorage.findAllReviewByFilmId(reviewId, count);
    }

    @Override
    public Review getReviewById(long id) {
        if (reviewStorage.isNotExistsReview(id)) {
            throw new ObjectNotFoundException(String.format("Отзыв не найден: id=%d", id));
        }
        return reviewStorage.findById(id);
    }

    @Override
    public Review addReview(Review review) {
        if (filmStorage.isNotExistsFilm(review.getFilmId()) || userStorage.isNotExistsUser(review.getUserId())) {
            throw new ObjectNotFoundException("Ошибка заполения поля USER_ID или FILM_ID");
        }
        return reviewStorage.save(review);
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
    public Review updateReview(Review review) {
        if (reviewStorage.isNotExistsReview(review.getReviewId())) {
            throw new ObjectNotFoundException(String.format("Отзыв не найден: id=%d", review.getReviewId()));
        }
        return reviewStorage.update(review);
    }

    @Override
    public void removeReview(long reviewId) {
        reviewStorage.delete(reviewId);
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