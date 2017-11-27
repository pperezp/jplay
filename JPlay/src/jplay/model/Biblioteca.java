package jplay.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author pperezp
 */
public class Biblioteca implements Serializable{
    private List<Cancion> canciones;
    private List<Album> albums;
    
    public Biblioteca(){
        albums = new ArrayList<>();
        canciones = new ArrayList<>();
    }
    
    public void add(Cancion c){
        if(!estaCancion(c)){
            this.canciones.add(c);
        }
    }
    
    public List<Cancion> getCanciones() {
        return canciones;
    }
    
    public void remover(Cancion c){
        canciones.remove(c);
    }
    
    public int removerNoExistentes(){
        Iterator<Cancion> iterator = canciones.iterator();
        Cancion c;
        int cont = 0;
        while(iterator.hasNext()){
            c = iterator.next();
            
            if(!c.exists()){
                this.remover(c);
                cont++;
            }
        }
        
        return cont;
    }
    
    public boolean estaCancion(Cancion c){
        return canciones.contains(c);
    }

    public List<Cancion> getCancionesMasReproducidas(){
        List<Cancion> topCanciones = new ArrayList<>();
        
        for(Cancion c : canciones){
            if(c.getCantidadReproducciones() != 0){
                topCanciones.add(c);
            }
        }
        
        /*acá tengo que ordenarlas (de la mas reproducida a la menos)*/
        
        /*------ Proceso de ordenado de lista ------*/
        Collections.sort(topCanciones, new Comparator<Cancion>() {
            @Override
            public int compare(Cancion o1, Cancion o2) {
                if(o1.getCantidadReproducciones() > o2.getCantidadReproducciones()){
                    return -1;
                }else if(o1.getCantidadReproducciones() < o2.getCantidadReproducciones()){
                    return 1;
                }else{
                    return 0;
                }
            }
        });
        /*------ Proceso de ordenado de lista ------*/
        
        return topCanciones;
    }
//    
//    public Cancion getCancion(Cancion c){
//        for(Cancion cancion : CANCIONES){
//            if(cancion.getNombre().equalsIgnoreCase(c.getNombre()) &&
//                    cancion.getAutor().equalsIgnoreCase(c.getAutor()) &&
//                    cancion.getAlbum().equalsIgnoreCase(c.getAlbum())){
//                return cancion;
//            }
//        }
//        
//        return null;
//    }
    
    public void procesarAlbums(){
        for (Cancion cancion : canciones) {
            addToAlbum(cancion);
        }
        
        printAlbums();
    }

    private void addToAlbum(Cancion c) {
        boolean encontrado = false;
        for (Album a : albums) {
            if(a.getName().trim().equalsIgnoreCase(c.getAlbum().trim())){
                a.addCancion(c);
                encontrado = true;
                break;
            }
        }
        
        if(!encontrado){
            Album a = new Album(c.getAutor(), c.getAlbum());
            a.addCancion(c);
            
            albums.add(a);
        }
    }
    
    private void printAlbums(){
        for (Album album : albums) {
            System.out.println(album);
        }
    }
    
    public Album getAlbum(Cancion c){
        for (Album a : albums) {
            if(a.existCancion(c)){
                return a;
            }
        }
        return null;
    }
}
