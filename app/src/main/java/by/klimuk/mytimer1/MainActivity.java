package by.klimuk.mytimer1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends Activity implements View.OnClickListener {

    private final String LOG_TAG = "myLog";
    //Log.d(LOG_TAG, "Создали активити");

    //глобальные переменные проекта
    public static final String TIMER_ID = "id";
    public static final String TIMER_NAME = "name";
    public static final String TIMER_MESSAGE = "message";
    public static final String TIMER_DURATION = "duration";
    public static final String TIMER_LOST_TIME = "lostTime";
    public static final String TIMER_RUN = "runTimer";
    public static final int TIMER_DELETE_RESULT = -51;//переменная используется чтобы сообщить об удалении таймера из настроек. Значение взято от балды
    public static final String SAVE_FILE_NAME = "myTimer1";
    public static final int MAX_TIMERS_AMOUNT = 10;//максимальное количество таймеров

    //настройки таймера по умолчанию
    private final int DEFAULT_ID = 0;
    private final String DEFAULT_NAME = "Default timer";
    private final String DEFAULT_MESSAGE = "Default message";
    private final int DEFAULT_DURATION = 10;

    //переменные доступа к view элементам
    LinearLayout timersList;//поле списка таймеров
    FrameLayout flMainMessBack;// поле подсветки основного сообщения
    FrameLayout flSoundOffBack;// поле подсветки кнопки октлючения звука
    TextView tvMainMess;//основное сообщение
    TextView btnSoundOff;//кнопка отключения звука
    TextView btnAdd;//кнопка добавления таймера

    //список таймеров
    HashMap<Integer, MyTimer> timers;

    //проигрыватель звуковых файлов
    MediaPlayer mp;

    //переменная онимации кнопок, если true - анимация включена
    private boolean animation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //создаем музыкальный проигрыватель
        mp = MediaPlayer.create(this, R.raw.music);
        //инициализация представлений
        initView();
        //создаем список для таймеров
        timers = new HashMap<Integer, MyTimer>();
        //заполняем список таймерами из файла (создаем таймеры из файла)
        createTimers();
    }

    private void initView() {
        timersList = (LinearLayout) findViewById(R.id.timersList);
        flMainMessBack = (FrameLayout) findViewById(R.id.flMainMessBack);
        flSoundOffBack = (FrameLayout) findViewById(R.id.flSoundOffBack);
        tvMainMess = (TextView) findViewById(R.id.tvMainMess);
        btnSoundOff = (TextView) findViewById(R.id.btnSoundOff);
        btnSoundOff.setOnClickListener(this);
        btnAdd = (TextView) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
    }

    private void createTimers() {
        //создаем объекты для работы с файлом сохранения таймеров
        SharedPreferences sPref = getSharedPreferences(SAVE_FILE_NAME, MODE_PRIVATE);
        for(int i = 0; i < MAX_TIMERS_AMOUNT; i++) {
            //проверяем есть ли в записях таймер с id == i, если нет - пропускаем этот id
            int id = sPref.getInt(TIMER_ID + i, -1);
            if(id == -1) {continue;}
            String name = sPref.getString(TIMER_NAME + i, DEFAULT_NAME);
            String mess = sPref.getString(TIMER_MESSAGE + i, DEFAULT_MESSAGE);
            int dur = sPref.getInt(TIMER_DURATION + i, DEFAULT_DURATION);
            createTimer(i, name, mess, dur);
        }
    }

    //создаем и добавляем новый таймер
    private void addNewTimer() {
        //ищем свободный id для нового таймера
        int freeId = findFreeId();
        //если свободного id нет, то выводим сообщение, что создано максимальное количество
        // таймеров и выходим из метода
        if(freeId >= MAX_TIMERS_AMOUNT) {
            Toast.makeText(this, getResources().getText(R.string.max_timers_amount) +
                    " = " + MAX_TIMERS_AMOUNT, Toast.LENGTH_LONG).show();
            return;
        }
        //создаем таймер с полученным свободным id
        createTimer(freeId, DEFAULT_NAME, DEFAULT_MESSAGE, DEFAULT_DURATION);
        //сохраняем новый таймер в файл
        saveTimer(timers.get(freeId));
    }

    //создаем новый таймер с заданным id
    private void createTimer(int _id, String _name, String _message, int _duration) {
        //создаем новый таймер
        MyTimer t = new MyTimer(this, _id, _name, _message, _duration);
        //помещаем его в список таймеров
        timers.put(_id, t);
        //добавляем новый таймер на экран
        timersList.addView(t.getLayoutMain());
    }

    //ищем сободный id для таймера
    private int findFreeId() {
        //создаем объекты для работы с файлом сохранения таймеров
        SharedPreferences sPref = getSharedPreferences(SAVE_FILE_NAME, MODE_PRIVATE);
        //SharedPreferences.Editor editor = sPref.edit();
        for(int i = 0; i < MAX_TIMERS_AMOUNT; i++) {
            //считываем имеющиеся id из файла, если id отсутствует,
            //значит от свободен, метод возвращает номер этого id, для создания нового таймера
            int id = sPref.getInt(TIMER_ID + i, -1);
            if (id == -1) {
                return i;
            }
        }
        //если все id заняты перезаписываем таймер с id 100, даже если он существует
        return MAX_TIMERS_AMOUNT;
    }

    //обрабатываем нажатие кнопок на главной панели
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd://нажата кнопка добавления таймера
                addNewTimer();//создать и добавить новый таймер
                break;
            case R.id.btnSoundOff://нажата кнопка выключения звука
                stopSound();//выключить звук
        }
    }

    // сообщаем активности, что сбросили таймер. Это нужно для отключения сигнала, если сработавший
    // таймер был последним (определяем по сообщению таймера: если оно совпадает с тем, что
    // на общем экране - значит это последний сработавший таймер)
    public void timerReset(String _message) {
        //выключаем звук
        stopSound();
        //Если сообщения таймера и главное сообщение совпадают, сбрасываем подсветку главного сообщения
        String mess = tvMainMess.getText().toString();
        if (mess.equals(_message)) {
            //убираем фон
            flMainMessBack.setBackgroundColor(Color.BLACK);
            //удаляем сообщение
            tvMainMess.setText(getResources().getText(R.string.no_message));
        }
    }

    // сообщаем активности о завершении отсчета для включения сигнала
    // и передаем ей сообщение таймера для вывода его на общий экран сообщений
    public void timerEnd(String _message) {
        //подсвечиваем панель главного сообщения
        flMainMessBack.setBackgroundResource(R.drawable.border_solid);
        //выводим сообщение таймера на панель главного сообщения
        tvMainMess.setText(_message);
        //включаем сигнал
        playSound();
    }
    //метод принимает настройки из TimerMenu и меняет настройки соответствующего таймера
    //вызов TimerMenu происходит из самого таймера командой
    // activity.startActivityForResult(intent, id);
    // ответ приходит сюда, так как MyTimer не
    //является активностью и не поддерживает метода onActivityResult
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // проверяем ответ настроек таймера с id записанным в requestCode
        if (resultCode == RESULT_OK) {
            String name = data.getStringExtra(TIMER_NAME);// извлекаем имя таймера из интента
            String mess = data.getStringExtra(TIMER_MESSAGE);// извлекаем сообщение таймера из интента
            int dur = data.getIntExtra(TIMER_DURATION, DEFAULT_DURATION);// извлекаем время таймера из интента
            timers.get(requestCode).initSetting(name, mess, dur);// отправляем настройки в таймер. в requestCode содержится id таймера который вызват меню настроек
            saveTimer(timers.get(requestCode));//сохраняем настройки таймера в файл
            return;
        }

        //удаляем таймер с id записанным в requestCode
        if(resultCode == TIMER_DELETE_RESULT) {
            //создаем объекты для работы с файлом сохранения таймеров
            SharedPreferences sPref = getSharedPreferences(SAVE_FILE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sPref.edit();
            //удаляем таймер из файла
            editor.remove(TIMER_ID + requestCode);
            editor.remove(TIMER_NAME + requestCode);
            editor.remove(TIMER_MESSAGE + requestCode);
            editor.remove(TIMER_DURATION + requestCode);
            editor.apply();
            //удаляем таймер с экрана
            timersList.removeView(timers.get(requestCode).getLayoutMain());
            //удаляем таймер из списка
            timers.remove(requestCode);
        }
    }

    //сохранение настроек таймера в файл
    private void saveTimer(MyTimer t) {
        SharedPreferences sPref = getSharedPreferences(SAVE_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putInt((TIMER_ID + t.getId()), t.getId());
        editor.putString((TIMER_NAME + t.getId()), t.getName());
        editor.putString((TIMER_MESSAGE + t.getId()), t.getMessage());
        editor.putInt((TIMER_DURATION + t.getId()), t.getDuration());
        editor.apply();
    }

    //начать воспроизведение сигнала
    private void playSound() {
        //включаем сигнал
        mp.release();//освобождаем ресурсы старого музыкального проигрывателя
        //создаем новый музыкальный проигрыватель
        mp = MediaPlayer.create(this, R.raw.music);
        mp.start();//начинаем воспроизведение
        //присваиваем слушателя, который определяет окончание композиции
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();//освобождаем ресурсы музыкального проигрывателя
                animation = false;
            }
        });
        //включаем пульсацию кнопки выключения сигнала SoundOff
        frameLayoutAnim(flSoundOffBack);
    }

    //остановить воспроизведение сигнала
    private void stopSound() {
        animation = false;//запретить анимацию кнопки выключения звука
        flSoundOffBack.setBackgroundColor(Color.BLACK);//убираем фон кнопки выключения звука
        mp.release();//выключаем звук и освобождаем ресурсы проигрывателя
    }

    private void frameLayoutAnim(FrameLayout fl) {
        if(animation) {return;}//если анимация уже запущена выходим из метода
        animation = true;//разрешаем анимацию
        //создаем анимацию в другом потоке
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //если анимация запрещена устанавливаем черный фон и покидаем метод
                if(animation == false) {
                    fl.setBackgroundColor(Color.BLACK);
                    return;
                }
                //создаем и начинаем анимацию
                Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.alpha);
                fl.setBackgroundResource(R.drawable.border_solid);//фон анимации
                fl.startAnimation(anim);
                handler.postDelayed(this, 2000);
            }
        });
    }

    protected void onDestroy() {
        mp.release();//убиваем плеер при выходе из приложения
        super.onDestroy();
    }
}