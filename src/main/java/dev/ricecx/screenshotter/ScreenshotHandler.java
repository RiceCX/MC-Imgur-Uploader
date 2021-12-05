package dev.ricecx.screenshotter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;

public class ScreenshotHandler {
    private static IntBuffer pixelBuffer;
    private static int[] pixelData;

    private static final File minecraftDir = Minecraft.getMinecraft().mcDataDir;

    public static BufferedImage takeScreenshoot(int w, int h, Framebuffer buffer) {
        if(OpenGlHelper.isFramebufferEnabled()) {
            w = buffer.framebufferTextureWidth;
            h = buffer.framebufferTextureHeight;
        }

        int pixels = w * h;

        if(pixelBuffer == null || pixelBuffer.capacity() < pixels) {
            pixelBuffer = BufferUtils.createIntBuffer(pixels);
            pixelData = new int[pixels];
        }

        GL11.glPixelStorei(3333, 1);
        GL11.glPixelStorei(3317, 1);
        pixelBuffer.clear();
        if(OpenGlHelper.isFramebufferEnabled()) {
            GL11.glBindTexture(3553, buffer.framebufferTexture);
            GL11.glGetTexImage(3553, 0, 32993, 33639, pixelBuffer);
        } else
            GL11.glReadPixels(0,0, w, h,32993,33639, pixelBuffer);

        pixelBuffer.get(pixelData);

        TextureUtil.processPixelValues(pixelData, w, h);
        BufferedImage image = new BufferedImage(w, h, 1);
        image.setRGB(0,0, w, h, pixelData, 0, w);

        return image;
    }

    public static void addToClipboard(String str) {
        StringSelection selection = new StringSelection(str);

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }
}
