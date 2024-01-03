package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import javax.swing.text.Caret;
import java.util.List;

// add the annotations to make this a REST controller
@RestController
// add the annotation to make this controller the endpoint for the following url
    // http://localhost:8080/categories
@RequestMapping("categories")
// add annotation to allow cross site origin requests
@CrossOrigin
public class CategoriesController
{
    private CategoryDao categoryDao;

    private ProductDao productDao;


    // create an Autowired controller to inject the categoryDao and ProductDao
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao){
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    // add the appropriate annotation for a get action
    @GetMapping
    public List<Category> getAll()
    {
        // find and return all categories
        return categoryDao.getAllCategories();

    }

    // add the appropriate annotation for a get action
    @GetMapping("{id}")
    public Category getById(@PathVariable int id)
    {
        // get the category by id
        Category category = categoryDao.getById(id);
        if(category == null){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,"Error Category not found."
            );
        }
        return category;
    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    @GetMapping("{categoryId}/products")
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        // get a list of product by categoryId

        return productDao.listByCategoryId(categoryId);
    }

    // add annotation to call this method for a POST action
    @PostMapping
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Category addCategory(@RequestBody Category category)
    {
        // insert the category
        try {
            if (categoryDao.create(category)!= null){
                throw new ResponseStatusException(HttpStatus.CREATED);
            }
            return categoryDao.create(category);
        }catch (Exception e){
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong please try again."
            );
        }

    }

    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    @PutMapping("{id}")
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        // update the category by id
        try {
             categoryDao.update(id,category);
        }catch (Exception e){
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong, please try again."
            );
        }
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCategory(@PathVariable int id)
    {
        // delete the category by id
        try {
            categoryDao.delete(id);
        }catch (Exception e){
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong, please try again"
            );
        }
         if (categoryDao.getById(id)== null){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Successfully deleted");
        }
    }
}
