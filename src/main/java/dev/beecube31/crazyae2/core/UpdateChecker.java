package dev.beecube31.crazyae2.core;

import dev.beecube31.crazyae2.common.sync.CrazyAEGuiText;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker {

    private final UpdateCheckThread thread;
    private int delay = 40;
    private boolean playerNotified = false;

    public UpdateChecker() {
        this.thread = new UpdateCheckThread();
        this.thread.start();
    }

    @SubscribeEvent
    public void tickStart(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        if (this.delay > 0) {
            this.delay--;
            return;
        }
        if (!this.playerNotified && this.thread.isComplete()) {
            this.playerNotified = true;
            MinecraftForge.EVENT_BUS.unregister(this);
            if (this.thread.getVersion().equals(ModVersion.get())) {
                CrazyAE.logger().info("[Update Checker] - Mod is up to date");
                return;
            }

            CrazyAE.logger().info("[Update Checker] - Update found: {} : {}", this.thread.getVersion(), this.thread.getDownload());

            if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
                TextComponentBase updateString = new TextComponentString(CrazyAEGuiText.DOWNLOAD_LINK.getLocalWithSpaceAtEnd());
                updateString.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.thread.getDownload()));
                updateString.getStyle().setItalic(true);

                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(CrazyAEGuiText.UPDATE_FOUND.getLocalWithSpaceAtEnd() + this.thread.getVersion()));
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(updateString);
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(CrazyAEGuiText.DISABLE_UPDATES_TIP.getLocalWithSpaceAtEnd()));
            }
        }
    }

    public static class UpdateCheckThread extends Thread {

        private String version = null;
        private boolean complete = false;

        private String download = null;

        public void run() {
            CrazyAE.logger().info("[Update Checker] - Starting update thread.");
            try {
                URL versionURL = new URL("https://raw.githubusercontent.com/beecupbe/crazyae/refs/heads/master/version.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(versionURL.openStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains(":")) {
                        String value = line.substring(line.indexOf(":") + 1);
                        if (line.contains("version")) {
                            this.version = value;
                            continue;
                        }
                        if (line.contains("link")) {
                            this.download = value;
                        }
                    }
                }
                if (this.download != null && this.version != null) {
                    this.complete = true;
                }
                CrazyAE.logger().info("[Update Checker] - Check success. Actual version: {}", getVersion());
            } catch (Exception e) {
                CrazyAE.logger().warn("[Update Checker] - Check Failed: {}", e.getMessage());
            }
        }

        public String getVersion() {
            return this.version;
        }


        public String getDownload() {
            return this.download;
        }

        public boolean isComplete() {
            return this.complete;
        }

    }

}
