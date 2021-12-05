package dev.ricecx.screenshotter.commands;

import com.google.gson.JsonParser;
import dev.ricecx.screenshotter.hosts.impl.ImgurHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class DeleteSSCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "deletess";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "deletess";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) throws CommandException {

        System.out.println("deleting hash " + strings[0]);
        delete(strings[0]);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender p_canCommandSenderUseCommand_1_) {
        return true;
    }

    public static void delete(String imgHash) {

        HttpURLConnection conn = null;
        try {
            URL url = new URL("https://api.imgur.com/3/image/" + imgHash);

            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Bearer " + ImgurHandler.getINSTANCE().getToken());

            conn.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(conn);
        }
    }
}
