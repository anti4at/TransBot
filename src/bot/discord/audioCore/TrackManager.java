package bot.discord.audioCore;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.*;

public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer PLAYER;
    private final List<AudioInfo> queue;
    private int currentTrack = 0;

    public TrackManager(AudioPlayer player) {
        this.PLAYER = player;
        this.queue = new LinkedList<>();
    }

    public void queue(AudioTrack track, Member author) {
        AudioInfo info = new AudioInfo(track, author);
        queue.add(info);

        if (PLAYER.getPlayingTrack() == null) {
            PLAYER.playTrack(track);
        }
    }

    public Set<AudioInfo> getQueue() {
        return new LinkedHashSet<>(queue);
    }

    public AudioInfo getInfo(AudioTrack track) {
        return queue.stream()
                .filter(info -> info.getTrack().equals(track))
                .findFirst().orElse(null);
    }

    public void purgeQueue() {
        queue.clear();
    }

    public void shuffleQueue() {
        List<AudioInfo> cQueue = new ArrayList<>(getQueue());
        AudioInfo current = cQueue.get(0);
        cQueue.remove(0);
        Collections.shuffle(cQueue);
        cQueue.add(0, current);
        purgeQueue();
        queue.addAll(cQueue);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        AudioInfo info = queue.get(currentTrack);
        VoiceChannel vChan = info.getAuthor().getVoiceState().getChannel();

        if (vChan == null)
            player.stopTrack();
        else
            info.getAuthor().getGuild().getAudioManager().openAudioConnection(vChan);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (currentTrack == queue.size() - 1)
            currentTrack = 0;
        else
            currentTrack++;
        player.playTrack(queue.get(currentTrack).getTrack());
    }
}

