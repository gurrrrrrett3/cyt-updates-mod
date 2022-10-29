package dev.gooch.updatemod;

import dev.gooch.updatemod.modules.socket.WebsocketClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

@Environment(EnvType.CLIENT)
public class UpdateMod implements ClientModInitializer {

    public static final String MOD_ID = "updatemod";

    public static final Logger LOGGER = LoggerFactory.getLogger(UpdateMod.MOD_ID);

    public static WebsocketClient ws;

    /**
     * stolen from <a href="https://github.com/DarkKronicle/AdvancedChatCore/blob/main/src/main/java/io/github/darkkronicle/advancedchatcore/AdvancedChatCore.java#L109-L122">AdvancedChatCore</a>
     * Returns the server address that the client is currently connected to.
     *
     * @return The server address if connected, 'singleplayer' if singleplayer, 'none' if none.
     */
    public static String getServer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isInSingleplayer()) {
            return "singleplayer";
        }
        if (client.getCurrentServerEntry() == null) {
            return "none";
        }
        return client.getCurrentServerEntry().address;
    }

    @Override
    public void onInitializeClient() {

        MinecraftClient client = MinecraftClient.getInstance();

        // run once player joins a server
        ClientPlayConnectionEvents.JOIN.register(
                ((handler, sender, client1) -> {
                    String serverIP = UpdateMod.getServer();
                    if (serverIP.contains("craftyourtown.com") || serverIP.equals("craftyour.town")) {
                        client.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, Text.of("CYT Update Mod"), Text.of("Connecting to data server...")));

                        boolean res = connectToDataServer();

                        if (res) {
                            client.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, Text.of("CYT Update Mod"), Text.of("Connected to data server!")));
                        } else {
                            client.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, Text.of("CYT Update Mod"), Text.of("Failed to connect to data server.")));
                        }
                    }
                }));

        // run once player disconnects
        ClientPlayConnectionEvents.DISCONNECT.register(
                ((handler, sender) -> {
                    if (ws != null) {
                        ws.close();
                    }
                }));


    }

    public static boolean connectToDataServer() {
        try {
            ws = new WebsocketClient(new URI("ws://95.216.205.34:3001/"));
            //ws = new WebsocketClient(new URI("ws://localhost:3001/"));
            ws.connect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}