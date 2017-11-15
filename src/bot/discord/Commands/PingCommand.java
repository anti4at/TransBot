package bot.discord.Commands;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PingCommand implements Commands {

    private final String HELP = "Usage !ping";

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
        if(!validate(event)){
            return;
        } else {
            event.getTextChannel().sendMessage("Pong!").queue();
        }
    }

    @Override
    public String help() {
        return HELP;
    }

    @Override
    public void execute(boolean success, MessageReceivedEvent event) {
        return;
    }
}
