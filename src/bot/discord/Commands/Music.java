package bot.discord.Commands;

import bot.discord.audioCore.*;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeCipherOperation;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;


public class Music implements Commands {


    private static Guild guild;
    private static final AudioPlayerManager MANAGER = new DefaultAudioPlayerManager();
    private static final Map<Guild, Map.Entry<AudioPlayer, TrackManager>> PLAYERS = new HashMap<>();


    public Music() {
        AudioSourceManagers.registerRemoteSources(MANAGER);
    }

    private AudioPlayer createPlayer(Guild g) {
        AudioPlayer p = MANAGER.createPlayer();
        TrackManager m = new TrackManager(p);
        p.addListener(m);

        guild.getAudioManager().setSendingHandler(new PlayerSendHandler(p));

        PLAYERS.put(g, new AbstractMap.SimpleEntry<>(p, m));

        return p;
    }

    private boolean hasPlayer(Guild g) {
        return PLAYERS.containsKey(g);
    }

    private AudioPlayer getPlayer(Guild g) {
        if (hasPlayer(g))
            return PLAYERS.get(g).getKey();
        else
            return createPlayer(g);
    }

    private TrackManager getManager(Guild g) {
        return PLAYERS.get(g).getValue();
    }

    private boolean isIdle(Guild g) {
        return !hasPlayer(g) || getPlayer(g).getPlayingTrack() == null;
    }

    private void loadTrack(String identifier, Member author, Message msg) {

        Guild guild = author.getGuild();
        getPlayer(guild);

        MANAGER.setFrameBufferDuration(5000);
        MANAGER.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                getManager(guild).queue(track, author);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                    getManager(guild).queue(playlist.getTracks().get(0), author);
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {
                exception.printStackTrace();
            }
        });

    }
    private void skip(Guild g) {
        getPlayer(g).stopTrack();
    }
    private String getTimestamp(long milis) {
        long seconds = milis / 1000;
        long hours = Math.floorDiv(seconds, 3600);
        seconds = seconds - (hours * 3600);
        long mins = Math.floorDiv(seconds, 60);
        seconds = seconds - (mins * 60);
        return (hours == 0 ? "" : hours + ":") + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }

    private String buildQueueMessage(AudioInfo info) {
        AudioTrackInfo trackInfo = info.getTrack().getInfo();
        String title = trackInfo.title;
        long length = trackInfo.length;
        return "`[ " + getTimestamp(length) + " ]` " + title + "\n";
    }

    private void sendErrorMsg(MessageReceivedEvent event, String content) {
        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.red)
                        .setDescription(content)
                        .build()
        ).queue();
    }


    @Override
    public boolean validate(MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {


        guild = event.getGuild();
        if (args.length < 1) {
            sendErrorMsg(event, help());
            return;
        }
        switch (args[0].toLowerCase()) {

            case "volume":
                int volume;
                try {
                    volume = Integer.parseInt(args[1]);
                }catch (Exception e){
                    volume = 100;
                }
                getPlayer(guild).setVolume(volume <= 100 ? volume : 100);
                break;

            case "play":
            case "p":
                if (args.length < 2) {
                    sendErrorMsg(event, "Please enter a valid source!");
                    return;
                }

                String input = Arrays.stream(args).skip(1).map(s -> " " + s).collect(Collectors.joining()).substring(1);
                if (input.toLowerCase().startsWith("http://") ||input.toLowerCase().startsWith("https://") || input.toLowerCase().startsWith("www.")) {

                    event.getTextChannel().sendMessage(

                            new EmbedBuilder()
                                .setColor(Color.YELLOW)
                                .setTitle("SORRY")
                                .setDescription("I'm sorry. \nTry to search track by name")
                                .build()
                    ).queue();
                    return;
                }
                if (!(input.startsWith("http://") || input.startsWith("https://"))) {
                    input = "ytsearch: " + input;
                }
                loadTrack(input, event.getMember(), event.getMessage());

                break;


            case "pause":
                getPlayer(guild).setPaused(true);
                break;
            case "resume":
                getPlayer(guild).setPaused(false);
                break;

            case "skip":
            case "s":

                if (isIdle(guild)) return;
                skip(guild);
                break;


            case "stop":

                if (isIdle(guild)) return;
                getManager(guild).purgeQueue();
                skip(guild);
                guild.getAudioManager().closeAudioConnection();
                break;


            case "shuffle":
                if (isIdle(guild)) return;
                getManager(guild).shuffleQueue();
                break;

            case "now":
            case "info":
            case "np":

                if (isIdle(guild)) return;

                AudioTrack track = getPlayer(guild).getPlayingTrack();
                AudioTrackInfo info = track.getInfo();

                event.getTextChannel().sendMessage(
                        new EmbedBuilder()
                                .setDescription("**CURRENT TRACK INFO:**")
                                .addField("Title", info.title, false)
                                .addField("Duration", "`[ " + getTimestamp(track.getPosition()) + "/ " + getTimestamp(track.getDuration()) + " ]`", false)
                                .addField("Author", info.author, false)
                                .build()
                ).queue();

                break;



            case "queue":

                if (isIdle(guild)) return;

                int sideNumb = args.length > 1 ? Integer.parseInt(args[1]) : 1;

                List<String> tracks = new ArrayList<>();
                List<String> trackSublist;

                getManager(guild).getQueue().forEach(audioInfo -> tracks.add(buildQueueMessage(audioInfo)));

                if (tracks.size() > 100)
                    trackSublist = tracks.subList((sideNumb-1)*100, (sideNumb-1)*100+20);
                else
                    trackSublist = tracks;

                String out = trackSublist.stream().collect(Collectors.joining("\n"));
                int sideNumbAll = tracks.size() >= 100 ? tracks.size() / 100 : 1;

                event.getTextChannel().sendMessage(
                        new EmbedBuilder()
                                .setDescription(
                                        "**CURRENT QUEUE:**\n" +
                                                "*[ Tracks | Side " + sideNumb + " / " + sideNumbAll + "]*\n" +
                                                out
                                )
                                .build()
                ).queue();


                break;
        }


    }

    @Override
    public String help() {
        return "";
    }

    @Override
    public void execute(boolean success, MessageReceivedEvent event) {

    }
}
