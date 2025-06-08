package dev.beecube31.crazyae2.core;

import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
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
    private int delay = 50;
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
        if (!this.playerNotified && this.thread.shouldNotify()) {
            this.playerNotified = true;
            MinecraftForge.EVENT_BUS.unregister(this);
            if (this.thread.getVersion().equals(ModVersion.get())) {
                return;
            }

            if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
                TextComponentBase updateString = new TextComponentString(CrazyAEGuiText.DOWNLOAD_LINK.getLocalWithSpaceAtEnd());
                updateString.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.thread.getDownload()));

                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(
                        CrazyAEGuiText.UPDATE_FOUND.getLocalWithSpaceAtEnd()
                        + this.thread.getVersion()
                        + this.thread.getDescription()
                ));
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(updateString);
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(CrazyAEGuiText.DISABLE_UPDATES_TIP.getLocalWithSpaceAtEnd()));
            }
        }
    }

    public static class UpdateCheckThread extends Thread {
        private String version = null;
        private String desc = "";
        private boolean complete = false;
        private boolean shouldNotify = false;

        private String download = null;

        public void run() {
            CrazyAE.logger().info("[Update Checker] - Checking for update...");
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

                        if (line.contains("desc")) {
                            this.desc = value;
                            continue;
                        }

                        if (line.contains("link")) {
                            this.download = value;
                        }
                    }
                }
                if (this.download != null && this.version != null) {
                    this.complete = true;

                    if (this.version.equals(ModVersion.get())) {
                        CrazyAE.logger().info("[Update Checker] - Mod is up to date");
                        return;
                    }

                    this.shouldNotify = true;
                    CrazyAE.logger().info("[Update Checker] - Update found: {} : {}", this.version, this.download);
                }

                CrazyAE.logger().info("[Update Checker] - Check success. Actual version: {}", getVersion());
            } catch (Exception e) {
                CrazyAE.logger().warn("[Update Checker] - Check failed: {}", e.getMessage());
            }
        }

        public String getVersion() {
            return this.version;
        }

        public String getDescription() {
            return this.desc;
        }

        public boolean shouldNotify() {
            return this.shouldNotify;
        }


        public String getDownload() {
            return this.download;
        }

        public boolean isComplete() {
            return this.complete;
        }

    }
}
