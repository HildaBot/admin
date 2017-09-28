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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Collections;

import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.MessageBuilder.Formatting;
import net.dv8tion.jda.core.MessageBuilder.SplitPolicy;
import net.dv8tion.jda.core.entities.Message;

class AdminThreadsCommand extends ChannelSubCommand {

    AdminThreadsCommand(final Hilda hilda, final ChannelSeniorCommand senior) {
        super(hilda, senior);

        this.setName("threads");
        this.setAliases(Collections.singletonList("thread"));
        this.setDescription("Gets information about open threads.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        MessageBuilder mb = new MessageBuilder();

        mb.append("Thread information", Formatting.BOLD).append("\n");

        final ThreadMXBean thbean = ManagementFactory.getThreadMXBean();
        final long[] ids = thbean.getAllThreadIds();

        for (long id : ids) {
            ThreadInfo info = thbean.getThreadInfo(id);

            if (info == null) { // Thread died in intervening period
                continue;
            }

            mb.append("\n");

            mb.append(info.getThreadName(), Formatting.BOLD).append(" ");
            mb.append(info.getThreadId()).append(" (");
            mb.append(info.getThreadState().toString()).append(")");
        }

        mb.buildAll(SplitPolicy.NEWLINE).forEach(m -> this.reply(message, m));
    }

}
