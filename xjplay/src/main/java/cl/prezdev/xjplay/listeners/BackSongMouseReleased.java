package cl.prezdev.xjplay.listeners;

import cl.prezdev.xjplay.services.LoggerService;
import cl.prezdev.xjplay.services.loader.ServiceLoader;
import javazoom.jlgui.basicplayer.BasicPlayerException;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BackSongMouseReleased extends MouseAdapter {
    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if(ServiceLoader.songService.hasSongs()){
            try {
                ServiceLoader.playerService.playPreviousSong();
            } catch (BasicPlayerException ex) {
                LoggerService.getInstance(BackSongMouseReleased.class).info("BasicPlayerException", ex);
            }
        }
    }
}
