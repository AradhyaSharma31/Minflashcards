package com.flashcard.FlashcardBackend.Repository;

import com.flashcard.FlashcardBackend.Entity.SetsFolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FolderRepo extends JpaRepository<SetsFolder, UUID> {

    List<SetsFolder> findByUserId(UUID userId);

}