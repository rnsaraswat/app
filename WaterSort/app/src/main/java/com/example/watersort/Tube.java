package com.example.watersort;

import java.util.ArrayList;
import java.util.List;

public class Tube {

    public static final int CAPACITY = 4;

    private final List<Integer> colors =
            new ArrayList<>();

    public List<Integer> getColors() {
        return colors;
    }

    public boolean isEmpty() {
        return colors.isEmpty();
    }

    public boolean isFull() {
        return colors.size() >= CAPACITY;
    }

    public int topColor() {

        if (colors.isEmpty())
            return -1;

        return colors.get(
                colors.size() - 1);
    }

    public void push(int color) {

        if (!isFull())
            colors.add(color);
    }

    public int pop() {

        return colors.remove(
                colors.size() - 1);
    }

    public boolean canPourTo(
            Tube target) {

        if (isEmpty())
            return false;

        if (target.isFull())
            return false;

        if (target.isEmpty())
            return true;

        return topColor() ==
                target.topColor();
    }

    public boolean isCompleted() {

        if (colors.size() != CAPACITY)
            return false;

        int c = colors.get(0);

        for (int color : colors) {

            if (color != c)
                return false;
        }

        return true;
    }

    public int countTopSameColor() {

        if (colors.isEmpty())
            return 0;

        int top = topColor();

        int count = 0;

        for (int i = colors.size() - 1;
             i >= 0;
             i--) {

            if (colors.get(i) == top)
                count++;
            else
                break;
        }

        return count;
    }

    public int emptySlots() {

        return CAPACITY - colors.size();
    }

    public boolean canReceiveColor(
            int color) {

        if (isFull())
            return false;

        if (isEmpty())
            return true;

        return topColor() == color;
    }
}