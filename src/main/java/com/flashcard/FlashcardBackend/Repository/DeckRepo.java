package com.flashcard.FlashcardBackend.Repository;

import com.flashcard.FlashcardBackend.Entity.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeckRepo extends JpaRepository<Deck, UUID> {

}
