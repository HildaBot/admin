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

import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.admin.AdminPlugin;
import ch.jamiete.hilda.admin.AdminUtil;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

class AdminMemoryCommand extends ChannelSubCommand {
    private final AdminPlugin plugin;

    AdminMemoryCommand(final Hilda hilda, final ChannelSeniorCommand senior, final AdminPlugin plugin) {
        super(hilda, senior);

        this.plugin = plugin;

        this.setName("memory");
        this.setDescription("Gets runtime statistics about memory usage.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        if (arguments.length == 1 && arguments[0].equalsIgnoreCase("quiet")) {
            this.plugin.memory = !this.plugin.memory;
            this.reply(message, "OK, you will " + (this.plugin.memory ? "now" : "no longer") + " receive memory warnings!");
            return;
        }

        final Runtime runtime = Runtime.getRuntime();
        final EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Memory usage");

        final long used = runtime.totalMemory() - runtime.freeMemory();
        final long total = runtime.totalMemory();
        final double percent = used * 1.0 / total * 100;

        eb.addField("Per cent used", Math.round(percent * 100.0) / 100.0 + "%", false);
        eb.addField("Current memory usage", AdminUtil.getFriendly(used, true), true);
        eb.addField("Available memory", AdminUtil.getFriendly(runtime.freeMemory(), true), true);
        eb.addField("Total memory", AdminUtil.getFriendly(total, true), true);
        eb.addField("Maximum memory", AdminUtil.getFriendly(runtime.maxMemory(), true), true);

        this.reply(message, eb.build());
    }

}
