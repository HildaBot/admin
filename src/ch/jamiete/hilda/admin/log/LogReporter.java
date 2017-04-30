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
package ch.jamiete.hilda.admin.log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import ch.jamiete.hilda.LogFormat;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.MessageBuilder.Formatting;
import net.dv8tion.jda.core.MessageBuilder.SplitPolicy;
import net.dv8tion.jda.core.entities.TextChannel;

public class LogReporter extends Handler {
    public static final List<Level> LEVELS = Collections.unmodifiableList(Arrays.asList(new Level[] { Level.WARNING, Level.SEVERE }));
    private final LogFormat formatter = new LogFormat();
    private final TextChannel channel;
    private boolean loud = false;

    public LogReporter(final TextChannel channel) {
        this.channel = channel;
    }

    @Override
    public void close() throws SecurityException {
        return;
    }

    @Override
    public void flush() {
        return;
    }

    public boolean getLoud() {
        return this.loud;
    }

    public void setLoud(final boolean loud) {
        this.loud = loud;
    }

    @Override
    public void publish(final LogRecord record) {
        if (!LogReporter.LEVELS.contains(record.getLevel()) && !this.loud || this.loud && !(LogReporter.LEVELS.contains(record.getLevel()) || record.getLevel() == Level.INFO) || this.channel == null) {
            return;
        }

        final MessageBuilder mb = new MessageBuilder();

        mb.append("Caught a " + record.getLevel().getName() + " message:\n");
        mb.append(this.formatter.format(record), Formatting.BLOCK);

        mb.buildAll(SplitPolicy.NEWLINE).forEach(m -> this.channel.sendMessage(m).queue());
    }

}
