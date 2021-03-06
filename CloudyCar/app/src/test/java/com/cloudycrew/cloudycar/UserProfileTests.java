package com.cloudycrew.cloudycar;

import com.cloudycrew.cloudycar.elasticsearch.ElasticSearchService;
import com.cloudycrew.cloudycar.email.Email;
import com.cloudycrew.cloudycar.models.phonenumbers.PhoneNumber;
import com.cloudycrew.cloudycar.models.User;
import com.cloudycrew.cloudycar.users.DuplicateUserException;
import com.cloudycrew.cloudycar.users.IUserService;
import com.cloudycrew.cloudycar.users.IncompleteUserException;
import com.cloudycrew.cloudycar.users.UserDoesNotExistException;
import com.cloudycrew.cloudycar.users.UserPreferences;
import com.cloudycrew.cloudycar.users.UserService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Ryan on 2016-10-12.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserProfileTests {
    @Mock
    private ElasticSearchService<User> muhservice;
    @Mock
    private UserPreferences userPreferences;
    private IUserService userService;

    private User completeUser;
    private User incompleteUser;
    private PhoneNumber phoneNumber;
    private Email email;

    @Before
    public void set_up() {
        phoneNumber = new PhoneNumber("780-2676-364"); //Corndog
        email = new Email("wapoz@george.moe");
        completeUser = new User("janedoedoe");
        completeUser.setEmail(email);
        completeUser.setPhoneNumber(phoneNumber);
        incompleteUser = new User("I'm sad and have no contact info");
        userService = new UserService(muhservice, userPreferences);
    }

    @Test
    public void test_addNewUser_withAnUnusedUserName_andTestGettingUserFromUsernameOnly() {
        userService.createUser(completeUser);
        verify(muhservice).create(completeUser);
    }

    @Test(expected=DuplicateUserException.class)
    public void test_addNewUser_withAUsedUserName_throwingDuplicateUserException() {
        when(muhservice.search(any(String.class))).thenReturn(Arrays.asList(completeUser));
        userService.createUser(completeUser);
    }

    @Test(expected=IncompleteUserException.class)
    public void test_addNewUser_withIncompleteInformation_throwingIncompleteUserException() {
        userService.createUser(incompleteUser);
    }

    @Test(expected=Email.InvalidEmailException.class)
    public void test_EditingUserEmailWithInvalidEmail_throwingInvalidEmailException() {
        completeUser.setEmail(new Email("notarealemail"));
    }

    @Test(expected=PhoneNumber.InvalidPhoneNumberException.class)
    public void test_EditingUserPhoneNumber_throwingInvalidPhoneNumberException() {
        completeUser.setPhoneNumber(new PhoneNumber("corndog")); //Corndog
    }

    @Test
    public void test_EditingUserPhoneNumber() {
        PhoneNumber newPhone = new PhoneNumber("780-1111-222");
        completeUser.setPhoneNumber(newPhone);
        assertEquals(newPhone, completeUser.getPhoneNumber());
    }

    @Test
    public void test_EditingUserEmailAddress() {
        Email newEmail = new Email("abc@test.ca");
        completeUser.setEmail(newEmail);
        assertEquals(newEmail, completeUser.getEmail());
    }

    @Test(expected=UserDoesNotExistException.class)
    public void test_GettingANonExistentUser_Excepts() {
        userService.getUser("I don't exist");
    }

}
