package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IncorrectReviewLikeException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Collection;

@Slf4j
@AllArgsConstructor
@Repository
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review saveReview(Review review) {
        String sqlSaveReview = "INSERT INTO REVIEW (CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(sqlSaveReview, review.getContent(), review.getIsPositive(), review.getUserId(),
                review.getFilmId(), review.getUseful());
        String sqlIdReview = "SELECT REVIEW_ID FROM REVIEW ORDER BY REVIEW_ID DESC LIMIT 1";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlIdReview);
        if (sqlRowSet.next()) {
            review.setReviewId(sqlRowSet.getLong("REVIEW_ID"));
        }
        log.info("Добавлен новый отзыв: reviewId={} на фильм: filmId={} ", review.getReviewId(), review.getFilmId());
        return review;

    }

    @Override
    public Review update(Review review) {
        String sqlUpdateReview = "UPDATE REVIEW SET CONTENT=?,IS_POSITIVE =? WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlUpdateReview, review.getContent(), review.getIsPositive(), review.getReviewId());
        String sqlReviewsQuery = "SELECT * FROM REVIEW WHERE REVIEW_ID = ?";
        SqlRowSet updateSet = jdbcTemplate.queryForRowSet(sqlReviewsQuery, review.getReviewId());
        if (updateSet.next()) {
            review = Review.builder().reviewId(updateSet.getInt("review_id")).content(updateSet.getString("content")).isPositive(updateSet.getBoolean("is_positive")).userId(updateSet.getLong("user_id")).filmId(updateSet.getLong("film_id")).useful(updateSet.getInt("useful")).build();
        }
        log.info("Данные отзыва обновлены: reviewId={}", review.getReviewId());
        return review;

    }

    @Override
    public void delete(long reviewId) {
        String sqlRemoveReviewById = "DELETE FROM REVIEW WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlRemoveReviewById, reviewId);
        log.info("Отзыв удален: id={}", reviewId);

    }

    @Override
    public Review findById(long reviewId) {
        String sqlFindReviewById = "SELECT * FROM REVIEW WHERE REVIEW_ID = ?";
        SqlRowSet foundReview = jdbcTemplate.queryForRowSet(sqlFindReviewById, reviewId);
        if (foundReview.next()) {
            Review review = Review.builder().reviewId(foundReview.getInt("review_id")).content(foundReview.getString("content")).isPositive(foundReview.getBoolean("is_positive")).userId(foundReview.getLong("user_id")).filmId(foundReview.getLong("film_id")).useful(foundReview.getInt("useful")).build();
            log.info("Отзыв найден: id={}", reviewId);
            return review;

        } else {
            throw new ObjectNotFoundException(String.format("Отзыв не найден: reviewId=%d", reviewId));
        }
    }

    @Override
    public Collection<Review> getAllReviewsByFilmId(Long filmId, Integer count) {
        if (filmId == null) {
            String sqlReviewsQuery = "SELECT * FROM REVIEW ORDER BY USEFUL DESC LIMIT ?";
            return jdbcTemplate.query(sqlReviewsQuery, (rs, rowNum) -> createReview(rs), count);
        }
        String sqlReviewsQuery = "SELECT * FROM REVIEW WHERE FILM_ID = ? ORDER BY USEFUL DESC LIMIT ?";
        return jdbcTemplate.query(sqlReviewsQuery, (rs, rowNum) -> createReview(rs), filmId, count);
    }

    private Review createReview(ResultSet rs) throws SQLException {
        return Review.builder().reviewId(rs.getLong("review_id")).content(rs.getString("content")).isPositive(rs.getBoolean("is_positive")).userId(rs.getLong("user_id")).filmId(rs.getLong("film_id")).useful(rs.getInt("useful")).build();
    }


    @Override
    public void addLikeToReview(long reviewId, long userId) {
        String addLikeReview = "INSERT INTO REVIEW_LIKE (REVIEW_ID, USER_ID, IS_LIKE)  " + "VALUES (?, ?, ?)";
        if (jdbcTemplate.update(addLikeReview, reviewId, userId, true) > 0) {
            String updateUseful = "UPDATE REVIEW SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?";
            jdbcTemplate.update(updateUseful, reviewId);
            log.info("Пользователь с ID = {} добавил лайк для отзыва ID = {}.", userId, reviewId);
        } else {
            log.info("Ошибка при добавлении лайка для отзыва ID = {} от пользователя с ID = {}.", reviewId, userId);
            throw new IncorrectReviewLikeException(String.format("Ошибка при добавлении лайка для отзыва ID = %d от пользователя с ID = %d.", reviewId, userId));
        }
    }

    @Override
    public void addDislikeToReview(long reviewId, long userId) {
        String addDisLikeReview = "INSERT INTO REVIEW_LIKE (REVIEW_ID, USER_ID, IS_LIKE) " + "VALUES (?, ?, ?)";
        if (jdbcTemplate.update(addDisLikeReview, reviewId, userId, false) > 0) {
            String updateUseful = "UPDATE REVIEW SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?";
            jdbcTemplate.update(updateUseful, reviewId);
            log.info("Пользователь с ID = {} добавил дизлайк для отзыва ID = {}.", userId, reviewId);
        } else {
            log.info("Ошибка при добавлении дизлайка для отзыва ID = {} от пользователя с ID = {}.", reviewId, userId);
            throw new IncorrectReviewLikeException(String.format("Ошибка при добавлении лайка для отзыва ID = %d от пользователя с ID = %d.", reviewId, userId));
        }
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        String deleteLikeReview = "DELETE FROM REVIEW_LIKE WHERE REVIEW_ID = ? AND USER_ID = ?";
        if (jdbcTemplate.update(deleteLikeReview, reviewId, userId) < 1) {
            jdbcTemplate.update(deleteLikeReview, reviewId, userId);
            log.info("Пользователь с ID = {} удалил лайк для отзыва ID = {}.", userId, reviewId);
        } else {
            log.info("Ошибка при удалении лайка для отзыва ID = {} от пользователя с ID = {}.", reviewId, userId);
            throw new IncorrectReviewLikeException(String.format("Ошибка при удалении лайка для отзыва ID = {} от пользователя с ID = {}.", reviewId, userId));
        }
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        String deleteDisLikeReview = "DELETE FROM REVIEW_LIKE WHERE REVIEW_ID = ? AND USER_ID = ?";
        if (jdbcTemplate.update(deleteDisLikeReview, reviewId, userId) < 1) {
            jdbcTemplate.update(deleteDisLikeReview, reviewId, userId);
            log.info("Пользователь с ID = {} удалил дизлайк для отзыва ID = {}.", userId, reviewId);

        } else {
            log.info("Ошибка при удалении дизлайка для отзыва ID = {} от пользователя с ID = {}.", reviewId, userId);
            throw new IncorrectReviewLikeException(String.format("Ошибка при удалении дизлайка для отзыва ID = {} от пользователя с ID = {}.", reviewId, userId));

        }

    }


}

