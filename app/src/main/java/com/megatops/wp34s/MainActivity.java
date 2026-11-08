/*
 * AWP34S WP34-S Scientific Calculator Port to Android
 *
 * MainActivity.java: Java code for the interface
 *
 * Copyright (C) 2020 Pablo Martin Medrano <pablo@odkq.com>
 * Copyright (C) 2026 Ding Zhaojie <zhaojie_ding@msn.com>
 *
 * AWP34S is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AWP34S is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with 34S.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.megatops.wp34s;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/* Main Activity is the entrypoint for an Android Application */
public class MainActivity extends AppCompatActivity {

    public static final String DATA_DIR;

    static {
        // DataDir for the application is writable without
        // special permissions
        DATA_DIR = Environment.getExternalStorageDirectory() +
                "/Android/data/com.megatops.wp34s";
        // Used to load the wp34s C library on application startup.
        System.loadLibrary("wp34s");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Configuration conf = getResources().getConfiguration();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(new CalcView(this));
        setupSystemBars();
        wp34sInit();
    }

    // change system bar color to match theme
    @SuppressWarnings("deprecation")
    private void setupSystemBars() {
        Window window = getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
        );
        window.setStatusBarColor(Color.parseColor("#292729"));
        window.setNavigationBarColor(Color.parseColor("#272526"));

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);
    }

    public static void logString(String text) {
        Log.i("WP34S.C", text);
    }

    public native int wp34sInit();
}

/*
 * Main View of the program, representing visual elements and input
 */
class CalcView extends View {
    Context context;            /* Store context */
    Handler handler = new Handler(Looper.getMainLooper());  /* Handler for timers */

    private final Bitmap BACKGROUND;  /* Bitmap for the whole interface */
    private Canvas dotsCanvas;        /* Sub Canvas used for the 6x43 dots grid */
    private final Bitmap DOTS;        /* Bitmap for the dots */
    private final Rect DOTS_RECT;     /* Geometry of the sub-canvas before drawing */

    float lcdRatioHeight = 0.0f;   /* To get Ratio of bitmap vs canvas height */
    float ratioHeight = 0.0f;      /* Aspect ratio between bitmap and canvas */
    float ratioWidth = 0.0f;       /* Aspect ratio between bitmap and canvas. Those
                                      are used to get the relative pixel that is clicked */

    /* Dictionary mapping strings (name of assets) to loaded Bitmaps */
    Map<String, Bitmap> bitmapMap = new HashMap<String, Bitmap>();

    /* Constructor */
    public CalcView(Context context) {
        super(context);
        this.context = context;
        loadAssets();

        BACKGROUND = Objects.requireNonNull(this.bitmapMap.get("wp34s_V3_highres_modern"));
        Bitmap fullscreen = Objects.requireNonNull(this.bitmapMap.get("fullscreen"));
        lcdRatioHeight = (float) fullscreen.getHeight() / BACKGROUND.getHeight();
        DOTS = Bitmap.createBitmap(fullscreen.getWidth(), fullscreen.getHeight(),
                Bitmap.Config.ARGB_8888);
        DOTS_RECT = new Rect(0, 0, DOTS.getWidth(), DOTS.getHeight());

        /* Class to define a timer callback on another thread */
        Runnable runnableCode = new Runnable() {
            // Do something here on the main thread
            // Repeat this the same runnable code block again another 100 ms
            // 'this' is referencing the Runnable object
            @Override
            public void run() {
                // heartbeat key, "pressed" every 100ms
                if (wp34sPutKey(-2) == 0) {
                    invalidate(); // only refresh on screen update
                }
                handler.postDelayed(this, 100);
            }
        };
        handler.post(runnableCode);
    }

    /* Load all the pngs for the different segments on digits and parts of
     * the LCD screen from the Assets directory of the application
     */
    private void loadAssets() {
        Bitmap bitmap;
        InputStream is = null;
        AssetManager mgr = this.context.getAssets();
        /* To list the contents of the assets dir filesystem permissions
         * have to be granted to the app, to open the file once known it does
         * not. That is why we list all the assets to be opened here (without
         * the ending .png)
         */
        String[] fileList = {
                "0_minus", "10_1", "10_2", "10_3", "10_4", "10_5",
                "10_6", "10_7", "10_comma", "10_dot", "11_1",
                "11_2", "11_3", "11_4", "11_5", "11_6", "11_7",
                "11_comma", "11_dot", "1_1", "12_1", "12_2", "12_3",
                "12_4", "12_5", "12_6", "12_7", "12_comma", "12_dot",
                "1_2", "1_3", "1_4", "1_5", "1_6", "1_7", "1_comma",
                "1_dot", "2_1", "2_2", "2_3", "2_4", "2_5", "2_6",
                "2_7", "2_comma", "2_dot", "3_1", "3_2", "3_3",
                "3_4", "3_5", "360", "3_6", "3_7", "3_comma", "3_dot",
                "4_1", "4_2", "4_3", "4_4", "4_5", "4_6", "4_7",
                "4_comma", "4_dot", "5_1", "5_2", "5_3", "5_4",
                "5_5", "5_6", "5_7", "5_comma", "5_dot", "6_1",
                "6_2", "6_3", "6_4", "6_5", "6_6", "6_7", "6_comma",
                "6_dot", "7_1", "7_2", "7_3", "7_4", "7_5", "7_6", "7_7",
                "7_comma", "7_dot", "8_1", "8_2", "8_3", "8_4", "8_5",
                "8_6", "8_7", "8_comma", "8_dot", "9_1", "9_2", "9_3",
                "9_4", "9_5", "9_6", "9_7", "9_comma", "9_dot",
                "arrow_down", "batt", "beg", "bit_equal", "e0_minus", "e1_1",
                "e1_2", "e1_3", "e1_4", "e1_5", "e1_6", "e1_7", "e2_1",
                "e2_2", "e2_3", "e2_4", "e2_5", "e2_6", "e2_7", "e3_1",
                "e3_2", "e3_3", "e3_4", "e3_5", "e3_6", "e3_7",
                "rad", "rcl", "rpn", "small_equal", "sto", "input",
                "wp34s_V3_highres_modern", "fullscreen"};

        for (String s : fileList) {
            // Log.i("WP34S.MainActivity", "path " + fileList[i]);
            try {
                String name = s + ".webp";
                is = mgr.open(name, AssetManager.ACCESS_BUFFER);
            } catch (IOException e) {
                Log.e("WP34S.MainActivity", "failed to load asset", e);
            }
            bitmap = new BitmapDrawable(getResources(), is).getBitmap();
            this.bitmapMap.put(s, bitmap);
        }
    }

    /* Draw asset by name over the background canvas */
    private void drawAsset(Canvas canvas, Paint p, String name) {
        Bitmap bitmap = this.bitmapMap.get(name);
        if (bitmap == null) {
            Log.i("WP34S.MainActivity", "asset " + name + " not found ");
            return;
        }
        Rect subSrc = new Rect(0, 0,
                bitmap.getWidth(), bitmap.getHeight());
        Rect clip = canvas.getClipBounds();

        Rect subDst = new Rect(clip.left, clip.top, clip.right, (int) (clip.bottom * lcdRatioHeight));
        canvas.drawBitmap(bitmap, subSrc, subDst, p);
    }

    /* Code to draw a digit directly from the interface. This is used for configuration/debugging
     * as segments to be drawn are addressed directly by the WP34S C code
     */
    private void drawDigit(Canvas canvas, Paint p, int digit, int pos, boolean exponent) {
        boolean[][] digArray = {
                {true, true, true, true, true, true, false},
                {false, false, true, true, false, false, false},
                {false, true, true, false, true, true, true},
                {false, true, true, true, true, false, true},
                {true, false, true, true, false, false, true},
                {true, true, false, true, true, false, true},
                {true, true, false, true, true, true, true},
                {false, true, true, true, false, false, false},
                {true, true, true, true, true, true, true},
                {true, true, true, true, true, false, true}
        };

        for (int i = 0; i < digArray[digit].length; i++) {
            if (digArray[digit][i]) {
                String prefix = "";
                if (exponent) {
                    prefix = "e";
                }
                String name = String.format(Locale.getDefault(), "%s%d_%d", prefix, pos, i + 1);

                drawAsset(canvas, p, name);
                if (!exponent) {
                    String comma = String.format(Locale.getDefault(), "%s%d_comma", prefix, pos);
                    String dot = String.format(Locale.getDefault(), "%s%d_dot", prefix, pos);
                    drawAsset(canvas, p, comma);
                    drawAsset(canvas, p, dot);
                }
            }
        }
    }

    /* Clear dot canvas */
    private void clearDots() {
        dotsCanvas = new Canvas(DOTS);
        final Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        dotsCanvas.drawRect(DOTS_RECT, paint);
    }

    /* Draw the dots bitmap over the background */
    private void drawDots(Canvas canvas, Paint p) {
        Rect clip = canvas.getClipBounds();
        Rect subDst = new Rect(clip.left, clip.top, clip.right,
                (int) (clip.bottom * lcdRatioHeight));
        canvas.drawBitmap(DOTS, DOTS_RECT, subDst, p);
    }

    /* Draw a dot (rectangular square) into the dots canvas */
    private void drawDot(int x, int y) {
        int canvasY = 30 + (11 * y);
        int canvasX = 60 + (10 * x);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStrokeWidth(0);
        Rect rect = new Rect(canvasX, canvasY, canvasX + 9, canvasY + 10);
        dotsCanvas.drawRect(rect, p);
    }

    /*
       Translate relative coordinate of key down event (relative to the
       skin bitmap, not to the canvas, to keycode.

       Keycodes are as follows:

                |  0    1    2    3    4   5
             --------------------------------
             0  |  1    2    3    4    5   6
             1  |  7    8    9   10   11  12         (col + 1) + (row * 6)
             2  |    13     15   16   17  18
                |---------------------------
             3  | 19    20    21    22    23
             4  | 24    25    26    27    28         col + ((row - 3) * 5) + 19
             5  | 29    30    31    32    33
             6  | 34    35    36    37    38
             --------------------------------
                  0      1     2     3     4
     */
    private int retrieveKey(int x, int y) {
        /* Abscise points of corners of buttons on top three rows */
        final int[] X_UP = {18, 137, 147, 266, 276, 395, 405, 524, 533, 653, 662, 782};
        /* Abscise points of corners of buttons on bottom four rows */
        final int[] X_DOWN = {18, 162, 172, 318, 328, 472, 482, 628, 638, 782};

        int row = retrieveRow(y);
        if (row == -1) {
            return 0;
        }
        int col = -1;

        /* Special case of the ENTER key */
        if (row == 2) {
            if ((x >= X_UP[0]) && (x <= X_UP[3])) {
                return 13;
            }
        }

        if (row <= 2) {
            for (int i = 0; i < 6; i++) {
                int min = X_UP[(i * 2)];
                int max = X_UP[(i * 2) + 1];
                if ((x >= min) && (x <= max)) {
                    col = i;
                }
            }
        } else {
            for (int i = 0; i < 5; i++) {
                int min = X_DOWN[(i * 2)];
                int max = X_DOWN[(i * 2) + 1];
                if ((x >= min) && (x <= max)) {
                    col = i;
                }
            }
        }

        if (col == -1) {
            return 0;
        }
        Log.i("WP34S.MainActivity", "row" + row + " col " + col);
        if (row <= 2) {
            return (col + 1) + (row * 6);
        } else {
            return col + ((row - 3) * 5) + 19;
        }
    }

    /* Return the row, common to all keys */
    private int retrieveRow(int y) {
        final int OFFSET = 19; // move the keypad down for better view

        /* Coordinate points of corners of buttons in all rows */
        final int[] Y_ROW = {280, 365, 420, 504, 592, 677, 720, 808, 850, 935, 980, 1064, 1108, 1194};
        for (int i = 0; i < Y_ROW.length / 2; i++) {
            int min = Y_ROW[(i * 2)] + OFFSET;
            int max = Y_ROW[(i * 2) + 1] + OFFSET;
            if ((y >= min) && (y <= max)) {
                return i;
            }
        }
        return -1;
    }

    /* Get the asset string associated to the number passed */
    private String get_asset_from_dot(int n) {
        String[] dots = {
                "uno",
                "dos",
                "tres",
                "cuatro"
        };
        return dots[1];
    }

    private String bitPosToResource(int n) {
        /*
         * Converts from LCD Segment names/ indexes to the resources nomenclature
         * TODO: Change the resources nomenclature to avoid this step*
         *
         *         "2"
         *      --- a:0 ----
         *      |          |
         *  "1" f:5        b:1 "3"
         *      |          |
         *      +-- g:6 ---+
         *      |   "7"    |
         * "6"  e:4        c:2 "4"
         *      |          |
         *      +---d:3----+
         *          "5"       h:7 "dot"
         *                     i:8 "comma"
         */
        String[] resources = {
                "2", "1", "7", "3", "6", "5", "4", "dot", "comma"
        };
        /*       0    1    2    3    4    5    6    7       8     */
        /*       *         *    *    *    *                       */
        if (n > resources.length) {
            return "Error";
        }
        return resources[n];
    }

    private void drawAssetIf(Canvas canvas, byte[] data, Paint p, String name, int index) {
        if (data[index] != 0x00) {
            drawAsset(canvas, p, name);
        }
    }

    /* onDraw is called each time the display is refreshed after calling
     * invalidate()
     */
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        Rect clip = canvas.getClipBounds();
        Log.i("WP34S.MainActivity", String.format(Locale.US, "rect %d %d %d %d", clip.left, clip.right, clip.bottom, clip.top));
        Paint p = new Paint();

        p.setAntiAlias(true);
        p.setFilterBitmap(true);
        Rect src = new Rect(0, 0, BACKGROUND.getWidth(), BACKGROUND.getHeight());
        canvas.drawBitmap(BACKGROUND, src, clip, p);

        ratioWidth = (float) BACKGROUND.getWidth() / clip.width();
        ratioHeight = (float) BACKGROUND.getHeight() / clip.height();

        byte[] data = wp34sGetLcdData();

        StringBuilder buffer = new StringBuilder("in onDraw ");
        for (int i = 0; i < data.length; i++) {
            String message = String.format(Locale.US, "[%03d:%02x]", i, data[i]);
            buffer.append(message);
        }

        Log.i("WP34S.MainActivity", String.format(Locale.US, "data.length -> %d", data.length));

        /* First the digits */
        for (int i = 0; i < LcdConstants.DISPLAY_DIGITS; i++) {
            int baseBit = i * LcdConstants.SEGS_PER_DIGIT;
            String baseName = String.format(Locale.US, "%d_", (i + 1));
            boolean dot = false;
            boolean comma = false;
            for (int bitPos = 0; bitPos < LcdConstants.SEGS_PER_DIGIT; bitPos++) {
                int bb = baseBit + bitPos;

                if (data[bb] != 0x00) {
                    if (bitPos == 7) {
                        dot = true;
                    } else if (bitPos == 8) {
                        comma = true;
                    } else {
                        String asset = baseName + bitPosToResource(bitPos);
                        drawAsset(canvas, p, asset);
                    }
                }
            }
            if (comma) {
                drawAsset(canvas, p, baseName + "comma");
            } else if (dot) {
                drawAsset(canvas, p, baseName + "dot");
            }
        }

        /* The exponent */
        for (int i = 0; i < 3; i++) {
            int baseBit = LcdConstants.SEGS_EXP_BASE + (i * LcdConstants.SEGS_PER_EXP_DIGIT);
            String baseName = String.format(Locale.US, "e%d_", (i + 1));
            boolean dot = false;
            boolean comma = false;
            for (int bitPos = 0; bitPos < LcdConstants.SEGS_PER_EXP_DIGIT; bitPos++) {
                int bb = baseBit + bitPos;
                if (data[bb] != 0x00) {
                    String asset = baseName + bitPosToResource(bitPos);
                    drawAsset(canvas, p, asset);
                }
            }
        }

        /* The indicators */
        drawAssetIf(canvas, data, p, "0_minus", LcdConstants.MANT_SIGN);
        drawAssetIf(canvas, data, p, "e0_minus", LcdConstants.EXP_SIGN);
        drawAssetIf(canvas, data, p, "bit_equal", LcdConstants.BIG_EQ);
        drawAssetIf(canvas, data, p, "small_equal", LcdConstants.LIT_EQ);
        drawAssetIf(canvas, data, p, "arrow_down", LcdConstants.DOWN_ARR);
        drawAssetIf(canvas, data, p, "input", LcdConstants.INPUT);
        drawAssetIf(canvas, data, p, "batt", LcdConstants.BATTERY);
        drawAssetIf(canvas, data, p, "beg", LcdConstants.BEG);
        drawAssetIf(canvas, data, p, "sto", LcdConstants.STO_annun);
        drawAssetIf(canvas, data, p, "rcl", LcdConstants.RCL_annun);
        drawAssetIf(canvas, data, p, "rad", LcdConstants.RAD);
        drawAssetIf(canvas, data, p, "360", LcdConstants.DEG);
        drawAssetIf(canvas, data, p, "rpn", LcdConstants.RPN);

        clearDots();
        int index = 0;

        for (int x = 0; x < LcdConstants.BITMAP_WIDTH; x++) {
            for (int y = 0; y < 6; y++) {
                if (data[LcdConstants.MATRIX_BASE + index] != 0x00) {
                    drawDot(x, y);
                }
                index++;
            }
        }
        drawDots(canvas, p);
    }

    /*
     * Callback from the touchscreen events. Detects the relative coordinates
     * inside the parent bitmap; translate from physical coordinates to coordinates in the
     * original png, and calculate the button (if any) being pressed.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int what = e.getAction();
        if (what != MotionEvent.ACTION_DOWN && what != MotionEvent.ACTION_UP) {
            return true;
        }

        if (what == MotionEvent.ACTION_DOWN) {
            int x = (int) (e.getX() * ratioWidth);
            int y = (int) (e.getY() * ratioHeight);
            int cur_key = retrieveKey(x, y);
            invalidate();
            Log.i("WP34S.MainActivity", "action down " + x + " " + y + " key " + cur_key);
            wp34sPutKey(cur_key);

        } else {
            invalidate();
            Log.i("WP34S.MainActivity", "action up");
            wp34sPutKey(-1);
        }
        return true;
    }

    public native int wp34sIsDot(int n);

    public native byte[] wp34sGetLcdData();

    public native int wp34sPutKey(int key);
}
