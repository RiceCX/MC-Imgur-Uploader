package dev.ricecx.screenshotter;

import dev.ricecx.screenshotter.hosts.impl.ImgurHandler;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class Config {
    public Configuration config;

    public String accessToken;

    public Config(File configFIle) {
        FMLCommonHandler.instance().bus().register(this);
        this.config = new Configuration(configFIle);
        this.config.load();
        this.config.load();
        loadConfig();
        if(this.config.hasChanged())
            this.config.save();
    }

    private void loadConfig() {
        this.accessToken = this.config.get("imgur", "accessToken", "").getString();
     //   this.config.get("imgur", "refreshToken", "");
     //   this.config.get("imgur", "expiresIn", 0);


        if(accessToken != null)
            ImgurHandler.getINSTANCE().setAccessToken(accessToken);

        System.out.println(accessToken);
    }

    public void setImgurCredentials(String accessToken, String refreshToken, Long expiresIn) {
        this.config.get("imgur", "accessToken", "").set(accessToken);
        this.config.get("imgur", "refreshToken", "").set(refreshToken);
        this.config.get("imgur", "expiresIn", 0).set(expiresIn);

        this.config.save();
    }

    public void flushCredentials() {

    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent evt) {
        if(evt.modID.equals(ScreenShotter.MODID)) loadConfig();
    }
}