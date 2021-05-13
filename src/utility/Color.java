package utility;

import java.io.Serializable;

/**
 * Stores colors in either RGB, HSV or HSL
 */
@SuppressWarnings({"FieldMayBeFinal", "unused"})
public class Color implements Serializable {//TODO lerp, mix
    private ColorMode _mode;

    private float _v0;
    private float _v1;
    private float _v2;
    private float _alpha;

    public Color(ColorMode mode, float v0, float v1, float v2, float v3) {
        _mode = mode;
        switch (mode) {
            case RGBA, HSVA, HSLA -> {
                _v0 = v0;
                _v1 = v1;
                _v2 = v2;
                _alpha = v3;
            }
            case ARGB, AHSV, AHSL -> {
                _alpha = 0;
                _v0 = v1;
                _v1 = v2;
                _v2 = v3;
            }
        }
    }
    public Color(ColorMode mode, float v0, float v1, float v2, float v3, float range0, float range1, float range2, float range3) {
        this(mode, v0 / range0, v1 / range1, v2 / range2, v3 / range3);
    }

    public float getAlpha() {
        return _alpha;
    }
    public float getAlpha(float range) {
        return _alpha * range;
    }

    public void setAlpha(float value) {
        _alpha = value;
    }
    public void setAlpha(float value, float range) {
        _alpha = value / range;
    }

    //region RGB
    public float getRed() {
        return switch (_mode) {
            case RGBA, ARGB -> _v0;
            case HSVA, AHSV -> {
                float c = _v1 * _v2;
                float h = _v0 / (60 / 360f);
                float x = c * (1 - Math.abs(h % 2 - 1));
                float m = _v2 - c;

                if (!Float.isFinite(h)) {
                    yield 0;
                }
                yield switch ((int) Math.ceil(h)) {
                    case 6, 1, 0 -> c;
                    case 5, 2 -> x;
                    default -> 0;
                } + m;
            }
            case HSLA, AHSL -> {
                float c = (1 - Math.abs(2 * _v2 - 1)) * _v1;
                float h = _v0 / (60 / 360f);
                float x = c * (1 - Math.abs(h % 2 - 1));
                float m = _v2 - c / 2;

                if (!Float.isFinite(h)) {
                    yield 0;
                }
                yield switch ((int) h) {
                    case 5, 0 -> c;
                    case 4, 1 -> x;
                    default -> 0;
                } + m;
            }
        };
    }
    public float getGreen() {
        return switch (_mode) {
            case RGBA, ARGB -> _v1;
            case HSVA, AHSV -> {
                float c = _v1 * _v2;
                float h = _v0 / (60 / 360f);
                float x = c * (1 - Math.abs(h % 2 - 1));
                float m = _v2 - c;

                if (!Float.isFinite(h)) {
                    yield 0;
                }
                yield switch ((int) Math.ceil(h)) {
                    case 4, 1, 0 -> x;
                    case 3, 2 -> c;
                    default -> 0;
                } + m;
            }
            case HSLA, AHSL -> {
                float c = (1 - Math.abs(2 * _v2 - 1)) * _v1;
                float h = _v0 / (60 / 360f);
                float x = c * (1 - Math.abs(h % 2 - 1));
                float m = _v2 - c / 2;

                if (!Float.isFinite(h)) {
                    yield 0;
                }
                yield switch ((int) h) {
                    case 3, 0 -> x;
                    case 2, 1 -> c;
                    default -> 0;
                } + m;
            }
        };
    }
    public float getBlue() {
        return switch (_mode) {
            case RGBA, ARGB -> _v2;
            case HSVA, AHSV -> {
                float c = _v1 * _v2;
                float h = _v0 / (60 / 360f);
                float x = c * (1 - Math.abs(h % 2 - 1));
                float m = _v2 - c;

                if (!Float.isFinite(h)) {
                    yield 0;
                }
                yield switch ((int) Math.ceil(h)) {
                    case 6, 3 -> x;
                    case 5, 4 -> c;
                    default -> 0;
                } + m;
            }
            case HSLA, AHSL -> {
                float c = (1 - Math.abs(2 * _v2 - 1)) * _v1;
                float h = _v0 / (60 / 360f);
                float x = c * (1 - Math.abs(h % 2 - 1));
                float m = _v2 - c / 2;

                if (!Float.isFinite(h)) {
                    yield 0;
                }
                yield switch ((int) h) {
                    case 5, 2 -> x;
                    case 4, 3 -> c;
                    default -> 0;
                } + m;
            }
        };
    }

    public float getRed(float range) {
        return getRed() * range;
    }
    public float getGreen(float range) {
        return getGreen() * range;
    }
    public float getBlue(float range) {
        return getBlue() * range;
    }

    public void setRed(float value) {
        _v0 = value;
    }
    public void setGreen(float value) {
        _v1 = value;
    }
    public void setBlue(float value) {
        _v2 = value;
    }

    public void setRed(float value, float range) {
        _v0 = value / range;
    }
    public void setGreen(float value, float range) {
        _v1 = value / range;
    }
    public void setBlue(float value, float range) {
        _v2 = value / range;
    }
    //endregion

    //region HSV/HSL
    public float getHue() {
        return switch (_mode) {
            case HSVA, HSLA, AHSV, AHSL -> _v0;
            case RGBA, ARGB -> {
                float cMax = MathF.max(_v0, _v1, _v2);
                float cMin = MathF.min(_v0, _v1, _v2);
                float delta = (cMax - cMin) * 6; //Want the answer to be in the range of 0 to 1

                if (cMax == _v0) {
                    yield (_v1 - _v2) / delta % 1;
                } else if (cMax == _v1) {
                    yield (_v2 - _v0) / delta + (2 / 6f);
                } else if (cMax == _v2) {
                    yield (_v0 - _v1) / delta + (4 / 6f);
                }
                yield 0;
            }
        };
    }
    public float getHSVSaturation() {
        return switch (_mode) {
            case HSVA, AHSV -> _v1;
            case HSLA, AHSL -> {
                float v = getValue();
                if (v == 0) yield 0;
                yield 2 * (1 - getLightness() / v);
            }
            case RGBA, ARGB -> {
                float cMax = MathF.max(_v0, _v1, _v2);
                float cMin = MathF.min(_v0, _v1, _v2);
                float delta = (cMax - cMin);

                yield cMax == 0 ? 0 : delta / cMax;
            }
        };
    }
    public float getHSLSaturation() {
        return switch (_mode) {
            case HSLA, AHSL -> _v1;
            case HSVA, AHSV -> {
                float l = getLightness();
                if (l == 0 || l == 1) yield 0;
                yield (getValue() - l) / (Math.min(l, 1 - l));
            }
            case RGBA, ARGB -> {
                float cMax = MathF.max(_v0, _v1, _v2);
                float cMin = MathF.min(_v0, _v1, _v2);
                float delta = (cMax - cMin);

                yield cMax == 0 ? 0 : delta / (1 - Math.abs(cMax + cMin - 1));
            }
        };
    }
    public float getValue() {
        return switch (_mode) {
            case HSVA, AHSV -> _v2;
            case HSLA, AHSL -> {
                float l = getLightness();
                if (l == 0 || l == 1) yield 0;
                yield l + getHSLSaturation() * Math.min(l, 1 - l);
            }
            case RGBA, ARGB -> MathF.max(_v0, _v1, _v2);
        };
    }
    public float getLightness() {
        return switch (_mode) {
            case HSLA, AHSL -> _v2;
            case HSVA, AHSV -> getValue() * (1 - getValue() / 2);
            case RGBA, ARGB -> (MathF.max(_v0, _v1, _v2) + MathF.min(_v0, _v1, _v2)) / 2;
        };
    }

    public float getHue(float range) {
        return getHue() * range;
    }
    public float getHSVSaturation(float range) {
        return getHSVSaturation() * range;
    }
    public float getHSLSaturation(float range) {
        return getHSLSaturation() * range;
    }
    public float getValue(float range) {
        return getValue() * range;
    }
    public float getLightness(float range) {
        return getLightness() * range;
    }

    public void setHue(float value) {
        _v0 = value;
    }
    public void setHSVSaturation(float value) {
        _v1 = value;
    }
    public void setHSLSaturation(float value) {
        _v1 = value;
    }
    public void setValue(float value) {
        _v2 = value;
    }
    public void setLightness(float value) {
        switch (_mode) {
            case RGBA, ARGB -> {
                float r = getRed();
                float g = getGreen();
                float b = getBlue();

                float k = value / getLightness();

                setRed(r * k);
                setGreen(g * k);
                setBlue(b * k);
            }
            case HSVA -> {
            }
            case AHSV -> {
            }
            case HSLA, AHSL -> _v2 = value;
        }
    }

    public void setHue(float value, float range) {
        _v0 = value / range;
    }
    public void setHSVSaturation(float value, float range) {
        _v1 = value / range;
    }
    public void setHSLSaturation(float value, float range) {
        _v1 = value / range;
    }
    public void setValue(float value, float range) {
        _v2 = value / range;
    }
    public void setLightness(float value, float range) {
        _v2 = value / range;
    }
    //endregion

    public void set(ColorMode mode, float v0, float v1, float v2, float v3) {
        _mode = mode;
        switch (mode) {
            case RGBA, HSVA, HSLA -> {
                _v0 = v0;
                _v1 = v1;
                _v2 = v2;
                _alpha = v3;
            }
            case ARGB, AHSV, AHSL -> {
                _alpha = 0;
                _v0 = v1;
                _v1 = v2;
                _v2 = v3;
            }
        }
    }
    public void set(ColorMode mode, float v0, float v1, float v2) {
        _mode = mode;
        switch (mode) {
            case RGBA, HSVA, HSLA -> {
                _v0 = v0;
                _v1 = v1;
                _v2 = v2;
            }
            case ARGB, AHSV, AHSL -> {
                _alpha = 0;
                _v0 = v1;
                _v1 = v2;
            }
        }
    }

    public void set(ColorMode mode, float v0, float v1, float v2, float v3, float range0, float range1, float range2, float range3) {
        set(mode, v0 / range0, v1 / range1, v2 / range2, v3 / range3);
    }
    public void set(ColorMode mode, float v0, float v1, float v2, float range0, float range1, float range2) {
        set(mode, v0 / range0, v1 / range1, v2 / range2);
    }


    public static Color lerp(float t, Color a, Color b) {
        if (a._mode != b._mode) throw new IllegalArgumentException("todo");

        return new Color(
                b._mode,
                a._v0 + t * (b._v0 - a._v0),
                a._v1 + t * (b._v1 - a._v1),
                a._v2 + t * (b._v2 - a._v2),
                a._alpha + t * (b._alpha - a._alpha)
        );
    }
    public void lerp(float t, Color target) {
        if (_mode != target._mode) throw new IllegalArgumentException("todo");

        _v0 = _v0 + t * (target._v0 - _v0);
        _v1 = _v1 + t * (target._v1 - _v1);
        _v2 = _v2 + t * (target._v2 - _v2);
        _alpha = _alpha + t * (target._alpha - _alpha);
    }

    public static void main(String[] args) {
        Color col = new Color(ColorMode.RGBA, 100 / 255f, 50 / 255f, 20 / 255f, 0);
        Debug.logAll(col.getRed() * 255, col.getGreen() * 255, col.getBlue() * 255);
        Debug.logAll(col.getHue() * 360, col.getHSLSaturation() * 1, col.getLightness() * 1);
    }
}
