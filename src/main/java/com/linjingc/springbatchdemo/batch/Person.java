package com.linjingc.springbatchdemo.batch;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 实体
 */
@Data
@AllArgsConstructor
public class Person {

    private String lastName;
    private String firstName;
}