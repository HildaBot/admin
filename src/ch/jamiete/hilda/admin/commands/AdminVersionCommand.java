package ch.jamiete.hilda.admin.commands;

import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import ch.jamiete.hilda.plugins.HildaPlugin;
import ch.jamiete.hilda.plugins.PluginData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class AdminVersionCommand extends ChannelSubCommand {
    AdminVersionCommand(Hilda hilda, ChannelSeniorCommand senior) {
        super(hilda, senior);

        this.setName("version");
        this.setAliases(Collections.singletonList("versions"));
        this.setDescription("Lists the versions of plugins and the underlying architecture.");
    }

    @Override
    public void execute(Message message, String[] arguments, String label) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Versions");

        try {
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("version.properties"));
            String ver = properties.getProperty("version");

            if (ver != null) {
                eb.addField("Hilda version:", ver, true);
            }
        } catch (Exception e) {
            // Ignore
        }

        for (HildaPlugin plugin : this.hilda.getPluginManager().getPlugins()) {
            PluginData data = plugin.getPluginData();
            eb.addField(data.getName(), "Version: " + data.getVersion() + "\nAuthor: " + data.getAuthor(), true);
        }

        this.reply(message, eb.build());
    }
}
