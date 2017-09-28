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
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.Util;
import ch.jamiete.hilda.admin.AdminUtil;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

public class AdminOverviewCommand extends ChannelSubCommand {
    private long start = System.currentTimeMillis();

    protected AdminOverviewCommand(final Hilda hilda, final ChannelSeniorCommand senior) {
        super(hilda, senior);

        this.setName("overview");
        this.setAliases(Arrays.asList(new String[] { "vitals", "statistics", "stats" }));
        this.setDescription("Gets information about the bot.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        Runtime runtime = Runtime.getRuntime();
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Hilda statistics");

        eb.addField("Servers", String.valueOf(this.hilda.getBot().getGuilds().size()), true);

        long voice = this.hilda.getBot().getGuilds().stream().filter(g -> g.getAudioManager().isConnected()).count();
        eb.addField("Voice connections", String.valueOf(voice), true);

        eb.addField("Users", String.valueOf(this.hilda.getBot().getUsers().size()), true);

        eb.addField("Threads", String.valueOf(Thread.activeCount()), true);

        long used = runtime.totalMemory() - runtime.freeMemory();
        long total = runtime.totalMemory();
        double percent = ((used * 1.0) / total) * 100;

        eb.addField("Per cent memory usage", Math.round(percent * 100.0) / 100.0 + "%", true);
        eb.addField("Current memory usage", AdminUtil.getFriendly(used, true), true);
        eb.addField("Available memory", AdminUtil.getFriendly(runtime.freeMemory(), true), true);

        eb.addField("Uptime", Util.getFriendlyTime(System.currentTimeMillis() - this.start), true);

        eb.addField("Plugins", String.valueOf(this.hilda.getPluginManager().getPlugins().size()), true);

        eb.addField("Commands", String.valueOf(this.hilda.getCommandManager().getChannelCommands().size()), true);

        eb.addField("Command executions", String.valueOf(this.hilda.getCommandManager().getExecutions()), true);

        String executor = "";
        executor += this.hilda.getExecutor().getTaskCount() + " total tasks\n";
        executor += this.hilda.getExecutor().getCompletedTaskCount() + " completed\n";
        executor += this.hilda.getExecutor().getActiveCount() + " active";
        eb.addField("Executor executions", executor, true);

        this.reply(message, eb.build());
    }

}