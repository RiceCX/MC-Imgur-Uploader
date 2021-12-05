package dev.ricecx.screenshotter.commands;

import com.google.gson.JsonObject;
import dev.ricecx.screenshotter.ScreenshotHandler;
import dev.ricecx.screenshotter.Utils;
import dev.ricecx.screenshotter.hosts.impl.ImgurHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class LastSSCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "getlastss";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/getlastsss";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) throws CommandException {
        JsonObject obj = ImgurHandler.getINSTANCE().getLastUploadData();
        if(obj == null) return;
        ScreenshotHandler.addToClipboard(ImgurHandler.getINSTANCE().getLastUploadData().getAsJsonObject("data").get("link").getAsString());
        Utils.sendMessage("Copied to clipboard.");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender p_canCommandSenderUseCommand_1_) {
        return true;
    }
}
