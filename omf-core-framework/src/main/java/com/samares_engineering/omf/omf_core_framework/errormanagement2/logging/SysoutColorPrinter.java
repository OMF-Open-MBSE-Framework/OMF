/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging;


public class SysoutColorPrinter {
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static void warn(String s){print(s, YELLOW);}
    public static void status(String s){print(s, BLUE);}
    public static void err(String s){print(s, RED);}
    public static void success(String s){print(s, GREEN);}

    public static void print(String s){print(s, BLACK);}

    public static void print(String s, String COLOR){System.out.println(COLOR + s + RESET);}

}
