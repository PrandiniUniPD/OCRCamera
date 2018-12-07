package unipd.se18.ocrcamera;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class PopUp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popwindows);

        Log.d("inci", "ok");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        ArrayList<String> ingredient = getIntent().getExtras().getStringArrayList("ingredienti");

        Log.d("inci", ingredient.size()+"");

        String text="";
        for (int i=0; i<ingredient.size(); i++){
            text=text+ingredient.get(i)+"\n";
        }

        TextView textView = (TextView) findViewById(R.id.textViewPop);
        textView.setText(text);

        getWindow().setLayout((int)(width*0.8),(int)(height*0.6));
    }
}
