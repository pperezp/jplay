package cl.prezdev.xjplay.services.loader;

import cl.prezdev.xjplay.services.LoggerService;
import cl.prezdev.xjplay.services.PlayerService;
import cl.prezdev.xjplay.services.SongService;

public class ServiceLoader {

    public static SongService songService;
    public static PlayerService playerService;

    public static void init(){
        songService = new SongService();
        playerService = new PlayerService();
    }

}
