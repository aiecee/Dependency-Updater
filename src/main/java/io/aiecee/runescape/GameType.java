package io.aiecee.runescape;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Date: 30/04/2014
 * Time: 10:43
 *
 * @author Matt Collinge
 */
public enum GameType {
    OLD_SCHOOL("http://oldschool1.runescape.com/jav_config.ws"),
    EVOLUTION_OF_COMBAT("http://world1.runescape.com/jav_config.ws");

    private final String configUrl;

    private GameType(String configUrl) {
        this.configUrl = configUrl;
    }

    public URL getURL() {
        try {
            return new URL(configUrl);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
