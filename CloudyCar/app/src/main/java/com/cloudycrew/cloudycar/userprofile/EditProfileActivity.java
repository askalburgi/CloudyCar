package com.cloudycrew.cloudycar.userprofile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudycrew.cloudycar.BaseActivity;
import com.cloudycrew.cloudycar.R;
import com.cloudycrew.cloudycar.email.Email;
import com.cloudycrew.cloudycar.models.phonenumbers.PhoneNumber;
import com.cloudycrew.cloudycar.models.User;
import com.cloudycrew.cloudycar.models.phonenumbers.PhoneNumberTextWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends BaseActivity implements IEditProfileView
{
    private EditProfileController editProfileController;
    private String username;
    private User editedUser;

    @BindView(R.id.editprofile_username)
    protected TextView usernameTextView;
    @BindView(R.id.emailaddress_edit)
    protected EditText emailEditText;
    @BindView(R.id.phonenumber_edit)
    protected EditText phoneEditText;
    @BindView(R.id.cardescription_edit)
    protected EditText carDescriptionEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Intent myIntent = getIntent();
        username = myIntent.getStringExtra("username");
        resolveDependencies();
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        editProfileController.attachView(this);
        editProfileController.loadUser(username);
    }

    @Override
    protected void onPause() {
        super.onPause();
        editProfileController.detachView();
    }

    private void resolveDependencies() {
        this.editProfileController = getCloudyCarApplication().getEditProfileController();
    }

    @Override
    public void displayLoading() {
        //Maybe next time
    }

    @Override
    public void displayUser(User user) {
        editedUser = user;
        usernameTextView.setText(user.getUsername());
        emailEditText.setText(user.getEmail().getEmail());
        phoneEditText.addTextChangedListener(new PhoneNumberTextWatcher(phoneEditText));
        phoneEditText.setText(user.getPhoneNumber().getPhoneNumber());

        if (user.hasCarDescription()) {
            carDescriptionEditText.setText(user.getCarDescription());
        } else {
            carDescriptionEditText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEditSuccess() {
        finish();
    }

    @Override
    public void onEditFailure() {
        //If the user can't update right now then let them know it didn't work!
        Context context = getApplicationContext();
        CharSequence text = "We couldn't update your account, please try again later";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);

        toast.show();
    }

    /**
     * Handler for the confirm button
     * @param v
     */
    public void commitChanges(View v) {
        editedUser.setEmail(new Email(emailEditText.getText().toString()));
        editedUser.setPhoneNumber(new PhoneNumber(phoneEditText.getText().toString()));
        if (carDescriptionEditText.getVisibility() != View.GONE) {
            editedUser.setCarDescription(carDescriptionEditText.getText().toString());
        }
        editProfileController.updateUser(editedUser);
    }

    /**
     * Handler for the cancel button
     * @param v
     */
    public void cancelChanges(View v) {
        finish();
    }
}
