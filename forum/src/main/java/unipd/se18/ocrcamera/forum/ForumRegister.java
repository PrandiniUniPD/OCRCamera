package unipd.se18.ocrcamera.forum;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import unipd.se18.ocrcamera.forum.viewmodels.Login_VM;
import unipd.se18.ocrcamera.forum.viewmodels.Register_VM;


/**
 * A fragment where a user can register to the forum service
 *
 * @author Alberto Valente (g2)
 */
public class ForumRegister extends Fragment {

    /**
     * ***************************
     * **   GLOBAL VARIABLES    **
     * ***************************
     */

    /**
     * String used for logs to identify the fragment throwing it
     */
    private final String LOG_TAG = String.valueOf(R.string.logTagForumLogin);

    /**
     * String used to indicate that no username has been passed
     */
    private final String LOG_NULL_USERNAME = String.valueOf(R.string.logNullUsername);

    /**
     * String used to indicate that no error message has been passed
     */
    private final String LOG_NULL_ERROR_MESSAGE = String.valueOf(R.string.logNullErrorMessage);

    /**
     * Key to identify the username passed to ShowPosts instance
     */
    private final String KEY_USERNAME = String.valueOf(R.string.keyUsername);

    //UI objects declaration
    private EditText usernameEditText;
    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText pwdEditText;
    private EditText confirmPwdEditText;
    private Button signUpButton;

    //Support variables to retain registration fields by the user
    private String userUsername;
    private String userName;
    private String userSurname;
    private String userPwd;

    //Corresponding view model declaration
    private Register_VM viewModel;

    public ForumRegister() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //View model initialization
        viewModel = ViewModelProviders.of(this).get(Register_VM.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forum_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        //UI object initialization
        usernameEditText = view.findViewById(R.id.usernameEditText);
        nameEditText = view.findViewById(R.id.nameEditText);
        surnameEditText = view.findViewById(R.id.surnameEditText);
        pwdEditText = view.findViewById(R.id.pwdEditText);
        confirmPwdEditText = view.findViewById(R.id.confirmPwdEditText);
        signUpButton = view.findViewById(R.id.signUpButton);

        //Definition of view model listener
        viewModel.setForumRegisterListener(new Register_VM.ForumRegisterListener() {

            /**
             * This method of the listener is triggered when the database response
             * to the registration request is positive and the view model requests to
             * close the current fragment and return to the ForumLogin fragment, so that
             * the user can now perform a login action with his new credentials
             *
             * @param username The username of the user that successfully signed up
             */
            @Override
            public void onRegisterSuccess(String username) {

                if (username != null) {
                    //creates the username bundle
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_USERNAME, username);

                    //creates an instance of the fragment to be launched
                    ForumLogin forumLoginFragment = new ForumLogin();
                    //passes the bundle to the fragment as an argument
                    forumLoginFragment.setArguments(bundle);

                    //performs the fragment transition
                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(
                                    R.id.fragmentContainer,
                                    forumLoginFragment
                            )
                            .commit();
                }
                else {
                    Log.d(LOG_TAG, LOG_NULL_USERNAME);
                }
            }

            /**
             * This method of the listener is triggered when the database response
             * to the registration request is negative and the view model has ordered
             * to show the user an error message through the use of a toast
             *
             * @param message The error message about what went wrong with the registration request
             */
            @Override
            public void onRegisterFailure(String message) {

                if (message != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d(LOG_TAG, LOG_NULL_ERROR_MESSAGE);
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {

            /**
             * When the sign up button is clicked a registration request is sent through the view
             * model so that the user can confirm his credentials before logging in
             *
             * @param v the view where the click event is performed
             */
            @Override
            public void onClick(View v) {

                //Checks if all fields have been filled by using a support function
                if (areAllFieldsFilled()) {

                    //If so, texts from these fields can be retrieved
                    userUsername = usernameEditText.getText().toString();
                    userName = nameEditText.getText().toString();
                    userSurname = surnameEditText.getText().toString();

                    //Checks if password and confirmation password fields are equal
                    if (pwdEditText.getText().toString()
                            .equals(confirmPwdEditText.getText().toString())) {

                        //If so, also password variable is definitively initialized
                        userPwd = pwdEditText.getText().toString();

                        //The registration request is sent to the database through the view model
                        viewModel.registerUserToForum(requireContext(),userUsername, userPwd, userName,
                                userSurname);
                    }
                    else {

                        //If the two password fields doesn't match, the user is shown an error toast
                        Toast.makeText(getContext(), R.string.pwdEditTextsDoNotMatch,
                                Toast.LENGTH_LONG).show();

                        //The invalid password fields are also made blank again
                        pwdEditText.setText(null);
                        confirmPwdEditText.setText(null);
                    }
                }
                else {

                    //If not, the user is required to insert the missing information
                    Toast.makeText(getContext(), R.string.allFieldsNotFilled,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * Support function to ensure that all the EditTexts of this fragment have not left totally
     * blank by the user during the registration process
     *
     * @return if all EditTexts of the current view are at least partially filled
     */
    private boolean areAllFieldsFilled() {

        //String used to indicate that a NULL field occurred
        final String LOG_NULL_FIELD_ERROR_MESSAGE = "A NULL field to check occurred!";

        //String used to indicate that a NULL fieldText occurred
        final String LOG_NULL_FIELDTEXT_ERROR_MESSAGE = "A NULL fieldText to check occurred!";

        //A list of the EditTexts to check is created and filled
        ArrayList<EditText> fieldsList = new ArrayList<>();

        fieldsList.add(usernameEditText);
        fieldsList.add(nameEditText);
        fieldsList.add(surnameEditText);
        fieldsList.add(pwdEditText);
        fieldsList.add(confirmPwdEditText);

        //Checks if all fields are not null nor empty
        for (EditText field: fieldsList) {

            //The text object is prepared to be checked
            try {
                Object fieldText = field.getText();

                //If only one of the fields is null, a negative response is returned
                if (fieldText == null) {
                    return false;
                }

                //If the text object is not null, its value is prepared to be checked
                try {
                    String fieldTextValue = fieldText.toString();

                    //If only one of the fields is empty, a negative response is returned
                    if (fieldTextValue.equals("")) {
                        return false;
                    }
                }
                catch (NullPointerException e) {
                    //The variable "fieldText" should not be null because it was previously checked
                    Log.d(LOG_TAG, LOG_NULL_FIELDTEXT_ERROR_MESSAGE);
                }
            }
            catch (NullPointerException e) {
                //The variable "field" should not be null because the list is manually filled
                Log.d(LOG_TAG, LOG_NULL_FIELD_ERROR_MESSAGE);
            }
        }

        //If no fields resulted null or empty, a positive response is returned
        return true;
    }

}
