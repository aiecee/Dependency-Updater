package io.aiecee;

import flexjson.JSONSerializer;
import io.aiecee.analyser.Cache;
import io.aiecee.analyser.base.Analyser;
import io.aiecee.analyser.base.AnalyserBase;
import io.aiecee.deobber.base.DeObberBase;
import io.aiecee.runescape.GameDefinition;
import org.objectweb.asm.tree.ClassNode;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Date: 30/04/2014
 * Time: 13:30
 *
 * @author Matt Collinge
 */
public final class Updater {

    private final GameDefinition gameDefinition;
    private final Map<Class<? extends AnalyserBase>, AnalyserBase> analysers;
    private final Map<Class<? extends DeObberBase>, DeObberBase> deobbers;
    private Cache cache;

    public Updater(GameDefinition gameDefinition) {
        this.gameDefinition = gameDefinition;
        analysers = loadAnalysers();
        deobbers = loadDeObbers();
        this.cache = new Cache();
    }

    private Map<Class<? extends AnalyserBase>, AnalyserBase> loadAnalysers() {
        Reflections reflections = new Reflections("io.aiecee.analyser.impl");
        Set<Class<? extends AnalyserBase>> analyserClasses = reflections.getSubTypesOf(AnalyserBase.class);
        Map<Class<? extends AnalyserBase>, AnalyserBase> instantiatedAnalysers = new HashMap<>();
        for (Class<? extends AnalyserBase> analyserClass : analyserClasses) {
            Analyser analyser = analyserClass.getAnnotation(Analyser.class);
            if (analyser == null || analyser.type() != gameDefinition.gameType())
                continue;
            try {
                AnalyserBase analyserBase = analyserClass.newInstance();
                analyserBase.setGameContainer(gameDefinition.gameContainer());
                instantiatedAnalysers.put(analyserClass, analyserBase);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return instantiatedAnalysers;
    }

    private Map<Class<? extends DeObberBase>, DeObberBase> loadDeObbers() {
        Reflections reflections = new Reflections("io.aiecee.deobber.impl");
        Set<Class<? extends DeObberBase>> deobberClasses = reflections.getSubTypesOf(DeObberBase.class);
        Map<Class<? extends DeObberBase>, DeObberBase> instantiatedDeObbers = new HashMap<>();
        for (Class<? extends DeObberBase> deobberClass : deobberClasses) {
            try {
                instantiatedDeObbers.put(deobberClass, deobberClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return instantiatedDeObbers;
    }

    public void run()  {
        runDeObbers();
        runAnalysers();
        print();
    }

    public void runAnalysers() {
        for (AnalyserBase analyserBase : analysers.values()) {
            boolean canRun = true;
            Analyser analyser = analyserBase.getClass().getAnnotation(Analyser.class);
            for (Class<? extends AnalyserBase> analyserClass: analyser.dependencies()) {
                if (!analysers.get(analyserClass).hasRun()) {
                    canRun = false;
                    break;
                }
            }
            if (canRun) {
                for (ClassNode classNode : gameDefinition.gameContainer().getAllClassNodes()) {
                    if (analyserBase.execute(classNode)) {
                        cache.classes.add(analyserBase.classCache(classNode));
                        break;
                    }
                }
            }
        }
    }

    public void runDeObbers() {
        for (ClassNode classNode : gameDefinition.gameContainer().getAllClassNodes()) {
            for (DeObberBase deObberBase : deobbers.values()) {
                deObberBase.execute(classNode);
            }
        }
    }

    public void print() {
        System.out.println(new JSONSerializer().exclude("*.class").deepSerialize(cache));
    }

}
