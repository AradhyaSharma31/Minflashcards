package com.flashcard.FlashcardBackend.Entity;

import com.flashcard.FlashcardBackend.Enumeration.CardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

// Card Entity
@Entity
@Table(name = "card")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false, updatable = true)
    private String term;

    @Column(nullable = false, updatable = true)
    private String definition;

    @Column(name = "image")
    private String image;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", referencedColumnName = "id", nullable = false)
    private Deck deck;

    private float easeFactor = 2.5f;
    @Column(name = "study_interval")
    private Integer interval;
    private int repetitionCount = 0;
    private int reviewCount = 0;
    private int lapses = 0;

    private ZonedDateTime nextReview = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
    private byte quality;

    @Enumerated(EnumType.STRING)
    private CardStatus status = CardStatus.NEW;

    private boolean isDue;
    private int consecutiveCorrectAnswers = 0;
    private ZonedDateTime lastTimeEasy = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
    private String performance;

    public void updateCard(String performance) {
        reviewCount++; // Increment total review count for the card
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")); // Get the current time

        ZonedDateTime previousNextReview = this.nextReview; // Store the previous nextReview

//        easeFactor += (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f));
//        easeFactor = Math.max(1.3f, easeFactor); // Ensure easeFactor doesn't go below 1.3

        easeFactor += (float) (0.1f - 0.14f * (5 - quality) - 0.02f * Math.pow((5 - quality), 2));
        easeFactor = Math.max(1.3f, easeFactor);

        switch (performance) {
            case "AGAIN":
                lapses++;
                if (previousNextReview != null) {
                    nextReview = lapses > 3 ? previousNextReview.plusSeconds(30) : previousNextReview.plusMinutes(1); // Increment previous nextReview dynamically based of lapses
                } else {
                    nextReview = now.plusMinutes(1); // Set to current time plus 1 minute if previous was null
                }
                interval = 1; // Reset interval to 1 minute
                status = CardStatus.RELEARNING; // Set status to RELEARNING
                break;

            case "HARD":
                if (status == CardStatus.LEARNING) {
                    interval = (int) Math.ceil(interval * 1.3); // Increase by 30%
                    nextReview = now.plusMinutes(interval); // Update next review time
                } else if (status == CardStatus.REVIEWING) {
                    easeFactor = Math.max(1.3f, easeFactor - 0.15f); // Reduce ease factor slightly
                    interval = (int) Math.ceil(interval * (lapses > 2 ? 0.6 : 0.7)); // Decrease interval dynamically based of lapses
                    nextReview = now.plusDays(interval); // Update next review time
                }
                break;

            case "GOOD":

                easeFactor *= 1.05f; // increment ease factor by 10%

                if (status == CardStatus.NEW || status == CardStatus.LEARNING) {
                    if (interval == 0) {
                        // First learning step (10 minutes)
                        interval = 10; // 10 minutes
                        nextReview = now.plusMinutes(interval);
                        status = CardStatus.LEARNING; // Stay in learning phase
                    } else if (interval == 10) {
                        // After first GOOD, move to next learning step (1 day)
                        interval = 1; // 1 day
                        nextReview = now.plusDays(interval);
                        status = CardStatus.REVIEWING; // Transition to review phase
                    } else {
                        // Continue in the reviewing phase
                        interval = (int) Math.ceil(interval * easeFactor);
                        nextReview = now.plusDays(interval);
                    }
                } else {
                    interval = (int) Math.ceil(interval * easeFactor);
                    nextReview = now.plusDays(interval);
                }
                break;

            case "EASY":

                easeFactor *= 1.15f; // Increase ease by 15%

                if (status == CardStatus.NEW || status == CardStatus.LEARNING) {
                    interval = 5; // For example, 5 days after easy in learning
                    nextReview = now.plusDays(interval);
                    status = CardStatus.REVIEWING; // Transition to review phase
                } else {
                    interval = (int) Math.ceil(interval * 1.5); // Increase interval by 50%
                    nextReview = now.plusDays(interval);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown performance type: " + performance);
        }

        if (interval > 36500) {
            interval = 36500; // Cap interval at 100 years
            nextReview = now.plusDays(interval);
        }

        if (nextReview.isBefore(now)) {
            nextReview = now.plusMinutes(1); // minimum next review 1 minute ahead
        }

        // OLD IMPLEMENTATION: Update due status
//        this.isDue = this.nextReview.isBefore(now);

        this.isDue = ChronoUnit.SECONDS.between(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")), this.nextReview) <= 0;

        if (status == CardStatus.REVIEWING && interval >= 21 && easeFactor >= 2.5 && consecutiveCorrectAnswers >= 5) {
            status = CardStatus.MATURE; // Mark as mature after 21 days
        }
    }
}

