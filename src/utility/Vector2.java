package utility;

import java.io.Serializable;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Vector2 implements Serializable {

    public float x;
    public float y;

    //region Constructors
    public Vector2(Vector2 v) {
        x = v.x;
        y = v.y;
    }
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public Vector2(float[] values) {
        x = values[0];
        y = values[1];
    }
    public Vector2(float val) {
        x = val;
        y = val;
    }
    public Vector2() {
        x = 0f;
        y = 0f;
    }

    public static Vector2 random() {
        return fromAngle((float) (Math.random() * Math.PI * 2));
    }

    public static final Vector2 MAX = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
    public static final Vector2 MIN = new Vector2(Float.MIN_VALUE, Float.MIN_VALUE);
    //endregion

    //region Conversion
    public static float[] toFloatArray(Vector2 v) {
        return new float[]{v.x, v.y};
    }
    public float[] toFloatArray() {
        return new float[]{x, y};
    }

    public Vector2 fromFloatArray(float[] array) {
        if (array.length != 2)
            throw new IllegalArgumentException("Input must be length 3");
        x = array[0];
        y = array[1];
        return this;
    }
    //endregion

    //region Operators

    //region Add
    public static Vector2 sum(Vector2... vectors) {
        Vector2 result = new Vector2();
        for (Vector2 v : vectors)
            result.add(v);
        return result;
    }

    public static Vector2 add(Vector2 a, Vector2 b) {
        return new Vector2(
                a.x + b.x,
                a.y + b.y);
    }
    public static Vector2 add(Vector2 v, float amount) {
        return new Vector2(
                v.x + amount,
                v.y + amount);
    }
    public static Vector2 add(Vector2 v, float x, float y) {
        return new Vector2(
                v.x + x,
                v.y + y);
    }
    public static Vector2 add(float x, float y, Vector2 v) {
        return new Vector2(
                x + v.x,
                y + v.y);
    }
    public static Vector2 add(float x1, float y1, float x2, float y2) {
        return new Vector2(
                x1 + x2,
                y1 + y2);
    }
    public Vector2 add(Vector2 v) {
        x += v.x;
        y += v.y;
        return this;
    }
    public Vector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public static Vector2 addAxis(int axis, Vector2 v, float val) {
        return switch (axis) {
            case 0 -> add(v, val, 0);
            case 1 -> add(v, 0, val);
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }
    public Vector2 addAxis(int axis, float val) {
        return switch (axis) {
            case 0 -> add(val, 0);
            case 1 -> add(0, val);
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }
    //endregion

    //region Sub
    public static Vector2 sub(Vector2 a, Vector2 b) {
        return new Vector2(
                a.x - b.x,
                a.y - b.y);
    }
    public static Vector2 sub(Vector2 v, float amount) {
        return new Vector2(
                v.x - amount,
                v.y - amount);
    }
    public static Vector2 sub(Vector2 v, float x, float y) {
        return new Vector2(
                v.x - x,
                v.y - y);
    }
    public static Vector2 sub(float x, float y, Vector2 v) {
        return new Vector2(
                x - v.x,
                y - v.y);
    }
    public static Vector2 sub(float x1, float y1, float x2, float y2) {
        return new Vector2(
                x1 - x2,
                y1 - y2);
    }
    public Vector2 sub(Vector2 v) {
        x -= v.x;
        y -= v.y;
        return this;
    }
    public Vector2 sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public static Vector2 subAxis(int axis, Vector2 v, float val) {
        return switch (axis) {
            case 0 -> sub(v, val, 0);
            case 1 -> sub(v, 0, val);
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }
    public Vector2 subAxis(int axis, float val) {
        return switch (axis) {
            case 0 -> sub(val, 0);
            case 1 -> sub(0, val);
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }
    //endregion

    //region Mult
    public static Vector2 mult(Vector2 a, Vector2 b) {
        return new Vector2(
                a.x * b.x,
                a.y * b.y);
    }
    public static Vector2 mult(Vector2 a, float amount) {
        return new Vector2(
                a.x * amount,
                a.y * amount);
    }
    public static Vector2 mult(Vector2 v, float x, float y) {
        return new Vector2(
                v.x * x,
                v.y * y);
    }
    public static Vector2 mult(float x, float y, Vector2 v) {
        return new Vector2(
                x * v.x,
                y * v.y);
    }
    public static Vector2 mult(float x1, float y1, float x2, float y2) {
        return new Vector2(
                x1 * x2,
                y1 * y2);
    }
    public Vector2 mult(Vector2 v) {
        x *= v.x;
        y *= v.y;
        return this;
    }
    public Vector2 mult(float x, float y) {
        this.x *= x;
        this.y *= y;
        return this;
    }
    public Vector2 mult(float amount) {
        x *= amount;
        y *= amount;
        return this;
    }

    public static Vector2 multAxis(int axis, Vector2 v, float val) {
        return switch (axis) {
            case 0 -> mult(v, val, 1);
            case 1 -> mult(v, 1, val);
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }
    public Vector2 multAxis(int axis, float val) {
        return switch (axis) {
            case 0 -> mult(val, 1);
            case 1 -> mult(1, val);
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }
    //endregion

    //region Div
    public static Vector2 div(Vector2 a, Vector2 b) {
        return new Vector2(
                a.x / b.x,
                a.y / b.y);
    }
    public static Vector2 div(Vector2 v, float amount) {
        return new Vector2(
                v.x / amount,
                v.y / amount);
    }
    public static Vector2 div(Vector2 v, float x, float y) {
        return new Vector2(
                v.x / x,
                v.y / y);
    }
    public static Vector2 div(float x, float y, Vector2 v) {
        return new Vector2(
                x / v.x,
                y / v.y);
    }
    public static Vector2 div(float x1, float y1, float x2, float y2) {
        return new Vector2(
                x1 / x2,
                y1 / y2);
    }
    public Vector2 div(Vector2 v) {
        x /= v.x;
        y /= v.y;
        return this;
    }
    public Vector2 div(float x, float y) {
        this.x /= x;
        this.y /= y;
        return this;
    }
    public Vector2 div(float amount) {
        x /= amount;
        y /= amount;
        return this;
    }

    public static Vector2 divAxis(int axis, Vector2 v, float val) {
        return switch (axis) {
            case 0 -> div(v, val, 1);
            case 1 -> div(v, 1, val);
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }
    public Vector2 divAxis(int axis, float val) {
        return switch (axis) {
            case 0 -> div(val, 1);
            case 1 -> div(1, val);
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }
    //endregion

    //endregion

    //region Angles
    public static Vector2 fromAngle(float angle) {
        return new Vector2(
                (float) Math.cos(angle),
                (float) Math.sin(angle));
    }
    public static Vector2 fromAngle(float angle, float length) {
        return new Vector2(
                (float) Math.cos(angle) * length,
                (float) Math.sin(angle) * length);
    }

    public static Vector2 rotate(Vector2 v, float angle) {
        return rotate(
                v,
                (float) Math.cos(angle),
                (float) Math.sin(angle));
    }
    public static Vector2 rotate(Vector2 v, float cos, float sin) {
        return new Vector2(
                v.x * cos - v.y * sin,
                v.x * sin + v.y * cos);
    }
    public Vector2 rotate(float angle) {
        return rotate(
                (float) Math.cos(angle),
                (float) Math.sin(angle));
    }
    public Vector2 rotate(float cos, float sin) {
        float x = this.x * cos - this.y * sin;
        float y = this.x * sin + this.y * cos;
        return set(x, y);
    }

    public static float dot(Vector2 a, Vector2 b) {
        return a.x * b.x + a.y * b.y;
    }
    public float dot(Vector2 v) {
        return x * v.x + y * v.y;
    }
    public float dot(float x, float y) {
        return x * x + y * y;
    }
    //endregion

    //region Magnitude
    public static float magnitudeSqr(Vector2 v) {
        return v.x * v.x + v.y * v.y;
    }
    public float magnitudeSqr() {
        return x * x + y * y;
    }
    public static float magnitude(Vector2 v) {
        return (float) Math.sqrt(v.x * v.x + v.y * v.y);
    }
    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public static Vector2 setMagnitude(Vector2 v, float mag) {
        return new Vector2(v).setMagnitude(mag);
    }
    public Vector2 setMagnitude(float mag) {
        //TODO: Why is a "set" method static and returning a new value...? Different from Vector3's?
        return magnitudeSqr() > 0 ? mult(mag / magnitude()) : this;
    }

    public static Vector2 normalize(Vector2 v) {
        return new Vector2(v).normalize();
    }
    public Vector2 normalize() {
        return magnitudeSqr() > 0 ? div(magnitude()) : this;
    }
    //endregion

    //region Comparators

    //region >
    public static boolean greaterThan(Vector2 a, Vector2 b) {
        return a.x > b.x && a.y > b.y;
    }
    public static boolean greaterThan(Vector2 v, float x, float y) {
        return v.x > x && v.y > y;
    }
    public static boolean greaterThan(Vector2 v, float value) {
        return v.x > value && v.y > value;
    }
    public boolean greaterThan(Vector2 v) {
        return x > v.x && y > v.y;
    }
    public boolean greaterThan(float x, float y) {
        return this.x > x && this.y > y;
    }
    public boolean greaterThan(float value) {
        return x > value && y > value;
    }
    //endregion

    //region >=
    public static boolean greaterThanOrEqual(Vector2 a, Vector2 b) {
        return a.x >= b.x && a.y >= b.y;
    }
    public static boolean greaterThanOrEqual(Vector2 v, float x, float y) {
        return v.x >= x && v.y >= y;
    }
    public static boolean greaterThanOrEqual(Vector2 v, float value) {
        return v.x >= value && v.y >= value;
    }
    public boolean greaterThanOrEqual(Vector2 v) {
        return x >= v.x && y >= v.y;
    }
    public boolean greaterThanOrEqual(float x, float y) {
        return this.x >= x && this.y >= y;
    }
    public boolean greaterThanOrEqual(float value) {
        return x >= value && y >= value;
    }
    //endregion

    //region <
    public static boolean lessThan(Vector2 a, Vector2 b) {
        return a.x < b.x && a.y < b.y;
    }
    public static boolean lessThan(Vector2 v, float x, float y) {
        return v.x < x && v.y < y;
    }
    public static boolean lessThan(Vector2 v, float value) {
        return v.x < value && v.y < value;
    }
    public boolean lessThan(Vector2 v) {
        return x < v.x && y < v.y;
    }
    public boolean lessThan(float x, float y) {
        return this.x < x && this.y < y;
    }
    public boolean lessThan(float value) {
        return x < value && y < value;
    }
    //endregion

    //region <=
    public static boolean lessThanOrEqual(Vector2 a, Vector2 b) {
        return a.x <= b.x && a.y <= b.y;
    }
    public static boolean lessThanOrEqual(Vector2 v, float x, float y) {
        return v.x <= x && v.y <= y;
    }
    public static boolean lessThanOrEqual(Vector2 v, float value) {
        return v.x <= value && v.y <= value;
    }
    public boolean lessThanOrEqual(Vector2 v) {
        return x <= v.x && y <= v.y;
    }
    public boolean lessThanOrEqual(float x, float y) {
        return this.x <= x && this.y <= y;
    }
    public boolean lessThanOrEqual(float value) {
        return x <= value && y <= value;
    }
    //endregion

    //region ==
    public boolean equals(float x, float y) {
        return this.x == x && this.y == y;
    }
    //endregion

    //endregion

    //region Misc.
    public Vector2 set(Vector2 v) {
        x = v.x;
        y = v.y;
        return this;
    }
    public Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public float getAxis(int axis) {
        return switch (axis) {
            case 0 -> x;
            case 1 -> y;
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }

    public static Vector2 lerp(float t, Vector2 a, Vector2 b) {
        return new Vector2(
                (a.x * (1.0f - t)) + (b.x * t),
                (a.y * (1.0f - t)) + (b.y * t));
    }
    public Vector2 lerp(float t, Vector2 v) {
        return set (
                (x * (1.0f - t)) + (v.x * t),
                (y * (1.0f - t)) + (v.y * t)
        );
        //return lerp(t, this, v);
    }

    public static Vector2 average(Vector2... vectors) {
        return sum(vectors).div(vectors.length);
    }

    public static Vector2 min(Vector2... vectors) {

        if (vectors.length == 0)
            throw new IllegalArgumentException("There must be more than 0 Vectors");

        Vector2 result = MAX.copy();
        for (Vector2 v : vectors) {
            result.set(
                    Math.min(result.x, v.x),
                    Math.min(result.y, v.y)
            );
        }
        return result;
    }
    public static Vector2 min(Vector2 v, float x, float y) {
        return new Vector2(
                Math.min(v.x, x),
                Math.min(v.y, y));
    }
    public static Vector2 min(float x, float y, Vector2 v) {
        return new Vector2(
                Math.min(v.x, x),
                Math.min(v.y, y));
    }
    public static Vector2 max(Vector2... vectors) {

        if (vectors.length == 0)
            throw new IllegalArgumentException("There must be more than 0 Vectors");

        Vector2 result = MIN.copy();
        for (Vector2 v : vectors) {
            result.set(
                    Math.max(result.x, v.x),
                    Math.max(result.y, v.y)
            );
        }

        return result;
    }
    public static Vector2 max(Vector2 v, float x, float y) {
        return new Vector2(
                Math.max(v.x, x),
                Math.max(v.y, y));
    }
    public static Vector2 max(float x, float y, Vector2 v) {
        return new Vector2(
                Math.max(v.x, x),
                Math.max(v.y, y));
    }

    public static Vector2 constrain(Vector2 v, Vector2 min, Vector2 max) {
        return min(max(v, min), max);
    }
    public static Vector2 constrain(Vector2 v, float minX, float minY, float maxX, float maxY) {
        return min(max(v, minX, minY), maxX, maxY);
    }
    public Vector2 constrain(Vector2 min, Vector2 max) {
        return set(min(max(this, min), max));
    }
    public Vector2 constrain(float minX, float minY, float maxX, float maxY) {
        x = Math.min(Math.max(x, minX), maxX);
        y = Math.min(Math.max(y, minY), maxY);
        return this;
    }

    public static Vector2 wrap(Vector2 v, float minX, float minY, float maxX, float maxY) {
        Vector2 result = new Vector2(v);
        while (result.x > maxX) result.x -= (maxX - minX);
        while (result.x < minX) result.x += (maxX - minX);
        while (result.y > maxY) result.y -= (maxY - minY);
        while (result.y < minY) result.y += (maxY - minY);
        return result;
    }
    public Vector2 wrap(float minX, float minY, float maxX, float maxY) {
        while (x > maxX) x -= (maxX - minX);
        while (x < minX) x += (maxX - minX);
        while (y > maxY) y -= (maxY - minY);
        while (y < minY) y += (maxY - minY);
        return this;
    }

    public static Vector2 round(Vector2 v, float snapping) {
        return new Vector2(Math.round(v.x / snapping) * snapping, Math.round(v.y / snapping) * snapping);
    }
    public static Vector2 round(Vector2 v) {
        return new Vector2(Math.round(v.x), Math.round(v.y));
    }
    public  Vector2 round(float snapping) {
        return set(Math.round(x / snapping) * snapping, Math.round(y / snapping) * snapping);
    }
    public  Vector2 round() {
        return set(Math.round(x), Math.round(y));
    }

    public Vector2 copy() {
        return new Vector2(x, y);
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Vector2))
            return false;

        Vector2 other = (Vector2) obj;
        return x == other.x &&
                y == other.y;
    }

    public boolean about(Vector2 a, Vector2 b, float epsilon) {
        return Vector2.sub(a, b).magnitudeSqr() < epsilon * epsilon;
    }
    public boolean about(Vector2 v, float epsilon) {
        return Vector2.sub(this, v).magnitudeSqr() < epsilon * epsilon;
    }
    //endregion


    @Override
    public String toString() {
        return "Vector2{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
