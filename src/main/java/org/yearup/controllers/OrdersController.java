package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.*;
import org.yearup.models.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("orders")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class OrdersController {


    private UserDao userDao;
    private ShoppingCartDao shoppingCartDao;
    private ProfileDao profileDao;
    private OrderDao orderDao;
    private OrderLineItemDao orderLineItemDao;

    @Autowired
    public OrdersController(UserDao userDao, ShoppingCartDao shoppingCartDao, ProfileDao profileDao, OrderDao orderDao, OrderLineItemDao orderLineItemDao) {
        this.userDao = userDao;
        this.shoppingCartDao = shoppingCartDao;
        this.profileDao = profileDao;
        this.orderDao = orderDao;
        this.orderLineItemDao = orderLineItemDao;
    }

    @PostMapping()
    public List<OrderLineItem> orderCheckout(Principal principal){
        String userName = principal.getName();
        User user= userDao.getByUserName(userName);
        Profile profile = profileDao.getByUserId(user.getId());

        ShoppingCart shoppingCart = shoppingCartDao.getByUserId(profile.getUserId());

        Order order = orderDao.create(profile, shoppingCart);

        for (ShoppingCartItem item :  shoppingCart.getItems().values()){
            orderLineItemDao.create(order,item);
        }
        List<OrderLineItem> orderLineItems = orderLineItemDao.getByOrderId(order.getOrderId());
        shoppingCartDao.clearCart(profile.getUserId());

        return orderLineItems;
    }



}
