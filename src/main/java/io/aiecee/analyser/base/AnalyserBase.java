package io.aiecee.analyser.base;

import io.aiecee.analyser.ClassCache;
import io.aiecee.analyser.FieldCache;
import io.aiecee.analyser.MethodCache;
import io.aiecee.runescape.base.GameContainer;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 30/04/2014
 * Time: 13:34
 *
 * @author Matt Collinge
 */
public abstract class AnalyserBase {

    private GameContainer gameContainer;
    private boolean hasRun = false;
    private List<FieldCache> fields = new ArrayList<>();
    private List<MethodCache> methods = new ArrayList<>();


    protected final GameContainer gameContainer() {
        return gameContainer;
    }

    public final void setGameContainer(GameContainer gameContainer) {
        this.gameContainer = gameContainer;
    }

    public final void addField(String name, String realName, String returnType, int access) {
        fields.add(new FieldCache(name, realName, returnType, access));
    }

    public final void addMethod(String name, String realName, String returnType, int access) {
        methods.add(new MethodCache(name, realName, returnType, access));
    }

    public final ClassCache classCache(ClassNode classNode) {
        Analyser analyser = getClass().getAnnotation(Analyser.class);
        ClassCache classCache = new ClassCache(analyser.className(), classNode.name);
        for (FieldCache field : fields) {
            classCache.addField(field);
        }
        for (MethodCache method : methods) {
            classCache.addMethod(method);
        }
        return classCache;
    }

    public final boolean hasRun() {
        return hasRun;
    }

    public final boolean execute(ClassNode node) {
        if (canRun(node)) {
            hasRun = run(node);
        }
        return hasRun;
    }

    public abstract boolean canRun(ClassNode node);

    public abstract boolean run(ClassNode node);

}
