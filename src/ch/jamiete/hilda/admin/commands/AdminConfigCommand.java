/*******************************************************************************
 * Copyright 2017 jamietech
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ch.jamiete.hilda.admin.commands;

import java.util.Arrays;
import com.google.gson.JsonElement;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.admin.AdminPlugin;
import ch.jamiete.hilda.commands.ChannelCommand;
import ch.jamiete.hilda.configuration.Configuration;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

public class AdminConfigCommand extends ChannelCommand {
    private AdminPlugin plugin;

    protected AdminConfigCommand(Hilda hilda, AdminPlugin plugin) {
        super(hilda);

        this.plugin = plugin;

        this.setName("config");
        this.setAliases(Arrays.asList(new String[] { "configure", "configuration" }));
        this.setDescription("Manages the configuration for the admin plugin.");
    }

    @Override
    public void execute(Message message, String[] args, String label) {
        if (!this.plugin.canRun(message)) {
            return;
        }

        Configuration config = this.hilda.getConfigurationManager().getConfiguration(this.plugin);

        if (args.length == 0) {
            this.usage(message, "<role/log>", label);
            return;
        }

        if (args[0].equalsIgnoreCase("role")) {
            if (args.length == 1) { // Provide current value
                JsonElement output = config.get().get("role");

                if (output == null) {
                    this.reply(message, "There is no role currently set."); // Probably impossible to reach
                } else {
                    Role role = message.getGuild().getRoleById(output.getAsString());

                    if (role == null) {
                        config.get().remove("role");
                        config.save();
                        this.reply(message, "The role specified no longer exists. I've removed it.");
                    } else {
                        this.reply(message, "I'm currently responding to " + role.getName() + " (" + role.getId() + ")");
                    }
                }
            } else {
                if (message.getMentionedRoles().isEmpty()) {
                    this.reply(message, "Please mention the role you want me to respond to.");
                } else {
                    Role role = message.getMentionedRoles().get(0);

                    config.get().addProperty("role", role.getId());
                    config.save();
                    this.reply(message, "I'm now responding to " + role.getAsMention());
                }
            }
        }

        if (args[0].equalsIgnoreCase("log")) {
            if (args.length == 1) { // Provide current value
                JsonElement output = config.get().get("log");

                if (output == null) {
                    this.reply(message, "There is no log channel currently set."); // Probably impossible to reach
                } else {
                    TextChannel channel = message.getGuild().getTextChannelById(output.getAsString());

                    if (channel == null) {
                        config.get().remove("log");
                        config.save();
                        this.reply(message, "The log channel specified no longer exists. I've removed it.");
                    } else {
                        this.reply(message, "I'm currently logging to " + channel.getAsMention());
                    }
                }
            } else {
                if (message.getMentionedChannels().isEmpty()) {
                    this.reply(message, "Please mention the channel you want me to respond to.");
                } else {
                    TextChannel channel = message.getMentionedChannels().get(0);

                    config.get().addProperty("log", channel.getId());
                    config.save();
                    this.reply(message, "I'm now logging to " + channel.getAsMention());
                }
            }
        }
    }

}
