package com.wjy.vo;

import java.io.Serializable;

public class Result<T> implements Serializable{
	private static final long serialVersionUID = -2133812360249329853L;
	public static final int SUCCESS=0;
	public static final int FAILED=1;
	public static final String SUCCESS_MSG="success";
	public static final String FAILED_MSG="failed";
	private int code;
    private String message;
    private T data;
    private long count;
    
    public Result(int code, String message) {
    	this(code, message,null,0);
    }
    public Result(int code, String message,T data,long count) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.count=count;
    }
    
    public static <T> Result<T> success() {
        return success(SUCCESS_MSG,null,0);
    }
    public static <T> Result<T> success(String message) {
        return success(message,null,0);
    }
    public static <T> Result<T> success(String message,T data) {
        return success(message,data,data!=null?1:0);
    }
    public static <T> Result<T> success(T data,long count) {
        return success(SUCCESS_MSG,data,count);
    }
    public static <T> Result<T> success(String message,T data,long count) {
        return new Result<T>(SUCCESS,message,data,count);
    }
    public static <T> Result<T> failed() {
        return failed(FAILED_MSG,null,0);
    }
    public static <T> Result<T> failed(String message) {
        return failed(message,null,0);
    }
    public static <T> Result<T> failed(String message,T data) {
        return failed(message,data,data!=null?1:0);
    }
    public static <T> Result<T> failed(String message,T data,long count) {
        return new Result<T>(FAILED,message,data,count);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

    
    

}
