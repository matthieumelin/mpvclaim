package com.geeklegend.utils;

public class NumberUtils {
	public static enum Result {
	    POSITIVE, NEGATIVE, ZERO
	}
	
	public static Result bySignum(Integer integer) {
	    int result = Integer.signum(integer);
	    if (result == 1) {
	        return Result.POSITIVE;
	    } else if (result == -1) {
	        return Result.NEGATIVE;
	    }
	    return Result.ZERO;
	}
}
