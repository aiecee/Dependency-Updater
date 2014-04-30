package io.aiecee.analyser;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 30/04/2014
 * Time: 15:01
 *
 * @author Matt Collinge
 */
public class ClassCache {

    public String name;
    public String realName;
    public List<FieldCache> fields = new ArrayList<>();
    public List<MethodCache> methods = new ArrayList<>();

    public ClassCache(String name, String realName) {
        this.name = name;
        this.realName = realName;
    }

    public void addField(String name, String realName, String returnType, int access) {
        fields.add(new FieldCache(name, realName, returnType, access));
    }

    public void addField(FieldCache fieldCache) {
        fields.add(fieldCache);
    }

    public void addMethod(MethodCache methodCache) {
        methods.add(methodCache);
    }

    public void addMethod(String name, String realName, String returnType, int access) {
        methods.add(new MethodCache(name, realName, returnType, access));
    }

}
