package dev.ricecx.screenshotter;


import com.google.gson.JsonObject;
import dev.ricecx.screenshotter.commands.DeleteSSCommand;
import dev.ricecx.screenshotter.commands.LastSSCommand;
import dev.ricecx.screenshotter.hosts.ImageHost;
import dev.ricecx.screenshotter.hosts.impl.ImgurHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.*;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import java.awt.image.BufferedImage;

@Mod(modid = ScreenShotter.MODID, name = ScreenShotter.VERSION)
public class ScreenShotter {
    public static final String MODID = "screenshotter";
    public static final String VERSION = "1.0";

    public static Config config;

    public static KeyBinding screenshotKey;

    public static ImgurHandler imgur;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        if (evt.getSide().isClient()) {
            imgur = ImgurHandler.getINSTANCE();
            config = new Config(evt.getSuggestedConfigurationFile());

            MinecraftForge.EVENT_BUS.register(this);
            MinecraftForge.EVENT_BUS.register(config);
            FMLCommonHandler.instance().bus().register(this);
            ClientCommandHandler.instance.registerCommand(new LastSSCommand());
            ClientCommandHandler.instance.registerCommand(new DeleteSSCommand());
            screenshotKey = new KeyBinding("rst.key.upload", 88, "key.categories.misc");
            new LocalServer().start();
            ClientRegistry.registerKeyBinding(screenshotKey);
        } else {
            FMLLog.bigWarning("Don't load screenshotter on the server :P");
        }
    }

    @SubscribeEvent
    public void onKeyBindPress(InputEvent.KeyInputEvent evt) {
        if(screenshotKey.isPressed()) {
            if(!imgur.hasAccessToken()) {
                Utils.sendMessageNoPrefix(new ChatComponentText(EnumChatFormatting.BLUE + "Click to log into imgur."));
                Minecraft.getMinecraft().thePlayer.addChatMessage(ForgeHooks.newChatWithLinks(imgur.getEndpoint("http://localhost:9383/success"))); // Just for logs
            } else {
                BufferedImage img = ScreenshotHandler.takeScreenshoot(Utils.getScreenSize().x, Utils.getScreenSize().y, Minecraft.getMinecraft().getFramebuffer());
                ImgurHandler.getINSTANCE().upload(img, ImageHost.UPLOAD_METHOD.ACCOUNT, new Runnable() {
                    @Override
                    public void run() {

                        JsonObject obj = ImgurHandler.getINSTANCE().getLastUploadData();
                        if(obj == null) {
                            Utils.sendMessage("There was an error uploading your screenshot.");
                            return;
                        }
                        JsonObject data = ImgurHandler.getINSTANCE().getLastUploadData().getAsJsonObject("data");
                        String link = data.get("link").getAsString();
                        Utils.sendMessage(EnumChatFormatting.GREEN + "Image uploaded to Imgur.");
                        IChatComponent view = ForgeHooks.newChatWithLinks(EnumChatFormatting.GOLD + "[View] ");
                        IChatComponent copy = ForgeHooks.newChatWithLinks(EnumChatFormatting.GOLD + "[Copy] ");
                        IChatComponent delete = ForgeHooks.newChatWithLinks(EnumChatFormatting.GOLD + "[Delete] ");

                        view.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
                        copy.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/getlastss"));
                        delete.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deletess " + data.get("deletehash").getAsString()));
                        ChatComponentText txt = new ChatComponentText(EnumChatFormatting.GRAY + "> ");
                        txt.appendSibling(view).appendSibling(copy).appendSibling(delete);
                        Minecraft.getMinecraft().thePlayer.addChatMessage(txt);

                        ChatComponentText jartexStuff = generateJartexCommands();

                        Minecraft.getMinecraft().thePlayer.addChatMessage(jartexStuff);
                    }
                });
            }
        }
    }



    private ChatComponentText generateJartexCommands() {
        ChatComponentText txt = new ChatComponentText(EnumChatFormatting.GRAY + "> ");

        IChatComponent gmrept = ForgeHooks.newChatWithLinks(EnumChatFormatting.GOLD + "[Gameplay Report] ");
        IChatComponent chatrept = ForgeHooks.newChatWithLinks(EnumChatFormatting.GOLD + "[Chat Report] ");

        gmrept.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://jartexnetwork.com/form/12/select"));
        chatrept.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://jartexnetwork.com/form/11/select"));

        String link = ImgurHandler.getINSTANCE().getLastUploadData().getAsJsonObject("data").get("link").getAsString();

        ScreenshotHandler.addToClipboard(link);


        txt.appendSibling(gmrept).appendSibling(chatrept);
        return txt;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt) {

    }

}
