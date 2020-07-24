package cl.prezdev.xjplay.services;

import cl.prezdev.jplay.MusicPlayer;
import cl.prezdev.jplay.Song;
import cl.prezdev.xjplay.exceptions.NoSongException;
import cl.prezdev.xjplay.services.loader.ServiceLoader;
import javazoom.jlgui.basicplayer.BasicPlayerException;

public class PlayerService {

    public void playPreviousSong() throws BasicPlayerException {
        if(hasCurrentSong()){
            try {
                play(ServiceLoader.songService.getPreviousSong());
            } catch (NoSongException e) {
                LoggerService.getInstance(PlayerService.class).info("No song exception", e);
            }
        }else{
            LoggerService.getInstance(PlayerService.class).info("No current song");
        }
    }

    public void play(Song song) throws BasicPlayerException {
        MusicPlayer.getInstance().play(song);
    }

    public void stop() throws BasicPlayerException {
        MusicPlayer.getInstance().stop();
    }

    public boolean hasCurrentSong() {
        return MusicPlayer.getInstance().hasCurrentSong();
    }
}
