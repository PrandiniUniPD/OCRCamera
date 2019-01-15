package unipd.se18.ocrcamera.forum.viewmodels;

import android.content.Context;

public interface PostDetailMethods
{
    /**
     * Gets the detail of the specified post
     * @param context context The reference of the activity/fragment that calls this method
     * @param post The specified post ID for which retrieve the detail
     */
    void getPostDetail(Context context, String post);
}
