package com.flashcard.FlashcardBackend.Repository;

import com.flashcard.FlashcardBackend.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepo extends JpaRepository<Role, UUID> {
}
