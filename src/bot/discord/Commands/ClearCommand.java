package bot.discord.Commands;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;


public class ClearCommand implements Commands {

    EmbedBuilder error = new EmbedBuilder().setColor(Color.RED).setTitle("Error");

    private int getInt(String n, MessageReceivedEvent event){
        try {
            return Integer.parseInt(n);
        }catch (Exception e){
            event.getTextChannel().sendMessage(error.setDescription("Invalid argument").build()).queue();
            return 0;
        }
    }

    @Override
    public boolean validate(MessageReceivedEvent event) {
        return true;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        if(args.length == 0){
            event.getTextChannel().sendMessage(error.setDescription("Invalid Argument").build()).queue();
            return;
        }
        event.getMessage().delete().queue();
        int n = getInt(args[0], event);
        n = ++n < 100 ? n : 99;
        List<Message> msg = new MessageHistory(event.getTextChannel()).retrievePast(n).complete();
        event.getTextChannel().deleteMessages(msg).queue();

    }

    @Override
    public String help() {
        return null;
    }

    @Override
    public void execute(boolean success, MessageReceivedEvent event) {

    }
}
