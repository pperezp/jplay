package xjplay.recursos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.awt.Image;
import javax.swing.ImageIcon;
import xjplay.model.log.Log;
import xjplay.model.rules.Rules;

public class Recurso {

    public static final File FUENTE_ROBOTO
            = new File("recursos" + File.separator + "Roboto-Regular.ttf");
    public static final File FUENTE_TYPEWRITER
            = new File("recursos" + File.separator + "TravelingTypewriter.ttf");

    public static final Image ICONO_JPLAY = new ImageIcon(Recurso.class.getResource(Ruta.ICONO_JPLAY)).getImage();

    

    /**
     * Obtiene una lista de imagenes de una ruta en específico
     * @param f
     * @return 
     */
    public static List<ImageIcon> getFotos(File f) {
        List<ImageIcon> fotos = new ArrayList<>();
        Image image;
        ImageIcon imageIcon;
        for (File archivo : f.getParentFile().listFiles()) {
            Log.add(archivo.getName());
            if (archivo.getName().contains(".jpg")
                    || archivo.getName().contains(".png")) {
                image = new ImageIcon(archivo.getPath()).getImage();
                Log.add("RUTA FOTO: "+archivo.getPath());
                imageIcon = new ImageIcon(
                        image.getScaledInstance(
                                (int)Rules.COVER_DIMENSION.getWidth(),
                                (int)Rules.COVER_DIMENSION.getHeight(),
                                Image.SCALE_SMOOTH)
                );
                fotos.add(imageIcon);
            }
        }

        return fotos;
    }

//    public static List<File> getFotos(File f){
//        List<File> fotos = new ArrayList<>();
//        
//        for(File archivo : f.getParentFile().listFiles()){
//            System.out.println(archivo.getName());
//            if(
//                archivo.getName().contains(".jpg") ||
//                archivo.getName().contains(".png")
//            ){
//                fotos.add(archivo);
//            }
//        }
//        
//        return fotos;
//    }
}
