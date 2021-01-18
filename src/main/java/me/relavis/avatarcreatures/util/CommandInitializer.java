package me.relavis.avatarcreatures.util;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import me.relavis.avatarcreatures.AvatarCreatures;
import me.relavis.avatarcreatures.commands.AppaCommand;

public class CommandInitializer {

    @Getter
    PaperCommandManager manager = new PaperCommandManager(AvatarCreatures.getInstance());

    public CommandInitializer() {
        manager.registerCommand(new AppaCommand());
        manager.enableUnstableAPI("help");
    }
}
