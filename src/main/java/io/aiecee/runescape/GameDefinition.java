package io.aiecee.runescape;

import io.aiecee.runescape.base.GameContainer;
import io.aiecee.runescape.impl.EOCGameContainer;
import io.aiecee.runescape.impl.OSGameContainer;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Date: 30/04/2014
 * Time: 09:33
 *
 * @author Matt Collinge
 */
public final class GameDefinition {

    private final Map<String, String> parameters;
    private final GameType gameType;
    private GameContainer gameContainer;

    public GameDefinition(GameType gameType) {
        parameters = new HashMap<>();
        this.gameType = gameType;
        if (loadConfig())
            loadGameContainer();
    }

    public boolean loaded() {
        return gameContainer != null;
    }

    public GameContainer gameContainer() {
        return gameContainer;
    }

    public GameType gameType() {
        return gameType;
    }

    public String parameter(String name) {
        return parameters.get(name);
    }

    private boolean loadConfig() {
        try {
            URL url = gameType.getURL();
            URLConnection uc = url.openConnection();
            uc.addRequestProperty(
                    "Accept",
                    "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
            uc.addRequestProperty("Accept-Charset",
                    "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
            uc.addRequestProperty("Accept-Encoding", "gzip,deflate");
            uc.addRequestProperty("Accept-Language", "en-gb,en;q=0.5");
            uc.addRequestProperty("Connection", "keep-alive");
            uc.addRequestProperty("Host", url.getHost());
            uc.addRequestProperty("Keep-Alive", "300");
            uc.addRequestProperty(
                    "User-Agent",
                    "Mozilla/4.0 (" + System.getProperty("os.name") + " "
                            + System.getProperty("os.version") + ") Java/"
                            + System.getProperty("java.version")
            );
            DataInputStream di = new DataInputStream(uc.getInputStream());
            byte[] buffer = new byte[uc.getContentLength()];
            di.readFully(buffer);
            di.close();
            String page = new String(buffer);
            page = page.replaceAll("param=", "").replaceAll("msg=", "");
            String[] lines = page.split("\n");
            for (String line: lines) {
                int length = line.length();
                int index = line.indexOf('=');
                if (length != 0) {
                    parameters.put(line.substring(0, index), line.substring(index + 1, length));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return !parameters.isEmpty();
    }

    private void loadGameContainer() {
        switch (gameType) {
            case OLD_SCHOOL:
                gameContainer = new OSGameContainer(this);
                break;
            case EVOLUTION_OF_COMBAT:
                gameContainer = new EOCGameContainer(this);
                break;
        }
    }
}
