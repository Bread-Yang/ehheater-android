package com.xtremeprog.xpgconnect;

import java.lang.Thread;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import android.util.Log;
import java.util.Date;

public class Utils 
{
	private static final String TAG = "xpgcJava";

	public static void PrintLog(String msg) 
	{
		DateFormat dateFormat = new SimpleDateFormat("(HH:mm:ss) ");
		String timeStr = dateFormat.format(Calendar.getInstance().getTime());
		msg = timeStr + msg;

		Log.i(Utils.TAG, msg);
	}

    public static void Sleep(long milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        }
        catch (Exception ex)
        {
        }
    }

    public static String GetFileLineMethod()
    { 
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1]; 
        StringBuffer toStringBuffer = new StringBuffer("[").append( 
        traceElement.getFileName()).append(" | ").append( 
        traceElement.getLineNumber()).append(" | ").append( 
        traceElement.getMethodName()).append("]"); 
        return toStringBuffer.toString(); 
    }

    public static String _FILE_() { 
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1]; 
        return traceElement.getFileName(); 
    } 

    // 当前方法名 
    public static String _FUNC_() { 
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1]; 
        return traceElement.getMethodName(); 
    } 

    // 当前行号 
    public static int _LINE_() { 
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1]; 
        return traceElement.getLineNumber(); 
    } 

    // 当前时间 
    public static String _TIME_() { 
        Date now = new Date(); 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
        return sdf.format(now); 
    }
}
