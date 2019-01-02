package unipd.se18.ocrcamera.forum;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import unipd.se18.ocrcamera.forum.models.Post;
import unipd.se18.ocrcamera.forum.viewmodels.ShowPosts_VM;


/**
 * A fragment where the user can see the posts in the forum
 */
public class ShowPosts extends Fragment {

    /**
     * ***************************
     * **   GLOBAL VARIABLES    **
     * ***************************
     */

    private OnFragmentInteractionListener mListener;
    private RecyclerView forumPosts;
    private ShowPosts_VM viewModel;

    public ShowPosts()
    {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //View model initialization
        viewModel = ViewModelProviders.of(this).get(ShowPosts_VM.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_show_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //UI object initialization
        forumPosts = view.findViewById(R.id.forumPosts);

        //View model posts observer definition
        Observer<ArrayList<Post>> obsPosts = new Observer<ArrayList<Post>>()
        {
            @Override
            public void onChanged(@Nullable ArrayList<Post> posts)
            {
                if (posts != null)
                {

                }
            }
        };
        viewModel.livePosts.observe(getActivity(), obsPosts);

        //View model error observer definition
        Observer<String> obsError = new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s)
            {
                if (s != null)
                {
                    Toast.makeText(view.getContext(), s, Toast.LENGTH_LONG).show();
                }
            }
        };
        viewModel.liveError.observe(getActivity(), obsError);

        //Invoke the method of the view model to get the forum's posts
        viewModel.getPosts(view.getContext());
    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
