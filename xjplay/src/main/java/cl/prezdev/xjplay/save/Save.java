package cl.prezdev.xjplay.save;

import cl.prezdev.jplay.Album;
import cl.prezdev.jplay.Song;
import cl.prezdev.xjplay.artist.list.ArtistCoverArt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;

// @TODO: Cambiar a xml o json
public class Save implements Serializable{
    public List<Song>               songs;
    public List<Album>              albums;
    public List<ArtistCoverArt>     artistCoversArt;
    public int                      indexTab;
    public Icon                     cover;
    public int                      volume;

    public void saveToJsonFile(){
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            //String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(songs);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("save.json"), this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(Save.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
