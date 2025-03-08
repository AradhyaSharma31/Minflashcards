package com.flashcard.FlashcardBackend.Controller;

import com.flashcard.FlashcardBackend.DTO.CategoryDTO;
import com.flashcard.FlashcardBackend.DTO.CategoryRequestsDTO;
import com.flashcard.FlashcardBackend.Service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/flashcard/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create/{userId}/{requestId}")
    public ResponseEntity<CategoryDTO> createCategory(
            @PathVariable UUID userId,
            @PathVariable UUID requestId) {
        try {
            CategoryDTO categoryDTO = categoryService.createCategory(userId, requestId);
            return ResponseEntity.status(HttpStatus.CREATED).body(categoryDTO);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/generateRequest/{userId}")
    public ResponseEntity<CategoryRequestsDTO> generateCategoryRequest(
            @PathVariable UUID userId,
            @Valid @RequestBody CategoryRequestsDTO categoryRequestsDTO) {
        try {
            CategoryRequestsDTO categoryRequest = categoryService.sendCategoryRequest(userId, categoryRequestsDTO.getCategoryRequestName());
            return ResponseEntity.status(HttpStatus.CREATED).body(categoryRequest);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/updateDeckCategory/{deckId}/{categoryId}/{userId}")
    public ResponseEntity<CategoryDTO> updateDeckCategory(
            @PathVariable UUID deckId,
            @PathVariable UUID categoryId,
            @PathVariable UUID userId) {
        try {
            CategoryDTO updatedCategory = categoryService.selectCategory(categoryId, userId, deckId, null);
            return ResponseEntity.ok(updatedCategory);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable UUID categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok("Category deleted successfully.");
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        try {
            List<CategoryDTO> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/get/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable UUID categoryId) {
        try {
            CategoryDTO category = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/requests")
    public ResponseEntity<List<CategoryRequestsDTO>> getAllCategoryRequests() {
        try {
            List<CategoryRequestsDTO> requests = categoryService.getAllCategoryRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/requests/{requestId}")
    public ResponseEntity<CategoryRequestsDTO> getCategoryRequestById(@PathVariable UUID requestId) {
        try {
            CategoryRequestsDTO request = categoryService.getCategoryRequestById(requestId);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
