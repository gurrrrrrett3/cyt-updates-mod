package dev.gooch.updatemod.modules.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class Utils {

    public static void runCommand(String command) {
        try {
            MinecraftClient.getInstance().player.sendCommand(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showClientSideMessage(String text) {
        try {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.sendMessage(Text.of(text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String text) {
        try {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.sendChatMessage(text, Text.of(text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
