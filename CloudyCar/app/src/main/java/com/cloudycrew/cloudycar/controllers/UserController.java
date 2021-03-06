package com.cloudycrew.cloudycar.controllers;

import com.cloudycrew.cloudycar.models.User;
import com.cloudycrew.cloudycar.users.DuplicateUserException;
import com.cloudycrew.cloudycar.users.IUserHistoryService;
import com.cloudycrew.cloudycar.users.IUserService;
import com.cloudycrew.cloudycar.users.IncompleteUserException;
import com.cloudycrew.cloudycar.users.UserDoesNotExistException;

import java.util.Date;

/**
 * Created by George on 2016-10-23.
 */
public class UserController {
    private IUserService userService;
    private IUserHistoryService userHistoryService;

    /**
     * Instantiates a new User controller.
     *
     * @param userService the user service
     */
    public UserController(IUserService userService, IUserHistoryService userHistoryService) {
        this.userService = userService;
        this.userHistoryService = userHistoryService;
    }

    /**
     * Create user.
     *
     * @param user the user
     * @throws DuplicateUserException  the duplicate user exception
     * @throws IncompleteUserException the incomplete user exception
     */
    public void createUser(User user) throws DuplicateUserException, IncompleteUserException {
        userService.createUser(user);
    }

    /**
     * Gets user.
     *
     * @param username the username
     * @return the user
     * @throws UserDoesNotExistException the user does not exist exception
     */
    public User getUser(String username) throws UserDoesNotExistException {
        return userService.getUser(username);
    }

    /**
     * Gets current user.
     *
     * @return the current user
     * @throws UserDoesNotExistException the user does not exist exception
     */
    public User getCurrentUser() throws UserDoesNotExistException{
        return userService.getCurrentUser();
    }

    /**
     * Sets current user.
     *
     * @param user the user
     */
    public void setCurrentUser(User user) { userService.setCurrentUser(user); }

    /**
     * Update user.
     *
     * @param user the user
     */
    public void updateUser(User user) {
        userService.updateUser(user);
    }

    /**
     * Update current user.
     *
     * @param user the user
     */
    public void updateCurrentUser(User user) { userService.updateCurrentUser(user); }

    /**
     * Marks a request as read for the current user
     *
     * @param requestId the request being read
     */
    public void markRequestAsRead(String requestId) {
        userHistoryService.markRequestAsRead(requestId);
    }

    /**
     * Gets the specific last read time for a request
     *
     * @param requestId the being queried
     *
     * @return the last read time of the request
     */
    public Date getLastReadTime(String requestId) {
        return userHistoryService.getLastReadTime(requestId);
    }
}
