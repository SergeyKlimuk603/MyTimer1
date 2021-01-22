package by.klimuk.mytimer1;

public class MyTimer {

    //Создаем переменную лога для наладки класса
    private final String LOG_TAG = "myLog";

    //Переменные таймера
    private int id; // идентификатор таймера
    private String name; // имя таймера
    private String message; // сообщение выдаваемое таймером по окончании отсчета
    private int duration; // время отсчитываемое таймером
    private MainActivity activity; // ссылка на вызывающую таймер активность

    // конструктор класса MyTimer
    public MyTimer(MainActivity _activity, int _id, String _name, String _message,
                   int _duration) {

        setActivity(_activity);
        setId(_id);
        setName(_name);
        setMessage(_message);
        setDuration(_duration);

        // инициализация таймера
        initTimer();
    }

    private void initTimer() {
        // инициализация представлений (view) таймера
        initView();
    }

    private void initView() {

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
}
