package com.flashcard.FlashcardBackend.Service;

import com.flashcard.FlashcardBackend.DTO.CategoryDTO;
import com.flashcard.FlashcardBackend.DTO.CategoryRequestsDTO;
import com.flashcard.FlashcardBackend.DTO.DeckDTO;
import com.flashcard.FlashcardBackend.Repository.CategoryRepo;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    public CategoryDTO createCategory(UUID userId, UUID categoryRequestId);

    public CategoryRequestsDTO sendCategoryRequest(UUID userId, String categoryRequestName);

    public DeckDTO updateDeckCategory(UUID deckId, UUID categoryId, UUID userId);

    public void deleteCategory(UUID categoryId);

    public CategoryDTO selectCategory(UUID categoryId, UUID userId, UUID deckId, DeckDTO deckDTO);

    public List<CategoryDTO> getAllCategories();

    public List<CategoryRequestsDTO> getAllCategoryRequests();

    public CategoryRequestsDTO getCategoryRequestById(UUID categoryRequestId);

    public CategoryDTO getCategoryById(UUID categoryId);
}
