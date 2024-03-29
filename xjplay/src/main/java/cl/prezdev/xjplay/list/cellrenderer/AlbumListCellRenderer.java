package cl.prezdev.xjplay.list.cellrenderer;

import cl.prezdev.jplay.Album;
import cl.prezdev.xjplay.artist.list.BackAlbum;
import cl.prezdev.xjplay.resources.Resource;
import cl.prezdev.xjplay.rules.Rule;

import java.awt.Component;
import java.awt.Image;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;

public class AlbumListCellRenderer extends DefaultListCellRenderer {

    private final List<Album> albums;

    public AlbumListCellRenderer(List<Album> albums) {
        this.albums = albums;
    }

    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {

        JLabel albumLabel = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus
        );

        albumLabel.setHorizontalAlignment(SwingConstants.CENTER);
        albumLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        albumLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

        Album album = albums.get(index);

        if (album instanceof BackAlbum) {
            albumLabel.setText("ATRÁS");
        } else {
            Image imageCover;

            // @TODO: encapsular en Album (no se puede, porque es algo gráfico y no corresponde)
            // @TODO: las reglas, RULE, podrían estar en un proyecto en común (jplayCommon)
            if (album.hasCoversArt()) {
                imageCover = album.getCoverArt().getImage();

                imageCover = imageCover.getScaledInstance(
                        Rule.ARTIST_COVER_ART.width,
                        Rule.ARTIST_COVER_ART.height,
                        Image.SCALE_SMOOTH
                );
            } else {
                // el disco no tiene imagen
                imageCover = Resource.JPLAY_ICON.getScaledInstance(
                        Rule.ARTIST_COVER_ART.width,
                        Rule.ARTIST_COVER_ART.height,
                        Image.SCALE_SMOOTH
                );
            }

            albumLabel.setIcon(new ImageIcon(imageCover));
            // @TODO: encapsular en Album
            albumLabel.setText(album.getName().toUpperCase() + "(" + album.getYear() + ")");
        }

        return albumLabel;
    }
}
