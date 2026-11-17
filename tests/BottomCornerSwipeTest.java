package com.jiusis.mistep;

public final class BottomCornerSwipeTest {
    private static int checks;
    private static void check(boolean value, String message) {
        checks++;
        if (!value) throw new AssertionError(message);
    }
    private static BottomCenterSwipe start(float x, float y) {
        BottomCenterSwipe swipe = new BottomCenterSwipe();
        swipe.down(7, 1, x, y, 1000, 1080, 2400, 0, 24, 72);
        return swipe;
    }
    private static boolean move(BottomCenterSwipe swipe, float x, float y, long time) {
        return swipe.move(7, 1, x, y, time, 1080, 2400, 0, 58, 24);
    }
    public static void main(String[] args) {
        BottomCenterSwipe left = start(20, 2390);
        check(!left.horizontalIntent(17, 2390, 2), "outward movement cannot retain input");
        check(left.canRetain(7, 1, 23, 2390, 1010, 2, 24), "inward intent retains input");
        check(!move(left, 77, 2390, 1100), "short swipe stays pending");
        check(move(left, 78, 2390, 1200) && left.sign() == 1, "left inward opens right main");
        check(!move(left, 100, 2390, 1250), "one trigger per stream");
        BottomCenterSwipe right = start(1060, 2390);
        check(!move(right, 1120, 2390, 1100), "right outward cannot open");
        check(move(right, 1002, 2390, 1200) && right.sign() == -1, "right inward opens left main");
        check(!start(540, 2390).active(), "bottom center excluded");
        check(!start(72, 2390).active(), "left corner boundary excluded");
        check(start(1008, 2376).active(), "right corner and edge boundary included");
        check(!start(20, 2375).active(), "above bottom edge excluded");
        check(!start(20, 2401).active(), "outside display excluded");
        check(!move(start(20, 2390), 78, 2365, 1100), "vertical drift cancels");
        check(!move(start(20, 2390), 78, 2390, 1601), "timeout cancels");
        check(!start(20, 2390).move(8, 1, 78, 2390, 1100, 1080, 2400, 0, 58, 24), "pointer change cancels");
        check(!start(20, 2390).move(7, 2, 78, 2390, 1100, 1080, 2400, 0, 58, 24), "multitouch cancels");
        check(!start(20, 2390).move(7, 1, 78, 2390, 1100, 2400, 1080, 1, 58, 24), "rotation cancels");
        BottomCenterSwipe cancelled = start(20, 2390);
        cancelled.reset();
        check(!move(cancelled, 78, 2390, 1100), "cancelled stream cannot revive");
        BottomCenterSwipe existing = new BottomCenterSwipe();
        existing.down(7, 1, 540, 2395, 1000, 1080, 2400, 0, 8);
        check(move(existing, 598, 2395, 1100), "existing bottom mode still accepts center");
        check(new WorkspaceOptions(0, 0, 2).entryGesture == 2, "new mode survives normalization");
        check(EntryGesturePolicy.effectiveMode(2, false) == 2, "corner mode supports visible gesture line");
        System.out.println("BottomCornerSwipe: " + checks + " checks passed");
    }
}
