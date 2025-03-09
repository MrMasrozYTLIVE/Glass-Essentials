package net.glasslauncher.glassbrigadier.impl.client.mixinhooks;

import java.util.List;

public interface ChatScreenHooks {
    String getMessage();
    void setMessage(String newMessage);
    void glass_Essentials$setCompletions(List<String> completions);
}
