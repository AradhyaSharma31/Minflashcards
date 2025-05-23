package com.flashcard.FlashcardBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeckDTO {

    private UUID id;

    @NotBlank(message = "Title Cannot Be Empty")
    private String title;

    private String description;

    private String categoryName;

    private List<CardDTO> cards;
}
