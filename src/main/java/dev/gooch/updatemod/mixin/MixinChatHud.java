package dev.gooch.updatemod.mixin;

import com.google.gson.JsonObject;
import dev.gooch.updatemod.UpdateMod;
import dev.gooch.updatemod.modules.socket.WebsocketClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.FabricUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = ChatHud.class, priority = 1050)
public class MixinChatHud {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(
            method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void addMessage(Text message, MessageSignatureData signature, MessageIndicator indicator, CallbackInfo ci) {

        String msg = message.getString();

        JsonObject json = new JsonObject();
        json.addProperty("type", "chat");
        json.addProperty("data", msg);
        json.addProperty("timestamp", System.currentTimeMillis());

        UpdateMod.ws.send(json.toString());
    }
}
