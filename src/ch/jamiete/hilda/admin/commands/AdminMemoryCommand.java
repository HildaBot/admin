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

import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.admin.AdminUtil;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

public class AdminMemoryCommand extends ChannelSubCommand {

    protected AdminMemoryCommand(final Hilda hilda, final ChannelSeniorCommand senior) {
        super(hilda, senior);

        this.setName("memory");
        this.setDescription("Gets runtime statistics about memory usage.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        Runtime runtime = Runtime.getRuntime();
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Memory usage");

        long used = runtime.totalMemory() - runtime.freeMemory();
        long total = runtime.totalMemory();
        double percent = ((used * 1.0) / total) * 100;

        eb.addField("Per cent used", Math.round(percent * 100.0) / 100.0 + "%", false);
        eb.addField("Current memory usage", AdminUtil.getFriendly(used, true), true);
        eb.addField("Available memory", AdminUtil.getFriendly(runtime.freeMemory(), true), true);
        eb.addField("Total memory", AdminUtil.getFriendly(total, true), true);
        eb.addField("Maximum memory", AdminUtil.getFriendly(runtime.maxMemory(), true), true);

        this.reply(message, eb.build());
    }

}
