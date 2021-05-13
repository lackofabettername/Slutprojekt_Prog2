package utility;

import java.io.Serializable;

public class Bounds2 implements Serializable {
    public float x;
    public float y;
    public float w;
    public float h;

    public Bounds2(Bounds2 bounds) {
        x = bounds.x;
        y = bounds.y;
        w = bounds.w;
        h = bounds.h;
    }
    public Bounds2(Vector2 xy, Vector2 wh) {
        x = xy.x;
        y = xy.y;
        w = wh.x;
        h = wh.y;
    }
    public Bounds2(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public Vector2 center() {
        return Vector2.sub(x, y, w / 2, h / 2);
    }

    public boolean inBounds(Vector2 point) {
        return point.greaterThanOrEqual(x, y) && point.lessThanOrEqual(x + w, y + h);
    }
    public boolean inBounds(float x, float y) {
        return x >= this.x && y >= this.y && x <= this.x + w && y <= this.y + h;
    }

    public boolean overlaping(Bounds2 bounds2) {
        return bounds2.x < x + w &&
                bounds2.y < y + h &&
                bounds2.x + bounds2.x > x &&
                bounds2.y + bounds2.y > y;
    }
    public boolean overlaping(Vector2 xy, Vector2 wh) {
        return xy.x < x + w &&
                xy.y < y + h &&
                xy.x + wh.x > x &&
                xy.y + wh.y > y;
    }

    public Vector2 getCorner(int index) {
        return switch (index % 4) {
            case 0 -> new Vector2(x, y);
            case 1 -> new Vector2(x + w, y);
            case 2 -> new Vector2(x + w, y + h);
            case 3 -> new Vector2(x, y + h);
            default -> null;
        };
    }

    public Vector2 getClosestCorner(float x, float y) {
        return new Vector2(
                x < this.x + w / 2 ? this.x : this.x + w,
                y < this.y + h / 2 ? this.y : this.y + h
        );
    }

    public float getDistFromEdgeSquared(float x, float y) {
        float dx = Math.max(Math.abs(x - this.x - w / 2) - w / 2, 0);
        float dy = Math.max(Math.abs(y - this.y - this.y / 2) - h / 2, 0);
        return dx * dx + dy * dy;
    }
    public float getDistFromEdgeSquared(Vector2 point) {
        float dx = Math.max(Math.abs(point.x - x - w / 2) - w / 2, 0);
        float dy = Math.max(Math.abs(point.y - y - y / 2) - h / 2, 0);
        return dx * dx + dy * dy;
    }
    public float getSignedDistFromEdge(float x, float y) {
        Vector2 d = Vector2.max(
                Vector2.sub(this.x, this.y, x, y),
                Vector2.sub(x, y, this.x + w, this.y + h)
        );
        return Vector2.max(0, 0, d).magnitude() + Math.min(0, Math.max(d.x, d.y));
        //return length(max(vec2(0.0), d)) + min(0.0, max(d.x, d.y));
    }
    public float getSignedDistFromEdge(Vector2 point) {
        Vector2 d = Vector2.max(Vector2.sub(x, y, point), Vector2.sub(point, x + w, y + h));
        return Vector2.add(
                Vector2.max(0, 0, d),
                Math.min(0, Math.max(d.x, d.y))
        ).magnitudeSqr();
        //return length(max(vec2(0.0), d)) + min(0.0, max(d.x, d.y));
    }
}
