package com.dms_uz.rtubase.controller;

public class RestPreconditions {
    public static <T> T checkFound(T resource) {
        if (resource == null) {
            System.out.println("error");
        }
        return resource;
    }
}