package by.klimuk.mytimer1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import static by.klimuk.mytimer1.MainActivity.*;

public class TimerMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_menu);

        Intent intent = getIntent();
        EditText et = (EditText) findViewById(R.id.et);
        et.setText(intent.getStringExtra(TIMER_NAME));

        Intent intentt = new Intent();
        intentt.putExtra(TIMER_NAME, et.getText());
        setResult(RESULT_OK, intent);
    }


}