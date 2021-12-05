package dev.ricecx.screenshotter.hosts.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.ricecx.screenshotter.Config;
import dev.ricecx.screenshotter.ScreenShotter;
import dev.ricecx.screenshotter.Utils;
import dev.ricecx.screenshotter.hosts.ImageHost;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ImgurHandler extends ImageHost {


    private static ImgurHandler INSTANCE;

    private JsonObject lastUploadData = null;

    private String accessToken = null;

    private static final String CLIENTID = "00cb1ba2a7af74c";
    private static final String CLIENT_SECRET = "665627cab72a90fa1149055956f0f829ae7db97e";
    private static final String RESP_TYPE = "token";

    private static final String ENDPOINT = "https://api.imgur.com/oauth2/authorize?client_id="+ CLIENTID + "&response_type="+ RESP_TYPE +"&state=%s";
    public static final String CALLBACK = "http://localhost:9383";

    private ImgurHandler() {
        super("imgur");
        INSTANCE = this;
    }

    @Override
    public boolean upload(BufferedImage image, UPLOAD_METHOD method, Runnable callback) {

        HttpURLConnection conn = null;
        ByteArrayOutputStream imageByteArray = null;
        OutputStreamWriter wr = null;
        BufferedReader in = null;
        try {
            URL url = new URL("https://api.imgur.com/3/image");
            imageByteArray = new ByteArrayOutputStream();
            ImageIO.write(image, "png", imageByteArray);
            imageByteArray.flush();
            byte[] imageInByte = imageByteArray.toByteArray();
            String encoded = Base64.encodeBase64String(imageInByte);
            String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(encoded, "UTF-8");

            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));




            this.lastUploadData = (new JsonParser()).parse(in).getAsJsonObject();
            callback.run();
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            accessToken = null;
            return false;
        } finally {
            IOUtils.close(conn);
            IOUtils.closeQuietly(wr);
            IOUtils.closeQuietly(imageByteArray);
            IOUtils.closeQuietly(in);
        }
    }

    @Override
    public boolean deleteLast() {
        return false;
    }

    @Override
    public String getLink() {
        return null;
    }

    public boolean hasAccessToken() {
        return accessToken != null;
    }

    public String getEndpoint(String t) {
        return String.format(ENDPOINT, t);
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public static ImgurHandler getINSTANCE() {
        if(INSTANCE == null) INSTANCE = new ImgurHandler();
        return INSTANCE;
    }


    public String getToken() {
        return accessToken;
    }
    public JsonObject getLastUploadData() {
        return lastUploadData;
    }

    public void login(String token, long expiresIn, String refresh, String type) {
        accessToken = token;

        ScreenShotter.config.setImgurCredentials(token, refresh, expiresIn);

        Utils.sendMessage("You are now logged into imgur");
    }
}
