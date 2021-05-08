/*
 * AWP34S WP34-S Scientific Calculator Port to Android
 *
 * wp34s_wrapper.h: Functions prototypes used in wps34_android.cpp
 * (C++ code)
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

#ifndef WP34S_WRAPPER_H_
#define WP34S_WRAPPER_H_
extern "C"
{
    extern uint64_t LcdData[10];
    extern unsigned char dots[400];
    extern void init_calculator();
    void updateScreen();
    extern int is_dot_wrapper(int n);
    extern int xput_key(int k);
    extern void xprocess_keycode(int k);
    extern char *get_datfile(char *datfile, const char *basename);
};
#endif