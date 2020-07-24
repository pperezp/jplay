package cl.prezdev.xjplay.test;

import cl.prezdev.jplay.Song;
import cl.prezdev.xjplay.exceptions.NoSongException;
import cl.prezdev.xjplay.services.loader.ServiceLoader;

public class MainTest {
    public static void main(String[] args) {
        Song song1 = new Song("1");
        Song song2 = new Song("2");
        Song song3 = new Song("3");
        Song song4 = new Song("4");
        Song song5 = new Song("5");

        ServiceLoader.init();

        ServiceLoader.songService.addSong(song1);
        ServiceLoader.songService.addSong(song2);
        ServiceLoader.songService.addSong(song3);
        ServiceLoader.songService.addSong(song4);
        ServiceLoader.songService.addSong(song5);

        try {
            System.out.println(ServiceLoader.songService.getPreviousSong().getPath());// 5
            System.out.println(ServiceLoader.songService.getPreviousSong().getPath());// 4
            System.out.println(ServiceLoader.songService.getPreviousSong().getPath());// 3
            System.out.println(ServiceLoader.songService.getPreviousSong().getPath());// 2
            System.out.println(ServiceLoader.songService.getPreviousSong().getPath());// 1
            System.out.println(ServiceLoader.songService.getPreviousSong().getPath());// 5
            System.out.println(ServiceLoader.songService.getPreviousSong().getPath());// 4
            System.out.println(ServiceLoader.songService.getPreviousSong().getPath());// 3
        } catch (NoSongException e) {
            e.printStackTrace();
        }
    }
}
