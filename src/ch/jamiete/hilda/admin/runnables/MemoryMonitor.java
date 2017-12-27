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

package ch.jamiete.hilda.admin.runnables;

import java.awt.Color;
import java.time.Instant;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.admin.AdminPlugin;
import net.dv8tion.jda.core.EmbedBuilder;

public class MemoryMonitor implements Runnable {
    private final AdminPlugin plugin;

    public MemoryMonitor(final AdminPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!this.plugin.memory) {
            return;
        }

        final Runtime runtime = Runtime.getRuntime();
        final long used = runtime.maxMemory() - runtime.freeMemory();
        final long total = runtime.maxMemory();
        final double percent = used * 1.0 / total * 100;

        Hilda.getLogger().fine("Memory usage currently at " + percent + "%");

        if (percent < 90) {
            return;
        }

        final EmbedBuilder eb = new EmbedBuilder();
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
