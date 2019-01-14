package unipd.se18.ocrcamera.forum;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import unipd.se18.ocrcamera.forum.models.Post;
import unipd.se18.ocrcamera.forum.viewmodels.PostDetail_VM;


/**
 * A fragment where the user can see the detail of a specific post
 * @author Leonardo Rossi g2
 */
public class PostDetail extends android.support.v4.app.Fragment
{

    /**
     * ***************************
     * **   GLOBAL VARIABLES    **
     * ***************************
     */
    private OnFragmentInteractionListener mListener;
    private PostDetail_VM viewModel;
    private RecyclerView postComments;
    private Button btnAddComment;
    private String loggedUser;
    private CommentsAdapter adapter;

    public PostDetail()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //View model initialization
        viewModel = ViewModelProviders.of(this).get(PostDetail_VM.class);

        //Fragment parameters reading
        loggedUser = getArguments().getString(getResources().getString(R.string.usernameFrgParam), "default.user");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_post_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //UI object initialization
        postComments = view.findViewById(R.id.postComments);
        postComments.setLayoutManager(new LinearLayoutManager(view.getContext()));
        btnAddComment = view.findViewById(R.id.btnAddComment);

        TextView lblPostTile = view.findViewById(R.id.lblDettPostTitle);
        TextView lblPosDate = view.findViewById(R.id.lblDettPostDate);
        TextView lblPostMessage = view.findViewById(R.id.lblDettPostMessage);
        TextView lblPostAuthor = view.findViewById(R.id.lblDettPostAuthor);
        TextView lblPostLikes = view.findViewById(R.id.lblDettPostLikes);
        final TextView lblPostComments = view.findViewById(R.id.lblDettPostComments);
        final EditText txtComment = view.findViewById(R.id.txtComment);

        //Reading of fragment parameters
        final Post post = getArguments().getParcelable("post");
        viewModel.postID = post.getID();

        //UI population with post data
        lblPostTile.setText(post.getTitle());
        lblPostAuthor.setText("Author: " + post.getAuthor());
        lblPostMessage.setText(post.getMessage());
        lblPostLikes.setText("Likes: " + post.getLikes());
        lblPostComments.setText("Comments: " + post.getComments());

        SimpleDateFormat format = new SimpleDateFormat(Post.DATE_FORMAT);
        lblPosDate.setText(format.format(post.getDate()));

        //Definition of view model listener
        viewModel.setGetDetailListener(new PostDetail_VM.GetPostDetailListener() {
            @Override
            public void onGetDetailSuccess(ArrayList<Post> comments)
            {
                //If the detail has successfully been downloaded, then the UI is updated
                adapter = new CommentsAdapter(view.getContext(), comments);
                postComments.setAdapter(adapter);
            }

            @Override
            public void onGetDetailFailure(String message)
            {
                //If something when wrong during post's detail download a message is shown to the user
                Toast.makeText(view.getContext(), message, Toast.LENGTH_LONG).show();
            }
        });


        viewModel.setAddCommentListener(new PostDetail_VM.AddCommentListener() {
            @Override
            public void onAddCommentSuccess(Post comment)
            {
                //Notify the user with a success message
                Toast.makeText(view.getContext(), getResources().getString(R.string.addCommentSuccess), Toast.LENGTH_LONG).show();

                //Update the UI
                post.addComment();
                lblPostComments.setText("Comments: " + post.getComments());
                txtComment.setText("");
                adapter.addComment(comment);
                adapter.notifyItemInserted(adapter.getLastPosition());
            }

            @Override
            public void onAddCommentFailure(String message)
            {
                Toast.makeText(view.getContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        //Listener for btnAddComment
        btnAddComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               if (!txtComment.getText().toString().equals(""))
               {
                   Post comment = new Post();
                   comment.setAuthor(loggedUser);
                   comment.setMessage(txtComment.getText().toString());
                   comment.setDate(new Date());

                   viewModel.addComment(view.getContext(), comment, post.getComments());
               }
            }
        });


        viewModel.getPostDetail(view.getContext(), post.getID());
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
    public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentHolder>
    {

        /**
         * Holder tells how a comment is made. It contains all the elements inside a comment
         */
        public class CommentHolder extends RecyclerView.ViewHolder
        {

            TextView lblAuthor;
            TextView lblMessage;
            TextView lblDate;

            public CommentHolder(@NonNull View itemView)
            {
                super(itemView);

                lblAuthor = itemView.findViewById(R.id.lblCommentAuthor);
                lblMessage = itemView.findViewById(R.id.lblCommentMessage);
                lblDate = itemView.findViewById(R.id.lblCommentDate);
            }
        }

        /**
         * ***************************
         * **   GLOBAL VARIABLES    **
         * ***************************
         */
        Context context;
        ArrayList<Post> comments;

        /**
         * Defines an object of type CommentAdapter
         * @param context The reference to the activity/fragment that will use the adapter
         * @param comments The lists of the post's comments
         */
        CommentsAdapter(Context context, ArrayList<Post> comments)
        {
            this.context = context;
            this.comments = comments;
        }

        /**
         * Create a new card with the inflated layout. At this point I just create an object containing the layout of the card.
         * @param parent the main ViewGroup where the card will be inflated
         * @param  identifier of the view that I want to implement using default views
         */
        @NonNull
        @Override
        public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int identifier)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.commentlayout, parent, false);
            CommentHolder holder = new CommentHolder(view);
            return holder;
        }

        /**
         * Update the PostHolder content with the item at the given position
         * @param commentHolder Holder of the card containing all its elements
         * @param position auto-increment value starting from 0 that tells which position of the list is now active
         */
        @Override
        public void onBindViewHolder(@NonNull CommentHolder commentHolder, int position)
        {
            //Get the current comment
            Post currentComment = comments.get(position);

            commentHolder.lblAuthor.setText(currentComment.getAuthor());
            commentHolder.lblMessage.setText(currentComment.getMessage());

            SimpleDateFormat format = new SimpleDateFormat(Post.DATE_FORMAT);
            commentHolder.lblDate.setText(format.format(currentComment.getDate()));
        }

        @Override
        public int getItemCount() { return comments.size(); }

        /**
         * Returns the last element's index in the list
         * @return The last element's index
         */
        public int getLastPosition() { return comments.size() - 1; }

        /**
         * Adds the specified comment to the list
         * @param comment The specified comment
         */
        public void addComment(Post comment) { comments.add(comment); }
    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
