package cl.prezdev.xjplay.test;

import cl.prezdev.jlog.Log;
import cl.prezdev.xjplay.cover.art.CoverArtThread;
import cl.prezdev.xjplay.test.progress.WorkerStringProgress;
import cl.prezdev.xjplay.test.progress.WorkerValueProgress;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 *
 * @author pperezp
 */
public class NewJFrame extends javax.swing.JFrame {

    private NewJDialog p;

    /**
     * Creates new form NewJFrame
     */
    public NewJFrame() {
        initComponents();
        jSlider1.setVisible(jToggleButton1.isSelected());

        /*Código para escuchar a un boton para todos los componentes*/
        
        /*CON CTRL + F y f3 funciona el buscar*/
        this.getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK), "refresh");
        this.getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0), "refresh");
        /*CON CTRL + F y f3 funciona el buscar*/

        this.getRootPane().getActionMap().put("refresh", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Log.add("F3");
                if (p == null) {
                    p = new NewJDialog(NewJFrame.this, false);
                    //            p.setUndecorated(true);
                    p.setBounds(NewJFrame.this.getX(), NewJFrame.this.getY(), NewJFrame.this.getWidth(), p.getHeight());
                    p.setVisible(true);
                } else {
                    p.resetTextField();
                    p.setVisible(!p.isVisible());
                }
            }
        });
        /*Código para escuchar a un boton para todos los componentes*/
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        shapePainter1 = new org.jdesktop.swingx.painter.ShapePainter();
        jSlider1 = new javax.swing.JSlider();
        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lbl2 = new javax.swing.JLabel();
        lbl1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton2 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        jSlider1.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider1MouseReleased(evt);
            }
        });

        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("jLabel1");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl2.setText("jLabel3");
        jPanel1.add(lbl2, new org.netbeans.lib.awtextra.AbsoluteConstraints(138, 2, 130, 130));

        lbl1.setText("jLabel2");
        jPanel1.add(lbl1, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 130, 130));

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("jButton2");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextField1.setText("1281351");
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jTextField2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField2FocusLost(evt);
            }
        });
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jButton3.setText("jButton3");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(140, 140, 140)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(24, 24, 24)
                            .addComponent(jButton1))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(56, 56, 56)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(98, 98, 98)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                    .addComponent(jTextField1)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToggleButton1)
                    .addComponent(jLabel1))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addComponent(jButton2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        jSlider1.setVisible(jToggleButton1.isSelected());
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jSlider1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider1MouseReleased
        jToggleButton1.setSelected(false);
        jSlider1.setVisible(jToggleButton1.isSelected());
    }//GEN-LAST:event_jSlider1MouseReleased

    private void cLabel1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cLabel1MouseExited
//        cLabel1.setBackground(new java.awt.Color(233, 30, 99));
    }//GEN-LAST:event_cLabel1MouseExited

    private void cLabel1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cLabel1MouseEntered
//        cLabel1.setBackground(new java.awt.Color(236, 64, 122));
    }//GEN-LAST:event_cLabel1MouseEntered

    private void cLabel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cLabel1MousePressed
//        cLabel1.setBackground(new java.awt.Color(194, 24, 91));
    }//GEN-LAST:event_cLabel1MousePressed

    private void cLabel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cLabel1MouseReleased
//        cLabel1.setBackground(new java.awt.Color(233, 30, 99));
    }//GEN-LAST:event_cLabel1MouseReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Image im1 = new ImageIcon("test/01.jpg").getImage();
        Image im2 = new ImageIcon("test/02.jpg").getImage();
        Image im3 = new ImageIcon("test/03.jpg").getImage();
        Image im4 = new ImageIcon("test/04.jpg").getImage();
        Image im5 = new ImageIcon("test/05.png").getImage();

        ImageIcon ii1 = new ImageIcon(im1.getScaledInstance(130, 130, Image.SCALE_SMOOTH));
        ImageIcon ii2 = new ImageIcon(im2.getScaledInstance(130, 130, Image.SCALE_SMOOTH));
        ImageIcon ii3 = new ImageIcon(im3.getScaledInstance(130, 130, Image.SCALE_SMOOTH));
        ImageIcon ii4 = new ImageIcon(im4.getScaledInstance(130, 130, Image.SCALE_SMOOTH));
        ImageIcon ii5 = new ImageIcon(im5.getScaledInstance(130, 130, Image.SCALE_SMOOTH));

//        lbl1.setIcon(ii1);
//        lbl2.setIcon(ii2);
        final List<ImageIcon> imagenes = new ArrayList<>();
        imagenes.add(ii1);
        imagenes.add(ii2);
        imagenes.add(ii3);
        imagenes.add(ii4);
        imagenes.add(ii5);

        CoverArtThread hca = new CoverArtThread(lbl1, imagenes);
        hca.start();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        final int max = Integer.parseInt(jTextField1.getText());

        jProgressBar1.setMaximum(max);
        jProgressBar1.setStringPainted(true);

        WorkerStringProgress w2 = new WorkerStringProgress(jProgressBar1);
        WorkerValueProgress w1 = new WorkerValueProgress(jProgressBar1, max, w2);

        w1.execute();
        w2.execute();

//        final Thread h2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int i = 0;
//                try {
//                    for (int hor = 0; hor < 24; hor++) {
//                        for (int min = 0; min < 60; min++) {
//                            for (int seg = 0; seg < 60; seg++) {
//                                jProgressBar1.setString(min + ":" + (seg < 10 ? "0" + seg : seg));
//
//                                Thread.sleep(1000);
//
//                            }
//                        }
//                    }
//                } catch (InterruptedException ex) {
//                    System.out.println("H2 interrumpido!");
//                }
//
//            }
//        }
//        );
//
//        final Thread h1 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i <= max; i++) {
//                    try {
//                        jProgressBar1.setValue(i);
//
//                        Thread.sleep(100);
//                    } catch (InterruptedException ex) {
//                        h2.interrupt();
//                    }
//                }
//                h2.interrupt();
//            }
//        });
//
//        h1.start();
//        h2.start();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusGained
        new Thread(new Runnable() {
            int x;
            int y;
            int ancho;
            int alto;
            int limite = 300;

            @Override
            public void run() {
                for (int i = 0; i < limite; i++) {
                    x = jTextField2.getX();
                    y = jTextField2.getY();

                    ancho = jTextField2.getWidth();
                    alto = jTextField2.getHeight();

                    ancho += 1;

                    jTextField2.setBounds(x, y, ancho, alto);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }//GEN-LAST:event_jTextField2FocusGained

    private void jTextField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusLost
        new Thread(new Runnable() {
            int x;
            int y;
            int ancho;
            int alto;

            @Override
            public void run() {
                for (int i = 0; i < 300; i++) {
                    x = jTextField2.getX();
                    y = jTextField2.getY();

                    ancho = jTextField2.getWidth();
                    alto = jTextField2.getHeight();

                    ancho -= 1;

                    jTextField2.setBounds(x, y, ancho, alto);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }//GEN-LAST:event_jTextField2FocusLost

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased

    }//GEN-LAST:event_formKeyReleased

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
//        if(evt.getKeyCode() == KeyEvent.VK_F3){
//            if(p == null){
//                p = new NewJDialog(this, false);
//    //            p.setUndecorated(true);
//                p.setBounds(this.getX(), this.getY(), this.getWidth(), p.getHeight());
//                p.setVisible(true);
//            }else {
//                p.resetTextField();
//                p.setVisible(!p.isVisible());
//            }
//        }
    }//GEN-LAST:event_jTextField1KeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new NewJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private org.jdesktop.swingx.painter.ShapePainter shapePainter1;
    // End of variables declaration//GEN-END:variables

   

}
