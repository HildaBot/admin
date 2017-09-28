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

import java.util.logging.Level;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.Util;
import ch.jamiete.hilda.admin.AdminPlugin;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import ch.jamiete.hilda.music.MusicManager;
import ch.jamiete.hilda.music.MusicServer;
import ch.jamiete.hilda.music.QueueItem;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

class AdminStreamCommand extends ChannelSubCommand {

    private class LoadResults implements AudioLoadResultHandler {
        private final MusicServer server;
        private final Message message;
        private final Member member;

        LoadResults(final MusicServer server, final Message message) {
            this.server = server;
            this.message = message;
            this.member = message.getGuild().getMember(message.getAuthor());
        }

        @Override
        public void loadFailed(final FriendlyException e) {
            MusicManager.getLogger().log(Level.WARNING, "Couldn't load track", e);
            AdminStreamCommand.this.reply(this.message, "I couldn't load that track: " + e.getMessage() + ".");
            Hilda.getLogger().log(Level.WARNING, "Couldn't load track", e);
            this.server.prompt();
        }

        @Override
        public void noMatches() {
            AdminStreamCommand.this.reply(this.message, "I couldn't find anything matching that query.");
            this.server.prompt();
        }

        @Override
        public void playlistLoaded(final AudioPlaylist playlist) {
            AdminStreamCommand.this.reply(this.message, "You cannot load playlists with this command.");
            this.server.prompt();
        }

        @Override
        public void trackLoaded(final AudioTrack track) {
            MusicManager.getLogger().fine("Loaded a track");

            if (this.server.isQueued(track)) {
                MusicManager.getLogger().fine("Song already queued.");
                AdminStreamCommand.this.reply(this.message, "That song is already queued.");
                this.server.prompt();
                return;
            }

            if (this.message.getGuild().getSelfMember().hasPermission(this.message.getTextChannel(), Permission.MESSAGE_MANAGE)) {
                this.message.delete().reason("I automatically delete some command invocations. If you don't want this to happen, remove my manage messages permission in the channel.").queue();
            }

            final StringBuilder sb = new StringBuilder();
            sb.append("Queued ").append(MusicManager.getFriendly(track)).append(" for ").append(this.member.getEffectiveName());

            if (this.server.getPlaying() == null) {
                sb.append("; up next!");
            } else if (this.server.getPlayer().getPlayingTrack() == null) {
                // Something's gone wrong
                sb.append("; up soon!");
            } else {
                long time = 0;

                for (final QueueItem item : this.server.getQueue()) {
                    time += item.getTrack().getDuration();
                }

                time += this.server.getPlayer().getPlayingTrack().getDuration() - this.server.getPlayer().getPlayingTrack().getPosition();

                sb.append("; playing in ").append(Util.getFriendlyTime(time)).append("!");
            }

            AdminStreamCommand.this.reply(this.message, sb.toString());
            this.server.queue(new QueueItem(track, this.member.getUser().getId()));
            MusicManager.getLogger().fine("Queued a song");
        }

    }

    private final AdminPlugin plugin;

    public AdminStreamCommand(final Hilda hilda, final ChannelSeniorCommand senior, final AdminPlugin plugin) {
        super(hilda, senior);

        this.plugin = plugin;

        this.setName("stream");
        this.setDescription("Queues a sound file to be played. Accepts URLs.");
    }

    @Override
    public void execute(final Message message, final String[] args, final String label) {
        if (args.length != 2) {
            this.usage(message, "<channel_id> <url>", label);
            return;
        }

        final VoiceChannel channel = this.hilda.getBot().getVoiceChannelById(args[0]);

        if (channel == null) {
            this.reply(message, "I couldn't find that channel.");
            return;
        }

        final Guild guild = channel.getGuild();

        final MusicServer server = this.plugin.getMusicManager().hasServer(guild) ? this.plugin.getMusicManager().getServer(guild) : this.plugin.getMusicManager().createServer(guild);

        if (server.getChannel() == null) {
            server.setChannel(channel); // Join channel
        }

        // URL logic
        MusicManager.getLogger().info("Attempting to load URL " + args[1]);
        message.getChannel().sendTyping().queue();
        this.plugin.getMusicManager().getAudioPlayerManager().loadItemOrdered(server.getPlayer(), args[1], new LoadResults(server, message));
    }

}
