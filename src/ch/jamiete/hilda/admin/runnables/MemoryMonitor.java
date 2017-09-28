package ch.jamiete.hilda.admin.runnables;

import java.awt.Color;
import java.time.Instant;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.admin.AdminPlugin;
import net.dv8tion.jda.core.EmbedBuilder;

public class MemoryMonitor implements Runnable {
    private AdminPlugin plugin;

    public MemoryMonitor(AdminPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!plugin.memory) {
            return;
        }

        Runtime runtime = Runtime.getRuntime();
        long used = runtime.maxMemory() - runtime.freeMemory();
        long total = runtime.maxMemory();
        double percent = ((used * 1.0) / total) * 100;

        Hilda.getLogger().fine("Memory usage currently at " + percent + "%");

        if (percent < 90) {
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Memory warning");
        eb.addField("Per cent memory usage", percent + "%", false);
        eb.setTimestamp(Instant.now());

        if (percent >= 90) {
            eb.setColor(Color.decode("#ff6700"));
            eb.setDescription("**Warning**\nMemory usage is VERY HIGH.");
        }

        if (percent >= 95) {
            eb.setColor(Color.decode("#ce2029"));
            eb.setDescription("**FATAL warning**\nMemory usage is EXTREMELY HIGH. Hilda may cease working imminently.");
        }

        this.plugin.getChannel().sendMessage(eb.build()).queue();
    }

}
