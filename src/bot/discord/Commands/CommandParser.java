package bot.discord.Commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;

public class CommandParser {
    public CommandContainer parse(String rw, MessageReceivedEvent event){
            ArrayList<String> split  = new ArrayList<>();
            String raw = rw;
            String beheaded = rw.replaceFirst("!", "");
            String[] splitbeheaded  = beheaded.split(" ");
            for(String s : splitbeheaded){
                split.add(s);
            }
            String invoke = split.get(0);
            String[] args = new String[split.size() - 1];
            split.subList(1, split.size()).toArray(args);
            return new CommandContainer(raw, beheaded, splitbeheaded, invoke, args, event);
    }

    public class CommandContainer {
       public final String raw, beheaded, invoke;
       public final String[] splitbeheaded, args;
       public final MessageReceivedEvent event;

        public CommandContainer(String raw, String beheaded,  String[] splitbeheaded, String invoke, String[] args, MessageReceivedEvent event) {
            this.raw = raw;
            this.beheaded = beheaded;
            this.invoke = invoke;
            this.splitbeheaded = splitbeheaded;
            this.args = args;
            this.event = event;
        }
    }
}
