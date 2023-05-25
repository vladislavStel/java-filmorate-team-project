package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review upadteReview(@Valid @RequestBody Review review) {
        return reviewService.upadateReview(review);
    }

    @DeleteMapping("{id}")
    public void removeReview(@PathVariable("id") long id) {
        reviewService.removeReview(id);
    }

    @GetMapping("{id}")
    public Review findReviewById(@PathVariable("id") long id) {
        return reviewService.findReviewById(id);
    }

    @GetMapping
    public Collection<Review> getAllReviewsByIdFilm(@RequestParam(required = false) Long filmId, @RequestParam(defaultValue = "10", required = false) int count) {
        return reviewService.getAllReviewsByFilmId(filmId, count);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        reviewService.addLikeToReview(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void addDislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.addDislikeToReview(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public void removeDislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.removeDislike(id, userId);
    }


}
