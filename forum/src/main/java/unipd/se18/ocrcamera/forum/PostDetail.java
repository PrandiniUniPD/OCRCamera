package unipd.se18.ocrcamera.forum;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import unipd.se18.ocrcamera.forum.models.Post;
import unipd.se18.ocrcamera.forum.viewmodels.PostDetail_VM;


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

        TextView lblPostTile = view.findViewById(R.id.lblDettPostTitle);
        TextView lblPosDate = view.findViewById(R.id.lblDettPostDate);
        TextView lblPostMessage = view.findViewById(R.id.lblDettPostMessage);
        TextView lblPostAuthor = view.findViewById(R.id.lblDettPostAuthor);
        TextView lblPostLikes = view.findViewById(R.id.lblDettPostLikes);
        TextView lblPostComments = view.findViewById(R.id.lblDettPostComments);

        //Reading of fragment parameters
        Post post = getArguments().getParcelable("post");

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
                CommentsAdapter adapter = new CommentsAdapter(view.getContext(), comments);
                postComments.setAdapter(adapter);
            }

            @Override
            public void onGetDetailFailure(String message)
            {
                Toast.makeText(view.getContext(), message, Toast.LENGTH_LONG).show();
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
    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
