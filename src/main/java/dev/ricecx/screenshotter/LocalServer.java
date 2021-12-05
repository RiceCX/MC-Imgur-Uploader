package dev.ricecx.screenshotter;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.ricecx.screenshotter.hosts.impl.ImgurHandler;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalServer implements Closeable {

    private HttpServer server;

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", 9383), 0);
            server.createContext("/callback", new CallBackHandler());
            server.createContext("/catchtoken", new TokenHandler());
            server.createContext("/success", new ReadyHandler());
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        server.stop(1);
    }

    private class CallBackHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            String response = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<script>" +
                    "" +
                    "var params = {}, queryString = location.hash.substring(1),\n" +
                    "    regex = /([^&=]+)=([^&]*)/g, m;\n" +
                    "while (m = regex.exec(queryString)) {\n" +
                    "  params[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);\n" +
                    "}\n" +
                    "\n" +
                    "// And send the token over to the server\n" +
                    "var req = new XMLHttpRequest();\n" +
                    "// consider using POST so query isn't logged\n" +
                    "req.open('GET', 'http://' + window.location.host + '/catchtoken?' + queryString, true);\n" +
                    "\n" +
                    "\n" +
                    "req.onreadystatechange = function (e) {\n" +
                    "console.log(e);\n" +
                    "  if (req.readyState == 4) {\n" +
                    "     if(req.status == 200){\n" +
                    "       window.location = params['state']\n" +
                    "   }\n" +
                    "  else if(req.status == 400) {\n" +
                    "        alert('There was an error processing the token.')\n" +
                    "    }\n" +
                    "    else {\n" +
                    "      alert('something else other than 200 was returned\\n' + e)\n" +
                    "    }\n" +
                    "  }\n" +
                    "};\n" +
                    "req.send(null);" +
                    "</script>" +
                    "</html>";

            httpExchange.sendResponseHeaders(200, response.length());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(response.getBytes());
            httpExchange.getResponseBody().write(out.toByteArray());
            out.close();
            httpExchange.close();
        }
    }

    private class TokenHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            Map<String, String> parameters = queryToMap(httpExchange.getRequestURI().getQuery());
            final String token = parameters.get("access_token");
            final String refresh = parameters.get("refresh_token");
            final String type = parameters.get("token_type");

            for (Map.Entry<String, String> stringStringEntry : parameters.entrySet()) {
                System.out.println(stringStringEntry.getKey() + " " + stringStringEntry.getValue());
            }
            ImgurHandler.getINSTANCE().login(token, 0, refresh, type);

            httpExchange.sendResponseHeaders(200, 0);
        }
    }

    private class ReadyHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String title = "Success";

            String message = "You may close this tab";

            String response = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "  <head>\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "    <title>MC SS</title>\n" +
                    "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.5/css/bulma.min.css\">\n" +
                    "    <script defer src=\"https://use.fontawesome.com/releases/v5.3.1/js/all.js\"></script>\n" +
                    "  </head>\n" +
                    "  <body class=\"hero is-dark is-fullheight\">\n" +
                    "  <section class=\"section has-text-centered\">\n" +
                    "    <div class=\"container\">\n" +
                    "      <h1 class=\"title\">\n" +
                    "        " + title + "\n" +
                    "      </h1>\n" +
                    "      <p class=\"subtitle\">\n" +
                    "        " + message + "\n" +
                    "      </p>\n" +
                    "    </div>\n" +
                    "  </section>\n" +
                    "  </body>\n" +
                    "</html>";

            httpExchange.sendResponseHeaders(200, response.length());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(response.getBytes());
            httpExchange.getResponseBody().write(out.toByteArray());
            out.close();
            httpExchange.close();
        }
    }


    private Map<String, String> queryToMap(String s) {
        System.out.println("AAAA " + s);
        Map<String, String> map = new HashMap<String,String>();
        for (String s1 : s.split("&")) {
            String[] entry = s1.split("=");
            map.put(entry[0], entry[1]);
        }

        return map;
    }
}
