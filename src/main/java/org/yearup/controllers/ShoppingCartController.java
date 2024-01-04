package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

// convert this class to a REST controller
// only logged in users should have access to these actions
@RestController
@RequestMapping("cart")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController
{
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao){
        this.shoppingCartDao = shoppingCartDao;
        this.productDao= productDao;
        this.userDao= userDao;
    }


    // each method in this controller requires a Principal object as a parameter
    @GetMapping()
    public ShoppingCart getCart(Principal principal)
    {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shoppingcartDao to get all items in the cart and return the cart
            return shoppingCartDao.getByUserId(userId);


    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added
    @PostMapping("/products/{id}")
    public ShoppingCart addShoppingCart(@PathVariable int id, Principal principal){

        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();

        ShoppingCart shoppingCart = shoppingCartDao.addProductToCart(userId,id);
        return shoppingCart;
    }


    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated
    @PutMapping("/products/{id}")
    public ShoppingCart updateShoppingCart(@PathVariable int id, @RequestBody ShoppingCartItem shoppingCartItem, Principal principal){
        String userName= principal.getName();
        User user= userDao.getByUserName(userName);
        int userId= user.getId();

        shoppingCartDao.updateProductQuantity(id, userId, shoppingCartItem.getQuantity());

        return shoppingCartDao.getByUserId(userId);
    }

    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart
    @DeleteMapping
    public void deleteShoppingCart(Principal principal){
        String userName = principal.getName();
        User user  = userDao.getByUserName(userName);
        int userId = user.getId();

        shoppingCartDao.clearCart(userId);
    }

}
