package bot.discord.Commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface Commands {

    public boolean validate(MessageReceivedEvent event);
    public boolean called(String[] args, MessageReceivedEvent event);
    public void action(String[] args, MessageReceivedEvent event) throws InterruptedException;
    public String help();
    public void execute(boolean success, MessageReceivedEvent event);

}
