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
package ch.jamiete.hilda.admin;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.Start;
import ch.jamiete.hilda.admin.commands.AdminBaseCommand;
import ch.jamiete.hilda.admin.commands.ReportCommand;
import ch.jamiete.hilda.admin.commands.SetupCommand;
import ch.jamiete.hilda.admin.listeners.ServerJoinListener;
import ch.jamiete.hilda.admin.log.LogReporter;
import ch.jamiete.hilda.admin.runnables.MemoryMonitor;
import ch.jamiete.hilda.configuration.Configuration;
import ch.jamiete.hilda.music.MusicManager;
import ch.jamiete.hilda.music.MusicPlugin;
import ch.jamiete.hilda.plugins.HildaPlugin;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.MessageBuilder.Formatting;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

public class AdminPlugin extends HildaPlugin {
    private Role role;
    private TextChannel channel;
    private Configuration config;
    private LogReporter reporter;

    public AdminPlugin(final Hilda hilda) {
        super(hilda);
    }

    public boolean canRun(final Message message) {
        if (Start.DEBUG) {
            return true;
        }

        if (this.role == null) {
            return false;
        }

        final Member member = this.role.getGuild().getMember(message.getAuthor());

        if (member == null) {
            return false;
        }

        if (member.getRoles().contains(this.role)) {
            return true;
        }

        return false;
    }

    public TextChannel getChannel() {
        return this.channel;
    }

    public MusicManager getMusicManager() {
        final HildaPlugin plugin = this.getHilda().getPluginManager().getPlugin("music");

        if (plugin == null || !(plugin instanceof MusicPlugin)) {
            return null;
        }

        final MusicPlugin music = (MusicPlugin) plugin;
        return music.getMusicManager();
    }

    public LogReporter getReporter() {
        return this.reporter;
    }

    public Role getRole() {
        return this.role;
    }

    public String getServerInfo(final Guild guild) {
        final MessageBuilder mb = new MessageBuilder();

        mb.append(guild.getName(), Formatting.BOLD);
        mb.append(" ");
        mb.append("(" + guild.getId() + ")", Formatting.ITALICS);
        mb.append(" — ");
        mb.append(guild.getMembers().size() + " members, ");
        mb.append(guild.getTextChannels().size() + " text channels, ");
        mb.append(guild.getVoiceChannels().size() + " voice channels");

        return mb.build().getContent();
    }

    @Override
    public void onEnable() {
        this.getHilda().getCommandManager().registerChannelCommand(new AdminBaseCommand(this.getHilda(), this));
        this.getHilda().getCommandManager().registerChannelCommand(new ReportCommand(this.getHilda(), this));
        this.getHilda().getCommandManager().registerChannelCommand(new SetupCommand(this.getHilda(), this));

        this.getHilda().getBot().addEventListener(new ServerJoinListener(this));

        this.getHilda().getExecutor().scheduleWithFixedDelay(new MemoryMonitor(this), 1, 1, TimeUnit.MINUTES);

        this.config = this.getHilda().getConfigurationManager().getConfiguration(this);

        if (this.config.get().get("role") == null) {
            this.config.get().addProperty("role", "");
            Hilda.getLogger().warning("No role specified.");
        }

        if (this.config.get().get("log") == null) {
            this.config.get().addProperty("log", "");
            Hilda.getLogger().warning("No log specified.");
        }

        this.config.save();

        final String cfg_role = this.config.get().get("role").getAsString();

        if (cfg_role.isEmpty()) {
            Hilda.getLogger().warning("No role specified.");
        } else {
            this.role = this.getHilda().getBot().getRoleById(cfg_role);
        }

        final String cfg_log = this.config.get().get("log").getAsString();

        if (cfg_log.isEmpty()) {
            Hilda.getLogger().warning("No log specified.");
        } else {
            this.channel = this.getHilda().getBot().getTextChannelById(cfg_log);
        }

        if (this.role == null || this.channel == null) {
            Hilda.getLogger().warning("Not currently configured correctly.");
        }

        for (final Handler h : Hilda.getLogger().getHandlers()) {
            if (h instanceof LogReporter) {
                h.close();
                Hilda.getLogger().removeHandler(h);
            }
        }

        for (final Handler h : MusicManager.getLogger().getHandlers()) {
            if (h instanceof LogReporter) {
                h.close();
                MusicManager.getLogger().removeHandler(h);
            }
        }

        this.reporter = new LogReporter(this.channel);
        Hilda.getLogger().addHandler(this.reporter);
        MusicManager.getLogger().addHandler(this.reporter);

        final Configuration cfg = this.getHilda().getConfigurationManager().getConfiguration(this, "ignoredusers");
        final JsonArray array = cfg.get().getAsJsonArray("users");

        if (array != null) {
            final Iterator<JsonElement> iterator = array.iterator();

            while (iterator.hasNext()) {
                this.getHilda().getCommandManager().addIgnoredUser(iterator.next().getAsString());
            }

            Hilda.getLogger().info("Ignored " + array.size() + " naughty users.");
        }
    }

}
