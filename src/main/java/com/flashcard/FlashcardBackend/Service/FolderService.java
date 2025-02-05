package com.flashcard.FlashcardBackend.Service;

import com.flashcard.FlashcardBackend.DTO.DeckDTO;
import com.flashcard.FlashcardBackend.DTO.SetsFolderDTO;

import java.util.List;
import java.util.UUID;

public interface FolderService {
    SetsFolderDTO createFolder(UUID userId, String folderName);
    SetsFolderDTO updateFolder(UUID folderId, UUID userId, String newFolderName);
    void deleteFolder(UUID folderId, UUID userId);
    List<SetsFolderDTO> getAllFolders(UUID userId);
    SetsFolderDTO getFolderById(UUID folderId, UUID userId);
    public List<DeckDTO> getAllDecksInFolder(UUID folderId, UUID userId);
    public void addDeckToFolder(UUID folderId, UUID deckId, UUID userId);
    public void removeDeckFromFolder(UUID folderId, UUID deckId, UUID userId);
}