package com.flashcard.FlashcardBackend.Controller;

import com.flashcard.FlashcardBackend.DTO.DeckDTO;
import com.flashcard.FlashcardBackend.DTO.SetsFolderDTO;
import com.flashcard.FlashcardBackend.Service.FolderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/flashcard/folder")
public class FolderController {

    @Autowired
    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    // Create a new folder for a user
    @PostMapping("/createFolder/{userId}")
    public ResponseEntity<SetsFolderDTO> createFolder(@PathVariable UUID userId, @Valid @RequestBody SetsFolderDTO setsFolderDTO) {
        try {
            SetsFolderDTO createdFolder = folderService.createFolder(userId, setsFolderDTO.getFolderName());
            log.info("Creating folder for user: {}, with name: {}", userId, setsFolderDTO.getFolderName());
            return ResponseEntity.status(201).body(createdFolder);
        } catch (Exception e) {
            log.info("Couldn't create folder for user: {}, with name: {}", userId, setsFolderDTO.getFolderName());
            return ResponseEntity.status(400).body(null);
        }
    }

    // Update an existing folder
    @PutMapping("/updateFolder/{userId}/{folderId}")
    public ResponseEntity<SetsFolderDTO> updateFolder(@PathVariable UUID folderId, @PathVariable UUID userId, @RequestBody SetsFolderDTO setsFolderDTO) {
        try {
            SetsFolderDTO updatedFolder = folderService.updateFolder(folderId, userId, setsFolderDTO.getFolderName());
            return ResponseEntity.status(201).body(updatedFolder);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Delete a folder
    @DeleteMapping("/deleteFolder/{folderId}")
    public ResponseEntity<String> deleteFolder(@PathVariable UUID folderId, @RequestParam UUID userId) {
        try {
            folderService.deleteFolder(folderId, userId);
            return ResponseEntity.ok("Folder Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    // Get all folders of a user
    @GetMapping("/getAllFolders")
    public ResponseEntity<List<SetsFolderDTO>> getAllFolders(@RequestParam UUID userId) {
        try {
            List<SetsFolderDTO> folderDTOS = folderService.getAllFolders(userId);
            return ResponseEntity.status(200).body(folderDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(null);
        }
    }

    // Get a folder by its ID
    @GetMapping("/getFolder/{folderId}")
    public ResponseEntity<SetsFolderDTO> getFolderById(@PathVariable UUID folderId, @RequestParam UUID userId) {
        try {
            SetsFolderDTO folderDTO = folderService.getFolderById(folderId, userId);
            return ResponseEntity.ok(folderDTO);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Get all decks inside a specific folder
    @GetMapping("/getDecksInFolder/{folderId}")
    public ResponseEntity<List<DeckDTO>> getAllDecksInFolder(@PathVariable UUID folderId, @RequestParam UUID userId) {
        try {
            List<DeckDTO> deckDTOS = folderService.getAllDecksInFolder(folderId, userId);
            return ResponseEntity.status(200).body(deckDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(null);
        }
    }

    // Add a deck to a folder
    @PostMapping("/addDeckToFolder/{folderId}/{deckId}")
    public ResponseEntity<String> addDeckToFolder(@PathVariable UUID folderId, @PathVariable UUID deckId, @RequestParam UUID userId) {
        try {
            folderService.addDeckToFolder(folderId, deckId, userId);
            return ResponseEntity.ok("Deck added to folder successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    // Remove a deck from a folder
    @DeleteMapping("/removeDeckFromFolder/{folderId}/{deckId}")
    public ResponseEntity<String> removeDeckFromFolder(@PathVariable UUID folderId, @PathVariable UUID deckId, @RequestParam UUID userId) {
        try {
            folderService.removeDeckFromFolder(folderId, deckId, userId);
            return ResponseEntity.ok("Deck removed from folder successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }
}
