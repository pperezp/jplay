package cl.prezdev.xjplay.services;

import cl.prezdev.jplay.Song;
import cl.prezdev.xjplay.exceptions.NoSongException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class SongService {
    private List<Song> songs;
    private Song current;
    private Integer currentIndex;

    public SongService(){
        this.songs = new ArrayList<>();
    }

    public Song getPreviousSong() throws NoSongException {
        if(hasSongs()){
            if(hasCurrentSong()){
                if(isCurrentSongTheFirstOne()){// La actual, es la primera? --> previous --> última
                    setCurrent(getLastSong());
                }else{
                    decrementCurrentIndex();
                    setCurrent(this.songs.get(currentIndex));
                }
            }else{
                setCurrent(getLastSong());
            }
        }else{
            throw new NoSongException();
        }

        return this.current;
    }

    private void decrementCurrentIndex(){
        this.currentIndex -= 1;
    }

    public void setCurrent(Song song){
        this.current = song;
        this.currentIndex = songs.indexOf(song);
    }

    private Song getFirstSong() {
        return this.songs.get(0);
    }

    private Song getLastSong() {
        return this.songs.get(getLastSongIndex());
    }

    public boolean isCurrentSongTheLastOne() {
        int lastIndex = getLastSongIndex();

        return this.current == songs.get(lastIndex);
    }

    public int getLastSongIndex() {
        return this.songs.size() - 1;
    }

    public boolean isCurrentSongTheFirstOne() {
        return this.current == songs.get(0);
    }

    public boolean hasSongs() {
        return !songs.isEmpty();
    }

    public boolean hasCurrentSong() {
        return this.current != null;
    }

    public void addSong(Song song){
        this.songs.add(song);
    }
}
