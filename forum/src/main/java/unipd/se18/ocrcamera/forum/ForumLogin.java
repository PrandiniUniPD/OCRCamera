package unipd.se18.ocrcamera.forum;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import unipd.se18.ocrcamera.forum.viewmodels.Login_VM;


/**
 * A fragment where a user can login to the forum service
 */
public class ForumLogin extends Fragment {

    private OnFragmentInteractionListener mListener;
    private EditText emailEditText;
    private EditText pwdEditText;
    private Button loginButton;
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
        emailEditText = view.findViewById(R.id.eMailEditText);
        pwdEditText = view.findViewById(R.id.pwdEditText);
        loginButton = view.findViewById(R.id.loginButton);
    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
