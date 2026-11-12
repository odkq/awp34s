package com.odkq.wp34s;

/* Constants coming from lcd.h
 * TODO: Find some way to automate constants extraction
 */
public class LcdConstants {
    public static final int DISPLAY_DIGITS = 12;
    public static final int SEGS_PER_DIGIT = 9;
    public static final int SEGS_PER_EXP_DIGIT = 7;
    public static final int SEGS_EXP_BASE = (DISPLAY_DIGITS * SEGS_PER_DIGIT);
    public static final int BITMAP_WIDTH = 43;

    public static final int MANT_SIGN	=129;
    public static final int EXP_SIGN	=130;
    public static final int BIG_EQ		=131;
    public static final int LIT_EQ		=132;
    public static final int DOWN_ARR	=133;
    public static final int INPUT		=134;
    public static final int BATTERY		=135;
    public static final int BEG		    =136;
    public static final int STO_annun	=137;
    public static final int RCL_annun	=138;
    public static final int RAD		    =139;
    public static final int DEG		    =140;
    public static final int RPN		    =141;
    public static final int MATRIX_BASE	=142;
}
