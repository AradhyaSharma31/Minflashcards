package com.flashcard.FlashcardBackend.Service.Implementation;

import com.flashcard.FlashcardBackend.DTO.CategoryDTO;
import com.flashcard.FlashcardBackend.DTO.CategoryRequestsDTO;
import com.flashcard.FlashcardBackend.DTO.DeckDTO;
import com.flashcard.FlashcardBackend.Entity.Category;
import com.flashcard.FlashcardBackend.Entity.CategoryRequests;
import com.flashcard.FlashcardBackend.Entity.Deck;
import com.flashcard.FlashcardBackend.Entity.User;
import com.flashcard.FlashcardBackend.Repository.CategoryRepo;
import com.flashcard.FlashcardBackend.Repository.CategoryRequestsRepo;
import com.flashcard.FlashcardBackend.Repository.DeckRepo;
import com.flashcard.FlashcardBackend.Repository.UserRepo;
import com.flashcard.FlashcardBackend.Service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final CategoryRequestsRepo categoryRequestsRepo;
    private final DeckRepo deckRepo;

    @Autowired
    public CategoryServiceImpl(CategoryRepo categoryRepo, UserRepo userRepo, ModelMapper modelMapper, CategoryRequestsRepo categoryRequestsRepo, DeckRepo deckRepo) {
        this.categoryRepo = categoryRepo;
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
        this.categoryRequestsRepo = categoryRequestsRepo;
        this.deckRepo = deckRepo;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDTO createCategory(UUID userId, UUID categoryRequestId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        CategoryRequests categoryRequests = categoryRequestsRepo.findById(categoryRequestId)
                .orElseThrow(() -> new RuntimeException("Request Not Found"));

        Category category = new Category();
        category.setCategoryName(categoryRequests.getCategoryRequestName());
        category.setUser(user);

        categoryRequestsRepo.delete(categoryRequests);

        return mapToDTO(categoryRepo.save(category));
    }

    @Override
    public CategoryRequestsDTO sendCategoryRequest(UUID userId, String categoryRequestName) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        CategoryRequests categoryRequests = new CategoryRequests();
        categoryRequests.setCategoryRequestName(categoryRequestName.toLowerCase());
        categoryRequests.setUser(user);

        categoryRequests = categoryRequestsRepo.save(categoryRequests);

        return modelMapper.map(categoryRequests, CategoryRequestsDTO.class);
    }

    @Override
    @Transactional
    public DeckDTO updateDeckCategory(UUID deckId, UUID categoryId, UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        Deck deck = deckRepo.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck Not Found"));

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category Not Found"));

        deck.setCategory(category);
        Deck updatedDeck = deckRepo.save(deck);

        return modelMapper.map(updatedDeck, DeckDTO.class);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category Not Found"));

        category.getDecks().forEach(deck -> deck.setCategory(null));
        deckRepo.saveAll(category.getDecks());

        categoryRepo.delete(category);
    }


    @Override
    public CategoryDTO selectCategory(UUID categoryId, UUID userId, UUID deckId, DeckDTO deckDTO) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category Not Found"));

        Deck deck = deckRepo.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck Not Found"));

        deck.setCategory(category);
        deck.setCategoryName(category.getCategoryName());
        deckRepo.save(deck);

        return mapToDTO(category);
    }


    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepo.findAll();

        return categories.stream()
                .map(category -> {
                    CategoryDTO categoryDTO = mapToDTO(category);
                    categoryDTO.setDeckDTOS(category.getDecks().stream()
                            .map(deck -> modelMapper.map(deck, DeckDTO.class))
                            .collect(Collectors.toList()));
                    return categoryDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<CategoryRequestsDTO> getAllCategoryRequests() {
        List<CategoryRequests> categoryRequests = categoryRequestsRepo.findAll();

        return categoryRequests.stream()
                .map(request -> modelMapper.map(request, CategoryRequestsDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryRequestsDTO getCategoryRequestById(UUID categoryRequestId) {
        CategoryRequests categoryRequest = categoryRequestsRepo.findById(categoryRequestId)
                .orElseThrow(() -> new RuntimeException("Category Request Not Found"));

        return modelMapper.map(categoryRequest, CategoryRequestsDTO.class);
    }

    @Override
    public CategoryDTO getCategoryById(UUID categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found or does not belong to the user"));

        CategoryDTO categoryDTO = mapToDTO(category);

        categoryDTO.setDeckDTOS(category.getDecks().stream()
                .map(deck -> modelMapper.map(deck, DeckDTO.class))
                .collect(Collectors.toList()));

        return categoryDTO;
    }

    private CategoryDTO mapToDTO(Category category) {
        return modelMapper.map(category, CategoryDTO.class);
    }
}
