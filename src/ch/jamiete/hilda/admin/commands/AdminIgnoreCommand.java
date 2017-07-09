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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.Util;
import ch.jamiete.hilda.admin.AdminPlugin;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import ch.jamiete.hilda.configuration.Configuration;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.MessageBuilder.SplitPolicy;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class AdminIgnoreCommand extends ChannelSubCommand {
    private AdminPlugin plugin;

    protected AdminIgnoreCommand(final Hilda hilda, final ChannelSeniorCommand senior, final AdminPlugin plugin) {
        super(hilda, senior);

        this.plugin = plugin;

        this.setName("ignore");
        this.setAliases(Arrays.asList(new String[] { "unignore" }));
        this.setDescription("Manages ignored users.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        if (arguments.length == 1 && arguments[0].equalsIgnoreCase("list")) {
            List<String> strings = this.hilda.getCommandManager().getIgnoredUsers();

            if (strings.isEmpty()) {
                this.reply(message, "I'm not ignoring any channels!");
            } else {
                MessageBuilder mb = new MessageBuilder();
                mb.append("I'm currently ignoring ");

                List<String> pieces = new ArrayList<String>();

                for (String s : strings) {
                    User u = this.hilda.getBot().getUserById(s);

                    if (u == null) {
                        pieces.add("<@!" + s + ">");
                    } else {
                        pieces.add(u.getAsMention() + " (" + Util.getName(u) + ")");
                    }
                }

                mb.append(Util.getAsList(pieces));
                mb.append(".");
                this.reply(message, mb.build());
            }

            return;
        }

        if (arguments.length == 0) {
            this.usage(message, "<@user.../id...>");
        }

        IgnoreDirection direction = IgnoreDirection.valueOf(label.toUpperCase());
        List<String> ids = new ArrayList<String>();

        if (!message.getMentionedUsers().isEmpty()) {
            message.getMentionedUsers().forEach(u -> ids.add(u.getId()));
        }

        for (String arg : arguments) {
            if (!arg.startsWith("<@")) {
                ids.add(arg);
            }
        }

        Configuration cfg = this.hilda.getConfigurationManager().getConfiguration(this.plugin, "ignoredusers");
        JsonArray array = cfg.get().getAsJsonArray("users");

        if (array == null) {
            array = new JsonArray();
        }

        for (String id : ids) {
            if (direction == IgnoreDirection.IGNORE) {
                this.hilda.getCommandManager().addIgnoredUser(id);

                if (!array.contains(new JsonPrimitive(id))) {
                    array.add(id);
                }
            }

            if (direction == IgnoreDirection.UNIGNORE) {
                this.hilda.getCommandManager().removeIgnoredUser(id);
                array.remove(new JsonPrimitive(id));
            }
        }

        cfg.get().add("users", array);
        cfg.save();

        MessageBuilder mb = new MessageBuilder();

        mb.append("OK! I'm ").append(direction == IgnoreDirection.IGNORE ? "now" : "no longer").append(" ignoring ");

        List<String> pieces = new ArrayList<String>();

        for (String id : ids) {
            User u = this.hilda.getBot().getUserById(id);

            if (u == null) {
                pieces.add("<@!" + id + ">");
            } else {
                pieces.add(u.getAsMention() + " (" + Util.getName(u) + ")");
            }
        }

        mb.append(Util.getAsList(pieces));
        mb.append(".");

        mb.buildAll(SplitPolicy.SPACE).forEach(m -> message.getChannel().sendMessage(m).queue());
    }

    private enum IgnoreDirection {
        IGNORE, UNIGNORE;
    }

}
