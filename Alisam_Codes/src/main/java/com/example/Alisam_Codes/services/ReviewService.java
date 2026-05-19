package com.example.Alisam_Codes.services;

import com.example.Alisam_Codes.models.Review;
import com.example.Alisam_Codes.models.User;
import com.example.Alisam_Codes.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public Review saveReview(Review review) {
        Review savedReview = reviewRepository.save(review);

        // Feature: Reward 3 coins if they provided a photo
        if (savedReview.getPhotoUrl() != null && !savedReview.getPhotoUrl().isEmpty() && savedReview.getUser() != null) {
            User user = savedReview.getUser();
            int currentCoins = user.getCoins() != null ? user.getCoins() : 0;
            user.setCoins(currentCoins + 3);
            userService.updateUser(user);
        }

        return savedReview;
    }
}
