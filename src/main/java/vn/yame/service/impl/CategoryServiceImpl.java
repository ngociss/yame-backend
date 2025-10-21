package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.common.enums.CommonStatus;
import vn.yame.common.enums.ErrorCode;
import vn.yame.dto.request.CategoryRequest;
import vn.yame.dto.reponse.CategoryResponse;
import vn.yame.exception.ExistingResourcesException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.CategoryMapper;
import vn.yame.model.Category;
import vn.yame.repository.CategoryRepository;
import vn.yame.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Creating new category with name: {}", request.getName());

        // Check if slug already exists
        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new ExistingResourcesException(
                ErrorCode.CATEGORY_SLUG_DUPLICATE,
                "Category with slug '" + request.getSlug() + "' already exists"
            );
        }

        // Check if name already exists
        if (categoryRepository.existsByName(request.getName())) {
            throw new ExistingResourcesException(
                ErrorCode.CATEGORY_ALREADY_EXISTS,
                "Category with name '" + request.getName() + "' already exists"
            );
        }

        Category category = categoryMapper.toEntity(request);
        category.setStatus(CommonStatus.INACTIVE);
        Category savedCategory = categoryRepository.save(category);

        log.info("Category created successfully with id: {}", savedCategory.getId());
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.info("Updating category with id: {}", id);

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.CATEGORY_NOT_FOUND,
                "Category not found with id: " + id
            ));

        // Check if slug is being changed and if new slug already exists
        if (!category.getSlug().equals(request.getSlug()) &&
            categoryRepository.existsBySlug(request.getSlug())) {
            throw new ExistingResourcesException(
                ErrorCode.CATEGORY_SLUG_DUPLICATE,
                "Category with slug '" + request.getSlug() + "' already exists"
            );
        }

        // Check if name is being changed and if new name already exists
        if (!category.getName().equals(request.getName()) &&
            categoryRepository.existsByName(request.getName())) {
            throw new ExistingResourcesException(
                ErrorCode.CATEGORY_ALREADY_EXISTS,
                "Category with name '" + request.getName() + "' already exists"
            );
        }

        categoryMapper.updateEntity(category, request);
        Category updatedCategory = categoryRepository.save(category);

        log.info("Category updated successfully with id: {}", id);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.CATEGORY_NOT_FOUND,
                "Category not found with id: " + id
            ));

        // TODO: Check if category has products before deleting
//         if (category.getProducts() != null && !category.getProducts().isEmpty() && category.getStatus() == CommonStatus.ACTIVE) {
//             throw new InvalidDataException(
//                 ErrorCode.CATEGORY_HAS_PRODUCTS,
//                 "Cannot delete category with existing products"
//             );
//         }

        categoryRepository.delete(category);
        log.info("Category deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        log.info("Fetching category with id: {}", id);

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.CATEGORY_NOT_FOUND,
                "Category not found with id: " + id
            ));

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        log.info("Fetching category with slug: {}", slug);

        Category category = categoryRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.CATEGORY_NOT_FOUND,
                "Category not found with slug: " + slug
            ));

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        log.info("Fetching all categories");
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toResponseList(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategoriesWithPagination(Pageable pageable) {
        log.info("Fetching categories with pagination - page: {}, size: {}",
                 pageable.getPageNumber(), pageable.getPageSize());

        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return categoryPage.map(categoryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveCategories() {
        log.info("Fetching active categories");

        List<Category> categories = categoryRepository.findAll().stream()
            .filter(category -> category.getStatus() == CommonStatus.ACTIVE)
            .collect(Collectors.toList());

        return categoryMapper.toResponseList(categories);
    }

}

