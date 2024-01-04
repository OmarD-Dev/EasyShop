package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("profile")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ProfileController {
    private ProfileDao profileDao;
    private UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao){
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @GetMapping
    public Profile getById (Principal principal){
        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();
        return profileDao.getByUserId(userId);
    }

    @PutMapping
    public void updateProfile(Principal principal, @RequestBody Profile profile){
        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();

        profileDao.updateProfile(userId, profile);
    }

}
