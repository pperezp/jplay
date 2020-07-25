package cl.prezdev.xjplay.services;

import cl.prezdev.jplay.Song;
import cl.prezdev.xjplay.exceptions.NoSongException;
import cl.prezdev.xjplay.services.loader.ServiceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SongServiceTest {

    @BeforeEach
    public void before(){
        ServiceLoader.init();

        ServiceLoader.songService.addSong(new Song("1"));
        ServiceLoader.songService.addSong(new Song("2"));
        ServiceLoader.songService.addSong(new Song("3"));
        ServiceLoader.songService.addSong(new Song("4"));
        ServiceLoader.songService.addSong(new Song("5"));
    }

    @Test
    public void getPreviousSongTest() throws NoSongException {
        assertEquals("5", ServiceLoader.songService.getPreviousSong().getPath());
        assertEquals("4", ServiceLoader.songService.getPreviousSong().getPath());
        assertEquals("3", ServiceLoader.songService.getPreviousSong().getPath());
        assertEquals("2", ServiceLoader.songService.getPreviousSong().getPath());
        assertEquals("1", ServiceLoader.songService.getPreviousSong().getPath());
        assertEquals("5", ServiceLoader.songService.getPreviousSong().getPath());
        assertEquals("4", ServiceLoader.songService.getPreviousSong().getPath());
    }

    @Test
    public void getNextSongTest() throws NoSongException {
        assertEquals("1", ServiceLoader.songService.getNextSong().getPath());
        assertEquals("2", ServiceLoader.songService.getNextSong().getPath());
        assertEquals("3", ServiceLoader.songService.getNextSong().getPath());
        assertEquals("4", ServiceLoader.songService.getNextSong().getPath());
        assertEquals("5", ServiceLoader.songService.getNextSong().getPath());
        assertEquals("1", ServiceLoader.songService.getNextSong().getPath());
        assertEquals("2", ServiceLoader.songService.getNextSong().getPath());
    }
}