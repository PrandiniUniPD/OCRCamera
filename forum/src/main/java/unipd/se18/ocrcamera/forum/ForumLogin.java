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

import java.util.ArrayList;

import unipd.se18.ocrcamera.forum.models.Post;
import unipd.se18.ocrcamera.forum.viewmodels.Login_VM;


/**
 * A fragment where a user can login to the forum service
 *
 * @author Leonardo Rossi (g2), Alberto Valente (g2)
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

        //When the login button is clicked a request is sent through the viewmodel
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //The username and password inserted are gathered from the EditText objects
                userName = usernameEditText.getText().toString();
                userPwd = pwdEditText.getText().toString();

                //Then the credentials are handed to the viewmodel method to be checked
                viewModel.loginToForum(getContext(), userName, userPwd);
            }
        });

        /**
         * Login_VM viewmodel liveLoginResponse observer definition
         *
         * The method of the observer is triggered when liveLoginResponse variable inside the
         * viewmodel is initialized with a value, that is the username of the login request
         *
         * The observer itself is required to update the UI after receiving the username
         *
         * @author Alberto Valente (g2)
         */
        Observer<String> obsLogin = new Observer<String>() {

            @Override
            public void onChanged(@Nullable String username) {

                if(username != null) {
                    //creates the username bundle
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_USERNAME, username);

                    //creates an instance of the fragment to be launched
                    ShowPosts showPosts = new ShowPosts();
                    //passes the bundle to the fragment as an argument
                    showPosts.setArguments(bundle);

                    //performs the fragment transaction
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(
                                    R.id.fragmentContainer,
                                    showPosts
                            )
                            .addToBackStack(null)
                            .commit();
                }
                else {
                    Log.d(LOG_TAG, LOG_NULL_USERNAME);
                }
            }
        };
        viewModel.liveLoginResponse.observe(getActivity(), obsLogin);

        /**
         * Login_VM viewmodel liveError observer definition
         *
         * The method of this observer is triggered when liveError variable inside the
         * viewmodel is initialized with a value, that is the type of error occurred
         *
         * The error message is shown to the user through a toast
         *
         * @author Alberto Valente (g2)
         */
        Observer<String> obsError = new Observer<String>() {
            @Override
            public void onChanged(@Nullable String message) {

                if (message != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d(LOG_TAG, LOG_NULL_ERROR_MESSAGE);
                }
            }
        };
        viewModel.liveError.observe(getActivity(), obsError);

    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
