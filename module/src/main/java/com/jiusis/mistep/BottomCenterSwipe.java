package com.jiusis.mistep;

final class BottomCenterSwipe {
    private boolean candidate;
    private int pointer, width, height, rotation;
    private int sign, inwardSign;
    private float x, y;
    private long time;

    void down(int pointer, int count, float x, float y, long time,
            int width, int height, int rotation, int edgePx) {
        down(pointer, count, x, y, time, width, height, rotation, edgePx, 0);
    }

    void down(int pointer, int count, float x, float y, long time,
            int width, int height, int rotation, int edgePx, int cornerPx) {
        reset();
        if (count != 1 || width <= 0 || height <= 0
                || x < 0 || x >= width
                || y < height - edgePx || y > height) return;
        inwardSign = 0;
        if (cornerPx > 0) {
            if (x < cornerPx) inwardSign = 1;
            else if (x >= width - cornerPx) inwardSign = -1;
            else return;
        }
        this.pointer = pointer;
        this.x = x;
        this.y = y;
        this.time = time;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        candidate = true;
    }

    boolean move(int pointer, int count, float x, float y, long time,
            int width, int height, int rotation, int sweepPx, int maxVerticalDriftPx) {
        if (!candidate) return false;
        if (pointer != this.pointer || count != 1 || width != this.width || height != this.height
                || rotation != this.rotation || time < this.time || time - this.time > 600) {
            reset();
            return false;
        }
        if (Math.abs(y - this.y) > maxVerticalDriftPx) {
            reset();
            return false;
        }
        float horizontal = inwardSign == 0 ? Math.abs(x - this.x) : (x - this.x) * inwardSign;
        if (horizontal < sweepPx) return false;
        sign = x > this.x ? 1 : -1;
        reset();
        return true;
    }

    boolean horizontalIntent(float x, float y, int intentPx) {
        float horizontal = inwardSign == 0 ? Math.abs(x - this.x) : (x - this.x) * inwardSign;
        return candidate && horizontal >= intentPx && horizontal >= 2f * Math.abs(y - this.y);
    }

    boolean active() { return candidate; }
    boolean canRetain(int pointer, int count, float x, float y, long time, int intentPx, int verticalPx) {
        return candidate && pointer == this.pointer && count == 1 && time >= this.time && time - this.time <= 600
                && Math.abs(y - this.y) <= verticalPx && horizontalIntent(x, y, intentPx);
    }
    String progress(float x, float y, long time) {
        return "dx=" + Math.round(x - this.x) + " dy=" + Math.round(y - this.y)
                + " elapsed=" + (time - this.time);
    }
    void reset() { candidate = false; }
    int sign() { return sign; }
}
