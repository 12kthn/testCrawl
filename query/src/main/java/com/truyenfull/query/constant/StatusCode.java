package com.truyenfull.query.constant;

import org.springframework.http.HttpStatus;

public enum StatusCode {
    SUCCESS(HttpStatus.OK.value()),
    NOT_FOUND(HttpStatus.NOT_FOUND.value());

    private int value;

    StatusCode(int value) {
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
