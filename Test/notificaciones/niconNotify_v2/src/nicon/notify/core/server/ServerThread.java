/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nicon.notify.core.server;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TechJunior
 */
public class ServerThread extends Thread{
    
    private final ServerOSD server;
    private final int nid;
    private final int time;
    
    public ServerThread(ServerOSD server,int nid, int time){
        this.server = server;
        this.nid = nid;
        this.time = time;
    }
    
    @Override
    public void run(){
        try {
            System.out.println("ServerOSD: init run ServerThread process ...NID: "+nid);
            sleep(time);
            ServerOSD.getInstance().remove(nid);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    /**
     * Detiene el ServerThread en el momento en que una notificacion es cerrada
     * por el usuario
     */
    protected void killThread(){
        System.out.println("ServerOSD: stoping Thread for serverThread nid: "+nid);
        this.stop();
    }
}

