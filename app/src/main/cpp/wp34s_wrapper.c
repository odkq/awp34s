/*
 * AWP34S WP34-S Scientific Calculator Port to Android
 *
 * wp34s_wrapper.c: Bridge between JNI C++ and WPS34 C code
 *
 * Copyright (C) 2020 Pablo Martin Medrano <pablo@odkq.com>
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

#include <stdio.h>
#include <string.h>
#include "xeq.h"
#include "stopwatch.h"
#include "display.h"
#include "data.h"
#include "storage.h"
#include "serial.h"
#include "keys.h"
#include "lcd.h"
#include "int.h"

#include "wp34s_android.h"

uint64_t LcdData[10];
unsigned long long int instruction_count = 0;
int view_instruction_counter = 0;

/*
 * Return the directory for data files of this application in
 * android
 */
char *get_datfile(char *datfile, const char *basename)
{
    char line[1024];
    FILE *fp = fopen("/proc/self/cmdline", "r");

    /* cmdline is the name of the process, so the name of our app.
     * wp34s.odkq.com most of the time, but just to make sure */
    fgets(line, sizeof line, fp);
    fclose(fp);
    sprintf(datfile, "/data/data/%s/%s", line, basename);
    return datfile;
}

void init_calculator()
{
    char datfile[1024];

    for (int i = 0; i < 10; i++) {
        LcdData[i] = 0;
    }

    get_datfile(datfile, "wp34s.dat");
    load_statefile(datfile);

    DispMsg = NULL;
    init_34s();
    display();

    /*
    char prstr[1024];
    char tmpstr[16];
    unsigned char *data = (unsigned char *)LcdData;

    prstr[0] = '\0';
    strcat(prstr, "LcdData ");
    for (int i= 0; i < 400; i++) {
        sprintf(tmpstr, "[%03d:%02x]", i, (unsigned char)(data[i]));
        strcat(prstr, tmpstr);
    }
    android_logString(prstr);
    */
}

int is_key_pressed(void)
{
    return 0;
}
int put_key(int k)
{
    return k;
}
void xprocess_keycode(int k)
{
    if (k < 0)
        return;
    return process_keycode(k);
}
enum shifts shift_down(void)
{
    return SHIFT_N;
}
void put_byte( unsigned char byte )
{
    report_err(ERR_PROG_BAD);
}
void flush_comm( void )
{
}
int open_port( int baud, int bits, int parity, int stopbits )
{
    return 0;
}
extern void close_port( void )
{
}

void updateScreen()
{
    android_logString("updateScreen start");
}

int is_dot_wrapper(int n)
{
    return is_dot(n);
}
