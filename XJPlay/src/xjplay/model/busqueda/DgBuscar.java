package xjplay.model.busqueda;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author prez
 */
public class DgBuscar extends javax.swing.JDialog {

    private IBuscar ibuscar;
    /**
     * Creates new form dialogBuscar
     * @param parent
     * @param modal
     */
    public DgBuscar(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setUndecorated(true);
        initComponents();
        
        
        init();
        
        
        cboBuscar.getEditor().getEditorComponent().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
            }

            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if(e.getKeyCode() != KeyEvent.VK_ENTER){
                    comboKeyReleased(e);
                }else{
                    DgBuscar.this.setVisible(false);
                    ibuscar.focusOn();
                }
            }

            
        });
        
        // Esto es de SWINGX!
        AutoCompleteDecorator.decorate(this.cboBuscar);
    }
    
    private void comboKeyReleased(KeyEvent e) {
        ibuscar.search(cboBuscar.getEditor().getItem().toString().toLowerCase().trim());
    }

    public void setIbuscar(IBuscar ibuscar) {
        this.ibuscar = ibuscar;
        ibuscar.cargarComboDeBusqueda(cboBuscar);
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cboBuscar = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        cboBuscar.setEditable(true);
        cboBuscar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "", "asd", "canni", "creed" }));
        cboBuscar.setOpaque(false);
        cboBuscar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboBuscarItemStateChanged(evt);
            }
        });
        cboBuscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cboBuscarMouseReleased(evt);
            }
        });
        cboBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cboBuscarKeyReleased(evt);
            }
        });

        jButton1.setText("Cancelar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(cboBuscar, 0, 338, Short.MAX_VALUE)
                .addGap(7, 7, 7)
                .addComponent(jButton1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cboBuscar)
                .addComponent(jButton1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cboBuscarKeyReleased
        
    }//GEN-LAST:event_cboBuscarKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cboBuscarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cboBuscarMouseReleased
        
    }//GEN-LAST:event_cboBuscarMouseReleased

    private void cboBuscarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboBuscarItemStateChanged
        try {
            ibuscar.search(cboBuscar.getEditor().getItem().toString().toLowerCase().trim());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }//GEN-LAST:event_cboBuscarItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DgBuscar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DgBuscar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DgBuscar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DgBuscar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DgBuscar dialog = new DgBuscar(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboBuscar;
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables

    private void init() {
        /*Con escape, f3 y control f se desaparece el dialogo*/
        this.getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(com.sun.glass.events.KeyEvent.VK_F3, 0), "buscar");
        this.getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(com.sun.glass.events.KeyEvent.VK_ESCAPE, 0), "buscar");
        this.getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK), "buscar");
        /*Con escape, f3 y control f se desaparece el dialogo*/

        this.getRootPane().getActionMap().put("buscar", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                DgBuscar.this.setVisible(false);
            }
        });
    }
}
