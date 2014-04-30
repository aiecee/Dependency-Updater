package io.aiecee.deobber.base;

import org.objectweb.asm.tree.ClassNode;

/**
 * Date: 30/04/2014
 * Time: 13:49
 *
 * @author Matt Collinge
 */
public abstract class DeObberBase {

    public abstract void execute(ClassNode node);

}
