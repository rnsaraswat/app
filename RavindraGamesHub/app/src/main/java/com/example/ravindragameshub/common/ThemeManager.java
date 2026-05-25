package com.example.ravindragameshub.common;


// ==========================================
// ThemeManager.java
// Common Theme System for All Games
// ==========================================

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.Typeface;

import com.example.ravindragameshub.R;
import com.google.android.material.card.MaterialCardView;

public class ThemeManager {

    // THEMES
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_BLUE = 2;
    public static final int THEME_ORANGE = 3;

    // SharedPreferences
    private static final String PREF = "RGH_THEME_PREF";
    private static final String KEY_THEME = "CURRENT_THEME";

    // CURRENT THEME
    public static int currentTheme = THEME_LIGHT;

    //Theme Manager Colors
    public static int bgColor;
    public static int textColor;
    public static int borderColor;

    public static int dropdownBgColor;
    public static int dropdownTextColor;

    public static int selectedBgColor;
    public static int selectedTextColor;

    // ==========================================
    // SAVE THEME
    // ==========================================
    public static void saveTheme(Context context, int theme) {

        currentTheme = theme;
        SharedPreferences prefs =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        prefs.edit()
                .putInt(KEY_THEME, theme)
                .apply();
    }

    // ==========================================
    // LOAD THEME
    // ==========================================
    public static int getTheme(Context context) {

        SharedPreferences prefs =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        return prefs.getInt(KEY_THEME, THEME_LIGHT);
//        currentTheme =
//                pref.getInt(KEY_THEME, THEME_LIGHT);
    }

    // ==========================================
    // APPLY THEME
    // ==========================================
    public static void applyTheme(Activity activity,
                                  View rootLayout) {

        int theme = getTheme(activity);

        bgColor = Color.WHITE;
        textColor = Color.BLACK;
        borderColor = Color.BLACK;

        GradientDrawable gradient = new GradientDrawable();

        // ---------------- LIGHT ----------------
        switch (theme) {
//        if(theme.equals(LIGHT)) {
            case ThemeManager.THEME_LIGHT:
                ThemeManager.bgColor = Color.WHITE;
                ThemeManager.textColor = Color.BLACK;
                ThemeManager.borderColor = Color.BLACK;

                ThemeManager.dropdownBgColor = Color.DKGRAY;
                ThemeManager.dropdownTextColor = Color.WHITE;

                ThemeManager.selectedBgColor = Color.WHITE;
                ThemeManager.selectedTextColor = Color.WHITE;

                rootLayout.setBackgroundColor(bgColor);
                break;

            case ThemeManager.THEME_DARK:

                ThemeManager.bgColor = Color.BLACK;
                ThemeManager.textColor = Color.WHITE;
                ThemeManager.borderColor = Color.WHITE;

                ThemeManager.dropdownBgColor = Color.DKGRAY;
                ThemeManager.dropdownTextColor = Color.WHITE;

                ThemeManager.selectedBgColor = Color.WHITE;
                ThemeManager.selectedTextColor = Color.BLACK;

                rootLayout.setBackgroundColor(bgColor);
                break;

            case ThemeManager.THEME_BLUE:

                ThemeManager.bgColor = Color.BLACK;
                ThemeManager.textColor = Color.YELLOW;
                ThemeManager.borderColor = Color.YELLOW;

                ThemeManager.dropdownBgColor = Color.DKGRAY;
                ThemeManager.dropdownTextColor = Color.YELLOW;

                ThemeManager.selectedBgColor = Color.WHITE;
                ThemeManager.selectedTextColor = Color.BLACK;

                gradient.setOrientation(
                        GradientDrawable.Orientation.TOP_BOTTOM);

                gradient.setColors(new int[]{
                        Color.parseColor("#0D47A1"),
                        Color.parseColor("#42A5F5")
                });

                rootLayout.setBackground(gradient);
                break;

            case ThemeManager.THEME_ORANGE:

                ThemeManager.bgColor = Color.BLACK;
                ThemeManager.textColor = Color.YELLOW;
                ThemeManager.borderColor = Color.YELLOW;

                ThemeManager.dropdownBgColor = Color.DKGRAY;
                ThemeManager.dropdownTextColor = Color.YELLOW;

                ThemeManager.selectedBgColor = Color.WHITE;
                ThemeManager.selectedTextColor = Color.BLACK;

                gradient.setOrientation(
                        GradientDrawable.Orientation.TOP_BOTTOM);

                gradient.setColors(new int[]{
                        Color.parseColor("#E65100"),
                        Color.parseColor("#FFA726")
                });

                rootLayout.setBackground(gradient);
                break;
        }

        // Apply Theme to All Views
        applyToAllViews(rootLayout,
                textColor,
                borderColor,
                theme);
    }

    // ==========================================
    // APPLY COLORS TO ALL ITEMS
    // ==========================================
    private static void applyToAllViews(View view,
                                        int textColor,
                                        int borderColor,
                                        int theme) {

        // ---------- BUTTON ----------
        if(view instanceof Button) {

            Button btn = (Button) view;

//            btn.setTextColor(textColor);

            GradientDrawable shape =
                    new GradientDrawable();

            shape.setCornerRadius(20);

            shape.setStroke(4, borderColor);

            // Different Button Colors
            switch (currentTheme) {
                case ThemeManager.THEME_LIGHT:
                    shape.setColor(Color.WHITE);
                    btn.setTextColor(Color.WHITE);
                    break;
                case ThemeManager.THEME_DARK:
                    shape.setColor(Color.parseColor("#222222"));
                    btn.setTextColor(Color.WHITE);
                    break;
                case ThemeManager.THEME_BLUE :
                    shape.setColor(Color.parseColor("#1565C0"));
                    btn.setTextColor(Color.YELLOW);
                    break;
                case ThemeManager.THEME_ORANGE  :
                    shape.setColor(Color.parseColor("#EF6C00"));
                    btn.setTextColor(Color.YELLOW);
                    break;
            }

            btn.setBackground(shape);
        }

        // ---------- TEXTVIEW ----------
        else if(view instanceof TextView &&
                !(view instanceof Button))
        {
            TextView tv = (TextView) view;

            Object tagObj = tv.getTag();

            String tag = "";

            if(tagObj != null)
            {
                tag = tagObj.toString();
            }

            switch(tag)
            {
                // ---------- HEADER ----------
                case "header":

                    switch(currentTheme)
                    {
                        case THEME_LIGHT:
                            tv.setTextColor(
                                    Color.BLACK);
                            break;

                        case THEME_DARK:
                            tv.setTextColor(
                                    Color.WHITE);
                            break;

                        case THEME_BLUE:
                            tv.setTextColor(
                                    Color.YELLOW);
                            break;

                        case THEME_ORANGE:
                            tv.setTextColor(
                                    Color.YELLOW);
                            break;
                    }

//                    tv.setTextSize(28);

                    tv.setTypeface(
                            null,
                            Typeface.BOLD);

                    break;

                // ---------- SUBHEADER ----------
                case "subheader":

                    switch(currentTheme)
                    {
                        case THEME_LIGHT:
                            tv.setTextColor(
                                    Color.DKGRAY);
                            break;

                        case THEME_DARK:
                            tv.setTextColor(
                                    Color.LTGRAY);
                            break;

                        case THEME_BLUE:
                        case THEME_ORANGE:
                            tv.setTextColor(
                                    Color.WHITE);
                            break;
                    }

                    break;

                // ---------- WELCOME ----------
                case "welcome":

                    switch(currentTheme)
                    {
                        case THEME_LIGHT:
                            tv.setTextColor(
                                    Color.BLUE);
                            break;

                        case THEME_DARK:
                            tv.setTextColor(
                                    Color.CYAN);
                            break;

                        case THEME_BLUE:
                            tv.setTextColor(
                                    Color.YELLOW);
                            break;

                        case THEME_ORANGE:
                            tv.setTextColor(
                                    Color.WHITE);
                            break;
                    }

                    break;

                // ---------- PLAYER TURN ----------
                case "turn":

                    switch(currentTheme)
                    {
                        case THEME_LIGHT:
                            tv.setTextColor(
                                    Color.RED);
                            break;

                        case THEME_DARK:
                            tv.setTextColor(
                                    Color.GREEN);
                            break;

                        case THEME_BLUE:
                        case THEME_ORANGE:
                            tv.setTextColor(
                                    Color.WHITE);
                            break;
                    }

                    tv.setTypeface(
                            null,
                            Typeface.BOLD);

                    break;

                // ---------- SCORE ----------
                case "score":

                    switch(currentTheme)
                    {
                        case THEME_LIGHT:
                            tv.setTextColor(
                                    Color.MAGENTA);
                            break;

                        case THEME_DARK:
                            tv.setTextColor(
                                    Color.YELLOW);
                            break;

                        case THEME_BLUE:
                        case THEME_ORANGE:
                            tv.setTextColor(
                                    Color.WHITE);
                            break;
                    }

                    break;

                // ---------- DEFAULT ----------
                default:

                    tv.setTextColor(textColor);
            }
        }

        // ---------- SPINNER ----------
        else if(view instanceof Spinner)
        {
            Spinner spinner = (Spinner) view;

            // ---------- Spinner Background ----------
            GradientDrawable shape =
                    new GradientDrawable();

            shape.setCornerRadius(20);

            // Border Color
            switch(currentTheme)
            {
                case ThemeManager.THEME_LIGHT:
                    shape.setStroke(4, Color.BLACK);
                    break;

                case ThemeManager.THEME_DARK:
                    shape.setStroke(4, Color.WHITE);
                    break;

                case ThemeManager.THEME_BLUE:
                case ThemeManager.THEME_ORANGE:
                    shape.setStroke(4, Color.YELLOW);
                    break;
            }

            // Same Blue Background
            shape.setColor(Color.parseColor("#1565C0"));

            spinner.setBackground(shape);

            // ---------- Spinner Text Fix ----------
            if(spinner.getAdapter() != null)
            {
                ArrayAdapter adapter =
                        (ArrayAdapter)
                                spinner.getAdapter();

                adapter.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(
                        new ArrayAdapter(
                                spinner.getContext(),
                                android.R.layout.simple_spinner_item,
                                getSpinnerItems(adapter))
                        {

                            @Override
                            public View getView(
                                    int position,
                                    View convertView,
                                    ViewGroup parent)
                            {
                                TextView tv =
                                        (TextView)
                                                super.getView(
                                                        position,
                                                        convertView,
                                                        parent);

                                // CLOSED VIEW TEXT
                                if(currentTheme ==
                                        ThemeManager.THEME_LIGHT ||
                                        currentTheme ==
                                                ThemeManager.THEME_DARK)
                                {
                                    tv.setTextColor(Color.WHITE);
                                }
                                else
                                {
                                    tv.setTextColor(Color.YELLOW);
                                }

                                tv.setTextSize(18);

                                tv.setTypeface(
                                        null,
                                        Typeface.BOLD);

                                return tv;
                            }

                            @Override
                            public View getDropDownView(
                                    int position,
                                    View convertView,
                                    ViewGroup parent)
                            {
                                TextView tv =
                                        (TextView)
                                                super.getDropDownView(
                                                        position,
                                                        convertView,
                                                        parent);

                                // DROPDOWN COLORS
                                if(currentTheme ==
                                        ThemeManager.THEME_LIGHT)
                                {
                                    tv.setBackgroundColor(
                                            Color.WHITE);

                                    tv.setTextColor(
                                            Color.BLACK);
                                }
                                else
                                {
                                    tv.setBackgroundColor(
                                            Color.BLACK);

                                    tv.setTextColor(
                                            Color.WHITE);
                                }

                                tv.setTextSize(18);

                                return tv;
                            }
                        });
            }
        }

        // ---------- LAYOUT ----------
        else if(view instanceof LinearLayout) {

            // Optional
        }

        // ---------- CHILD VIEWS ----------
        if(view instanceof ViewGroup) {

            ViewGroup group = (ViewGroup) view;

            for(int i=0; i<group.getChildCount(); i++) {

                applyToAllViews(
                        group.getChildAt(i),
                        textColor,
                        borderColor,
                        theme
                );
            }
        }
    }

    //common button theme
    public static void applyButtonTheme(Button button, Context context)
    {
        // Blue background sabhi themes me
        button.setBackgroundResource(R.drawable.common_button_bg);

        // Text color theme ke hisab se
        if(currentTheme == THEME_LIGHT ||
                currentTheme == THEME_DARK)
        {

            button.setTextColor(
                    context.getResources().getColor(R.color.white)
            );
        }
        else
        {
            button.setTextColor(
                    context.getResources().getColor(R.color.yellow)
            );
        }
    }

    //common spinner theme
    public static void applySpinnerTheme(
            Spinner spinner,
            Context context,
            String[] items)
    {

        // Force blue spinner background
        spinner.setBackgroundResource(
                R.drawable.common_spinner_bg);

        // Border color change
        GradientDrawable drawable =
                (GradientDrawable)
                        spinner.getBackground();

        if(currentTheme == THEME_LIGHT)
        {
            drawable.setStroke(2,
                    Color.BLACK);
        }
        else if(currentTheme == THEME_DARK)
        {
            drawable.setStroke(2,
                    Color.WHITE);
        }
        else
        {
            drawable.setStroke(2,
                    Color.YELLOW);
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        context,
                        R.layout.spinner_selected_item,
                        items)
                {

                    @Override
                    public View getView(
                            int position,
                            View convertView,
                            ViewGroup parent)
                    {

                        View view = super.getView(
                                position,
                                convertView,
                                parent);

                        TextView tv = view.findViewById(
                                android.R.id.text1);

                        // Transparent
                        tv.setBackgroundColor(
                                Color.TRANSPARENT);

                        // Text size/style
                        tv.setTextSize(18);
                        tv.setTypeface(
                                null,
                                Typeface.BOLD);

                        // Selected spinner text color
                        // IMPORTANT
                        if(currentTheme == THEME_LIGHT ||
                                currentTheme == THEME_DARK)
                        {
                            tv.setTextColor(Color.WHITE);
                        }
                        else
                        {
                            tv.setTextColor(Color.YELLOW);
                        }

                        return view;
                    }

                    @Override
                    public View getDropDownView(
                            int position,
                            View convertView,
                            ViewGroup parent)
                    {
                        TextView tv =
                                (TextView) super.getDropDownView(
                                        position,
                                        convertView,
                                        parent);

                        tv.setTextSize(18);

                        // Dropdown colors
                        if(currentTheme == THEME_LIGHT)
                        {
                            tv.setBackgroundColor(Color.WHITE);
                            tv.setTextColor(Color.BLACK);
                        }
                        else
                        {
                            tv.setBackgroundColor(Color.BLACK);
                            tv.setTextColor(Color.WHITE);
                        }

                        return tv;
                    }
                };

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

    private static String[] getSpinnerItems(
            ArrayAdapter adapter)
    {
        String[] items =
                new String[adapter.getCount()];

        for(int i=0; i<adapter.getCount(); i++)
        {
            items[i] =
                    adapter.getItem(i).toString();
        }

        return items;
    }


    // CARD THEME
    public static void applyCardTheme(
            Context context,
            MaterialCardView card,
            TextView title,
            TextView desc
    ) {

        if (card == null) {
            return;
        }

        switch (currentTheme) {

            // LIGHT
            case ThemeManager.THEME_LIGHT:

                card.setCardBackgroundColor(
                        Color.WHITE);

                card.setStrokeColor(
                        Color.BLACK);

                title.setTextColor(
                        Color.BLACK);

                desc.setTextColor(
                        Color.DKGRAY);

                break;


            // DARK
            case ThemeManager.THEME_DARK:

                card.setCardBackgroundColor(
                        Color.parseColor("#222222"));

                card.setStrokeColor(
                        Color.WHITE);

                title.setTextColor(
                        Color.WHITE);

                desc.setTextColor(
                        Color.LTGRAY);

                break;


            // BLUE
            case ThemeManager.THEME_BLUE:

                card.setCardBackgroundColor(
                        Color.parseColor("#1565C0"));

                card.setStrokeColor(
                        Color.YELLOW);

                title.setTextColor(
                        Color.WHITE);

                desc.setTextColor(
                        Color.parseColor("#FFF59D"));

                break;


            // ORANGE
            case ThemeManager.THEME_ORANGE:

                card.setCardBackgroundColor(
                        Color.parseColor("#EF6C00"));

                card.setStrokeColor(
                        Color.YELLOW);

                title.setTextColor(
                        Color.WHITE);

                desc.setTextColor(
                        Color.parseColor("#FFF59D"));

                break;

        }

    }
}