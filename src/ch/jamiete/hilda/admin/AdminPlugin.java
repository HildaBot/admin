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
package ch.jamiete.hilda.admin;

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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;

import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class AdminPlugin extends HildaPlugin {
    private Role role;
    private TextChannel channel;
    private LogReporter reporter;
    public boolean memory = true;

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

        return member != null && member.getRoles().contains(this.role);
    }

    public TextChannel getChannel() {
        return this.channel;
    }

    public MusicManager getMusicManager() {
        final HildaPlugin plugin = this.getHilda().getPluginManager().getPlugin("music");

        if (!(plugin instanceof MusicPlugin)) {
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

        mb.append(guild.getName(), MessageBuilder.Formatting.BOLD);
        mb.append(" ");
        mb.append("(" + guild.getId() + ")", MessageBuilder.Formatting.ITALICS);
        mb.append(" — ");
        mb.append(String.valueOf(guild.getMembers().size())).append(" members, ");
        mb.append(String.valueOf(guild.getTextChannels().size())).append(" text channels, ");
        mb.append(String.valueOf(guild.getVoiceChannels().size())).append(" voice channels");

        return mb.build().getContentRaw();
    }

    @Override
    public void onLoad() {
        final Configuration servcfg = this.getHilda().getConfigurationManager().getConfiguration(this, "allowedservers");
        final JsonArray servarr = servcfg.get().getAsJsonArray("servers");

        if (servarr != null) {

            for (final JsonElement elem : servarr) {
                this.getHilda().addAllowedServer(elem.getAsString());
            }

            Hilda.getLogger().info("Allowed " + servarr.size() + " server IDs.");
        }
    }

    @Override
    public void onEnable() {
        this.getHilda().getCommandManager().registerChannelCommand(new AdminBaseCommand(this.getHilda(), this));
        this.getHilda().getCommandManager().registerChannelCommand(new ReportCommand(this.getHilda(), this));
        this.getHilda().getCommandManager().registerChannelCommand(new SetupCommand(this.getHilda(), this));

        this.getHilda().getBot().addEventListener(new ServerJoinListener(this));

        this.getHilda().getExecutor().scheduleWithFixedDelay(new MemoryMonitor(this), 1, 1, TimeUnit.MINUTES);

        final Configuration config = this.getHilda().getConfigurationManager().getConfiguration(this);

        if (config.get().get("role") == null) {
            config.get().addProperty("role", "");
            Hilda.getLogger().warning("No role specified.");
        }

        if (config.get().get("log") == null) {
            config.get().addProperty("log", "");
            Hilda.getLogger().warning("No log specified.");
        }

        config.save();

        final String cfg_role = config.get().get("role").getAsString();

        if (cfg_role.isEmpty()) {
            Hilda.getLogger().warning("No role specified.");
        } else {
            this.role = this.getHilda().getBot().getRoleById(cfg_role);
        }

        final String cfg_log = config.get().get("log").getAsString();

        if (cfg_log.isEmpty()) {
            Hilda.getLogger().warning("No log specified.");
        } else {
            this.channel = this.getHilda().getBot().getTextChannelById(cfg_log);
        }

        if (this.role == null || this.channel == null) {
            Hilda.getLogger().warning("Not currently configured correctly.");
        }

        final Logger global = Logger.getLogger("");

        for (final Handler h : global.getHandlers()) {
            if (h instanceof LogReporter) {
                h.close();
                global.removeHandler(h);
            }
        }

        this.reporter = new LogReporter(this.channel);
        global.addHandler(this.reporter);

        final Configuration cfg = this.getHilda().getConfigurationManager().getConfiguration(this, "ignoredusers");
        final JsonArray array = cfg.get().getAsJsonArray("users");

        if (array != null) {

            for (final JsonElement anArray : array) {
                this.getHilda().getCommandManager().addIgnoredUser(anArray.getAsString());
            }

            Hilda.getLogger().info("Ignored " + array.size() + " naughty users.");
        }

        if (this.channel != null) {
            this.channel.sendMessage("Hello! " + this.getHilda().getBot().getSelfUser().getName() + " has just started.").queue();
        }
    }

}
