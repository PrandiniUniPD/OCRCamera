package unipd.se18.ocrcamera.forum;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import unipd.se18.ocrcamera.forum.models.Post;
import unipd.se18.ocrcamera.forum.viewmodels.ShowPosts_VM;


/**
 * A fragment where the user can see the posts in the forum
 * @author Leonardo Rossi g2
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
    private String loggedUser = "";

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

        //Fragment parameters reading
        loggedUser = getArguments().getString(getResources().getString(R.string.usernameFrgParam), "default");

        setHasOptionsMenu(true);
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
        forumPosts.setLayoutManager(new LinearLayoutManager(view.getContext()));

        //Definition of view model listener
        viewModel.setGetPostsListener(new ShowPosts_VM.GetPostListener() {
            @Override
            public void onGetPostsSuccess(ArrayList<Post> posts)
            {
                PostsAdapter adapter = new PostsAdapter(view.getContext(), posts);
                forumPosts.setAdapter(adapter);
            }

            @Override
            public void onGetPostFailure(String message)
            {
                Toast.makeText(view.getContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        //Invoke the method of the view model to get the forum's posts
        viewModel.getPosts(view.getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        MenuItem btnAddPost = menu.add("Add post");
        btnAddPost.setShowAsAction(1);
        btnAddPost.setIcon(R.drawable.addpost);
        btnAddPost.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                AddPost f = new AddPost();
                Bundle params = new Bundle();
                params.putString(getString(R.string.usernameFrgParam), loggedUser);
                f.setArguments(params);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, f)
                        .addToBackStack(getString(R.string.fgAddPost))
                        .commit();

                return true;
            }
        });
    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * *******************
     * **   ADAPTER     **
     * *******************
     */

    /**
     * Adapter for the recycler view that displays the forum's posts
     * @author Leonardo Rossi g2
     */
    public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostHolder>
    {
        /**
         * Holder tells how a post is made. It contains all the elements inside a post
         */
        public class PostHolder extends RecyclerView.ViewHolder
        {
            TextView lblPostTitle;
            TextView lblPostAuthor;
            TextView lblPostDate;
            TextView lblPostMessage;
            TextView lblPostLikes;
            TextView lblPostComments;
            Button btnLike;
            Button btnComment;

            public PostHolder(@NonNull View itemView)
            {
                super(itemView);
                lblPostTitle = itemView.findViewById(R.id.lblPostTitle);
                lblPostAuthor = itemView.findViewById(R.id.lblPostAuthor);
                lblPostDate = itemView.findViewById(R.id.lblPostDate);
                lblPostMessage = itemView.findViewById(R.id.lblPostMessage);
                lblPostLikes = itemView.findViewById(R.id.lblPostLikes);
                lblPostComments = itemView.findViewById(R.id.lblPostComments);
                btnLike = itemView.findViewById(R.id.btnLike);
                btnComment = itemView.findViewById(R.id.btnComment);
            }
        }


        /**
         * ***************************
         * **   GLOBAL VARIABLES    **
         * ***************************
         */

        Context context;
        ArrayList<Post> posts;

        /**
         * Defines an object of type PostAdapter
         * @param context The reference to the activity/fragment that will use the adapter
         * @param posts The lists of the forum's posts
         */
        PostsAdapter(Context context, ArrayList<Post> posts)
        {
            this.context = context;
            this.posts = posts;
        }

        /**
         * Create a new card with the inflated layout. At this point I just create an object containing the layout of the card.
         * @param parent the main ViewGroup where the card will be inflated
         * @param  identifier of the view that I want to implement using default views
         */
        @NonNull
        @Override
        public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int identifier)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.postlayout, parent, false);
            PostHolder holder = new PostHolder(view);
            return holder;
        }

        /**
         * Update the PostHolder content with the item at the given position
         * @param postHolder Holder of the card containing all its elements
         * @param position auto-increment value starting from 0 that tells which position of the list is now active
         */
        @Override
        public void onBindViewHolder(@NonNull final PostHolder postHolder, int position)
        {
            //Obtaining the current post
            final Post currentPost = posts.get(position);

            //Population of the layout with post data
            postHolder.lblPostTitle.setText(currentPost.getTitle());
            postHolder.lblPostMessage.setText(currentPost.getMessage());
            postHolder.lblPostLikes.setText("Likes: " + currentPost.getLikes());
            postHolder.lblPostComments.setText("Comments: " + currentPost.getComments());
            postHolder.lblPostAuthor.setText("Author: " + currentPost.getAuthor());
            postHolder.lblPostDate.setText(Post.FORMATTER.format(currentPost.getDate()));

            //Listeners for buttons
            postHolder.btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //Implementation of the add like listener
                    viewModel.setAddLikeListener(new ShowPosts_VM.AddLikeListener() {
                        @Override
                        public void onAddLikeSuccess(String message)
                        {
                            //Updating model and UI
                            currentPost.addLike();
                            postHolder.lblPostLikes.setText("Likes: " + currentPost.getLikes());
                        }

                        @Override
                        public void onAddLikeFailure(String message)
                        {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }
                    });

                    //Store the new like into the db
                    viewModel.addLikeToPost(v.getContext(), currentPost.getID(), loggedUser, currentPost.getLikes());
                }
            });

            postHolder.btnComment.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    PostDetail fDetail = new PostDetail();

                    Bundle params = new Bundle();
                    params.putParcelable(context.getString(R.string.postParameterKey), currentPost);
                    params.putString(context.getString(R.string.usernameFrgParam), loggedUser);
                    fDetail.setArguments(params);

                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, fDetail)
                            .addToBackStack(context.getString(R.string.fgShowPosts))
                            .commit();
                }
            });

        }

        @Override
        public int getItemCount() { return posts.size(); }
    }
}
