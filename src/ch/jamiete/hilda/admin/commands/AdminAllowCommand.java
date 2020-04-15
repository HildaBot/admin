/*
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
 */
package ch.jamiete.hilda.admin.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.Util;
import ch.jamiete.hilda.admin.AdminPlugin;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import ch.jamiete.hilda.configuration.Configuration;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public class AdminAllowCommand extends ChannelSubCommand {
    private enum AllowDirection {
        ALLOW, DISALLOW
    }

    private final AdminPlugin plugin;

    AdminAllowCommand(final Hilda hilda, final ChannelSeniorCommand senior, final AdminPlugin plugin) {
        super(hilda, senior);

        this.plugin = plugin;

        this.setName("allow");
        this.setAliases(Collections.singletonList("disallow"));
        this.setDescription("Manages allowed servers.");
    }

    private String name(Guild guild) {
        return "**" + guild.getName() + " (" + guild.getId() + ")**";
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        if (arguments.length == 1 && arguments[0].equalsIgnoreCase("list")) {
            final List<String> strings = new ArrayList<>();

            for (String str : this.hilda.getAllowedServers()) {
                Guild guild = this.hilda.getBot().getGuildById(str);

                if (guild != null) {
                    strings.add(this.name(guild));
                }
            }

            if (strings.isEmpty()) {
                this.reply(message, "The whitelist function is not enabled!");
            } else {
                final MessageBuilder mb = new MessageBuilder();
                mb.append("I'm currently allowing ");

                if (strings.size() != this.hilda.getBot().getGuilds().size()) {
                    mb.append("only ");
                }

                mb.append(strings.size()).append(strings.size() == 1 ? "server" : "servers").append(": ");
                mb.append(Util.getAsList(strings));
                mb.append(".");
                this.reply(message, mb.build());
            }

            return;
        }

        if (arguments.length == 0) {
            this.usage(message, "<id.../list>");
        }

        final AllowDirection direction = AllowDirection.valueOf(label.toUpperCase());
        final List<String> ids = new ArrayList<>();
        final List<String> success = new ArrayList<>();
        final List<String> fail = new ArrayList<>();

        for (final String arg : arguments) {
            Guild guild = this.hilda.getBot().getGuildById(arg);

            if (guild == null) {
                fail.add(arg);
            } else {
                ids.add(arg);
                success.add(this.name(guild));
            }
        }

        if (!success.isEmpty()) {
            final Configuration cfg = this.hilda.getConfigurationManager().getConfiguration(this.plugin, "allowedservers");
            JsonArray array = cfg.get().getAsJsonArray("servers");

            if (array == null) {
                array = new JsonArray();
            }

            for (final String id : ids) {
                if (direction == AllowDirection.ALLOW) {
                    this.hilda.addAllowedServer(id);

                    if (!array.contains(new JsonPrimitive(id))) {
                        array.add(id);
                    }
                }

                if (direction == AllowDirection.DISALLOW) {
                    this.hilda.removeAllowedServer(id);
                    array.remove(new JsonPrimitive(id));
                }
            }

            cfg.get().add("servers", array);
            cfg.save();
        }

        final MessageBuilder mb = new MessageBuilder();

        mb.append("OK, ");

        if (!success.isEmpty()) {
            mb.append("I'm ").append(direction == AllowDirection.ALLOW ? "now" : "no longer").append(" allowing ");
            mb.append(Util.getAsList(success));
        }

        if (!fail.isEmpty()) {
            if (!success.isEmpty()) {
                mb.append(", however ");
            }

            mb.append("I couldn't find any servers matching ");
            mb.append(Util.getAsList(fail));
        }

        mb.append(".");

        mb.buildAll(MessageBuilder.SplitPolicy.SPACE).forEach(m -> message.getChannel().sendMessage(m).queue());
    }

}
