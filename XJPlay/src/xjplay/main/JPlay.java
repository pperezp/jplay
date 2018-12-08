package xjplay.main;

//iconos https://www.iconfinder.com/iconsets/snipicons
import java.awt.Color;
import java.awt.Dimension;
import xjplay.save.Guardar;
import xjplay.recursos.Ruta;
import jplay.model.Reproductor;
import xjplay.coverArt.HiloCover;
import jplay.model.Biblioteca;
import jplay.model.Cancion;
import xjplay.utils.Validar;
import xjplay.save.IO;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import xjplay.model.tableModel.TMCancion;
import xjplay.model.tableModel.TMCancionBiblioteca;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import jplay.model.Album;
import xjplay.model.busqueda.DgBuscar;
import xjplay.model.busqueda.IBuscar;
import xjplay.model.lastFM.LastFM;
import jlog.model.Log;
import jlog.model.LogEntry;
import jlog.model.TMLog;
import xjplay.model.progress.WorkerStringProgress;
import xjplay.rules.Rule;
import xjplay.model.scan.Scan;
import xjplay.model.scan.UpdateBibliotecaUI;
import xjplay.model.tree.CellRenderExplorer;
import xjplay.model.tree.CellRenderCancionLista;
import xjplay.model.tree.CellRenderCancionMasTocada;
import xjplay.recursos.Recurso;
import jlog.model.UpdateLogUI;
import xjplay.model.tree.CellRenderFavoritos;
import xjplay.utils.Util;
//import nicon.notify.core.Notification;

public class JPlay extends javax.swing.JFrame implements
        BasicPlayerListener, IBuscar, UpdateBibliotecaUI, UpdateLogUI {

    public static Reproductor reproductor;
    private Biblioteca biblioteca;
    private List<Cancion> canciones; // son las canciones de la lista de reproducción actual
    private Thread hiloRep;
    private Thread hiloCargar;
    private JPopupMenu popUpTree;
    private JPopupMenu popUpBiblio;
    private JPopupMenu popCover;

    private final boolean SAVE = true; // ESTO ES SOLO PARA DEBUGGING
    private List<Cancion> lFiltrada;
    private boolean isPlay;
    private boolean isStop;
    private boolean isRandom;
    private boolean repetirCancion;
    private int nextRandom;
    private Image icono;
    private HiloCover hCover; // hilo para animación de caratulas

    private int totalBytes; // GUARDA EL TOTAL DE DURACIÓN DE LA CANCION EN MILIS

    private int porcentaje;
    private WorkerStringProgress workerStringProgress; // para pintar los minutos en la barra
    private boolean imprimirBarraDeProgreso;

    private DgBuscar dialogBuscar;

    private int tabActual; // esto es para el drag and drop

    private TMLog modelLog;

    private List<Album> albums;
    /*
                                Esta lista la utilizo cuando guardo en SAVE.
                                Ya que si solo guardo las canciones, el icono
                                no se carga cuando por ejemplo, estoy escuchando
                                las canciones favoritas.
     */

    private long msListaActual;            // Son los milisegundos totales de la lista de 
    // reproducción actual.

    public JPlay() {
        initComponents();
        
        Rule.COVER_DIMENSION = new Dimension(lblCover.getWidth(), lblCover.getHeight());

        icono = Recurso.ICONO_JPLAY;

        icono = icono.getScaledInstance((int) Rule.COVER_DIMENSION.getWidth(),
                (int) Rule.COVER_DIMENSION.getHeight(),
                Image.SCALE_SMOOTH);

        initLog();

        canciones = new ArrayList<>();
        albums = new ArrayList<>();
        biblioteca = new Biblioteca();
        biblioteca.setUpdateLogUI(this);

        isRandom = false;

        crearArbolExplorer();
        listenerClickDerechoSobreArbol();
        crearPopUpTree();
        crearPopUpBiblioteca();
        crearPopUpCover();

        btnCancelarCarga.setEnabled(false);
//        indiceActual = -1;

        if (SAVE) {
            cargarSave();
        }

        this.setTitle(Rule.NOMBRE + " - " + Rule.VERSION);
        isPlay = false;
        isStop = true;
        repetirCancion = opRepetirCancion.isSelected();

        crearListenerTitulosTabla();
        createKeybindings(tablaCanciones);
        createKeybindings(tablaBiblioteca);

        // esto es para que no se pueda mover las columnas
        tablaBiblioteca.getTableHeader().setReorderingAllowed(false);

//        cargarArbolConCanciones(getDiscos(canciones));

        /*Se hace invisible la tabla antigua de temas y el boton limpiar*/
        jScrollPane2.setVisible(false);
        lblLimpiar.setVisible(false);
        /*Se hace invisible la tabla antigua de temas y el boton limpiar*/

        setIconImage(icono);

        /*VALIDAR SI ESTAS TODAS LAS COSAS NECESARIAS*/
//        File f = new File("res");
//        
//        if(!f.exists()){
//            f.mkdir();
//            
//            
//        }
//        lblCaratula.setIcon(new ImageIcon(icono));
        lblCover.setText(null);
//        lbl2.setText(null);

        setBounds(0, 0, 800, 600);
        setLocationRelativeTo(null);
        hCover = null;

        progress.setStringPainted(true);
        lblCover.requestFocus();
//        jSplitPane1.setDividerLocation(0.0);
//        jSplitPane2.setDividerLocation(1);
//        inicializarBarraProgreso();
        cargarArbolConCancionesMasEscuchadas();
        cargarArbolConFavoritos();
        initFonts();
//        Properties properties = System.getProperties();
//        Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();
//   
//        for (Map.Entry<Object, Object> entry : entrySet) {
//            System.out.println(entry);
//        }
//        togVol.setVisible(false);
        biblioteca.printAlbums();
        imprimirBarraDeProgreso = true;

        initBuscar();
        initDragDropTabbedPane();
        initIconosTabs();

        panelPrincipal.setBackground(Color.white);

        setBounds(0, 0, Rule.ANCHO, Rule.ALTO);
        setLocationRelativeTo(null);
        
        
    }

    // http://stackoverflow.com/questions/13516730/disable-enter-key-from-moving-down-a-row-in-jtable
    // este método es porque cuando apretaba enter en la tabla de canciones, se veia feo el que
    // el cursor bajara y despues subiera. Este método sobre escribe eso hecho por java automáticamente
    private void createKeybindings(JTable table) {
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        table.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        });

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogCanciones = new javax.swing.JDialog();
        jDialog1 = new javax.swing.JDialog();
        mainPanel = new javax.swing.JPanel();
        panelPrincipal = new javax.swing.JPanel();
        lblCover = new javax.swing.JLabel();
        btnTocarAnterior = new javax.swing.JButton();
        btnPause = new javax.swing.JButton();
        btnTocarSiguiente = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        lblTema = new javax.swing.JLabel();
        lblArtista = new javax.swing.JLabel();
        btnFav = new javax.swing.JToggleButton();
        opRepetirCancion = new javax.swing.JCheckBox();
        opAleatorio = new javax.swing.JCheckBox();
        slideVol = new javax.swing.JSlider();
        tabbedPrincipal = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        treeExplorer = new javax.swing.JTree();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaBiblioteca = new javax.swing.JTable();
        panelListaActual = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaCanciones = new javax.swing.JTable();
        lblLimpiar = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        treeSong = new javax.swing.JTree();
        panelMasEscuchadas = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        treeMasTocadas = new javax.swing.JTree();
        panelFavoritos = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        treeFavoritos = new javax.swing.JTree();
        panelLogger = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tableLogger = new javax.swing.JTable();
        progress = new javax.swing.JProgressBar();
        lblInfoCarga = new javax.swing.JLabel();
        btnCancelarCarga = new javax.swing.JButton();

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(254, 254, 254));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lblCover.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCover.setText("[COVER]");

        btnTocarAnterior.setBackground(new java.awt.Color(63, 81, 181));
        btnTocarAnterior.setIcon(new javax.swing.ImageIcon(getClass().getResource("/xjplay/recursos/iconos/material/ic_skip_previous_white_24dp_1x.png"))); // NOI18N
        btnTocarAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTocarAnteriorActionPerformed(evt);
            }
        });

        btnPause.setBackground(new java.awt.Color(76, 175, 80));
        btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/xjplay/recursos/iconos/material/ic_play_arrow_white_24dp_1x.png"))); // NOI18N
        btnPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPauseActionPerformed(evt);
            }
        });

        btnTocarSiguiente.setBackground(new java.awt.Color(63, 81, 181));
        btnTocarSiguiente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/xjplay/recursos/iconos/material/ic_skip_next_white_24dp_1x.png"))); // NOI18N
        btnTocarSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTocarSiguienteActionPerformed(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(254, 254, 254));
        jPanel5.setOpaque(false);

        lblTema.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        lblTema.setText("Artista / Canción");

        lblArtista.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        lblArtista.setText("Artista / Canción");

        btnFav.setIcon(new javax.swing.ImageIcon(getClass().getResource("/xjplay/recursos/iconos/fav.png"))); // NOI18N
        btnFav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFavActionPerformed(evt);
            }
        });

        opRepetirCancion.setBackground(new java.awt.Color(254, 254, 254));
        opRepetirCancion.setText("Repetir Canción");
        opRepetirCancion.setOpaque(false);
        opRepetirCancion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opRepetirCancionActionPerformed(evt);
            }
        });

        opAleatorio.setBackground(new java.awt.Color(254, 254, 254));
        opAleatorio.setText("Shuffle");
        opAleatorio.setOpaque(false);
        opAleatorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opAleatorioActionPerformed(evt);
            }
        });

        slideVol.setBackground(new java.awt.Color(255, 255, 255));
        slideVol.setMaximum(40);
        slideVol.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                slideVolMouseDragged(evt);
            }
        });
        slideVol.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                slideVolMouseWheelMoved(evt);
            }
        });
        slideVol.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                slideVolMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                slideVolMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                slideVolMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTema, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblArtista, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFav))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(opRepetirCancion, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(opAleatorio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(slideVol, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFav)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(lblTema, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblArtista)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(opRepetirCancion)
                        .addComponent(opAleatorio))
                    .addComponent(slideVol, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tabbedPrincipal.setToolTipText("");
        tabbedPrincipal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tabbedPrincipalMouseReleased(evt);
            }
        });

        treeExplorer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeExplorerMouseReleased(evt);
            }
        });
        treeExplorer.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeExplorerValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(treeExplorer);

        tabbedPrincipal.addTab("Explorer", jScrollPane3);

        tablaBiblioteca.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablaBiblioteca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tablaBibliotecaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tablaBibliotecaMouseReleased(evt);
            }
        });
        tablaBiblioteca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tablaBibliotecaKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tablaBiblioteca);

        tabbedPrincipal.addTab("Biblioteca", jScrollPane1);

        jScrollPane2.setBorder(null);

        tablaCanciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablaCanciones.setShowHorizontalLines(false);
        tablaCanciones.setShowVerticalLines(false);
        tablaCanciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tablaCancionesMouseReleased(evt);
            }
        });
        tablaCanciones.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tablaCancionesKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tablaCanciones);

        lblLimpiar.setBackground(new java.awt.Color(63, 81, 181));
        lblLimpiar.setForeground(new java.awt.Color(254, 254, 254));
        lblLimpiar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLimpiar.setText("Limpiar");
        lblLimpiar.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblLimpiar.setOpaque(true);
        lblLimpiar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblLimpiarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblLimpiarMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblLimpiarMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblLimpiarMouseEntered(evt);
            }
        });

        treeSong.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeSongMouseReleased(evt);
            }
        });
        jScrollPane4.setViewportView(treeSong);

        javax.swing.GroupLayout panelListaActualLayout = new javax.swing.GroupLayout(panelListaActual);
        panelListaActual.setLayout(panelListaActualLayout);
        panelListaActualLayout.setHorizontalGroup(
            panelListaActualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        panelListaActualLayout.setVerticalGroup(
            panelListaActualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelListaActualLayout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabbedPrincipal.addTab("Lista actual", panelListaActual);

        panelMasEscuchadas.setLayout(new java.awt.BorderLayout());

        treeMasTocadas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeMasTocadasMouseReleased(evt);
            }
        });
        jScrollPane5.setViewportView(treeMasTocadas);

        panelMasEscuchadas.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        tabbedPrincipal.addTab("+ escuchadas", panelMasEscuchadas);

        panelFavoritos.setLayout(new java.awt.BorderLayout());

        treeFavoritos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeFavoritosMouseReleased(evt);
            }
        });
        jScrollPane7.setViewportView(treeFavoritos);

        panelFavoritos.add(jScrollPane7, java.awt.BorderLayout.CENTER);

        tabbedPrincipal.addTab("Favoritos", panelFavoritos);

        panelLogger.setLayout(new java.awt.BorderLayout());

        tableLogger.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane6.setViewportView(tableLogger);

        panelLogger.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        tabbedPrincipal.addTab("Logger (DEV)", panelLogger);

        progress.setBackground(new java.awt.Color(254, 254, 254));
        progress.setForeground(new java.awt.Color(255, 255, 255));
        progress.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                progressMouseDragged(evt);
            }
        });
        progress.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                progressMouseReleased(evt);
            }
        });

        lblInfoCarga.setBackground(new java.awt.Color(254, 254, 254));
        lblInfoCarga.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        lblInfoCarga.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblInfoCarga.setText("[lblInfo]");

        btnCancelarCarga.setText("X");
        btnCancelarCarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarCargaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPrincipal)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(panelPrincipalLayout.createSequentialGroup()
                            .addComponent(btnTocarAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnTocarSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(lblCover, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnPause, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(lblInfoCarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelarCarga, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                .addComponent(tabbedPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCover, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPause, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addGap(117, 117, 117)
                        .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblInfoCarga, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCancelarCarga))
                    .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnTocarAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnTocarSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPauseActionPerformed
        try {
            if (reproductor != null) {
                if (isPlay) {
                    isPlay = false;
                    btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource(Ruta.PLAY)));
                    reproductor.pause();

                } else {
                    isPlay = true;
                    btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource(Ruta.PAUSE)));
                    if (isStop) {
                        reproducirCancionActual();
                        isPlay = true;
                        isStop = false;
                    } else {
                        reproductor.resume();
                    }
                }
            }
//            else if (indiceActual != -1) {
////                r = new Reproductor(canciones.get(indiceActual), this);
////                r.play();      
//                reproducir(canciones.get(indiceActual));
//                isPlay = true;
//                isStop = false;
//                btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource(Ruta.PAUSE)));
//            }
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnPauseActionPerformed

    private void slideVolMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slideVolMouseDragged
        setVolumen(slideVol.getValue());
    }//GEN-LAST:event_slideVolMouseDragged

    private void slideVolMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slideVolMouseClicked
//        System.out.println("Clicked");
    }//GEN-LAST:event_slideVolMouseClicked

    private void slideVolMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slideVolMousePressed
//        System.out.println("Pressed");
    }//GEN-LAST:event_slideVolMousePressed

    private void slideVolMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_slideVolMouseWheelMoved
//        System.out.println("Wheel");
    }//GEN-LAST:event_slideVolMouseWheelMoved

    private void treeExplorerMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeExplorerMouseReleased
        if (evt.getClickCount() == 2) {
            procesarCancionArbol();
        }
    }//GEN-LAST:event_treeExplorerMouseReleased

    private void treeExplorerValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeExplorerValueChanged
        // acá cargo los subdirectorios cuando hago click
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeExplorer.getLastSelectedPathComponent();

        if (node != null) {
            if (node.getChildCount() == 0) {
                /*Si no tengo hijos proceso*/
                Object o = node.getUserObject();

                if (o instanceof File) {
                    File f = (File) o;
                    if (f.isDirectory()) {
                        cargarArchivosEnNodoArbol(node, f);
                    }

                }
            }
        }
    }//GEN-LAST:event_treeExplorerValueChanged

    private void btnCancelarCargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarCargaActionPerformed
        hiloCargar.stop();
        btnCancelarCarga.setEnabled(false);
        cargarArbolConCanciones(getDiscos(canciones));
    }//GEN-LAST:event_btnCancelarCargaActionPerformed

    private void btnTocarAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTocarAnteriorActionPerformed
        if (!canciones.isEmpty()) {
            if (isRandom) {
                reproducirRandom();
            } else if (repetirCancion) {
                reproducirCancionActual();
            } else {
                reproducirAnterior();
            }
        }
    }//GEN-LAST:event_btnTocarAnteriorActionPerformed

    private void btnTocarSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTocarSiguienteActionPerformed
        if (!canciones.isEmpty()) {
            if (isRandom) {
                reproducirRandom();
            } else if (repetirCancion) {
                reproducirCancionActual();
            } else {
                reproducirSiguiente();
            }
        }
    }//GEN-LAST:event_btnTocarSiguienteActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (SAVE) {
            try {
                Guardar g = new Guardar();

                g.canciones = canciones;
                g.indexTab = tabbedPrincipal.getSelectedIndex();
                g.logEntries = Log.getEntrys();
                g.cover = lblCover.getIcon();
                g.albums = albums;
                g.volume = slideVol.getValue();

                IO.escribirObjetoEn(g, Ruta.SAVE);
                IO.escribirObjetoEn(biblioteca, Ruta.BIBLIOTECA);
            } catch (IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_formWindowClosing

    private void tablaBibliotecaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaBibliotecaMouseReleased
        if (evt.getClickCount() == 2) {
            tocarCancionSeleccionadaEnTablaBiblioteca();
        }
    }//GEN-LAST:event_tablaBibliotecaMouseReleased

    private void tablaBibliotecaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaBibliotecaMousePressed
        if (SwingUtilities.isRightMouseButton(evt)) {
            popUpBiblio.show(tablaBiblioteca, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tablaBibliotecaMousePressed

    private void opRepetirCancionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opRepetirCancionActionPerformed
        repetirCancion = opRepetirCancion.isSelected();
    }//GEN-LAST:event_opRepetirCancionActionPerformed

    private void slideVolMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slideVolMouseReleased

    }//GEN-LAST:event_slideVolMouseReleased

    private void opAleatorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opAleatorioActionPerformed
        isRandom = opAleatorio.isSelected();
    }//GEN-LAST:event_opAleatorioActionPerformed

    private void tablaBibliotecaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaBibliotecaKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tocarCancionSeleccionadaEnTablaBiblioteca(); // xD
        }
    }//GEN-LAST:event_tablaBibliotecaKeyReleased

    private void treeSongMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeSongMouseReleased
        if (evt.getClickCount() == 2) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeSong.getLastSelectedPathComponent();
            if (node != null) {
                Object o = node.getUserObject();
                if (o instanceof Cancion) {
                    Cancion c = (Cancion) o;

//                    indiceActual = canciones.indexOf(c);
//                    System.out.println("Índice actual: " + indiceActual);
                    reproducir(c);
                }
            }
//            TMCancion model = (TMCancion) tablaCanciones.getModel();
//            indiceActual = tablaCanciones.getSelectedRow();
//            Cancion c = (Cancion) model.getValueAt(indiceActual, 0);
//
//            reproducir(c);

        }
    }//GEN-LAST:event_treeSongMouseReleased

    private void progressMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_progressMouseReleased
        cambiarProgress((evt.getX() * 100) / progress.getWidth(), true);
        // seteo el volumen por un bug descubierto hoy (9 de marzo de 2018)
        setVolumen(slideVol.getValue());
        imprimirBarraDeProgreso = true;
    }//GEN-LAST:event_progressMouseReleased

    private void progressMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_progressMouseDragged
        cambiarProgress((evt.getX() * 100) / progress.getWidth(), false);
        imprimirBarraDeProgreso = false;
    }//GEN-LAST:event_progressMouseDragged

    private void lblLimpiarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLimpiarMouseReleased
        lblLimpiar.setBackground(new java.awt.Color(63, 81, 181));
        canciones = new ArrayList<>();
        cargarArbolConCanciones(getDiscos(canciones));
    }//GEN-LAST:event_lblLimpiarMouseReleased

    private void lblLimpiarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLimpiarMousePressed
        lblLimpiar.setBackground(new java.awt.Color(26, 35, 126));
    }//GEN-LAST:event_lblLimpiarMousePressed

    private void lblLimpiarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLimpiarMouseExited
        lblLimpiar.setBackground(new java.awt.Color(63, 81, 181));
    }//GEN-LAST:event_lblLimpiarMouseExited

    private void lblLimpiarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLimpiarMouseEntered
        lblLimpiar.setBackground(new java.awt.Color(92, 107, 192));
    }//GEN-LAST:event_lblLimpiarMouseEntered

    private void tablaCancionesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaCancionesKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {

            TMCancion model = (TMCancion) tablaCanciones.getModel();
            int index = tablaCanciones.getSelectedRow();
            Cancion c = (Cancion) model.getValueAt(index, 0);

            reproducir(c);
            //            tablaCanciones.getSelectionModel().clearSelection();
//            tablaCanciones.getSelectionModel().setSelectionInterval(indiceActual, indiceActual);
        }
    }//GEN-LAST:event_tablaCancionesKeyReleased

    private void tablaCancionesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaCancionesMouseReleased
        if (evt.getClickCount() == 2) {
            TMCancion model = (TMCancion) tablaCanciones.getModel();
//            indiceActual = tablaCanciones.getSelectedRow();
//            Cancion c = (Cancion) model.getValueAt(indiceActual, 0);
//
//            reproducir(c);
        }
    }//GEN-LAST:event_tablaCancionesMouseReleased

    private void treeMasTocadasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMasTocadasMouseReleased
        if (evt.getClickCount() == 2) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeMasTocadas.getLastSelectedPathComponent();
            if (node != null) {
                Object o = node.getUserObject();
                if (o instanceof Cancion) {
                    Cancion c = (Cancion) o;

                    canciones = biblioteca.getCancionesMasReproducidas();

                    /*Ahora debo poner las canciones en un album*/
                    Album a = new Album(Rule.NOMBRE, "Canciones más escuchadas!", null);

                    for (Cancion can : canciones) {
                        a.addCancion(can);
                    }

                    List<ImageIcon> covers = new ArrayList<>();
                    covers.add(CellRenderCancionLista.crearIcono(Ruta.ICONO_CORAZON));
                    a.setCovers(covers);

                    List<Album> albums = new ArrayList<>();
                    albums.add(a);
                    /*Ahora debo poner las canciones en un album*/

                    cargarArbolConCanciones(albums);

                    reproducir(c);

                    tabbedPrincipal.setSelectedIndex(Rule.Tabs.LISTA_ACTUAL);
                }
            }
        }
    }//GEN-LAST:event_treeMasTocadasMouseReleased

    private void tabbedPrincipalMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabbedPrincipalMouseReleased
        if (evt.getClickCount() == 2) {
            if (tabbedPrincipal.getSelectedIndex() == 1) {
                cargarCancionesABiblioteca(biblioteca.getCanciones());
            }
        }

        switch (tabbedPrincipal.getSelectedIndex()) {
            case Rule.Tabs.BIBLIOTECA:
                lblInfoCarga.setText(biblioteca.getDuracionBiblioteca());
                break;
            case Rule.Tabs.EXPLORER:
                lblInfoCarga.setText(biblioteca.getDuracionBiblioteca());
                break;
            case Rule.Tabs.FAVORITOS:
                lblInfoCarga.setText(biblioteca.getDuracionFavoritos());
                break;
            case Rule.Tabs.LISTA_ACTUAL:
                lblInfoCarga.setText("Lista actual --> " + biblioteca.getDuracion(msListaActual));
                break;
            case Rule.Tabs.LOGGER:
                lblInfoCarga.setText(biblioteca.getDuracionBiblioteca());
                break;
            case Rule.Tabs.MAS_ESCUCHADAS:
                lblInfoCarga.setText(biblioteca.getDuracionMasEscuchadas());
                break;
        }
    }//GEN-LAST:event_tabbedPrincipalMouseReleased

    private void btnFavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFavActionPerformed
        if (reproductor.getCancionActual() != null) {
            if (btnFav.isSelected()) {
                biblioteca.addFavorita(reproductor.getCancionActual());
            } else {
                biblioteca.removeFavorita(reproductor.getCancionActual());
            }
            cargarArbolConFavoritos();
        }
    }//GEN-LAST:event_btnFavActionPerformed

    private void treeFavoritosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeFavoritosMouseReleased
        if (evt.getClickCount() == 2) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeFavoritos.getLastSelectedPathComponent();
            if (node != null) {
                Object o = node.getUserObject();
                if (o instanceof Cancion) {
                    Cancion c = (Cancion) o;

                    canciones = biblioteca.getFavoritos();

                    /*Ahora debo poner las canciones en un album*/
                    Album a = new Album(Rule.NOMBRE, "Favoritas!", null);

                    for (Cancion can : canciones) {
                        a.addCancion(can);
                    }

                    List<ImageIcon> covers = new ArrayList<>();
                    covers.add(CellRenderCancionLista.crearIcono(Ruta.IC_TAB_FAVORITOS));
                    a.setCovers(covers);

                    List<Album> albums = new ArrayList<>();
                    albums.add(a);
                    /*Ahora debo poner las canciones en un album*/

                    cargarArbolConCanciones(albums);

                    reproducir(c);

                    tabbedPrincipal.setSelectedIndex(Rule.Tabs.LISTA_ACTUAL);
                }
            }
        }
    }//GEN-LAST:event_treeFavoritosMouseReleased

    private void cargarSave() {
        if (new File(Ruta.SAVE).exists()) {
            try {
                Guardar g = (Guardar) IO.leerObjetoDesde(Ruta.SAVE);

                canciones = g.canciones;

                /*Recuperando el volumen del usuario*/
                slideVol.setValue(g.volume);
                setVolumen(slideVol.getValue());

                /*Recuperando el volumen del usuario*/
                tabbedPrincipal.setSelectedIndex(g.indexTab);

                Log.setLogEntries(g.logEntries);
                tabbedPrincipal.setTitleAt(Rule.Tabs.LOGGER, "Logger (" + tableLogger.getRowCount() + ")");

                setCover(g.cover);

                biblioteca = (Biblioteca) IO.leerObjetoDesde(Ruta.BIBLIOTECA);

//                Scan scan = new Scan(biblioteca, this);
//                scan.scanner();
                //<editor-fold defaultstate="collapsed" desc="Código de scaneo de biblioteca antiguo">
//                boolean e = true;
//
//                for (Cancion c : biblioteca.getCanciones()) {
//                    if (!c.exists()) {
//                        e = false;
//                        break;
//                    }
//                }
//
//                if (!e) {
//                    int cont = 0;
//                    if (JOptionPane.showConfirmDialog(this, "Se ha encontrado por lo menos 1 canción que no existe en la biblioteca. ¿Desea analizar la biblioteca completa?") == JOptionPane.YES_OPTION) {
//
//                        List<Cancion> cancBiblio = biblioteca.getCanciones();
//                        cargarCancionesABiblioteca(cancBiblio);
//                        for (Cancion c : cancBiblio) {
//                            if (!c.exists()) {
//                                cont++;
//                            }
//                        }
//
//                        if (JOptionPane.showConfirmDialog(this, "Se han encontrado " + cont + " canciones que no existen. ¿Desea eliminarlas?") == JOptionPane.YES_OPTION) {
//                            int cant = biblioteca.removerNoExistentes();
//                            JOptionPane.showMessageDialog(this, "Se han eliminado " + cant + " canciones de la biblioteca!", "Info", JOptionPane.INFORMATION_MESSAGE);
//
//                            Iterator<Cancion> iterator = canciones.iterator();
//
//                            Cancion c;
//                            cant = 0;
//                            while (iterator.hasNext()) {
//                                c = iterator.next();
//
//                                if (!biblioteca.estaCancion(c)) {
//                                    iterator.remove();
//                                    cant++;
//                                }
//                            }
//
//                            if (cant != 0) {
//                                JOptionPane.showMessageDialog(this, "Se han eliminado " + cant + " canciones de la lista principal!", "Info", JOptionPane.INFORMATION_MESSAGE);
//                            }
//
//                        }
//                    }
//                }
//              </editor-fold>
                Log.add("Cantidad de canciones cargadas del save: " + canciones.size());
//                indiceActual = g.indiceActual;
//                System.out.println("Índice actual: " + indiceActual);

                cargarArbolConCanciones(g.albums);
                cargarCancionesABiblioteca(biblioteca.getCanciones());
                imprimirTemaActual();

            } catch (InvalidClassException ex) {
                Log.add("EX: " + ex.getMessage());
                biblioteca = new Biblioteca();
                canciones = biblioteca.getCanciones();
                cargarDefault();
            } catch (ClassNotFoundException | IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            cargarDefault();
        }

        switch (tabbedPrincipal.getSelectedIndex()) {
            case Rule.Tabs.BIBLIOTECA:
                lblInfoCarga.setText(biblioteca.getDuracionBiblioteca());
                break;
            case Rule.Tabs.EXPLORER:
                lblInfoCarga.setText(biblioteca.getDuracionBiblioteca());
                break;
            case Rule.Tabs.FAVORITOS:
                lblInfoCarga.setText(biblioteca.getDuracionFavoritos());
                break;
            case Rule.Tabs.LISTA_ACTUAL:
                lblInfoCarga.setText("Lista actual --> " + biblioteca.getDuracion(msListaActual));
                break;
            case Rule.Tabs.LOGGER:
                lblInfoCarga.setText(biblioteca.getDuracionBiblioteca());
                break;
            case Rule.Tabs.MAS_ESCUCHADAS:
                lblInfoCarga.setText(biblioteca.getDuracionMasEscuchadas());
                break;
        }
    }

    private void cargarArchivosEnNodoArbol(DefaultMutableTreeNode raiz, File ar) {
        List<File> ordenada = new ArrayList<>();
        if (ar.listFiles() != null) {
            File arConNombre;
            for (File a : ar.listFiles()) {
                if (Validar.isArchivoCorrecto(a)) {
                    try {
                        if (a.isDirectory() || Validar.isCancion(a)) {
                            /*Esto es solo para que se vea el nombre, por ende
                             tuve que sobre escribir el método toString*/
                            arConNombre = new File(a.getPath()) {

                                @Override
                                public String toString() {
                                    return this.getName();
                                }

                            };
                            /*Esto es solo para que se vea el nombre, por ende
                             tuve que sobre escribir el método toString*/

                            ordenada.add(arConNombre);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            //ordenar acá
            Collections.sort(ordenada, new Comparator<File>() {

                @Override
                public int compare(File f1, File f2) {
                    return f1.compareTo(f2);
                }
            });

            for (File f : ordenada) {
                raiz.add(new DefaultMutableTreeNode(f));
            }
        }
    }

    /**
     * Método para cargar canciones cuando el usuario las quiera escoger desde
     * el arbol con el click secundario
     *
     * @param raiz
     */
    private void cargarCancionesABiblioteca(File raiz) throws IOException, InterruptedException {
        if (raiz.listFiles() != null) {
            for (File a : raiz.listFiles()) {
                if (a.isDirectory()) {
                    cargarCancionesABiblioteca(a);
                } else if (Validar.isCancion(a)) {
                    Cancion c = new Cancion(a.getPath());
                    biblioteca.add(c);
                    lblInfoCarga.setText("Agregando " + c);
                }
            }
        } else {
            Cancion c = new Cancion(raiz.getPath());
            biblioteca.add(c);
            lblInfoCarga.setText("Agregando " + c);
        }
    }

    private void cargarCancionesALista(File raiz) throws IOException, InterruptedException {
        if (raiz.listFiles() != null) {
            for (File a : raiz.listFiles()) {
                if (a.isDirectory()) {
                    cargarCancionesALista(a);
                } else if (Validar.isCancion(a)) {
                    Cancion c = new Cancion(a.getPath());
                    canciones.add(c);
                }
            }
        }
    }

    public static void main(String args[]) {

//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JPlay().setVisible(true);
            }
        });
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedLookAndFeelException ex) {
//            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelarCarga;
    private javax.swing.JToggleButton btnFav;
    private javax.swing.JButton btnPause;
    private javax.swing.JButton btnTocarAnterior;
    private javax.swing.JButton btnTocarSiguiente;
    private javax.swing.JDialog dialogCanciones;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JLabel lblArtista;
    private javax.swing.JLabel lblCover;
    private javax.swing.JLabel lblInfoCarga;
    private javax.swing.JLabel lblLimpiar;
    private javax.swing.JLabel lblTema;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JCheckBox opAleatorio;
    private javax.swing.JCheckBox opRepetirCancion;
    private javax.swing.JPanel panelFavoritos;
    private javax.swing.JPanel panelListaActual;
    private javax.swing.JPanel panelLogger;
    private javax.swing.JPanel panelMasEscuchadas;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JProgressBar progress;
    private javax.swing.JSlider slideVol;
    private javax.swing.JTabbedPane tabbedPrincipal;
    private javax.swing.JTable tablaBiblioteca;
    private javax.swing.JTable tablaCanciones;
    private javax.swing.JTable tableLogger;
    private javax.swing.JTree treeExplorer;
    private javax.swing.JTree treeFavoritos;
    private javax.swing.JTree treeMasTocadas;
    private javax.swing.JTree treeSong;
    // End of variables declaration//GEN-END:variables

    private void setSlideTime(int totalBytes) {
        this.totalBytes = totalBytes;
        progress.setMaximum(totalBytes);
    }

    private void setSlideValue(int readedBytes) {
//        System.out.println("cambio al valor: "+valor);
//        int ancho = progress.getWidth();;
//        int pixActual = (valorEnMilis * ancho) / totalMilis;
//        progress.setValue(readedBytes);W
//        System.out.println(readedBytes);
        if (imprimirBarraDeProgreso) {
//            progress.setStringPainted(true);
            progress.setValue(readedBytes);
//            progress.setString(readedBytes + " bytes / " + totalBytes + " bytes");
        }

    }

    /*Este metodo se llama cuando hago click
     secundario en una carpeta del arbol y escogo la opcion
     crear lista. llamo a este metodo con la lista de carpetas*/
//    private void cargarCancionesEnLista(File[] ar, List<Cancion> canciones) throws InterruptedException {
//        Thread.sleep(100);
//        for(File f : ar){
//            if(f.isDirectory()){
//                cargarCancionesEnLista(f.listFiles(), canciones);
//            }else {
//                try {
//                    if(Validar.isCancion(f)){
//                        agregarCancion(new Cancion(f.getPath()), canciones);
//                    }
//                } catch (IOException ex) {
//                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }
    /**
     * Open callback, stream is ready to play.
     *
     * properties map includes audio format dependant features such as bitrate,
     * duration, frequency, channels, number of frames, vbr flag, ...
     *
     * @param stream could be File, URL or InputStream
     * @param properties audio stream properties.
     */
    @Override
    public void opened(Object stream, Map properties) {
//        System.out.println("OPENED: "+stream);
//        System.out.println(properties);
//        
//        if(stream instanceof Cancion){
//            
//        }

    }

    /**
     *
     * @param bytesread from encoded stream.
     * @param microseconds elapsed (<b>reseted after a seek !</b>).
     * @param pcmdata PCM samples.
     * @param properties audio stream parameters.
     */
    @Override
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
        int milis = (int) microseconds / 1000;

//        setSlideValue(milis);
        setSlideValue(bytesread);

        // acá esta el error. Ver bien esto!
//        imprimirTemaActual(milis);
//        System.out.println("BYTES LEIDOS: "+bytesread);
//        System.out.println(properties);
//        System.out.println("MS: "+microseconds);
    }

    @Override
    public void stateUpdated(BasicPlayerEvent bpe) {
        Log.add("STATE UPDATED: " + bpe.toString());
        switch (bpe.getCode()) {
            case BasicPlayerEvent.EOM:
                /*
                End of music: Se ejecuta esto cuando finaliza de tocar un tema
                 */
                if (!repetirCancion) {
                    if (isRandom) {
                        reproducirRandom();
                    } else {
                        reproducirSiguiente();
                    }
                } else {
                    reproducirCancionActual();
                }

                Log.add("END_OF_MUSIC");
                break;
            case BasicPlayerEvent.STOPPED:
                break;

            case BasicPlayerEvent.RESUMED:
                workerStringProgress.resume();
                break;
            case BasicPlayerEvent.PAUSED:
                workerStringProgress.pausar();
                break;
//            setSlideTime(bpe.getPosition());
            case BasicPlayerEvent.SEEKED:
                break;
            case BasicPlayerEvent.OPENED:
                // el indice es -1 cuando cargo desde la biblioteca
//            if (indiceActual != -1) {
                setSlideTime((int) reproductor.getCancionActual().length());
//            }
                break;
            default:
                break;
        }
    }

    @Override
    public void setController(BasicController bc) {
        Log.add("SET CONTROLLER: " + bc);
    }

    private void setVolumen(int vol) {
        try {
            if (reproductor != null) {
                reproductor.setVol(vol);
            }
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void crearArbolExplorer() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("raiz");
        File[] discos = File.listRoots();

        for (File disco : discos) {
            raiz.add(new DefaultMutableTreeNode(disco));
        }

        treeExplorer.setModel(new javax.swing.tree.DefaultTreeModel(raiz));
        treeExplorer.setRootVisible(false);

        treeExplorer.setCellRenderer(new CellRenderExplorer(
                new ImageIcon(
                        CellRenderExplorer.crearIcono(
                                Ruta.ICONO_MUSIC).getImage().
                                getScaledInstance(Rule.ICON_EXPLORER_MUSIC_SIZE,
                                        Rule.ICON_EXPLORER_MUSIC_SIZE,
                                        Image.SCALE_SMOOTH
                                )
                ),
                new ImageIcon(
                        CellRenderExplorer.crearIcono(
                                Ruta.ICONO_FOLDER).getImage().
                                getScaledInstance(Rule.ICON_EXPLORER_SIZE,
                                        Rule.ICON_EXPLORER_SIZE,
                                        Image.SCALE_SMOOTH
                                )
                )
        )
        );
    }

    /**
     * Este método carga los discos al arbol de lista actual
     *
     * @param disco
     */
    private void cargarArbolConCanciones(List<Album> albums) {
        this.albums = albums;
        //ordenar acá
        Log.add("Se cargaron " + canciones.size() + " canciones a la lista principal");

        Collections.sort(canciones, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return f1.compareTo(f2);
            }
        });

        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("raiz");

        if (albums != null) {
            DefaultMutableTreeNode disco;
            for (Album a : albums) {
                disco = new DefaultMutableTreeNode(a);

                for (Cancion cancion : a.getCanciones()) {
                    disco.add(new DefaultMutableTreeNode(cancion));
                }

                raiz.add(disco);
            }
        }

        treeSong.setModel(new DefaultTreeModel(raiz));
        treeSong.setRootVisible(false);

        treeSong.expandRow(0);

        treeSong.setCellRenderer(
                new CellRenderCancionLista()
        );

        btnCancelarCarga.setEnabled(false);
        //lblInfoCarga.setText("Se cargaron " + canciones.size() + " canciones a la lista principal");

        msListaActual = 0;
        for (Cancion c : canciones) {
            msListaActual += c.getDuracionEnMilis();
        }

        lblInfoCarga.setText("Lista actual --> " + biblioteca.getDuracion(msListaActual));
    }

    private void cargarArbolConCancionesMasEscuchadas() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("raiz");

        List<Cancion> masRepro = biblioteca.getCancionesMasReproducidas();

        for (Cancion c : masRepro) {
            DefaultMutableTreeNode disco = new DefaultMutableTreeNode(c);

            raiz.add(disco);
        }

        treeMasTocadas.setModel(new javax.swing.tree.DefaultTreeModel(raiz));
        treeMasTocadas.setRootVisible(false);
//        treeSong.expandRow(0);

        treeMasTocadas.setCellRenderer(
                new CellRenderCancionMasTocada(
                        CellRenderExplorer.crearIcono(Ruta.ICONO_PLAY_ARBOL),
                        CellRenderExplorer.crearIcono(Ruta.ICONO_CD_ARBOL)
                )
        );

        tabbedPrincipal.setTitleAt(Rule.Tabs.MAS_ESCUCHADAS, "+ escuchadas (" + masRepro.size() + ")");
    }

    private void cargarArbolConFavoritos() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("raiz");

        List<Cancion> favoritos = biblioteca.getFavoritos();

        for (Cancion c : favoritos) {
            DefaultMutableTreeNode disco = new DefaultMutableTreeNode(c);

            raiz.add(disco);
        }

        treeFavoritos.setModel(new javax.swing.tree.DefaultTreeModel(raiz));
        treeFavoritos.setRootVisible(false);
//        treeSong.expandRow(0);

        treeFavoritos.setCellRenderer(
                new CellRenderFavoritos()
        );

        tabbedPrincipal.setTitleAt(Rule.Tabs.FAVORITOS, "Favoritos (" + favoritos.size() + ")");
    }

    private void listenerClickDerechoSobreArbol() {
        /*Este codigo es para que cuando el usuario haga click secundario
         se seleccione la fila del arbol*/
        MouseListener ml = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {

                    int selRow = treeExplorer.getRowForLocation(e.getX(), e.getY());
                    TreePath selPath = treeExplorer.getPathForLocation(e.getX(), e.getY());
                    treeExplorer.setSelectionPath(selPath);
                    if (selRow > - 1) {
                        treeExplorer.setSelectionRow(selRow);
                        popUpTree.show(treeExplorer, e.getX() + 10, e.getY() + 10);
//                       popup.show(tree, e.getX(), e.getY());
                    }
                }
            }
        };
        treeExplorer.addMouseListener(ml);
    }

    private void crearPopUpTree() {
        popUpTree = new JPopupMenu();
        JMenuItem itemAlistaNueva = new JMenuItem("A lista nueva");
        JMenuItem itemAlistaExistente = new JMenuItem("Añadir a existente");
        JMenuItem itemABiblioteca = new JMenuItem("Añadir a biblioteca");

        JPopupMenu.Separator sep = new JPopupMenu.Separator();

        popUpTree.add(itemAlistaNueva);
        popUpTree.add(itemAlistaExistente);
        popUpTree.add(sep);
        popUpTree.add(itemABiblioteca);

        itemAlistaNueva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                final File f = getSelectedTreeFile();
                canciones = new ArrayList<>();
                hiloCargar = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        btnCancelarCarga.setEnabled(true);
                        try {
                            cargarCancionesALista(f);
                            cargarArbolConCanciones(getDiscos(canciones));
                        } catch (IOException ex) {
                            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InterruptedException ex) {
                            // lblInfoCarga.setText("Cancelado");
//                            lblInfoCarga.setText(biblioteca.getDuracionBiblioteca());
                        }
                    }
                });
                hiloCargar.start();
            }
        });

        itemAlistaExistente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                hiloCargar = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        btnCancelarCarga.setEnabled(true);
                        try {
                            File f = getSelectedTreeFile();
                            cargarCancionesALista(f);
                            cargarArbolConCanciones(getDiscos(canciones));
                        } catch (IOException ex) {
                            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                });
                hiloCargar.start();

            }
        });

        itemABiblioteca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                hiloCargar = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        btnCancelarCarga.setEnabled(true);
                        try {
                            File f = getSelectedTreeFile();

                            cargarCancionesABiblioteca(f);
                            cargarCancionesABiblioteca(biblioteca.getCanciones());
                            biblioteca.procesarAlbums();
                            biblioteca.addRuta(f);
                        } catch (IOException | InterruptedException ex) {
                            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        System.out.println("HILO CARGAR BIBLIOTECA TERMINADO!");
                    }
                });

                hiloCargar.start();
            }
        });
    }

    private void crearPopUpBiblioteca() {
        popUpBiblio = new JPopupMenu();
        JMenuItem itemRemoverDeBiblioteca = new JMenuItem("Remover");
        JMenuItem itemTocarDisco = new JMenuItem("Reproducir Disco");
        JMenuItem itemAnadirDisco = new JMenuItem("Añadir Disco");

        popUpBiblio.add(itemTocarDisco);
        popUpBiblio.add(itemAnadirDisco);
        popUpBiblio.add(new Separator());
        popUpBiblio.add(itemRemoverDeBiblioteca);

        itemTocarDisco.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selRows = tablaBiblioteca.getSelectedRows();

                List<Album> albums = new ArrayList<>();
                Cancion c;
                Album a;

                canciones = new ArrayList<>();

                for (int fila : selRows) {
                    c = (Cancion) tablaBiblioteca.getValueAt(fila, TMCancionBiblioteca.OBJETO_COMPLETO);
                    a = biblioteca.getAlbum(c);

                    if (!albums.contains(a)) {
                        albums.add(a);

                        for (Cancion can : a.getCanciones()) {
                            canciones.add(can);
                        }
                    }
                }

                cargarArbolConCanciones(getDiscos(canciones));
                reproducir(canciones.get(0));
                tabbedPrincipal.setSelectedIndex(Rule.Tabs.LISTA_ACTUAL);
            }
        });

        itemAnadirDisco.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selRows = tablaBiblioteca.getSelectedRows();

                List<Album> albums = new ArrayList<>();
                Cancion c;
                Album a;

                for (int fila : selRows) {
                    c = (Cancion) tablaBiblioteca.getValueAt(fila, TMCancionBiblioteca.OBJETO_COMPLETO);
                    a = biblioteca.getAlbum(c);

                    if (!albums.contains(a)) {
                        albums.add(a);

                        for (Cancion can : a.getCanciones()) {
                            canciones.add(can);
                        }
                    }
                }

                cargarArbolConCanciones(getDiscos(canciones));
            }
        });

        itemRemoverDeBiblioteca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int[] selRows = tablaBiblioteca.getSelectedRows();

                List<Cancion> canciones = new ArrayList<>();

                for (int fila : selRows) {
                    canciones.add((Cancion) tablaBiblioteca.getValueAt(fila, TMCancionBiblioteca.OBJETO_COMPLETO));
                }

                for (Cancion c : canciones) {
                    biblioteca.remover(c);
                }

                cargarCancionesABiblioteca(biblioteca.getCanciones());
            }
        });

    }

    private File getSelectedTreeFile() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeExplorer.getLastSelectedPathComponent();
        if (node != null) {
            Object o = node.getUserObject();
            if (o instanceof File) {
//                return (File) o; 
                return new File(((File) o).getPath());
                /*POr una razon que desconozco, el objeto File guardaba una referencia a Jplay
                por ende, cuando lo serializaba, enviaba un error.*/
            }
        }
        return null;
    }

    private void cargarCancionesABiblioteca(List<Cancion> lista) {
        //ordenar acá
        Collections.sort(lista, new Comparator<File>() {

            @Override
            public int compare(File f1, File f2) {
                return f1.compareTo(f2);
            }
        });
//        listaCanciones.setModel(new LMCancion(canciones));
        // sin titulos las tabla
//        tablaBiblioteca.getTableHeader().setUI(null);
//        tablaBiblioteca.setRowHeight(20);
        tablaBiblioteca.setModel(new TMCancionBiblioteca(lista));
        tabbedPrincipal.setTitleAt(Rule.Tabs.BIBLIOTECA, "Biblioteca (" + lista.size() + ")");
//        Log.add("Se cargaron " + lista.size() + " canciones en biblioteca");
//        lblInfoCarga.setText("Se cargaron " + lista.size() + " canciones en biblioteca");

        Log.add(biblioteca.getDuracionBiblioteca());
//        lblInfoCarga.setText(biblioteca.getDuracionBiblioteca());
//        tablaBiblioteca.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

//        System.out.println(tablaBiblioteca.getColumnModel().getColumn(0).getMinWidth());
//        System.out.println(tablaBiblioteca.getColumnModel().getColumn(1).getMinWidth());
//        System.out.println(tablaBiblioteca.getColumnModel().getColumn(2).getMinWidth());
//        System.out.println(tablaBiblioteca.getColumnModel().getColumn(3).getMinWidth());
        tablaBiblioteca.getColumnModel().getColumn(0).setPreferredWidth(Rule.TRACK_NUMBER_COLUMN_SIZE);
        tablaBiblioteca.getColumnModel().getColumn(1).setPreferredWidth(Rule.ARTIST_COLUMN_SIZE);
        tablaBiblioteca.getColumnModel().getColumn(2).setPreferredWidth(Rule.ALBUM_COLUMN_SIZE);
        tablaBiblioteca.getColumnModel().getColumn(3).setPreferredWidth(Rule.ARTIST_COLUMN_SIZE);
        /**/
//        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
//        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
//        tablaBiblioteca.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        /**/

        btnCancelarCarga.setEnabled(false);
    }

    private void reproducir(final Cancion cancion) {

        btnFav.setSelected(biblioteca.isFavorita(cancion));

        try {
            new Thread(() -> {
                procesarCover(cancion);
            }).start();
//            lblCaratula.setIcon(new ImageIcon(icono)); 

//            pnlCoverArt.updateUI();
            if (reproductor != null) {
                reproductor.stop();
            }

            reproductor = new Reproductor(cancion, this);

            reproductor.play();

            cancion.aumentarContadorReproducciones();

            setTitle(Rule.NOMBRE + " - " + Rule.VERSION + " [" + cancion.getAutor() + " - " + cancion.getNombre() + " (" + cancion.getCantidadReproducciones() + ")]");
            setVolumen(slideVol.getValue());
//            lblTemaActual.setText(c.getAutor()+" / "+c.getNombre() + " ("+c.getDuracionAsString()+")");

//            btnPause.setText("Pause");
            btnPause.setIcon(new ImageIcon(getClass().getResource(Ruta.PAUSE)));
            isPlay = true;
            isStop = false;

//            treeSong.setCellRenderer(
//                new CellRenderCancionLista(
//                        CellRenderExplorer.crearIcono("/jplay/recursos/iconos/1443349568_music.png"),
//                        CellRenderExplorer.crearIcono("/jplay/recursos/iconos/1453541047_emblem-cd.png")
//                )
//            );
//            System.out.println("Esta en biblioteca: "+biblioteca.estaCancion(cancion));
//            System.out.println("-----------------------------------------");
//            System.out.println("LISTADO DE MÁS REPRODUCCIONES");
//            System.out.println("-----------------------------------------");
//            List<Cancion> lista = biblioteca.getCancionesMasReproducidas();
//            for (Cancion c : lista) {
//                System.out.println("[" + c.getCantidadReproducciones() + "]" + c.getNombre());
//            }
//            System.out.println("-----------------------------------------");
            cargarArbolConCancionesMasEscuchadas();

            if (this.workerStringProgress != null) {
                this.workerStringProgress.cancel(true);
            }

            this.workerStringProgress = new WorkerStringProgress(progress, cancion.getDuracionAsString());

            this.workerStringProgress.execute();

            imprimirTemaActual();

        } catch (BasicPlayerException ex) {
            JOptionPane.showMessageDialog(this, "Error al reproducir: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void reproducirCancionActual() {
        reproducir(canciones.get(canciones.indexOf(reproductor.getCancionActual())));
    }

//    private int getIndice(File f) {
//        return canciones.indexOf(f);
//    }
//    private void agregarCancion(Cancion cancion, List<Cancion> lista) {
//        lista.add(cancion);
//        lblInfoCarga.setText("Agregando " + cancion);
////        System.out.println("Agregando "+cancion);
//    }
    private void imprimirTemaActual() {
        String durActual = "0:00";
        String durTotal = "()";

        if (reproductor != null) {
//            if(milis != 0){
//                durActual = r.getCancionActual().getDuracionAsString(milis) + " / "+ r.getCancionActual().getDuracionAsString();
//            }else{
//                durActual = "0:00 / "+ r.getCancionActual().getDuracionAsString();
//            }

            lblArtista.setText(reproductor.getCancionActual().getAutor());
            lblTema.setText(reproductor.getCancionActual().getNombre() + " (" + reproductor.getCancionActual().getDuracionAsString() + ")");
        }
//        else if (indiceActual != -1) {
//            Cancion c = canciones.get(indiceActual);
//            durActual = "0:00 / " + c.getDuracionAsString();
//            lblArtista.setText(c.getAutor());
//            lblTema.setText(c.getNombre() + " (" + durActual + ")");
//        }
    }

    private void cargarDefault() {
        cargarCancionesABiblioteca(biblioteca.getCanciones());
        cargarArbolConCanciones(null);
//        lblCover.setIcon(new ImageIcon(icono));
        setCover(icono);
    }

    private void crearListenerTitulosTabla() {
        tablaBiblioteca.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tablaBiblioteca.columnAtPoint(e.getPoint());
                String name = tablaBiblioteca.getColumnName(col);
                Log.add("Column index selected " + col + " " + name);
            }
        });
    }

    public int getRandom() {
        return new Random().nextInt(canciones.size());
    }

    private void tocarCancionSeleccionadaEnTablaBiblioteca() {
        int fila = tablaBiblioteca.getSelectedRow();
        Cancion c = (Cancion) tablaBiblioteca.getValueAt(fila, TMCancionBiblioteca.OBJETO_COMPLETO);

//        System.out.println(biblioteca.getAlbum(c));
        TMCancionBiblioteca model = (TMCancionBiblioteca) tablaBiblioteca.getModel();

        canciones = model.canciones;
//        indiceActual = fila;

        cargarArbolConCanciones(getDiscos(canciones));

//        tablaCanciones.getSelectionModel().setSelectionInterval(fila, fila);
        reproducir(c);
    }

    // Método que se llama cuando hago doble click en un tema musical
    // o cuando apreto enter en el arbol
    private void procesarCancionArbol() {
        final File f = getSelectedTreeFile();
        if (f != null) {
            try {
                if (Validar.isCancion(f)) {
                    canciones = new ArrayList<>();
//                        System.out.println(f.getParentFile());
                    hiloCargar = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            btnCancelarCarga.setEnabled(true);
                            try {
                                cargarCancionesALista(f.getParentFile());
                            } catch (InterruptedException | IOException ex) {
                                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            cargarArbolConCanciones(getDiscos(canciones));
                        }
                    });

                    hiloCargar.start();
                    reproducir(new Cancion(f.getPath()));
                }
            } catch (IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private List<Album> getDiscos(List<Cancion> lista) {
        List<Album> discos = new ArrayList<>();
        Album a;

        for (Cancion c : lista) {
            a = biblioteca.getAlbum(c);

            if (a != null) {
                if (!discos.contains(a)) {
                    discos.add(a);
                }
            }
        }

        return discos;
    }

//    private void inicializarBarraProgreso() {
//        int ancho = progress.getWidth();
//        progress.setMaximum(ancho);
//        progress.setValue(ancho);
//    }
    private void cambiarProgress(int porc, boolean seek) {

        final float value = totalBytes * ((float) porc / (float) 100);
        progress.setValue((int) value);
//        progress.setString(value + " bytes");

//        progress.setString(porc + "%");
        if (seek) {
            try {
                this.workerStringProgress.cambiar(porc);
                reproductor.seek((long) value);
            } catch (BasicPlayerException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void procesarCover(Cancion cancion) {
//        lblCover.setIcon(new ImageIcon(icono));
        setCover(icono);
        Album album = biblioteca.getAlbum(cancion);

//        panelPrincipal.setBackground(Color.white);
        if (!cancion.exists()) { // si canción no existe
            if (JOptionPane.showConfirmDialog(
                    this,
                    cancion.exists() + "[" + cancion.getNombre() + "] no encontrada. "
                    + "¿Desea analizar la lista completa para eliminar los no encontrados?", "Error",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                Iterator<Cancion> iterator = canciones.iterator();

                Cancion c;
                int cont = 0;
                while (iterator.hasNext()) {
                    c = iterator.next();

                    if (!c.exists()) {
                        canciones.remove(c);
                        cont++;
                    }
                }

                JOptionPane.showMessageDialog(this, "Se han eliminado " + cont + " canciones de la lista.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (!album.hasImagenes()) { // si el Album NO tiene una lista de imagenes
            Log.add("La canción no tiene imágenes asociadas!");
            List<ImageIcon> fotos = Recurso.getFotos(cancion);
            Log.add("Se han encontrado " + fotos.size() + " foto");

            if (!fotos.isEmpty()) {
                /*
                si la lista de fotos no esta vacía por lo menos hay una
                para poder comenzar el hilo de las caratulas
                 */
                album.setCovers(fotos);
                Log.add("Se añadió una lista de fotos a la cancion [" + fotos.size() + " fotos]");
            } else { // no hay imagenes en la carpeta de la canción
                List<ImageIcon> covers = new ArrayList<>();
                try {
                    Image imLastFM = LastFM.getImage(cancion.getAutor(), cancion.getAlbum());
                    imLastFM = imLastFM.getScaledInstance((int) Rule.COVER_DIMENSION.getWidth(),
                            (int) Rule.COVER_DIMENSION.getHeight(),
                            Image.SCALE_SMOOTH);

                    covers.add(new ImageIcon(imLastFM));

                    Log.add("Se añadió una image desde LastFM!");
                } catch (Exception ex) {
                    /*Establezco la caratula por defecto (el disco)*/
//                        icono = icono.getScaledInstance(
//                                (int) Rule.COVER_DIMENSION.getWidth(),
//                                (int) Rule.COVER_DIMENSION.getHeight(),
//                                Image.SCALE_SMOOTH);
                    covers.add(new ImageIcon(icono));
                    Log.add("Se añadió una caratula POR DEFECTO --> " + ex.getMessage());
                }
                album.setCovers(covers);
            }
        } else {
            Log.add("La canción tiene caratula!");
        }

        if (hCover != null) {
            hCover.interrupt();
        }

//        lblCover.setIcon(new ImageIcon(album.getCovers().get(0).getImage().getScaledInstance((int) Rule.COVER_DIMENSION.getWidth(),
//                (int) Rule.COVER_DIMENSION.getHeight(),
//                Image.SCALE_SMOOTH)));
        setCover(album.getCovers().get(0).getImage());

        setColorFondo(album);

        hCover = new HiloCover(lblCover, album.getCovers());
        hCover.start();

        setIconImage(album.getCovers().get(0).getImage());

        treeSong.setCellRenderer(
                new CellRenderCancionLista()
        );

//        Notification.show(
//            cancion.getAutor(),
//            cancion.getNombre(),
//            cover,
//            8000, // Segundos en milis
//            new Dimension(100, 100),
//            soloUno
//        );
    }

    private void initFonts() {
        try {
            Font fuente = Font.createFont(Font.TRUETYPE_FONT, Recurso.FUENTE_ROBOTO);

            lblTema.setFont(fuente.deriveFont(Font.BOLD, Rule.FONT_SIZE_NORMAL));
            lblArtista.setFont(fuente.deriveFont(Font.PLAIN, 11));
            opAleatorio.setFont(fuente.deriveFont(Font.PLAIN, 13));
            opRepetirCancion.setFont(fuente.deriveFont(Font.PLAIN, 13));
//            txtBuscar.setFont(fuente.deriveFont(Font.PLAIN, 13));
            tabbedPrincipal.setFont(fuente.deriveFont(Font.PLAIN, 13));
            tablaBiblioteca.setFont(fuente.deriveFont(Font.PLAIN, 14));
            lblInfoCarga.setFont(fuente.deriveFont(Font.BOLD, 13));
            btnCancelarCarga.setFont(fuente.deriveFont(Font.PLAIN, 13));

            /*
            TAMBIEN CAMBIAR FUENTES EN LOS CELL RENDERERS (xjplay.model.tree)
             */
        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Reproduce el siguiente, si es el último reproduce el primero
     */
    private void reproducirSiguiente() {
        int indexActual = canciones.indexOf(reproductor.getCancionActual());
        indexActual++;
        if (indexActual < canciones.size()) {
            reproducir(canciones.get(indexActual));
        } else {
            reproducir(canciones.get(0));
        }
    }

    /**
     * Reproduce el anterior, si es el primero reproduce el último
     */
    private void reproducirAnterior() {
        int indexActual = canciones.indexOf(reproductor.getCancionActual());
        indexActual--;
        if (indexActual >= 0) {
            reproducir(canciones.get(indexActual));
        } else {
            // reproduce el último
            reproducir(canciones.get(canciones.size() - 1));
        }
    }

    private void reproducirRandom() {
        reproducir(canciones.get(getRandom()));
    }

    @Override
    public void search(String filtro) {
        lFiltrada = new ArrayList<>();

        int i = 1;
        for (Cancion c : biblioteca.getCanciones()) {
            if (c.getAutor().toLowerCase().contains(filtro)
                    || c.getAlbum().toLowerCase().contains(filtro)
                    || c.getNombre().toLowerCase().contains(filtro)) {
                lFiltrada.add(c);
            }
        }

        cargarCancionesABiblioteca(lFiltrada);
    }

    private void initBuscar() {
        //<editor-fold defaultstate="collapsed" desc="Código para escuchar a un boton para todos los componentes" >
        /*CON CTRL + F y f3 funciona el buscar*/
        this.getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK), "buscar");
        this.getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0), "buscar");
        /*CON CTRL + F y f3 funciona el buscar*/

        this.getRootPane().getActionMap().put("buscar", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPrincipal.setSelectedIndex(Rule.Tabs.BIBLIOTECA);
                if (dialogBuscar == null) {
                    dialogBuscar = new DgBuscar(JPlay.this, false);
                    dialogBuscar.setIbuscar(JPlay.this);
                    dialogBuscar.setBounds(JPlay.this.getX(), JPlay.this.getY(), JPlay.this.getWidth(), dialogBuscar.getHeight());
                    dialogBuscar.setVisible(true);
                } else {
//                    dialogBuscar.resetTextField();
                    dialogBuscar.setBounds(JPlay.this.getX(), JPlay.this.getY(), JPlay.this.getWidth(), dialogBuscar.getHeight());
                    dialogBuscar.setVisible(!dialogBuscar.isVisible());
                }
            }
        });
        /*Código para escuchar a un boton para todos los componentes*/
        // </editor-fold>
    }

    // este metodo se llama cuando apreta enter en buscar
    @Override
    public void focusOn(String filtro) {
        try {
            if (filtro.startsWith("/")) {
                Log.add("comando! " + filtro);

                // si es un comando, despues cargo de nuevo la biblioteca
                cargarCancionesABiblioteca(biblioteca.getCanciones());

                String text = "";

                if (filtro.equalsIgnoreCase("/rutas")) {
                    Log.add("RUTAS:");
                    text += "RUTAS:\n";

                    for (File ruta : biblioteca.getRutas()) {
                        Log.add("\t" + ruta.getPath());
                        text += ruta.getPath() + "\n";
                    }

                    JOptionPane.showMessageDialog(this, text);
                } else if (filtro.equalsIgnoreCase("/scan")) {
                    Scan s = new Scan(biblioteca, this);
                    s.start();
                } else if (filtro.equalsIgnoreCase("/favs")) {
                    Log.add("Canciones favoritas:");
                    for (Cancion c : biblioteca.getFavoritos()) {
                        Log.add(c.toString());
                    }
                }
            }
            tablaBiblioteca.setRowSelectionInterval(0, 0);
        } catch (HeadlessException e) {
            // cae aca cuando no hay canciones en la tabla biblioteca
            Log.add(e.getMessage());
        }

    }

    @Override
    public void cargarComboDeBusqueda(JComboBox cbo) {

        cbo.removeAllItems();
        cbo.addItem("");

        for (String artista : biblioteca.getArtistas()) {
            cbo.addItem(artista);
        }

        for (Album album : biblioteca.getAlbums()) {
            if (!album.getName().trim().equals("")) {
                cbo.addItem(album.getName());
            }
        }
    }

    private void crearPopUpCover() {
        popCover = new JPopupMenu();

        JMenuItem itemEliminarCover = new JMenuItem("Eliminar Cover");

        //JPopupMenu.Separator sep = new JPopupMenu.Separator();
        itemEliminarCover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (hCover.isAlive()) {
                        Image actualCover = hCover.getActualCover();

                        hCover.interrupt();

                        Album a = biblioteca.getAlbum(reproductor.getCancionActual());

                        a.removeImage(new ImageIcon(actualCover));

                        hCover = new HiloCover(lblCover, a.getCovers());
                        hCover.start();
                    }
                } catch (NullPointerException ex) {
                    Log.add("Objeto HCOVER es nulo");
                }
            }
        });

        popCover.add(itemEliminarCover);

        lblCover.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mostrarPopUpCover(e);
            }
        });
    }

    private void mostrarPopUpCover(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            popCover.show(e.getComponent(), e.getX() + 8, e.getY() + 8);
        }
    }

    private void initDragDropTabbedPane() {
        tabbedPrincipal.setDropTarget(new DropTarget(this, new DropTargetListener() {
            @Override
            public void drop(DropTargetDropEvent dtde) {

                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    Object o = dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                    final List<File> archs = (List) o;

                    hiloCargar = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (File f : archs) {
                                try {
//                                    Log.add(f.toString());

                                    cargarCancionesABiblioteca(f);
                                    cargarCancionesABiblioteca(biblioteca.getCanciones());
                                    biblioteca.procesarAlbums();
                                    biblioteca.addRuta(f);
                                } catch (IOException | InterruptedException ex) {
                                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            System.out.println("HILO CARGAR DROP TERMINADO!");
                        }
                    });

                    hiloCargar.start();
                } catch (UnsupportedFlavorException | IOException ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                tabActual = tabbedPrincipal.getSelectedIndex();
                tabbedPrincipal.setSelectedIndex(Rule.Tabs.BIBLIOTECA);
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
            }

            @Override
            public void dragExit(DropTargetEvent dte) {
                tabbedPrincipal.setSelectedIndex(tabActual);
            }
        }));
    }

    /*Este método sirve para actualizar la tabla de biblioteca despues del scan (Clase Scan)*/
    @Override
    public void updateBibliotecaUI(boolean huboCambios) {
        if (huboCambios) {
            cargarCancionesABiblioteca(biblioteca.getCanciones());
            biblioteca.procesarAlbums();
        } else {
            Log.add("[No hubo cambios en la biblioteca]");
        }
    }

    @Override
    public void updateLogUI(LogEntry newLogEntry) {
        try {
            tableLogger.updateUI();
        } catch (NullPointerException e) {

        }
        tabbedPrincipal.setTitleAt(Rule.Tabs.LOGGER, "Logger (" + tableLogger.getRowCount() + ")");
    }

    private void initLog() {
        Log.setUpdateLogUI(this);
        modelLog = new TMLog();
        tableLogger.setModel(modelLog);
    }

    private void initIconosTabs() {
        tabbedPrincipal.setIconAt(Rule.Tabs.EXPLORER, CellRenderCancionLista.crearIcono(Ruta.IC_TAB_EXPLORER));
        tabbedPrincipal.setIconAt(Rule.Tabs.BIBLIOTECA, CellRenderCancionLista.crearIcono(Ruta.IC_TAB_BIBLIOTECA));
        tabbedPrincipal.setIconAt(Rule.Tabs.LISTA_ACTUAL, CellRenderCancionLista.crearIcono(Ruta.IC_TAB_LISTA));
        tabbedPrincipal.setIconAt(Rule.Tabs.MAS_ESCUCHADAS, CellRenderCancionLista.crearIcono(Ruta.IC_TAB_ESCUCHADAS));
        tabbedPrincipal.setIconAt(Rule.Tabs.LOGGER, CellRenderCancionLista.crearIcono(Ruta.IC_TAB_LOG));
        tabbedPrincipal.setIconAt(Rule.Tabs.FAVORITOS, new ImageIcon(CellRenderCancionLista.crearIcono(Ruta.IC_TAB_FAVORITOS).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
    }

    private void setColorFondo(Album album) {
        /* Colores de fondo */
        Util.COLOR_FONDO = album.getColorPromedio();
        Util.COLOR_FOREGROUND = Util.getForeGroundColorBasedOnBGBrightness(Util.COLOR_FONDO);

        panelPrincipal.setBackground(Util.COLOR_FONDO);
        slideVol.setBackground(Util.COLOR_FONDO);
        lblTema.setForeground(Util.COLOR_FOREGROUND);
        lblArtista.setForeground(Util.COLOR_FOREGROUND);
        opRepetirCancion.setForeground(Util.COLOR_FOREGROUND);
        opAleatorio.setForeground(Util.COLOR_FOREGROUND);
        lblInfoCarga.setForeground(Util.COLOR_FOREGROUND);

        progress.setUI(new BasicProgressBarUI() {
            @Override
            protected Color getSelectionBackground() {
                return Util.COLOR_FONDO;
            }

            @Override
            protected Color getSelectionForeground() {
                return Util.COLOR_FOREGROUND;
            }
        });

        Color c = Util.COLOR_FONDO.darker().darker();
        tabbedPrincipal.setBackground(c);
        tabbedPrincipal.setForeground(Util.getForeGroundColorBasedOnBGBrightness(c));
    }

    private void setCover(Icon cover) {

        BufferedImage image = new BufferedImage(cover.getIconWidth(), cover.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        cover.paintIcon(null, image.getGraphics(), 0, 0);

        lblCover.setIcon(new ImageIcon(
                image.getScaledInstance(
                        (int) Rule.COVER_DIMENSION.getWidth(),
                        (int) Rule.COVER_DIMENSION.getHeight(),
                        Image.SCALE_SMOOTH)
        )
        );
    }

    private void setCover(Image cover) {
        lblCover.setIcon(new ImageIcon(
                cover.getScaledInstance(
                        (int) Rule.COVER_DIMENSION.getWidth(),
                        (int) Rule.COVER_DIMENSION.getHeight(),
                        Image.SCALE_SMOOTH)
        )
        );
    }
    
}
