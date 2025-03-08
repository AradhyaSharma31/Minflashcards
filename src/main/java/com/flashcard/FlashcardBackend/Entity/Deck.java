package com.flashcard.FlashcardBackend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

// Deck Entity
@Entity
@Table(name = "deck")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();


    @Column(nullable = false, updatable = true, length = 50)
    private String title;

    private String description;

    private String categoryName;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> cards = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "folder_decks",
            joinColumns = @JoinColumn(name = "deck_id"),
            inverseJoinColumns = @JoinColumn(name = "folder_id")
    )
    private Set<SetsFolder> folders = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = true)
    private Category category;

    // Method to retrieve the next card using a priority queue
    public Card getNextCard() {
        PriorityQueue<Card> cardQueue = new PriorityQueue<>(Comparator.comparing(Card::getNextReview));

        cardQueue.addAll(cards);

        return cardQueue.peek();
    }

    public boolean validateCardBelongsToDeck(UUID cardId) {
        return cards.stream().anyMatch(card -> card.getId().equals(cardId));
    }
}
