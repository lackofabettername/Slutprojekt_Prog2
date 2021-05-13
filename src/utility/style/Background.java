package utility.style;

@SuppressWarnings("unused")
public enum Background implements Style {
    Black(40),
    Red(41),
    Green(42),
    Orange(43),
    Blue(44),
    Magenta(45),
    Cyan(46),
    Grey(47),

    LightGrey(7),

    DarkGrey(100),
    LightRed(101),
    LightGreen(102),
    DarkYellow(103),
    LightBlue(104),
    LightMagenta(105),
    LightCyan(106),
    White(107);

    private final String code;
    Background (int value) {
        this.code = (char) 27 + "[" + value + "m";
    }
    @Override
    public String getCode () {
        return code;
    }
}
