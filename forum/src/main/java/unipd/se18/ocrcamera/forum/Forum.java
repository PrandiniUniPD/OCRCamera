package unipd.se18.ocrcamera.forum;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Forum extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        AddPost f = new AddPost();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, f).commit();
    }

}
