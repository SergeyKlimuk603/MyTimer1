package by.klimuk.mytimer1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = "myLog";
    //Log.d(LOG_TAG, "Создали активити");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout mmm = (LinearLayout) findViewById(R.id.mmm);
        MyTimer t = new MyTimer(this, 0, "Ser", "Hello", 5);
        mmm.addView(t.getLayoutMain());
        t = new MyTimer(this, 0, "Сергей", "Hello", 10);
        mmm.addView(t.getLayoutMain());
    }

    // сообщаем активности, что сбросили таймер. Это нужно для отключения сигнала, если сработавший
    // таймер был последним (определяем по сообщению таймера: если оно совпадает с тем, что
    // на общем экране - значит это последний сработавший таймер)
    public void timerReset(String _message) {
        Toast.makeText(this, "реализуй метод timerReset в MainActivity",
                Toast.LENGTH_LONG).show();
    }

    // сообщаем активности о завершении отсчета для включения сигнала
    // и передаем ей сообщение таймера для вывода его на общий экран сообщений
    public void timerEnd(String _message) {
        Toast.makeText(this, "реализуй метод timerEnd в MainActivity",
                Toast.LENGTH_LONG).show();

    }
}