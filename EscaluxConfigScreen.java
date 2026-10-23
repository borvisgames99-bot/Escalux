package com.qevro.escalux.client;

import com.qevro.escalux.config.EscaluxConfig;
import com.qevro.escalux.compat.CreateCompat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

public class EscaluxConfigScreen extends Screen {
    private final Screen parent;
    private Button createToggle;
    public EscaluxConfigScreen(Screen parent) {
        super(Component.literal("Escalux Settings"));
        this.parent = parent;
    }
    @Override protected void init() {
        boolean enabled = EscaluxConfig.CREATE_INTEGRATION_BETA.get();
        createToggle = Button.builder(toggleLabel(enabled), b -> {
            boolean next = !EscaluxConfig.CREATE_INTEGRATION_BETA.get();
            EscaluxConfig.CREATE_INTEGRATION_BETA.set(next);
            createToggle.setMessage(toggleLabel(next));
        }).bounds(width / 2 - 115, height / 2 - 10, 230, 24).build();
        addRenderableWidget(createToggle);
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(width / 2 - 100, height / 2 + 35, 200, 20).build());
    }
    private Component toggleLabel(boolean enabled) {
        String status = enabled ? "ON" : "OFF";
        String availability = CreateCompat.isCreateLoaded() ? "" : " (Create not installed)";
        return Component.literal("Интеграция с Create (BETA): " + status + availability);
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        graphics.drawCenteredString(font, title, width / 2, height / 2 - 55, 0xFFFFFF);
        graphics.drawCenteredString(font, "Escalux experimental options", width / 2, height / 2 - 35, 0xAAAAAA);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
    @Override public void onClose() {
        EscaluxConfig.SPEC.save();
        minecraft.setScreen(parent);
    }
}
