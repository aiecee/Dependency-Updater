package io.aiecee.analyser.base;

import io.aiecee.runescape.GameType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 30/04/2014
 * Time: 13:36
 *
 * @author Matt Collinge
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Analyser {
    String className();
    GameType type();
    Class<? extends AnalyserBase>[] dependencies();
}
