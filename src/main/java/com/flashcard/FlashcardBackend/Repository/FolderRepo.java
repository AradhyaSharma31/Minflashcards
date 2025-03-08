package com.flashcard.FlashcardBackend.Repository;

import com.flashcard.FlashcardBackend.Entity.SetsFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FolderRepo extends JpaRepository<SetsFolder, UUID> {

    List<SetsFolder> findByUserId(UUID userId);

}