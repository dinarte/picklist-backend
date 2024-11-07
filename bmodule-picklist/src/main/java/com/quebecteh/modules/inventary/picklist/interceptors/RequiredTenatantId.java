package com.quebecteh.modules.inventary.picklist.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * This annotation enforces that a resource must receive a valid `tenantId` 
 * through the URL. If the `tenantId` is missing or invalid, an exception 
 * will be thrown.
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequiredTenatantId {
}