package bot.discord;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BotListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getMessage().getContent().startsWith("!") && event.getAuthor() != event.getJDA().getSelfUser()){
            try {
                Main.handleCommand(Main.parser.parse(event.getMessage().getContent().toLowerCase(), event));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        String out = "\nThis bot is running on following servers :\n";
        int i = 1;
        for(Guild g :  event.getJDA().getGuilds()){
            out+= i++ + ")\t" + g.getName() + "(" + g.getId() + ") \n";
            g.getTextChannels().get(0).sendMessage("Hello").queue();
        }
        System.out.println(out);
    }


}
