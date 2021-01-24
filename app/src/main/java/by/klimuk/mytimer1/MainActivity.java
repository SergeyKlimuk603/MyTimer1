package by.klimuk.mytimer1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = "myLog";
    //Log.d(LOG_TAG, "Создали активити");

    //глобальные переменные проекта
    public static final String TIMER_ID = "id";
    public static final String TIMER_NAME = "name";
    public static final String TIMER_MESSAGE = "message";
    public static final String TIMER_DURATION = "duration";
    public static final String TIMER_LOST_TIME = "lostTime";
    public static final String TIMER_RUN = "runTimer";

    //массив таймеров
    MyTimer timers[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout mmm = (LinearLayout) findViewById(R.id.mmm);
        timers = new MyTimer[10];
        timers[0] = new MyTimer(this, 0, "Ser", "Hello", 5);
        timers[1] = new MyTimer(this, 1, "Sergey", "Hello", 10);
        mmm.addView(timers[0].getLayoutMain());
        mmm.addView(timers[1].getLayoutMain());
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
    //метод принимает настройки из TimerMenu и меняет настройки соответствующего таймера
    //вызов TimerMenu происходит из самого таймера командой
    // activity.startActivityForResult(intent, id);
    // ответ приходит сюда, так как MyTimer не
    //является активностью и не поддерживает метода onActivityResult
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // проверяем, что в качестве настроек не пришло null
        if (data == null) {
            Log.d(LOG_TAG, "в onActivityResult вернулся null");
            return;
        }
        Log.d(LOG_TAG, "requestCode = " + requestCode);
        String name = data.getStringExtra(TIMER_NAME);// извлекаем имя таймера из интента
        String mess = data.getStringExtra(TIMER_MESSAGE);// извлекаем сообщение таймера из интента
        int dur = data.getIntExtra(TIMER_DURATION, 1000);// извлекаем время таймера из интента
        timers[requestCode].initSetting(name, mess, dur);// отправляем настройки в таймер. в requestCode содержится id таймера который вызват меню настроек


        //TODO Сюда нужно добавить код сохранения настроек таймера в файл
        //ХХХ
    }
}