package io.aiecee.runescape.base;

import io.aiecee.runescape.GameDefinition;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.jar.JarFile;

/**
 * Date: 30/04/2014
 * Time: 10:43
 *
 * @author Matt Collinge
 */
public abstract class GameContainer {

    private final Map<String, ClassNode> classNodeMap;
    private JarFile jarFile;

    public GameContainer(GameDefinition definition) {
        try {
            URL jarURL = new URL("jar:" + definition.parameter("codebase") + definition.parameter("initial_jar") + "!/");
            jarFile = ((JarURLConnection) jarURL.openConnection()).getJarFile();
        } catch (IOException ignored) {
        }
        classNodeMap = loadClasses(definition);
    }

    public final ClassNode getClassNode(String name) {
        return classNodeMap.get(name);
    }

    public final ClassNode[] getAllClassNodes() {
        Collection<ClassNode> classNodes = classNodeMap.values();
        return classNodes.toArray(new ClassNode[classNodes.size()]);
    }

    protected final JarFile getJarFile() {
        return jarFile;
    }

    protected abstract Map<String, ClassNode> loadClasses(GameDefinition definition);
}
