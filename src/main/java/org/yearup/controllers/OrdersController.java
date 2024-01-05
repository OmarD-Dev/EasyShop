package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yearup.data.ProfileDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;

@RestController
@RequestMapping("orders")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class OrdersController {
    private UserDao userDao;
    private ShoppingCartDao shoppingCartDao;
    private ProfileDao profileDao;

    @Autowired
    public OrdersController(UserDao userDao, ShoppingCartDao shoppingCartDao, ProfileDao profileDao){
        this.userDao= userDao;
        this.shoppingCartDao= shoppingCartDao;
        this.profileDao = profileDao;
    }

}
