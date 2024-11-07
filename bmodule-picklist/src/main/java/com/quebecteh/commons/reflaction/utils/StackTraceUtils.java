package com.quebecteh.commons.reflaction.utils;

public class StackTraceUtils {
	
	 public static String getCallerMethodName() {
	        // Obtém o stack trace atual
	        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

	      
	        // Verifica se há elementos suficientes no stack trace
	        if (stackTrace.length >= 3) {	
	            return stackTrace[3].getMethodName();
	        } else {
	            return "undefined";
	        }
	    }

}
