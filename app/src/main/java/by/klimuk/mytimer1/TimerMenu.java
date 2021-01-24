package by.klimuk.mytimer1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import static by.klimuk.mytimer1.MainActivity.*;

public class TimerMenu extends Activity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {

    private final String LOG_TAG = "myLog";
    //Log.d(LOG_TAG, "Создали активити");

    // переменные доступа к view элементам меню
    private EditText etName;// поле имени таймера
    private EditText etMess;// поле сообщения таймера
    private TextView tvDur;// поле длительности таймера
    private SeekBar sbHour;// ползунок часов
    private SeekBar sbMin;// ползунок минут
    private SeekBar sbSec;// ползунок секунд
    private Button btnSave;// кнопка сохранения настроек
    private Button btnDelete;// кнопка удаления таймера
    private Button btnDeleteYes;// кнопка подтверждения удаления таймера
    private Button btnDeleteNo;// кнопка отмены удаления таймера
    private Button btnDeleteMess;//сообщение об удалении таймера. Удобно использовать кнопку без отклика
    private LinearLayout layoutBtn;// область кнопок меню

    //получаем данные таймера
    Intent intent;

    //данные таймера в меню настроек, их будем менять
    private int duration;


    // объект для преобразования времени в различные форматы
    private Converter timeConvert;

    //названия кнопок
    private String BTN_SAVE_NAME;//кнопка сохранения настроек
    private String BTN_DELETE_NAME;//кнопка удаления таймера
    private String BTN_DELETE_YES_NAME;//кнопка подтверждения удаления таймера
    private String BTN_DELETE_NO_NAME;//кнопка отмены удаления таймера
    private String BTN_DELETE_MESS_NAME;//сообщение подтверждения удаления таймера

    //id кнопок
    private final int BTN_SAVE_ID = 10;//id кнопки сохранения настроек
    private final int BTN_DELETE_ID = 11;//id кнопки удаления таймера
    private final int BTN_DELETE_YES_ID = 12;//id кнопки подтверждения удаления таймера
    private final int BTN_DELETE_NO_ID = 13;//id кнопки отмены удаления таймера
    private final int BTN_DELETE_MESS_ID = 14;//id сообщения подтверждения удаления таймера


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_menu);
        //получаем доступ к представлениям
        initView();
        //получаем данные от таймера
        intent = getIntent();
        // объект для преобразования времени в различные форматы
        timeConvert = new Converter();
        // заполняем поля настроек данными из таймера
        initSetting();
        Log.d(LOG_TAG, "метод initSetting завершен");
        //создание кнопок меню
        createButtons();
        //добавление кнопок на экран
        addButtons(btnDelete);

    }

    //инициализация Views
    private void initView() {
        etName = (EditText) findViewById(R.id.etName);
        etMess = (EditText) findViewById(R.id.etMess);
        tvDur = (TextView) findViewById(R.id.tvDur);
        sbHour = (SeekBar) findViewById(R.id.sbHour);
        sbMin = (SeekBar) findViewById(R.id.sbMin);
        sbSec = (SeekBar) findViewById(R.id.sbSec);
        //btnSave = (Button) findViewById(R.id.btnSave);
        //btnSave.setOnClickListener(this);
        layoutBtn = (LinearLayout) findViewById(R.id.layoutBtn);
    }
    //заполняем поля настроек данными из таймера
    private void initSetting() {
        etName.setText(intent.getStringExtra(TIMER_NAME));// выводим имя таймера
        etMess.setText(intent.getStringExtra(TIMER_MESSAGE));// выводим сообщение
        duration = intent.getIntExtra(TIMER_DURATION, 10);//Получаем значение уставки таймера в секундах
        tvDur.setText(timeConvert.intToStringTime(duration));//выводим длительность таймера
        //задаем значения расположения ползунков задания времени в соответствии с длительностью таймера
        sbHour.setProgress(timeConvert.hour);
        sbHour.setOnSeekBarChangeListener(this);
        sbMin.setProgress(timeConvert.min);
        sbMin.setOnSeekBarChangeListener(this);
        sbSec.setProgress(timeConvert.sec);
        sbSec.setOnSeekBarChangeListener(this);
    }

    //создание кнопок таймера
    private void createButtons() {
        //создаем LayoutParams кнопок для дальнейшего использования
        LinearLayout.LayoutParams btnLP = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLP.weight = 1;//кнопки равномерно распределяются в области layoutBtn
        btnLP.gravity = Gravity.CENTER;// надписи на кнопках располагаются в центре
        initBtnNames();//инициализируем имена кнопок

        // создаем сами кнопки
        // надо попробовать сделать это через цикл с использованием массива названий чтобы убрать
        // повторяемость кода
        btnSave = createButton(BTN_SAVE_NAME, BTN_SAVE_ID, btnLP, R.drawable.border);
        btnDelete = createButton(BTN_DELETE_NAME, BTN_DELETE_ID, btnLP, R.drawable.border);
        btnDeleteYes = createButton(BTN_DELETE_YES_NAME, BTN_DELETE_YES_ID, btnLP, R.drawable.border);
        btnDeleteNo = createButton(BTN_DELETE_NO_NAME, BTN_DELETE_NO_ID, btnLP, R.drawable.border);
        btnDeleteMess = createButton(BTN_DELETE_MESS_NAME, BTN_DELETE_MESS_ID, btnLP, R.drawable.border);
    }
    //имена кнопок берем из ресурса strings
    private void initBtnNames() {
        BTN_SAVE_NAME = getResources().getString(R.string._save);//кнопка сохранения настроек
        BTN_DELETE_NAME = getResources().getString(R.string.delete);//кнопка удаления таймера
        BTN_DELETE_YES_NAME = getResources().getString(R.string.yes);//кнопка подтверждения удаления таймера
        BTN_DELETE_NO_NAME = getResources().getString(R.string.no);//кнопка отмены удаления таймера
        BTN_DELETE_MESS_NAME = getResources().getString(R.string.delete_timer_mess);//сообщение подтверждения удаления таймера

    }

    //метод создания одной кнопки
    private Button createButton(String _name, int _id, ViewGroup.LayoutParams _lp,
                                int _background) {
        Button btn = new Button(this);// новая кнопка
        btn.setId(_id);// id кнопки
        btn.setText(_name);// название кнопки
        btn.setLayoutParams(_lp);// параметры расположения кнопки
        btn.setTextColor(getResources().getColor(R.color.textColor));// цвет текста кнопки
        btn.setBackgroundResource(_background);// рамка вокруг кнопки
        btn.setOnClickListener(this);//добавляем данный класс слушателем кнопки
        return btn;
    }

    //пересоздаем панель кнопок
    private void addButtons(Button... btns) {//получаем набор кнопок btns
        layoutBtn.removeAllViews();//удаляем старые кнопки
        //добавляем новые
        for (Button btn : btns) {
            layoutBtn.addView(btn);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                save();
                break;
        }
    }

    private void save() {
        Intent intent = new Intent();
        intent.putExtra(TIMER_NAME, etName.getText().toString());//новое имя таймера
        intent.putExtra(TIMER_MESSAGE, etMess.getText().toString());//новое сообщение таймера
        duration = timeConvert.toSeconds();//преобразуем время в секунды
        intent.putExtra(TIMER_DURATION, duration);//новое время таймера
        setResult(RESULT_OK, intent);//передаем настройки в MainActivity
        finish();//закрываем меню настроек таймера
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sbHour:
                timeConvert.hour = progress;
                break;
            case R.id.sbMin:
                timeConvert.min = progress;
                break;
            case R.id.sbSec:
                timeConvert.sec = progress;
                break;
        }
        tvDur.setText(timeConvert.intToStringTime());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}