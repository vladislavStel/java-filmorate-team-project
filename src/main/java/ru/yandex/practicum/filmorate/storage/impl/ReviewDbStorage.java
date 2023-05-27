package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Repository
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Review> reviewMapper;

    @Override
    public Review save(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("REVIEW")
                .usingGeneratedKeyColumns("review_id");
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue());
        log.info("Добавлен новый отзыв: reviewId={} на фильм: filmId={} ", review.getReviewId(), review.getFilmId());
        return review;
    }

    @Override
    public Review update(Review review) {
        String sqlUpdateReview = "UPDATE REVIEW SET content=?," +
                "is_positive =? WHERE review_id = ?";
        jdbcTemplate.update(sqlUpdateReview,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        log.info("Данные отзыва обновлены: reviewId={}", review.getReviewId());
        return findById(review.getReviewId());
    }

    @Override
    public void delete(long reviewId) {
        String sqlRemoveReviewById = "DELETE FROM REVIEW WHERE review_id = ?";
        jdbcTemplate.update(sqlRemoveReviewById, reviewId);
        log.info("Отзыв удален: id={}", reviewId);
    }

    @Override
    public Review findById(long reviewId) {
        String sqlFindReviewById = "SELECT * FROM REVIEW WHERE review_id = ?";
        return jdbcTemplate.queryForObject(sqlFindReviewById, reviewMapper, reviewId);
    }

    @Override
    public List<Review> findAllReviewByFilmId(Long filmId, Integer count) {
        if (filmId == null) {
            String sqlReviewsQuery = "SELECT * FROM REVIEW ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.queryForStream(sqlReviewsQuery, reviewMapper, count).collect(Collectors.toList());
        }
        String sqlReviewsQuery = "SELECT * FROM REVIEW WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.queryForStream(sqlReviewsQuery, reviewMapper, filmId, count).collect(Collectors.toList());
    }

    @Override
    public void addLikeToReview(long reviewId, long userId) {
        String addLikeReview = "INSERT INTO REVIEW_LIKE (review_id, user_id, is_like)  " +
                "VALUES (?, ?, ?)";
        if (jdbcTemplate.update(addLikeReview, reviewId, userId, true) > 0) {
            String updateUseful = "UPDATE REVIEW SET useful = useful + 1 WHERE review_id = ?";
            jdbcTemplate.update(updateUseful, reviewId);
            log.info("Пользователь с ID = {} добавил лайк для отзыва ID = {}.", userId, reviewId);
        } else {
            log.info("Ошибка при добавлении лайка для отзыва ID = {} от пользователя с ID = {}.", reviewId, userId);
            throw new ObjectNotFoundException(String.format("Ошибка при добавлении лайка для отзыва ID = %d " +
                    "от пользователя с ID = %d.", reviewId, userId));
        }
    }

    @Override
    public void addDislikeToReview(long reviewId, long userId) {
        String addDisLikeReview = "INSERT INTO REVIEW_LIKE (review_id, user_id, is_like) " +
                "VALUES (?, ?, ?)";
        if (jdbcTemplate.update(addDisLikeReview, reviewId, userId, false) > 0) {
            String updateUseful = "UPDATE REVIEW SET useful = useful - 1 WHERE review_id = ?";
            jdbcTemplate.update(updateUseful, reviewId);
            log.info("Пользователь с ID = {} добавил дизлайк для отзыва ID = {}.", userId, reviewId);
        } else {
            log.info("Ошибка при добавлении дизлайка для отзыва ID = {} от пользователя с ID = {}.", reviewId, userId);
            throw new ObjectNotFoundException(String.format("Ошибка при добавлении лайка для отзыва ID = %d " +
                    "от пользователя с ID = %d.", reviewId, userId));
        }
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        String deleteLikeReview = "DELETE FROM REVIEW_LIKE WHERE review_id = ? AND user_id = ?";
        if (jdbcTemplate.update(deleteLikeReview, reviewId, userId) < 1) {
            jdbcTemplate.update(deleteLikeReview, reviewId, userId);
            log.info("Пользователь с ID = {} удалил лайк для отзыва ID = {}.", userId, reviewId);
        } else {
            log.info("Ошибка при удалении лайка для отзыва ID = {} от пользователя с ID = {}.", reviewId, userId);
            throw new ObjectNotFoundException(String.format("Ошибка при удалении лайка для отзыва ID = {} " +
                    "от пользователя с ID = {}.", reviewId, userId));
        }
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        String deleteDisLikeReview = "DELETE FROM REVIEW_LIKE WHERE review_id = ? AND user_id = ?";
        if (jdbcTemplate.update(deleteDisLikeReview, reviewId, userId) < 1) {
            jdbcTemplate.update(deleteDisLikeReview, reviewId, userId);
            log.info("Пользователь с ID = {} удалил дизлайк для отзыва ID = {}.", userId, reviewId);
        } else {
            log.info("Ошибка при удалении дизлайка для отзыва ID = {} от пользователя с ID = {}.", reviewId, userId);
            throw new ObjectNotFoundException(String.format("Ошибка при удалении дизлайка для отзыва ID = {} " +
                    "от пользователя с ID = {}.", reviewId, userId));
        }
    }

    @Override
    public boolean isNotExistsReview(Long id) {
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet("SELECT * FROM REVIEW WHERE review_id = ?", id);
        return !reviewRows.next();
    }

}