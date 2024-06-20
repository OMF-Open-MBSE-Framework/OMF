/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging;


import static com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFColors.*;

public class SysoutColorPrinter {
    public static final String RESET = "\u001B[0m";

    public static void warn(String s){print(s, YELLOW);}
    public static void status(String s){print(s, BLUE);}
    public static void err(String s){print(s, RED);}
    public static void success(String s){print(s, GREEN);}

    public static void print(String s){print(s, BLACK);}

    public static void print(String s, String COLOR){System.out.println(COLOR + s + RESET);}

}
