package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    ShoppingCart addProductToCart(int userId, int productId);
    void removeProductFromCart(int productId, int userId);
    void updateProductQuantity (int productId, int userId, int quantity);
    void clearCart (int userId);
}
