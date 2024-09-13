package com.project.uber.uberApp.advices;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {

    private LocalDateTime timeStamp;
    private T data;
    private ApiError error;

    //Default Constructor
    public ApiResponse()
    {
        this.timeStamp = LocalDateTime.now();
    }

    //parameterized Constructor
    public ApiResponse(T data)
    {
        this();  // it will call default constructor
        this.data = data;
    }

    public ApiResponse(ApiError error)
    {
        this();
        this.error = error;
    }


}
