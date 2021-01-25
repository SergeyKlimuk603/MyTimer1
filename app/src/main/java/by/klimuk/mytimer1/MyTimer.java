package by.klimuk.mytimer1;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static by.klimuk.mytimer1.MainActivity.*;

public class MyTimer implements View.OnClickListener, Runnable {

    //Создаем переменную лога для наладки класса
    private final String LOG_TAG = "myLog";
    // Log.d(LOG_TAG, "sdkfj");

    //Переменные таймера
    private int id; // идентификатор таймера
    private String name; // имя таймера
    private String message; // сообщение выдаваемое таймером по окончании отсчета
    private int duration; // время отсчитываемое таймером
    private  int time;// текущее время таймера
    private int lostTime;// время оставшееся для отсчет после запуска или перезапуска.
    // Если lostTime < 0 - таймер в состоянии ожидания команды запуска
    // Если lostTime = 0 - таймер завершил отсчет и ждет сброса
    // Если lostTime > 0 - таймер в состоянии отсчета времени или паузы (зависит от переменной runTimer)
    private long startTime;// системное время запуска или перезапуска таймера
    private boolean runTimer; //флаг работы таймера. Если идет отсчет runTimer == true

    // Вспомогательные элементы таймера
    private Handler handler;// handler для отсчета времени
    private MainActivity activity; // ссылка на вызывающую таймер активность
    private Converter timeConvert;// объект для преобразования времени в различные форматы

    // переменные доступа к view элементам таймера
    private FrameLayout layoutMain;// корнеове View для таймера
    private FrameLayout layoutMainBack;// подсветка корневого слоя в пределах границы border
    private TextView tvName;// имя таймера в верхнем левом углу
    private TextView tvDur;// длительность таймера в верхнем правом углу
    private TextView tvMess;// сообщения таймера в центре
    private LinearLayout layoutBtn;// слой для добавления в него кнопок
    private ImageView ivSet;// значек настройки таймера

    // переменные кнопок
    private Button btnStart;
    private Button btnReset;
    private Button btnPause;
    private Button btnCont;
    // идентификаторы кнопок
    private final int BTN_START_ID = 1;
    private final int BTN_RESET_ID = 2;
    private final int BTN_PAUSE_ID = 3;
    private final int BTN_CONT_ID = 4;
    //названия кнопок
    private String BTN_START_NAME;
    private String BTN_RESET_NAME;
    private String BTN_PAUSE_NAME;
    private String BTN_CONT_NAME;

    // конструктор класса MyTimer
    public MyTimer(MainActivity _activity, int _id, String _name, String _message,
                   int _dur) {
        //передаем переменные из активити
        setActivity(_activity);
        setId(_id);
        //инициализируем переменные названий кнопок
        initBtnNames();
        // инициализация таймера
        initTimer();
        //настраиваем таймер
        initSetting(_name, _message, _dur);
        //создаем handler для отсчета интервала времени 1с
        handler = new Handler();
    }

    //имена кнопок берем из ресурса strings
    private void initBtnNames() {
        BTN_START_NAME = activity.getResources().getString(R.string.start);
        BTN_RESET_NAME = activity.getResources().getString(R.string.reset);
        BTN_PAUSE_NAME = activity.getResources().getString(R.string.pause);
        BTN_CONT_NAME = activity.getResources().getString(R.string.cont);
    }

    //инициализация таймера
    private void initTimer() {
        // инициализация представлений (view) таймера
        initView();
        //создаем кнопки управления таймером
        createButtons();
        //создаём конвертер для преобразования формата времени
        timeConvert = new Converter();
    }

    //настройка таймера (запускаем при создании таймера или при изменении его настроек)
    void initSetting(String _name, String _message, int _duration) {
        setName(_name);// установка имени таймера
        setMessage(_message);// установка сообщения таймера
        setDuration(_duration);// установка длительности таймера
        //заполняем таймер начальными значениями надписей и кнопками
        tvName.setText(name);// выводим имя таймера на экран
        //преобразуем время duration в удобный для отображения формат и выводим на экран в правом верхнем углу таймера
        tvDur.setText(timeConvert.intToStringTime(duration));
        reset(); //сбрасываем таймер
    }

    //получаем доступ к представления таймера
    private void initView() {
        //получаем inflater из activity и получаем корневое view из xml файла для отображения таймера
        LayoutInflater inflater = activity.getLayoutInflater();
        layoutMain = (FrameLayout) inflater.inflate(R.layout.timer_view, null, false);

        // доступ к View полям таймера
        layoutMainBack = (FrameLayout) layoutMain.findViewById(R.id.flMainBack);//слой для изменения фона таймера
        tvName = (TextView) layoutMain.findViewById(R.id.tvName);//область имени таймера в верхнем левом углу
        tvDur = (TextView) layoutMain.findViewById(R.id.tvDur);//область отображения времени отсчитываемого таймером в верхнем правом углу
        tvMess = (TextView) layoutMain.findViewById(R.id.tvMess);//область таймера для вывода сообщений в центре таймера
        //tvMess.setOnClickListener(this);// добавляем слушателя к панели сообщений для вызова меню настройки таймера
        layoutBtn = (LinearLayout) layoutMain.findViewById(R.id.layoutBtn);//область где будут размещаться кнопки таймера
        ivSet = (ImageView) layoutMain.findViewById(R.id.ivSet);// добавляем значек для вызова меню настроек таймера
        ivSet.setOnClickListener(this);//  добавляем слушателя к значку настройки таймера
    }

    //создание кнопок таймера
    private void createButtons() {
        //создаем LayoutParams кнопок для дальнейшего использования
        LinearLayout.LayoutParams btnLP = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLP.weight = 1;//кнопки равномерно распределяются в области layoutBtn
        btnLP.gravity = Gravity.CENTER;// надписи на кнопках располагаются в центре

        // создаем сами кнопки
        // надо попробовать сделать это через цикл с использованием массива названий чтобы убрать
        // повторяемость кода
        btnStart = createButton(BTN_START_NAME, BTN_START_ID, btnLP, R.drawable.border);
        btnReset = createButton(BTN_RESET_NAME, BTN_RESET_ID, btnLP, R.drawable.border_solid);
        btnPause = createButton(BTN_PAUSE_NAME, BTN_PAUSE_ID, btnLP, R.drawable.border_solid);
        btnCont = createButton(BTN_CONT_NAME, BTN_CONT_ID, btnLP, R.drawable.border_solid);
    }

    //метод создания одной кнопки
    private Button createButton(String _name, int _id, ViewGroup.LayoutParams _lp,
                                int _background) {
        Button btn = new Button(activity);// новая кнопка
        btn.setId(_id);// id кнопки
        btn.setText(_name);// название кнопки
        btn.setLayoutParams(_lp);// параметры расположения кнопки
        btn.setTextColor(activity.getResources().getColor(R.color.textColor));// цвет текста кнопки
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

    //обработка нажатия на кнопки
    public void onClick(View v) {
        switch (v.getId()) {
            case BTN_START_ID: //нажата кнопка Start
                start();
                break;
            case BTN_RESET_ID: //нажата кнопка Stop
                reset();
                break;
            case BTN_PAUSE_ID: //нажата кнопка Pause
                pause();
                break;
            case BTN_CONT_ID: //нажата кнопка Continue
                cont();
                break;
            case R.id.ivSet:
                callMenu();// вызвать меню таймера
                break;
            default:
                break;
        }
    }

    //запустить отсчет
    private void start() {
        addButtons(btnPause, btnReset);//обновляем кнопки
        runTimer = true;// таймер считает
        lostTime = duration; //начало отсчета, оставшееся для отсчета время
        startTime = System.currentTimeMillis();// системное время при пуске или перезапуске таймера
        run();//запускаем отсчет
    }

    //сброс таймера
    private void reset() {
        runTimer = false;// останавливаем таймер
        //устанавливаем кнопку старт
        addButtons(btnStart);//обновляем кнопки
        //сбрасываем сообщение таймера вместо сообщения выводим имя
        tvMess.setText(name);
        //убираем фон
        layoutMainBack.setBackgroundResource(R.color.transparent);
        //таймер в ожидании комманды запуска
        lostTime = -1;
        // сообщаем активности, что сбросили таймер. Это нужно для отключения сигнала, если сработавший
        // таймер был последним (определяем по сообщению таймера: если оно совпадает с тем, что
        // на общем экране - значит это последний сработавший таймер)
        activity.timerReset(message);
    }

    //приостановить отсчет времени
    private void pause() {
        addButtons(btnCont, btnReset);//обновляем кнопки
        runTimer = false;// останавливаем таймер
        lostTime = time;// запоминаем оставшееся для отсчета время
    }

    //продолжить отсчет времени
    private void cont() {
        addButtons(btnPause, btnReset);//обновляем кнопки
        // продолжаем остчет времени
        startTime = System.currentTimeMillis();// системное время при пуске или перезапуске таймера
        runTimer = true;
        run();
    }

    // вызываем меню таймера
    private void callMenu() {
        Intent intent = new Intent(activity, TimerMenu.class);
        intent.putExtra(TIMER_NAME, name);// передаем текущее имя таймера
        intent.putExtra(TIMER_MESSAGE, message);// передаем текущее сообщение таймера
        intent.putExtra(TIMER_DURATION, duration);// передаем текущую уставку времени таймера
        activity.startActivityForResult(intent, id);// вызываем меню настройки таймера с помощью, т.к. сам таймер такой функции не поддерживает
    }

    //отсчет закончен
    private void endTime() {
        addButtons(btnReset);//обновляем кнопки
        tvMess.setText(message);
        layoutMainBack.setBackgroundResource(R.color.background_main);// выделяем сработавший таймер фоном
        lostTime = 0;// таймер завершил отсчет времени и ждет сброса
        // сообщаем активности о завершении отсчета для включения сигнала
        // и передаем ей сообщение таймера для вывода его на общий экран сообщений
        activity.timerEnd(message);
    }

    // отсчет времени
    public void run() {
        if (!runTimer) return;//таймер остановлен, прекращаем отсчет
        time = lostTime - ((int) (System.currentTimeMillis() - startTime) / 1000);// оставшееся текущее время
        tvMess.setText(timeConvert.intToStringTime(time));//выводим оставшееся текущее время на экран
        if (time <= 0){
            endTime();
            return;//время вышло
        }
        handler.postDelayed(this, 1000);
    }

//___________Геттеры и сеттеры переменных таймера_____________________
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        if(duration < 0) {
            this.duration = 0;
        } else if (duration > 359999) {
            this.duration = 359999;
        } else {
            this.duration = duration;
        }
    }

    public MainActivity getActivity() {
        return activity;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public FrameLayout getLayoutMain() {
        return layoutMain;
    }
}
