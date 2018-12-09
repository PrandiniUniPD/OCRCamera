package unipd.se18.ocrcamera;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoginToDBActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_to_db);
        SharedPreferences dbLogin = getSharedPreferences("db_login", MODE_PRIVATE);

    }
}
