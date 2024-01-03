package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    void addProductToCart(int userId, ShoppingCart item);
    void removeProductFromCart(int productId, int userId);
    void updateProductQuantity (int productId, int userId, int quantity);
    void clearCart (int userId);
}
