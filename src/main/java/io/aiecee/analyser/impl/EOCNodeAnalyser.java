package io.aiecee.analyser.impl;

import io.aiecee.analyser.base.Analyser;
import io.aiecee.analyser.base.AnalyserBase;
import io.aiecee.runescape.GameType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ListIterator;

/**
 * Date: 30/04/2014
 * Time: 15:31
 *
 * @author Matt Collinge
 */
@Analyser(className = "Node", type = GameType.EVOLUTION_OF_COMBAT, dependencies = {})
public class EOCNodeAnalyser extends AnalyserBase {

    @Override
    public boolean canRun(ClassNode node) {
        int ownType = 0, longType = 0;
        if (!node.superName.contains("Object"))
            return false;
        for (FieldNode fn : (Iterable<FieldNode>) node.fields) {
            if ((fn.access & Opcodes.ACC_STATIC) == 0) {
                if (fn.desc.equals(String.format("L%s;", node.name)))
                    ownType++;
                if (fn.desc.equals("J"))
                    longType++;
            }
        }
        return ownType == 2 && longType == 1;
    }

    @Override
    public boolean run(ClassNode node) {
        ListIterator<FieldNode> fnIt = node.fields.listIterator();
        int i = 0;
        while (fnIt.hasNext()) {
            FieldNode fn = fnIt.next();
            if ((fn.access & Opcodes.ACC_STATIC) == 0) {
                if (fn.desc.equals(String.format("L%s;", node.name))) {
                    addField(i++ == 0 ? "next" : "previous", fn.name, fn.desc, fn.access);

                }
                if (fn.desc.equals("J"))
                    addField("uid", fn.name, fn.desc, fn.access);
            }
        }
        return true;
    }
}
