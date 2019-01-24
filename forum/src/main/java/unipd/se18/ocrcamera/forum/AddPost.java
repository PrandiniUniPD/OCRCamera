package unipd.se18.ocrcamera.forum;

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

import unipd.se18.ocrcamera.forum.viewmodels.AddPost_VM;


/**
 * A fragment where a user can add a new post to the forum
 * @author Giovanni Furlan g2 thanks to Pietro Prandini for some general advice
 */
public class AddPost extends Fragment {

    /**
     * ***************************
     * **   GLOBAL VARIABLES    **
     * ***************************
     */
    private AddPost_VM viewModel;
    private String titleText;
    private String messageText;
    private String loggedUser;

    /**
     * Ui initialization
     */
    private Button confirmBotton;
    private EditText titleEditText;
    private EditText messageEditText;

    public AddPost()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_add_post, container, false);
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new AddPost_VM();
        confirmBotton = view.findViewById(R.id.confirmButton);
        titleEditText = view.findViewById(R.id.titleEditText);
        messageEditText = view.findViewById(R.id.messageEditText);

        //reload information wrote before an error
        if(titleText!=null)
            titleEditText.setText(titleText);
        if(messageEditText!=null)
            messageEditText.setText(messageText);

        confirmBotton.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View view)
            {
                titleText =  titleEditText.getText().toString();
                messageText =  messageEditText.getText().toString();
                //Username passed with the fragment
                loggedUser = getArguments().getString(
                        getResources().getString(R.string.usernameFrgParam), "default");


                /**
                 * Listener of AddPost_VM
                 */
                viewModel.setAddPostListener(new AddPost_VM.addPostListener() {
                    @Override
                    public void onPostAdded(String response) {
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.messagePostAdded),
                                Toast.LENGTH_LONG).show();

                        //Pass Username with fragment
                        Bundle bundle = new Bundle();
                        bundle.putString(
                                getResources().getString(R.string.usernameFrgParam),
                                loggedUser);

                        //creates an instance of the fragment to be launched
                        ShowPosts showPostsFragment = new ShowPosts();
                        //passes the bundle to the fragment as an argument
                        showPostsFragment.setArguments(bundle);

                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(
                                        R.id.fragmentContainer,
                                        showPostsFragment
                                )
                                .addToBackStack(null)
                                .commit();
                    }

                    @Override
                    public void onAddingPostFailure(String error) {
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.messagePostNotAdded),
                                Toast.LENGTH_LONG).show();

                    }
                    
                    @Override
                    public void onNotValidParameters(String error){
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.messageWrongParameters),
                                Toast.LENGTH_LONG).show();

                    }
                });

                //Call to the AddPost_VM
                viewModel.addPostToForum(view.getContext(), titleText, messageText,loggedUser);
            }
        });
    }

}
