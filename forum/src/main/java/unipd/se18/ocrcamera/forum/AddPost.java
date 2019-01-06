package unipd.se18.ocrcamera.forum;

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

    private Button confirmBotton;
    private EditText titleEditText;
    private EditText messageEditText;
    private String titleText;
    private String messageText;


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
                titleText = titleEditText.getText().toString();
                messageText = messageEditText.getText().toString();

                viewModel.addPostToForum(titleText,messageText);
            }
        });
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
