package com.flashcard.FlashcardBackend.Service.Implementation;

import com.flashcard.FlashcardBackend.DTO.CardDTO;
import com.flashcard.FlashcardBackend.DTO.DeckDTO;
import com.flashcard.FlashcardBackend.DTO.SetsFolderDTO;
import com.flashcard.FlashcardBackend.Entity.Deck;
import com.flashcard.FlashcardBackend.Entity.SetsFolder;
import com.flashcard.FlashcardBackend.Entity.User;
import com.flashcard.FlashcardBackend.Repository.DeckRepo;
import com.flashcard.FlashcardBackend.Repository.FolderRepo;
import com.flashcard.FlashcardBackend.Repository.UserRepo;
import com.flashcard.FlashcardBackend.Service.FolderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FolderServiceImpl implements FolderService {

    private final FolderRepo setsFolderRepo;
    private final UserRepo userRepo;
    private final DeckRepo deckRepo;
    private final ModelMapper modelMapper;

    @Autowired
    public FolderServiceImpl(FolderRepo setsFolderRepo, UserRepo userRepo, DeckRepo deckRepo, ModelMapper modelMapper) {
        this.setsFolderRepo = setsFolderRepo;
        this.userRepo = userRepo;
        this.deckRepo = deckRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public SetsFolderDTO createFolder(UUID userId, String folderName) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SetsFolder folder = new SetsFolder();
        folder.setFolderName(folderName);
        folder.setCreatedAt(LocalDateTime.now());
        folder.setUpdatedAt(LocalDateTime.now());
        folder.setUser(user);
        folder.setDecks(new HashSet<>());  // Initialize the HashSet to avoid NullPointerException

        SetsFolder savedFolder = setsFolderRepo.save(folder);
        return modelMapper.map(savedFolder, SetsFolderDTO.class);
    }

    @Override
    @Transactional
    public SetsFolderDTO updateFolder(UUID folderId, UUID userId, String newFolderName) {
        SetsFolder folder = setsFolderRepo.findById(folderId)
                .filter(f -> f.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Folder not found or does not belong to user"));

        folder.setFolderName(newFolderName);
        folder.setUpdatedAt(LocalDateTime.now());
        SetsFolder updatedFolder = setsFolderRepo.save(folder);
        return modelMapper.map(updatedFolder, SetsFolderDTO.class);
    }

    @Override
    @Transactional
    public void deleteFolder(UUID folderId, UUID userId) {
        SetsFolder folder = setsFolderRepo.findById(folderId)
                .filter(f -> f.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Folder not found or does not belong to user"));

        // Remove all deck associations
        Set<Deck> decks = new HashSet<>(folder.getDecks());
        for (Deck deck : decks) {
            deck.getFolders().remove(folder);
            folder.getDecks().remove(deck);
            deckRepo.save(deck);
        }

        // Now delete the folder
        setsFolderRepo.delete(folder);
    }

    @Override
    public List<SetsFolderDTO> getAllFolders(UUID userId) {
        List<SetsFolder> folders = setsFolderRepo.findByUserId(userId);

        return folders.stream()
                .map(folder -> {
                    Set<DeckDTO> deckDTOs = folder.getDecks().stream()
                            .map(deck -> modelMapper.map(deck, DeckDTO.class))
                            .collect(Collectors.toSet());

                    SetsFolderDTO folderDTO = modelMapper.map(folder, SetsFolderDTO.class);
                    folderDTO.setDeckDTOS(deckDTOs);
                    return folderDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public SetsFolderDTO getFolderById(UUID folderId, UUID userId) {
        SetsFolder folder = setsFolderRepo.findById(folderId)
                .filter(f -> f.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Folder not found or does not belong to user"));

        Set<DeckDTO> deckDTOs = folder.getDecks().stream()
                .map(deck -> modelMapper.map(deck, DeckDTO.class))
                .collect(Collectors.toSet());

        SetsFolderDTO folderDTO = modelMapper.map(folder, SetsFolderDTO.class);
        folderDTO.setDeckDTOS(deckDTOs);
        return folderDTO;
    }

    @Override
    public List<DeckDTO> getAllDecksInFolder(UUID folderId, UUID userId) {
        SetsFolder folder = setsFolderRepo.findById(folderId)
                .filter(f -> f.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Folder not found or does not belong to user"));

        return folder.getDecks().stream()
                .map(deck -> modelMapper.map(deck, DeckDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addDeckToFolder(UUID folderId, UUID deckId, UUID userId) {
        SetsFolder folder = setsFolderRepo.findById(folderId)
                .filter(f -> f.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Folder not found or does not belong to user"));

        Deck deck = deckRepo.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        folder.getDecks().add(deck);
        deck.getFolders().add(folder);

        setsFolderRepo.save(folder);
    }

    @Override
    @Transactional
    public void removeDeckFromFolder(UUID folderId, UUID deckId, UUID userId) {
        SetsFolder folder = setsFolderRepo.findById(folderId)
                .filter(f -> f.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Folder not found or does not belong to user"));

        Deck deck = deckRepo.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        folder.getDecks().remove(deck);
        deck.getFolders().remove(folder);

        setsFolderRepo.save(folder);
        deckRepo.save(deck);
    }
}