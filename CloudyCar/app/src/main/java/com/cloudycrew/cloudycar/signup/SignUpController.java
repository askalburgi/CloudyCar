package com.cloudycrew.cloudycar.signup;

import com.cloudycrew.cloudycar.ViewController;
import com.cloudycrew.cloudycar.controllers.UserController;
import com.cloudycrew.cloudycar.email.Email;
import com.cloudycrew.cloudycar.models.phonenumbers.PhoneNumber;
import com.cloudycrew.cloudycar.models.User;
import com.cloudycrew.cloudycar.scheduling.ISchedulerProvider;
import com.cloudycrew.cloudycar.utils.ObservableUtils;

import rx.functions.Action0;
import rx.functions.Action1;


/**
 * Created by George on 2016-11-09.
 */
public class SignUpController extends ViewController<ISignUpView> {
    private UserController userController;
    private ISchedulerProvider schedulerProvider;

    /**
     * Instantiates a new Sign up controller.
     *
     * @param userController    the user controller
     * @param schedulerProvider the scheduler provider
     */
    public SignUpController(UserController userController, ISchedulerProvider schedulerProvider) {
        this.userController = userController;
        this.schedulerProvider = schedulerProvider;
    }

    /**
     * Asynchronously registers a user.
     *
     * @param username    the username
     * @param email       the email
     * @param phoneNumber the phone number
     */
    public void registerUser(String username, String email, String phoneNumber) {
        try {
            User newUser = createUser(username, email, phoneNumber);
            registerUser(newUser);
        } catch (Exception e) {
            dispatchOnMalformedUserFailure();
        }
    }

    private void registerUser(final User user) {
        ObservableUtils.create(new Action0() {
                           @Override
                           public void call() {
                               userController.createUser(user);
                           }
                       })
                       .subscribeOn(schedulerProvider.ioScheduler())
                       .observeOn(schedulerProvider.mainThreadScheduler())
                       .subscribe(new Action1<Void>() {
                           @Override
                           public void call(Void nothing) {
                               registerLocalUser(user);
                               dispatchOnSuccessfulRegistration();
                           }
                       }, new Action1<Throwable>() {
                           @Override
                           public void call(Throwable throwable) {
                               dispatchOnDuplicateUsernameFailure();
                           }
                       });
    }

    private void registerLocalUser(User user) {
        userController.setCurrentUser(user);
    }

    private User createUser(String username, String email, String phoneNumber) {
        User newUser = new User(username);
        newUser.setEmail(new Email(email));
        newUser.setPhoneNumber(new PhoneNumber(phoneNumber));

        return newUser;
    }

    private void dispatchOnMalformedUserFailure() {
        if (isViewAttached()) {
            getView().onMalformedUserFailure();
        }
    }

    private void dispatchOnDuplicateUsernameFailure() {
        if (isViewAttached()) {
            getView().onDuplicateUsernameFailure();
        }
    }

    private void dispatchOnSuccessfulRegistration() {
        if (isViewAttached()) {
            getView().onSuccessfulRegistration();
        }
    }
}
