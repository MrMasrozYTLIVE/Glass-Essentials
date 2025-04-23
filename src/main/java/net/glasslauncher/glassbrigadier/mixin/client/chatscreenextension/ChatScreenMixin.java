package net.glasslauncher.glassbrigadier.mixin.client.chatscreenextension;

import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.impl.client.mixinhooks.ChatScreenHooks;
import net.glasslauncher.glassbrigadier.impl.network.GlassBrigadierAutocompleteRequestPacket;
import net.glasslauncher.mods.gcapi3.api.CharacterUtils;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.input.Keyboard;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.List;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen implements ChatScreenHooks {
    @Unique
    private static final int YELLOW = CharacterUtils.getIntFromColour(Color.YELLOW);
    @Unique
    private static final int BLACK = CharacterUtils.getIntFromColour(Color.BLACK);
    @Unique
    private static final int WHITE = CharacterUtils.getIntFromColour(Color.WHITE);

    @Shadow public String text;
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
                if (completions != null && !completions.isEmpty()) {
                    currentCompletion++;
                    text = "/" + completions.get(currentCompletion % completions.size());
                }
                String message = text;
                if (!message.isEmpty() && message.charAt(0) == '/') {
                    message = message.substring(1);
                }
                PacketHelper.send(new GlassBrigadierAutocompleteRequestPacket(message, message.length()));
                break;

            case Keyboard.KEY_UP:
                if (completions != null && !completions.isEmpty()) {
                    currentCompletion++;
                    text = "/" + completions.get(currentCompletion % completions.size());
                }
                if (GlassBrigadier.PREVIOUS_MESSAGES.size() > currentMessageIndex+1) {
                    if (currentMessageIndex == -1)
                        currentMessage = text;
                    text = GlassBrigadier.PREVIOUS_MESSAGES.get(++currentMessageIndex);
                    invalidateSuggestions();
                }
                break;

            case Keyboard.KEY_DOWN:
                if (completions != null && !completions.isEmpty()) {
                    currentCompletion--;
                    if (currentCompletion < 0) {
                        currentCompletion = completions.size() - 1;
                    }
                    text = "/" + completions.get(currentCompletion % completions.size());
                }
                if (currentMessageIndex == 0) {
                    currentMessageIndex = -1;
                    text = currentMessage;
                } else if (currentMessageIndex > 0) {
                    text = GlassBrigadier.PREVIOUS_MESSAGES.get(--currentMessageIndex);
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

    @Unique
    void revalidateSuggestions() {
        if (completions == null || !text.startsWith("/")) {
            return;
        }
        currentCompletion = 0;
        String noSlash = text.substring(1);
        int noSlashPartSize = noSlash.split(" ").length;
        completions = completions.stream().filter(e -> e.split(" ").length == noSlashPartSize && e.startsWith(noSlash)).toList();
    }

    @Inject(method = "keyPressed", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/gui/screen/ChatScreen;text:Ljava/lang/String;", shift = At.Shift.AFTER))
    void revalidateWhenTextChanges(char c, int i, CallbackInfo ci) {
        revalidateSuggestions();
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ClientPlayerEntity;sendChatMessage(Ljava/lang/String;)V"))
    void addMessageToQueue(char c, int i, CallbackInfo ci) {
        GlassBrigadier.PREVIOUS_MESSAGES.add(0, text.trim());
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderCompletions(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (completions == null || text == null || !text.startsWith("/")) {
            return;
        }

        for (int i = 0; i < completions.size(); i++) {
            String[] lineParts = completions.get(i).split(" ");
            String line = lineParts[lineParts.length - 1];
            int x = textRenderer.getWidth("> " + text) + 4;
            int y = height - 24 - (i * 11);

            fill(x - 1, y - 1, x + textRenderer.getWidth(line) + 1, y + 10, BLACK);
            drawTextWithShadow(textRenderer, line, x, y, i == currentCompletion ? YELLOW : WHITE);
        }
    }
}
