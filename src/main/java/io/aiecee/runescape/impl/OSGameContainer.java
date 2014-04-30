package io.aiecee.runescape.impl;

import io.aiecee.runescape.GameDefinition;
import io.aiecee.runescape.base.GameContainer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Date: 30/04/2014
 * Time: 10:48
 *
 * @author Matt Collinge
 */
public class OSGameContainer extends GameContainer {

    public OSGameContainer(GameDefinition definition) {
        super(definition);
    }

    @Override
    protected Map<String, ClassNode> loadClasses(GameDefinition definition) {
        Map<String, ClassNode> classes = new HashMap<>();
        try {
            JarFile jarFile = getJarFile();
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                if (entry.getName().endsWith(".class")) {
                    ClassReader classReader = new ClassReader(jarFile.getInputStream(entry));
                    ClassNode classNode = new ClassNode();
                    classReader.accept(classNode, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    classes.put(classNode.name, classNode) ;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }
}
