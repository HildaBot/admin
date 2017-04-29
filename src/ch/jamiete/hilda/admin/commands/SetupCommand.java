package ch.jamiete.hilda.admin.commands;

import java.util.Arrays;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.admin.AdminPlugin;
import ch.jamiete.hilda.commands.ChannelCommand;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class SetupCommand extends ChannelCommand {
    private AdminPlugin plugin;

    public SetupCommand(Hilda hilda, AdminPlugin plugin) {
        super(hilda);

        this.plugin = plugin;

        this.setName("setup");
        this.setAliases(Arrays.asList(new String[] { "init", "initialise", "initialize" }));
        this.setDescription("The initial setup for a non-configured server.");
        this.setMinimumPermission(Permission.ADMINISTRATOR);
        this.setHide(true);
    }

    @Override
    public void execute(Message message, String[] args, String label) {
        if (plugin.getRole() == null || plugin.getChannel() == null) {
            MessageBuilder mb = new MessageBuilder();

            mb.append("Channel: ").append(message.getChannel().getId()).append('\n');
            mb.append("Roles:");

            for (Role role : message.getGuild().getRoles()) {
                mb.append('\n');
                mb.append(role.getName()).append(' ').append(role.getId());
            }

            this.reply(message, mb.build());
        }
    }

}
