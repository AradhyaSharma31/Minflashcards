package com.flashcard.FlashcardBackend.Repository;

import com.flashcard.FlashcardBackend.Entity.CategoryRequests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRequestsRepo extends JpaRepository<CategoryRequests, UUID> {
}
