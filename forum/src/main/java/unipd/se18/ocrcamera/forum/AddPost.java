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
 * @author Giovanni Furlan g2
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
    private String username;
    private String Tag;

    /**
     * Ui initialization
     */
    private Button confirmBotton;
    private EditText titleEditText;
    private EditText messageEditText;

    /**
     * Error messages
     */
    private final String messageNullCamps ="Inserimento di parametri non validi, riprovare";
    private final String messagePostAdded ="Post inserito correttamente";
    private final String messagePostNotAdded ="Post non inserito, riprovare";
    private final String messageWrongParameters ="Parametri non corretti, riprovare";
    private final String logWrongJSON ="Errore nella creazione del JSON";
    private final String messageWrongJSON ="Errore nella creazione del post, riprovare";



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

        confirmBotton = view.findViewById(R.id.confirmButton);
        titleEditText = view.findViewById(R.id.titleEditText);
        messageEditText = view.findViewById(R.id.messageEditText);

        confirmBotton.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View view)
            {
                titleText =  titleEditText.getText().toString();
                messageText =  messageEditText.getText().toString();
                //Username passed with the fragment
                username = getArguments().getString("username");

                //Call to the AddPost_VM
                viewModel.addPostToForum(view.getContext(), titleText,messageText,username);


                /**
                 * Listener of AddPost_VM
                 */
                viewModel.setAddPostListener(new AddPost_VM.addPostListener() {
                    @Override
                    public void onPostAdded(String response) {
                        Toast.makeText(getActivity(), messagePostAdded, Toast.LENGTH_LONG).show();

                        //Start showPost fragment
                        Fragment showPostFragment = new ShowPosts();
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(
                                        R.id.fragmentContainer,
                                        showPostFragment
                                )
                                .addToBackStack(null)
                                .commit();
                    }

                    @Override
                    public void onAddingPostFailure(String error) {
                        Toast.makeText(getActivity(), messagePostNotAdded, Toast.LENGTH_LONG).show();

                        //Restart fragment
                        Fragment addPostFragment = new AddPost();
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(
                                        R.id.fragmentContainer,
                                        addPostFragment
                                )
                                .addToBackStack(null)
                                .commit();
                    }

                    @Override
                    public void onNotValidParameters(String error){
                        Toast.makeText(getActivity(), messageNullCamps, Toast.LENGTH_LONG).show();

                        //Restart fragment
                        Fragment addPostFragment = new AddPost();
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(
                                        R.id.fragmentContainer,
                                        addPostFragment
                                )
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }
        });
    }

}
