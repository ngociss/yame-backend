package vn.yame.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.yame.common.enums.CommonStatus;
import vn.yame.dto.request.CategoryRequest;
import vn.yame.dto.reponse.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);

    CategoryResponse getCategoryById(Long id);

    CategoryResponse getCategoryBySlug(String slug);

    List<CategoryResponse> getAllCategories();

    Page<CategoryResponse> getAllCategoriesWithPagination(Pageable pageable);

    List<CategoryResponse> getActiveCategories();

    CategoryResponse updateStatus(Long id, CommonStatus status);
}
