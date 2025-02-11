package me.axieum.mcmod.authme.impl.gui;

import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import me.axieum.mcmod.authme.api.util.SessionUtils;
import me.axieum.mcmod.authme.impl.AuthMe;
import static me.axieum.mcmod.authme.impl.AuthMe.WIDGETS_TEXTURE;
import static me.axieum.mcmod.authme.impl.AuthMe.getConfig;

/**
 * A screen for choosing a user authentication method.
 */
public class AuthMethodScreen extends Screen
{
    // The parent (or last) screen that opened this screen
    private final Screen parentScreen;
    // A greeting message shown for the current session
    private Text greeting = null;

    /**
     * Constructs a new authentication method choice screen.
     *
     * @param parentScreen parent (or last) screen that opened this screen
     */
    public AuthMethodScreen(Screen parentScreen)
    {
        super(Text.translatable("gui.authme.method.title"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init()
    {
        super.init();
        assert client != null;

        // Set the greeting message
        greeting = Text.translatable(
            "gui.authme.method.greeting",
            Text.literal(SessionUtils.getSession().getUsername()).formatted(Formatting.YELLOW)
        );

        // Add a button for the 'Microsoft' authentication method
        TexturedButtonWidget msButton = new TexturedButtonWidget(
            width / 2 - 20 - 10 - 4, height / 2 - 5, 20, 20,
            0, 0, 20, WIDGETS_TEXTURE, 128, 128,
            button -> {
                if (getConfig().methods.microsoft.isDefaults()) {
                    client.setScreen(new MicrosoftAuthScreen(this, parentScreen));
                } else {
                    AuthMe.LOGGER.warn("Non-default Microsoft authentication URLs are in use!");
                    ConfirmScreen confirmScreen = new ConfirmScreen(
                        accepted -> client.setScreen(accepted ? new MicrosoftAuthScreen(this, parentScreen) : this),
                        Text.translatable("gui.authme.microsoft.warning.title"),
                        Text.translatable("gui.authme.microsoft.warning.body"),
                        Text.translatable("gui.authme.microsoft.warning.accept"),
                        Text.translatable("gui.authme.microsoft.warning.cancel")
                    );
                    client.setScreen(confirmScreen);
                    confirmScreen.disableButtons(40);
                }
            },
            Text.translatable("gui.authme.method.button.microsoft")
        );
        msButton.setTooltip(Tooltip.of(Text.translatable("gui.authme.method.button.microsoft")));
        addDrawableChild(msButton);

        // Add a button for the 'Mojang (or legacy)' authentication method
        TexturedButtonWidget mojangButton = new TexturedButtonWidget(
            width / 2 - 10, height / 2 - 5, 20, 20,
            20, 0, 20, WIDGETS_TEXTURE, 128, 128,
            button -> client.setScreen(new MojangAuthScreen(this, parentScreen)),
            Text.translatable("gui.authme.method.button.mojang")
        );
        mojangButton.setTooltip(Tooltip.of(Text.translatable("gui.authme.method.button.mojang")));
        addDrawableChild(mojangButton);

        // Add a button for the 'Offline' authentication method
        TexturedButtonWidget offlineButton = new TexturedButtonWidget(
            width / 2 + 10 + 4, height / 2 - 5, 20, 20,
            40, 0, 20, WIDGETS_TEXTURE, 128, 128,
            button -> client.setScreen(new OfflineAuthScreen(this, parentScreen)),
            Text.translatable("gui.authme.method.button.offline")
        );
        offlineButton.setTooltip(Tooltip.of(Text.translatable("gui.authme.method.button.offline")));
        addDrawableChild(offlineButton);

        // Add a button to go back
        addDrawableChild(
            ButtonWidget.builder(Text.translatable("gui.back"), button -> close())
                .dimensions(width / 2 - 50, height / 2 + 27, 100, 20)
                .build()
        );
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        assert client != null;

        // Render the background before any widgets
        renderBackground(matrices);

        // Render a title for the screen
        drawCenteredTextWithShadow(matrices, client.textRenderer, title, width / 2, height / 2 - 27, 0xffffff);

        // Render a greeting for the current session
        if (greeting != null) {
            drawCenteredTextWithShadow(matrices, client.textRenderer, greeting, width / 2, height / 2 - 47, 0xa0a0a0);
        }

        // Cascade the rendering
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close()
    {
        if (client != null) client.setScreen(parentScreen);
    }
}
