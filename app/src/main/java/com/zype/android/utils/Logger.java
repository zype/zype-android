package com.zype.android.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Field;

public class Logger {

    private static final String LOG_TAG = "Log";

    private final static boolean isDebug = true;

    static {
        Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());
    }

    public static void e(String message, Throwable cause) {
        if (isDebug) Log.e(LOG_TAG, "[" + message + "]", cause);
    }

    public static void e(String message) {
        if (isDebug) {

            Throwable t = new Throwable();
            StackTraceElement[] elements = t.getStackTrace();

            String callerClassName = elements[1].getFileName();
            Log.e(LOG_TAG, "[" + callerClassName + "] " + message);
        }
    }

    public static void i(String message, Throwable cause) {
        if (isDebug) {
            Log.i(LOG_TAG, "[" + message + "]", cause);
        }
    }

    public static void i(String message) {
        if (isDebug) {
            Throwable t = new Throwable();
            StackTraceElement[] elements = t.getStackTrace();

            String callerClassName = elements[1].getFileName();
            Log.i(LOG_TAG, "[" + callerClassName + "] " + message);
        }
    }

    public static void d(String message, Throwable cause) {
        if (isDebug) {
            Log.d(LOG_TAG, "[" + message + "]", cause);
        }
    }

    public static void d(String message) {
        if (isDebug) {
            Throwable t = new Throwable();
            StackTraceElement[] elements = t.getStackTrace();

            String callerClassName = elements[1].getFileName();
            Log.d(LOG_TAG, "[" + callerClassName + "] " + message);
        }
    }

    public static void w(String message, Throwable cause) {
        if (isDebug) {
            Log.w(LOG_TAG, "[" + message + "]", cause);
        }
    }

    public static void w(String message) {
        if (isDebug) {
            Throwable t = new Throwable();
            StackTraceElement[] elements = t.getStackTrace();

            String callerClassName = elements[1].getFileName();
            Log.w(LOG_TAG, "[" + callerClassName + "] " + message);
        }
    }

    public static void v(String message, Throwable cause) {
//        if (BuildConfig.DEBUG) {
        if (isDebug) {
            Log.v(LOG_TAG, message, cause);
        }
    }

    public static void v(String message) {
        if (isDebug) {
            Throwable t = new Throwable();
            StackTraceElement[] elements = t.getStackTrace();

            String callerClassName = elements[1].getFileName();
            Log.v(LOG_TAG, "[" + callerClassName + "] " + message);
        }
    }

    public static void dump(Object object) {
        String dump = getObjectDump(object);
        Logger.d("~~~Dump object~~~");
        Logger.d("");
        Logger.d(dump);
        Logger.d("");
        Logger.d("~~~Dump object end~~~");
    }

    public static void printExtras(Bundle bundle) {
        Logger.v("print bundle ++++++++++++++  ++++++++++++++  ++++++++++++++ ");
        Logger.v("");
        Logger.v("");
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            if (value != null) {
                Logger.v(String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }
        Logger.v("");
        Logger.v("");
        Logger.v("End print bundle ++++++++++++++  ++++++++++++++  ++++++++++++++ \"");
    }

    public static String dumpBundle(Bundle bundle) {
        StringBuilder out = new StringBuilder();
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            if (value != null) {
                out.append(String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName())).append('\n');
            }
        }
        return out.toString();
    }

    public static void printIntent(Intent i) {

        Bundle bundle = i.getExtras();
        if (bundle != null) {
//            Set<String> keys = bundle.keySet();
//            Iterator<String> it = keys.iterator();
            Logger.v("Dumping Intent start");
            Logger.v("");
            Logger.v("");
            for (String key : bundle.keySet()) {
//                String key = it.next();
                Logger.v("[" + key + "=" + bundle.get(key) + "]");
            }
            Logger.v("");
            Logger.v("");
            Logger.v("Dumping Intent end");
        }
    }

    public static String getObjectDump(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        sb.append(object.getClass().getSimpleName()).append('{');

        boolean firstRound = true;

        for (Field field : fields) {
            if (!firstRound) {
                sb.append(", ");
            }
            firstRound = false;
            field.setAccessible(true);
            try {
                final Object fieldObj = field.get(object);
                final String value;
                if (null == fieldObj) {
                    value = "null";
                } else {
                    value = fieldObj.toString();
                }
                sb.append(field.getName()).append('=').append('\'')
                        .append(value).append('\'');
            } catch (IllegalAccessException ignore) {
                //this should never happen
            }

        }

        sb.append('}');
        return sb.toString();
    }

    static class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        private final Thread.UncaughtExceptionHandler defaultUEH;

        public DefaultUncaughtExceptionHandler() {
            this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Logger.e("UncaughtException", ex);
            defaultUEH.uncaughtException(thread, ex);
        }
    }
}
