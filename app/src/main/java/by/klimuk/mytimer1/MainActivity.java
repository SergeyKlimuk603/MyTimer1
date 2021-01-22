package by.klimuk.mytimer1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = "myLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "Создали активити");
        LinearLayout mmm = (LinearLayout) findViewById(R.id.mmm);
        MyTimer t = new MyTimer(this, 0, "Ser", "Hello", 100);
        mmm.addView(t.getLayoutMain());
        t = new MyTimer(this, 0, "Ser", "Hello", 100);
        mmm.addView(t.getLayoutMain());
    }
}