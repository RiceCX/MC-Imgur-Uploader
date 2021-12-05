package dev.ricecx.screenshotter.hosts;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public abstract class ImageHost {

    public final String hostname;

    public static final HashMap<String, ImageHost> imageHosts = new HashMap<String, ImageHost>();

    public enum UPLOAD_METHOD {
        ANON, ACCOUNT, CUSTOM;
    }

    public ImageHost(String hostname) {

        if(imageHosts.containsKey(hostname)) {
            throw new IllegalArgumentException("image host " + hostname + " has already been registered.");
        }
        this.hostname = hostname;
        imageHosts.put(hostname, this);
    }

    public abstract boolean upload(BufferedImage image, UPLOAD_METHOD method, Runnable run);

    public abstract boolean deleteLast();

    public abstract String getLink();
}
