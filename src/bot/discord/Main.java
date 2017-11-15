package bot.discord;

import bot.discord.Commands.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;

import java.util.HashMap;

public class Main {
    public static JDA jda;
    public static HashMap<String, Commands> commandsHashMap = new HashMap<>();
    public static final CommandParser parser = new CommandParser();

    public static void _init(){
        JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT);
        jdaBuilder.setToken("Mzc5OTk4NDExMDA2NTQxODI0.DOyOxg.5UPxViMOw_0PpGzzmzxJeyrnizQ");
        jdaBuilder.setAutoReconnect(true);
        jdaBuilder.setStatus(OnlineStatus.ONLINE);

        try {
            BotListener botListener = new BotListener();
            jda = jdaBuilder.buildBlocking();
            jda.addEventListener(botListener);
        } catch (Exception e){

        }
    }
    public static void _initCommands(){
        commandsHashMap.put("ping", new PingCommand());
        commandsHashMap.put("clear", new ClearCommand());
        commandsHashMap.put("dad", new DadBogdanCommand());
        commandsHashMap.put("bogdan", commandsHashMap.get("dad"));
        commandsHashMap.put("m", new Music());
    }
    public static void main(String[] args) {
        _init();
    }
    public static void handleCommand(CommandParser.CommandContainer cmd) throws InterruptedException {
        _initCommands();
        if(commandsHashMap.containsKey(cmd.invoke)){
            boolean safe = commandsHashMap.get(cmd.invoke).called(cmd.args, cmd.event);

            if(safe){
                commandsHashMap.get(cmd.invoke).action(cmd.args, cmd.event);
                commandsHashMap.get(cmd.invoke).execute(safe, cmd.event);
            } else {
                commandsHashMap.get(cmd.invoke).execute(safe, cmd.event);
            }
        }
    }
}
