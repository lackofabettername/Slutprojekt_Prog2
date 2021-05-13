package utility;

import java.io.Serializable;

public class Coord2  implements Serializable {

    public int x;
    public int y;

    //region Constructors
    public Coord2(Coord2 v) {
        x = v.x;
        y = v.y;
    }
    public Coord2(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Coord2(int[] values) {
        x = values[0];
        y = values[1];
    }
    public Coord2(int val) {
        x = val;
        y = val;
    }
    public Coord2() {
        x = 0;
        y = 0;
    }

    public static Coord2 random() {
        return fromAngle((int) (Math.random() * Math.PI * 2));
    }

    public static final Coord2 MAX = new Coord2(Integer.MAX_VALUE, Integer.MAX_VALUE);
    public static final Coord2 MIN = new Coord2(Integer.MIN_VALUE, Integer.MIN_VALUE);
    //endregion

    //region Conversion
    public static int[] toIntegerArray(Coord2 v) {
        return new int[]{v.x, v.y};
    }
    public int[] toIntegerArray() {
        return new int[]{x, y};
    }

    public Coord2 fromIntegerArray(int[] array) {
        if (array.length != 2)
            throw new IllegalArgumentException("Input must be length 3");
        x = array[0];
        y = array[1];
        return this;
    }
    //endregion

    //region Operators

    //region Add
    public static Coord2 sum(Coord2... vectors) {
        Coord2 result = new Coord2();
        for (Coord2 v : vectors)
            result.add(v);
        return result;
    }

    public static Coord2 add(Coord2 a, Coord2 b) {
        return new Coord2(
                a.x + b.x,
                a.y + b.y);
    }
    public static Coord2 add(Coord2 v, int amount) {
        return new Coord2(
                v.x + amount,
                v.y + amount);
    }
    public static Coord2 add(Coord2 v, int x, int y) {
        return new Coord2(
                v.x + x,
                v.y + y);
    }
    public static Coord2 add(int x, int y, Coord2 v) {
        return new Coord2(
                x + v.x,
                y + v.y);
    }
    public static Coord2 add(int x1, int y1, int x2, int y2) {
        return new Coord2(
                x1 + x2,
                y1 + y2);
    }
    public Coord2 add(Coord2 v) {
        x += v.x;
        y += v.y;
        return this;
    }
    public Coord2 add(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public static Coord2 addAxis(int axis, Coord2 v, int val) {
        return switch(axis) {
            case 0 -> add(v, val, 0);
            case 1 -> add(v, 0, val);
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }
    public Coord2 addAxis(int axis, int val) {
        return switch(axis) {
            case 0 -> add(val, 0);
            case 1 -> add(0, val);
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }
    //endregion

    //region Sub
    public static Coord2 sub(Coord2 a, Coord2 b) {
        return new Coord2(
                a.x - b.x,
                a.y - b.y);
    }
    public static Coord2 sub(Coord2 v, int amount) {
        return new Coord2(
                v.x - amount,
                v.y - amount);
    }
    public static Coord2 sub(Coord2 v, int x, int y) {
        return new Coord2(
                v.x - x,
                v.y - y);
    }
    public static Coord2 sub(int x, int y, Coord2 v) {
        return new Coord2(
                x - v.x,
                y - v.y);
    }
    public static Coord2 sub(int x1, int y1, int x2, int y2) {
        return new Coord2(
                x1 - x2,
                y1 - y2);
    }
    public Coord2 sub(Coord2 v) {
        x -= v.x;
        y -= v.y;
        return this;
    }
    public Coord2 sub(int x, int y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public static Coord2 subAxis(int axis, Coord2 v, int val) {
        return switch(axis) {
            case 0 -> sub(v, val, 0);
            case 1 -> sub(v, 0, val);
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }
    public Coord2 subAxis(int axis, int val) {
        return switch(axis) {
            case 0 -> sub(val, 0);
            case 1 -> sub(0, val);
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }
    //endregion

    //region Mult
    public static Coord2 mult(Coord2 a, Coord2 b) {
        return new Coord2(
                a.x * b.x,
                a.y * b.y);
    }
    public static Coord2 mult(Coord2 a, int amount) {
        return new Coord2(
                a.x * amount,
                a.y * amount);
    }
    public static Coord2 mult(Coord2 v, int x, int y) {
        return new Coord2(
                v.x * x,
                v.y * y);
    }
    public static Coord2 mult(int x, int y, Coord2 v) {
        return new Coord2(
                x * v.x,
                y * v.y);
    }
    public static Coord2 mult(int x1, int y1, int x2, int y2) {
        return new Coord2(
                x1 * x2,
                y1 * y2);
    }
    public Coord2 mult(Coord2 v) {
        x *= v.x;
        y *= v.y;
        return this;
    }
    public Coord2 mult(int x, int y) {
        this.x *= x;
        this.y *= y;
        return this;
    }
    public Coord2 mult(int amount) {
        x *= amount;
        y *= amount;
        return this;
    }

    public static Coord2 multAxis(int axis, Coord2 v, int val) {
        return switch(axis) {
            case 0 -> mult(v, val, 1);
            case 1 -> mult(v, 1, val);
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }
    public Coord2 multAxis(int axis, int val) {
        return switch(axis) {
            case 0 -> mult(val, 1);
            case 1 -> mult(1, val);
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }
    //endregion

    //region Div
    public static Coord2 div(Coord2 a, Coord2 b) {
        return new Coord2(
                a.x / b.x,
                a.y / b.y);
    }
    public static Coord2 div(Coord2 v, int amount) {
        return new Coord2(
                v.x / amount,
                v.y / amount);
    }
    public static Coord2 div(Coord2 v, int x, int y) {
        return new Coord2(
                v.x / x,
                v.y / y);
    }
    public static Coord2 div(int x, int y, Coord2 v) {
        return new Coord2(
                x / v.x,
                y / v.y);
    }
    public static Coord2 div(int x1, int y1, int x2, int y2) {
        return new Coord2(
                x1 / x2,
                y1 / y2);
    }
    public Coord2 div(Coord2 v) {
        x /= v.x;
        y /= v.y;
        return this;
    }
    public Coord2 div(int x, int y) {
        this.x /= x;
        this.y /= y;
        return this;
    }
    public Coord2 div(int amount) {
        x /= amount;
        y /= amount;
        return this;
    }

    public static Coord2 divAxis(int axis, Coord2 v, int val) {
        return switch(axis) {
            case 0 -> div(v, val, 1);
            case 1 -> div(v, 1, val);
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }
    public Coord2 divAxis(int axis, int val) {
        return switch(axis) {
            case 0 -> div(val, 1);
            case 1 -> div(1, val);
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }
    //endregion

    //endregion

    //region Angles
    public static Coord2 fromAngle(int angle) {
        return new Coord2(
                (int) Math.cos(angle),
                (int) Math.sin(angle));
    }
    public static Coord2 fromAngle(int angle, int length) {
        return new Coord2(
                (int) Math.cos(angle) * length,
                (int) Math.sin(angle) * length);
    }

    public static Coord2 rotate(Coord2 v, int angle) {
        return rotate(
                v,
                (int) Math.cos(angle),
                (int) Math.sin(angle));
    }
    public static Coord2 rotate(Coord2 v, int cos, int sin) {
        return new Coord2(
                v.x * cos - v.y * sin,
                v.x * sin + v.y * cos);
    }
    public Coord2 rotate(int angle) {
        return rotate(
                (int) Math.cos(angle),
                (int) Math.sin(angle));
    }
    public Coord2 rotate(int cos, int sin) {
        int x = this.x * cos - this.y * sin;
        int y = this.x * sin + this.y * cos;
        return set(x, y);
    }

    public static int dot(Coord2 a, Coord2 b) {
        return a.x * b.x + a.y * b.y;
    }
    public int dot(Coord2 v) {
        return x * v.x + y * v.y;
    }
    public int dot(int x, int y) {
        return x * x + y * y;
    }
    //endregion

    //region Magnitude
    public static int magnitudeSqr(Coord2 v) {
        return v.x * v.x + v.y * v.y;
    }
    public int magnitudeSqr() {
        return x * x + y * y;
    }
    public static int magnitude(Coord2 v) {
        return (int) Math.sqrt(v.x * v.x + v.y * v.y);
    }
    public int magnitude() {
        return (int) Math.sqrt(x * x + y * y);
    }

    public static Coord2 setMagnitude(Coord2 v, int mag) {
        return new Coord2(v).setMagnitude(mag);
    }
    public Coord2 setMagnitude(int mag) {
        //TODO: Why is a "set" method static and returning a new value...? Different from Vector3's?
        return magnitudeSqr() > 0 ? mult(mag / magnitude()) : this;
    }

    public static Coord2 normalize(Coord2 v) {
        return new Coord2(v).normalize();
    }
    public Coord2 normalize() {
        return div(magnitude());
    }
    //endregion

    //region Comparators

    //region >
    public static boolean greaterThan(Coord2 a, Coord2 b) {
        return a.x > b.x && a.y > b.y;
    }
    public static boolean greaterThan(Coord2 v, int x, int y) {
        return v.x > x && v.y > y;
    }
    public static boolean greaterThan(Coord2 v, int value) {
        return v.x > value && v.y > value;
    }
    public boolean greaterThan(Coord2 v) {
        return x > v.x && y > v.y;
    }
    public boolean greaterThan(int x, int y) {
        return this.x > x && this.y > y;
    }
    public boolean greaterThan(int value) {
        return x > value && y > value;
    }
    //endregion

    //region >=
    public static boolean greaterThanOrEqual(Coord2 a, Coord2 b) {
        return a.x >= b.x && a.y >= b.y;
    }
    public static boolean greaterThanOrEqual(Coord2 v, int x, int y) {
        return v.x >= x && v.y >= y;
    }
    public static boolean greaterThanOrEqual(Coord2 v, int value) {
        return v.x >= value && v.y >= value;
    }
    public boolean greaterThanOrEqual(Coord2 v) {
        return x >= v.x && y >= v.y;
    }
    public boolean greaterThanOrEqual(int x, int y) {
        return this.x >= x && this.y >= y;
    }
    public boolean greaterThanOrEqual(int value) {
        return x >= value && y >= value;
    }
    //endregion

    //region <
    public static boolean lessThan(Coord2 a, Coord2 b) {
        return a.x < b.x && a.y < b.y;
    }
    public static boolean lessThan(Coord2 v, int x, int y) {
        return v.x < x && v.y < y;
    }
    public static boolean lessThan(Coord2 v, int value) {
        return v.x < value && v.y < value;
    }
    public boolean lessThan(Coord2 v) {
        return x < v.x && y < v.y;
    }
    public boolean lessThan(int x, int y) {
        return this.x < x && this.y < y;
    }
    public boolean lessThan(int value) {
        return x < value && y < value;
    }
    //endregion

    //region <=
    public static boolean lessThanOrEqual(Coord2 a, Coord2 b) {
        return a.x <= b.x && a.y <= b.y;
    }
    public static boolean lessThanOrEqual(Coord2 v, int x, int y) {
        return v.x <= x && v.y <= y;
    }
    public static boolean lessThanOrEqual(Coord2 v, int value) {
        return v.x <= value && v.y <= value;
    }
    public boolean lessThanOrEqual(Coord2 v) {
        return x <= v.x && y <= v.y;
    }
    public boolean lessThanOrEqual(int x, int y) {
        return this.x <= x && this.y <= y;
    }
    public boolean lessThanOrEqual(int value) {
        return x <= value && y <= value;
    }
    //endregion

    //region ==
    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }
    //endregion

    //endregion

    //region Misc.
    public Coord2 set(Coord2 v) {
        x = v.x;
        y = v.y;
        return this;
    }
    public Coord2 set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public int getAxis(int axis) {
        return switch (axis) {
            case 0 -> x;
            case 1 -> y;
            default -> throw new IndexOutOfBoundsException("Axis must be between 0 and 1 (inclusive)");
        };
    }

    public static Coord2 min(Coord2... vectors) {

        if (vectors.length == 0)
            throw new IllegalArgumentException("There must be more than 0 Vectors");

        Coord2 result = MAX.copy();
        for (Coord2 v : vectors) {
            result.set(
                    Math.min(result.x, v.x),
                    Math.min(result.y, v.y)
            );
        }
        return result;
    }
    public static Coord2 min(Coord2 v, int x, int y) {
        return new Coord2(
                Math.min(v.x, x),
                Math.min(v.y, y));
    }
    public static Coord2 min(int x, int y, Coord2 v) {
        return new Coord2(
                Math.min(v.x, x),
                Math.min(v.y, y));
    }
    public static Coord2 max(Coord2... vectors) {

        if (vectors.length == 0)
            throw new IllegalArgumentException("There must be more than 0 Vectors");

        Coord2 result = MIN.copy();
        for (Coord2 v : vectors) {
            result.set(
                    Math.max(result.x, v.x),
                    Math.max(result.y, v.y)
            );
        }

        return result;
    }
    public static Coord2 max(Coord2 v, int x, int y) {
        return new Coord2(
                Math.max(v.x, x),
                Math.max(v.y, y));
    }
    public static Coord2 max(int x, int y, Coord2 v) {
        return new Coord2(
                Math.max(v.x, x),
                Math.max(v.y, y));
    }

    public static Coord2 constrain(Coord2 v, Coord2 min, Coord2 max) {
        return min(max(v, min), max);
    }
    public static Coord2 constrain(Coord2 v, int minX, int minY, int maxX, int maxY) {
        return min(max(v, minX, minY), maxX, maxY);
    }
    public Coord2 constrain(Coord2 min, Coord2 max) {
        return set(min(max(this, min), max));
    }
    public Coord2 constrain(int minX, int minY, int maxX, int maxY) {
        x = Math.min(Math.max(x, minX), maxX);
        y = Math.min(Math.max(y, minY), maxY);
        return this;
    }

    public static Coord2 wrap(Coord2 v, int minX, int minY, int maxX, int maxY) {
        Coord2 result = new Coord2(v);
        while (result.x > maxX) result.x -= (maxX - minX);
        while (result.x < minX) result.x += (maxX - minX);
        while (result.y > maxY) result.y -= (maxY - minY);
        while (result.y < minY) result.y += (maxY - minY);
        return result;
    }
    public Coord2 wrap(int minX, int minY, int maxX, int maxY) {
        while (x > maxX) x -= (maxX - minX);
        while (x < minX) x += (maxX - minX);
        while (y > maxY) y -= (maxY - minY);
        while (y < minY) y += (maxY - minY);
        return this;
    }

    public Coord2 copy() {
        return new Coord2(x, y);
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Coord2 other))
            return false;

        return x == other.x &&
                y == other.y;
    }

    public boolean about(Coord2 a, Coord2 b, int epsilon) {
        return Coord2.sub(a, b).magnitudeSqr() < epsilon * epsilon;
    }
    public boolean about(Coord2 v, int epsilon) {
        return Coord2.sub(this, v).magnitudeSqr() < epsilon * epsilon;
    }
    //endregion
}
