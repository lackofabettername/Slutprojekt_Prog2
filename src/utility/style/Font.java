package utility.style;

@SuppressWarnings("unused")
public enum Font implements Style {
    Bold(1),
    Underline(4),
    StrikeThrough(9),
    Boxed(51);


    private final String code;
    Font (int value) {
        this.code = (char) 27 + "[" + value + "m";
    }
    @Override
    public String getCode () {
        return code;
    }
}