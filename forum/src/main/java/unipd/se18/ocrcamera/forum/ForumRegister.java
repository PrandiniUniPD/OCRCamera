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

    //Variables to retain registration fields by the user
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

                userUsername = usernameEditText.getText().toString();
                viewModel.registerUserToForum(requireContext(),userUsername, userPwd, userName,
                        userSurname);
            }
        });

    }

}
