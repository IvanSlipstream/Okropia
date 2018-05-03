package ru.slipstream.var.okropia;

import android.util.Log;

/**
 * Created by Slipstream-DESKTOP on 04.02.2018.
 */

public class L {

    public static void d(Class cl, String message){
        Log.d("dbg-"+cl.getSimpleName(), message);
    }
}
