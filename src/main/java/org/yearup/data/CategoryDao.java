package org.yearup.data;

import org.springframework.stereotype.Component;
import org.yearup.models.Category;

import java.util.List;
@Component
public interface CategoryDao
{
    List<Category> getAllCategories();
    Category getById(int categoryId);
    Category create(Category category);
    void update(int categoryId, Category category);
    void delete(int categoryId);
}
