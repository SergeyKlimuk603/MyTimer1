package by.klimuk.mytimer1;

public class Converter {
    //класс преобразующий непрерывное время таймера к удобному для отображения виду

    public static String intToStringTime(int time) {
        int sec = time % 60;
        int min = (time % 3600) / 60;
        int hour = time / 3600;
        return String.format("%02d:%02d:%02d", hour, min, sec);
    }
}
