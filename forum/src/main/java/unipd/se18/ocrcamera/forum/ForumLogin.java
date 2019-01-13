package unipd.se18.ocrcamera.forum;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
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
import android.widget.Toast;

import unipd.se18.ocrcamera.forum.viewmodels.Login_VM;


/**
 * A fragment where a user can login to the forum service
 *
 * @author Leonardo Rossi (g2), Alberto Valente (g2), Taulant Bullaku (g2)
 */
public class ForumLogin extends Fragment {

    /**
     * ***************************
     * **   GLOBAL VARIABLES    **
     * ***************************
     */

    private OnFragmentInteractionListener mListener;

    /**
     * String used for logs to identify the fragment throwing it
     */
    private final String LOG_TAG = "@@ForumLogin";

    /**
     * String used to indicate that no username has been passed
     */
    private final String LOG_NULL_USERNAME = "A NULL username passed through liveLoginResponse";

    /**
     * String used to indicate that no error message has been passed
     */
    private final String LOG_NULL_ERROR_MESSAGE = "A NULL error message passed through liveError";

    /**
     * Key to identify the username passed to ShowPosts instance
     */
    private final String KEY_USERNAME = "username";

    private EditText usernameEditText;
    private EditText pwdEditText;
    private Button loginButton;

    private String userName;
    private String userPwd;

    private Login_VM viewModel;

    public ForumLogin() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //View model initialization
        viewModel = ViewModelProviders.of(this).get(Login_VM.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forum_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        //UI object initialization
        usernameEditText = view.findViewById(R.id.usernameEditText);
        pwdEditText = view.findViewById(R.id.pwdEditText);
        loginButton = view.findViewById(R.id.loginButton);

        //Definition of view model listener
        viewModel.setForumLoginListener(new Login_VM.ForumLoginListener() {

            /**
             * The method of the listener is triggered when the database response
             * to the login request is positive and the view model requests to load
             * the ShowPost fragment, so that the user can access the forum contents
             *
             * @param username The username of the user that successfully logged in
             * @author Alberto Valente (g2), Taulant Bullaku (g2)
             */
            @Override
            public void onLoginSuccess(String username) {

                if(username != null) {
                    //creates the username bundle
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_USERNAME, username);

                    //creates an instance of the fragment to be launched
                    ShowPosts showPostsFragment = new ShowPosts();
                    //passes the bundle to the fragment as an argument
                    showPostsFragment.setArguments(bundle);

                    //performs the fragment transaction
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(
                                    R.id.fragmentContainer,
                                    showPostsFragment
                            )
                            .commit();
                }
                else {
                    Log.d(LOG_TAG, LOG_NULL_USERNAME);
                }
            }

            /**
             * The method of the listener is triggered when the database response to
             * the login request is negative and the view model requests to show the
             * user an error message, that is performed through the use of a toast
             *
             * @param message The error message about what was wrong with the login request
             * @author Alberto Valente (g2), Taulant Bullaku (g2)
             */
            @Override
            public void onLoginFailure(String message) {

                if (message != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d(LOG_TAG, LOG_NULL_ERROR_MESSAGE);
                }
            }
        });

        //When the login button is clicked a request is sent through the view model
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //The username and password inserted are gathered from the EditText objects
                userName = usernameEditText.getText().toString();
                userPwd = pwdEditText.getText().toString();

                if (!userName.equals("") && !userPwd.equals(""))
                {
                    //Then the credentials are handed to the viewmodel method to be checked
                    viewModel.loginToForum(getContext(), userName, userPwd);
                }
                else
                {
                    Toast.makeText(getContext(), "Please enter username and password", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
