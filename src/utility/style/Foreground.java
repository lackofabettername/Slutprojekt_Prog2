package utility.style;

@SuppressWarnings("unused")
public enum Foreground implements Style {
    Black(30),
    Red(31),
    Green(32),
    Orange(33),
    Blue(34),
    Magenta(35),
    Cyan(36),
    LightGrey(37),

    DarkGrey(90),
    LightRed(91),
    LightGreen(92),
    DarkYellow(93),
    LightBlue(94),
    LightMagenta(95),
    LightCyan(96),
    White(97);

    private final String code;
    Foreground (int value) {
        this.code = (char) 27 + "[" + value + "m";
    }
    @Override
    public String getCode () {
        return code;
    }
}
