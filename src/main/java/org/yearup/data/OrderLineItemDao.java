package org.yearup.data;

import org.yearup.models.Order;
import org.yearup.models.OrderLineItem;
import org.yearup.models.ShoppingCartItem;

import java.util.List;

public interface OrderLineItemDao {
    List<OrderLineItem> getByOrderId(int orderId);
    void create(Order order, ShoppingCartItem shoppingCartItem);
}
