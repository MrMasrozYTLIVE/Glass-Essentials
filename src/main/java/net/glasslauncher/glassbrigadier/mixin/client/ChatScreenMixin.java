package net.glasslauncher.glassbrigadier.mixin.client;

import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.impl.client.mixinhooks.ChatScreenHooks;
import net.glasslauncher.glassbrigadier.impl.network.GlassBrigadierAutocompletePacket;
import net.minecraft.client.gui.screen.ChatScreen;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.input.Keyboard;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin implements ChatScreenHooks {

    @Shadow protected String text;
    @Unique
    private int currentMessageIndex = -1;
    @Unique
    private String currentMessage = "";

    @Unique
    private List<String> completions;
    @Unique
    private int currentCompletion = 0;

    @Override
    public void glass_Essentials$setCompletions(List<String> completions) {
        this.completions = completions;
    }

    @Inject(method = "keyPressed(CI)V", at = @At("TAIL"))
    void checkKeys(char c, int i, CallbackInfo ci) {
        switch (i) {

            case Keyboard.KEY_TAB:
                if (completions != null) {
                    text = "/" + completions.get((currentCompletion) % completions.size());
                    currentCompletion++;
                }
                String message = text;
                if (!message.isEmpty() && message.charAt(0) == '/') {
                    message = message.substring(1);
                }
                PacketHelper.send(new GlassBrigadierAutocompletePacket(message, message.length()));
                break;

            case Keyboard.KEY_UP:
                if (GlassBrigadier.previousMessages.size() > currentMessageIndex+1) {
                    if (currentMessageIndex == -1)
                        currentMessage = text;
                    text  = GlassBrigadier.previousMessages.get(++currentMessageIndex);
                    invalidateSuggestions();
                }
                break;

            case Keyboard.KEY_DOWN:
                if (currentMessageIndex == 0) {
                    currentMessageIndex = -1;
                    text = currentMessage;
                } else if (currentMessageIndex > 0) {
                    text = GlassBrigadier.previousMessages.get(--currentMessageIndex);
                    invalidateSuggestions();
                }
                break;

        }
    }

    @Unique
    void invalidateSuggestions() {
        glass_Essentials$setCompletions(null);
        currentCompletion = 0;
    }

    @Inject(method = "keyPressed", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/gui/screen/ChatScreen;text:Ljava/lang/String;"))
    void invalidateWhenKeyPressed(char c, int i, CallbackInfo ci) {
        invalidateSuggestions();
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ClientPlayerEntity;sendChatMessage(Ljava/lang/String;)V"))
    void addMessageToQueue(char c, int i, CallbackInfo ci) {
        GlassBrigadier.previousMessages.add(0, text.trim());
    }
}
