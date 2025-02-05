package com.flashcard.FlashcardBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SetsFolderDTO {

        private UUID id;
        private String folderName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Set<DeckDTO> deckDTOS;

}
