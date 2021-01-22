package by.klimuk.mytimer1;


import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyTimer implements View.OnClickListener {

    //Создаем переменную лога для наладки класса
    private final String LOG_TAG = "myLog";
    // Log.d(LOG_TAG, "sdkfj");

    //Переменные таймера
    private int id; // идентификатор таймера
    private String name; // имя таймера
    private String message; // сообщение выдаваемое таймером по окончании отсчета
    private int duration; // время отсчитываемое таймером
    private MainActivity activity; // ссылка на вызывающую таймер активность


    // переменные доступа к view элементам таймера
    FrameLayout layoutMain;// корнеове View для таймера
    FrameLayout layoutMainBack;// подсветка корневого слоя в пределах границы border
    TextView tvName;// имя таймера в верхнем левом углу
    TextView tvDur;// длительность таймера в верхнем правом углу
    TextView tvMess;// сообщения таймера в центре
    LinearLayout layoutBtn;// слой для добавления в него кнопок

    // переменные кнопок
    Button btnStart;
    Button btnReset;
    Button btnPause;
    Button btnCont;
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
    public MyTimer(MainActivity activity, int _id, String _name, String _message,
                   int _dur) {
        //передаем переменные из активити
        setActivity(activity);
        setId(_id);
        //инициализируем переменные названий кнопок
        initBtnNames();
        // инициализация таймера
        initTimer();
        //настраиваем таймер
        initSetting(_name, _message, _dur);
        Log.d(LOG_TAG, "initSetting прошло успешно");
    }



    private void initBtnNames() {
        BTN_START_NAME = activity.getResources().getString(R.string.start);
        BTN_RESET_NAME = activity.getResources().getString(R.string.reset);
        BTN_PAUSE_NAME = activity.getResources().getString(R.string.pause);
        BTN_CONT_NAME = activity.getResources().getString(R.string.cont);
    }

    private void initTimer() {
        // инициализация представлений (view) таймера
        initView();
        //создаем кнопки управления таймером
        createButtons();
        //заполняем таймер начальными значениями надписей и кнопками
    }

    //настройка таймера
    private void initSetting(String _name, String _message, int _duration) {
        setName(_name);// установка имени таймера
        setMessage(_message);// установка сообщения таймера
        setDuration(_duration);// установка длительности таймера
        tvName.setText(name);// выводим имя таймера на экран
        //преобразуем время duration в удобный для отображения формат и выводим на экран в правом верхнем углу таймера
        tvDur.setText(Converter.intToStringTime(duration));
        reset(); //сбрасываем таймер
    }

    private void initView() {
        //получаем inflater из activity и получаем корневое view из xml файла для отображения таймера
        LayoutInflater inflater = activity.getLayoutInflater();
        layoutMain = (FrameLayout) inflater.inflate(R.layout.timer_view, null, false);

        // доступ к View полям таймера
        layoutMainBack = (FrameLayout) layoutMain.findViewById(R.id.flMainBack);
        tvName = (TextView) layoutMain.findViewById(R.id.tvName);
        tvDur = (TextView) layoutMain.findViewById(R.id.tvDur);
        tvMess = (TextView) layoutMain.findViewById(R.id.tvMess);
        layoutBtn = (LinearLayout) layoutMain.findViewById(R.id.layoutBtn);
    }


    //создание кнопок таймера
    private void createButtons() {
        //создаем LayoutParams кнопок для дальнейшего использования
        LinearLayout.LayoutParams btnLP = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLP.weight = 1;
        btnLP.gravity = Gravity.CENTER;

        // создаем сами кнопки
        // надо попробовать сделать это через цикл с использованием массива названий чтобы убрать повторяемость кода
        btnStart = createButton(BTN_START_NAME, BTN_START_ID, btnLP);
        btnReset = createButton(BTN_RESET_NAME, BTN_RESET_ID, btnLP);
        btnPause = createButton(BTN_PAUSE_NAME, BTN_PAUSE_ID, btnLP);
        btnCont = createButton(BTN_CONT_NAME, BTN_CONT_ID, btnLP);
    }

    //метод создания одной кнопки
    private Button createButton(String _name, int _id, ViewGroup.LayoutParams _lp) {
        Button btn = new Button(activity);// новая кнопка
        btn.setId(_id);// id кнопки
        btn.setText(_name);// название кнопки
        btn.setLayoutParams(_lp);// параметры расположения кнопки
        btn.setTextColor(activity.getResources().getColor(R.color.textColor));// цвет текста кнопки
        btn.setBackgroundResource(R.drawable.border);// рамка вокруг кнопки
        btn.setOnClickListener(this);//добавляем данный класс слушателем кнопки
        Log.d(LOG_TAG, "Создали кнопку");
        return btn;
    }

    //пересоздаем панель кнопок
    private void addButtons(Button... btns) {
        layoutBtn.removeAllViews();//удаляем старые кнопки
        //добавляем новые
        for (Button btn : btns) {
            layoutBtn.addView(btn);
        }
    }

    //обработка нажатия на кнопки
    public void onClick(View v) {

    }

    //сброс таймера
    private void reset() {
        //устанавливаем кнопку старт
        addButtons(btnStart);
        //сбрасываем сообщение таймера
        tvMess.setText(name);
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
        this.duration = duration;
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
