package com.example.studyroom.common;

import lombok.Getter;

/**
 * 自定义业务异常类
 */
@Getter
public class BaseException extends RuntimeException {
    private final Integer code;

    public BaseException(String msg) {
        super(msg);
        this.code = 500;
    }

    public BaseException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}
