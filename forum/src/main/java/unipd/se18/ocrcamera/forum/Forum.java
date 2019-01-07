package unipd.se18.ocrcamera.forum;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Forum extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        ForumLogin f = new ForumLogin();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, f).commit();

    }
}
