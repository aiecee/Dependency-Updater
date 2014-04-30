package io.aiecee.runescape.impl;

import io.aiecee.runescape.GameDefinition;
import io.aiecee.runescape.base.GameContainer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.*;
import java.util.zip.GZIPInputStream;

/**
 * Date: 30/04/2014
 * Time: 10:49
 *
 * @author Matt Collinge
 */
public class EOCGameContainer extends GameContainer {

    private static int[] charSet;

    static {
        int index;
        charSet = new int[128];
        for (index = 0; charSet.length > index; ++index) {
            charSet[index] = -1;
        }
        for (index = 65; index <= 90; ++index) {
            charSet[index] = index - 65;
        }
        for (index = 97; 122 >= index; ++index) {
            charSet[index] = index + 26 - 97;
        }
        for (index = 48; index <= 57; ++index) {
            charSet[index] = 4 + index;
        }
        final int[] var2 = charSet;
        charSet[43] = 62;
        var2[42] = 62;
        final int[] var1 = charSet;
        charSet[47] = 63;
        var1[45] = 63;
    }

    public EOCGameContainer(GameDefinition definition) {
        super(definition);
    }

    private byte[] toByte(final String key) {
        final int keyLength = key.length();
        if (keyLength == 0) {
            return new byte[0];
        } else {
            int unscrambledLength;
            final int lengthMod = -4 & keyLength + 3;
            unscrambledLength = lengthMod / 4 * 3;
            if (keyLength <= lengthMod - 2 || charIndex(key.charAt(lengthMod - 2)) == -1) {
                unscrambledLength -= 2;
            } else if (keyLength <= lengthMod - 1 || -1 == charIndex(key.charAt(lengthMod - 1))) {
                --unscrambledLength;
            }

            final byte[] keyBytes = new byte[unscrambledLength];
            unscramble(keyBytes, 0, key);
            return keyBytes;
        }
    }

    private int charIndex(final char character) {
        return character >= 0 && character < charSet.length ? charSet[character] : -1;
    }

    private int unscramble(final byte[] bytes, int offset, final String key) {
        final int start = offset;
        final int keyLength = key.length();
        int pos = 0;

        int readStart;
        int readOffset;
        while (true) {
            if (keyLength > pos) {
                final int currentChar = charIndex(key.charAt(pos));

                final int pos_1 = keyLength > pos + 1 ? charIndex(key.charAt(pos + 1)) : -1;
                final int pos_2 = pos + 2 < keyLength ? charIndex(key.charAt(2 + pos)) : -1;
                final int pos_3 = keyLength > pos + 3 ? charIndex(key.charAt(3 + pos)) : -1;
                bytes[offset++] = (byte) (pos_1 >>> 4 | currentChar << 2);
                if (pos_2 != -1) {
                    bytes[offset++] = (byte) (pos_1 << 4 & 240 | pos_2 >>> 2);
                    if (pos_3 != -1) {
                        bytes[offset++] = (byte) (192 & pos_2 << 6 | pos_3);
                        pos += 4;
                        continue;
                    }
                }
            }

            readOffset = offset;
            readStart = start;
            break;
        }

        return readOffset - readStart;
    }

    protected final byte[] read(InputStream in) throws IOException {
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    @Override
    protected Map<String, ClassNode> loadClasses(GameDefinition definition) {
        Map<String, ClassNode> classes = new HashMap<>();
        try {
            byte[] innerPack = null;
            JarFile file = getJarFile();
            Enumeration<JarEntry> enumeration = file.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                if (entry.getName().equals("inner.pack.gz")) {
                    innerPack = read(file.getInputStream(entry));
                    break;
                }
            }
            if (innerPack == null) {
                return null;
            }
            SecretKeySpec secretKeySpec = new SecretKeySpec(toByte(definition.parameter("0")), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(toByte(definition.parameter("-1"))));
            byte[] unscrambledInnerPack = cipher.doFinal(innerPack);

            Pack200.Unpacker unpacker = Pack200.newUnpacker();
            ByteArrayOutputStream out = new ByteArrayOutputStream(0x500000);
            JarOutputStream jarOut = new JarOutputStream(out);
            GZIPInputStream gzipIn = new GZIPInputStream(new ByteArrayInputStream(unscrambledInnerPack));
            unpacker.unpack(gzipIn, jarOut);

            JarInputStream in = new JarInputStream(new ByteArrayInputStream(out.toByteArray()));
            JarEntry entry;
            while ((entry = in.getNextJarEntry()) != null) {
                String entryName = entry.getName();
                if (entryName.endsWith(".class")) {
                    ClassReader classReader = new ClassReader(read(in));
                    ClassNode classNode = new ClassNode();
                    classReader.accept(classNode, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    classes.put(classNode.name, classNode);
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return classes;
    }
}
