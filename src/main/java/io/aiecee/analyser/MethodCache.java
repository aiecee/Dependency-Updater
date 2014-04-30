package io.aiecee.analyser;

/**
 * Date: 30/04/2014
 * Time: 15:01
 *
 * @author Matt Collinge
 */
public class MethodCache {

    public String name;
    public String realName;
    public String returnType;
    public int access;

    public MethodCache(String name, String realName, String returnType, int access) {
        this.name = name;
        this.realName = realName;
        this.returnType = returnType;
        this.access = access;
    }
}
