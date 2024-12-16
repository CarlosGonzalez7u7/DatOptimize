package vista;

import Dao.DaoCargo;
import Dao.DaoEmpleados;
import Dao.DaoNotas;
import Dao.DaoPendientes;
import Dao.DaoPiezas;
import Dao.conexion;
import com.itextpdf.text.BadElementException;
import com.mysql.cj.jdbc.PreparedStatementWrapper;
import java.awt.Color;
import java.io.IOException;
import static java.lang.String.join;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import modelo.Cambio;
import modelo.CustomRenderer;
import modelo.cargo;
import modelo.empleados;
import modelo.notas;
import modelo.pendientes;
import modelo.piezas;

/**
 *
 * @author juanc
 */
public class MenuPrincipal extends javax.swing.JFrame {

    private Set<Integer> filasResaltadas;
    private List<Cambio> cambiosRealizados = new ArrayList<>();
    // Declarar la lista al principio de tu clase
    List<String> nombresPiezasAcumuladas = new ArrayList<>();

    cargo cr = new cargo();
    DaoCargo daoC = new DaoCargo();
    DefaultTableModel modeloCargo = new DefaultTableModel();

    /////////////////////////////////////////////
    notas nt = new notas();
    DaoNotas daoN = new DaoNotas();
    DefaultTableModel modeloNotas = new DefaultTableModel();

    /////////////////////////////////////////////
    piezas pi = new piezas();
    DaoPiezas daoP = new DaoPiezas();
    DefaultTableModel modeloPiezas = new DefaultTableModel();

    /////////////////////////////////////////////
    empleados em = new empleados();
    DaoEmpleados daoE = new DaoEmpleados();
    DefaultTableModel modeloEmpleado = new DefaultTableModel();

    /////////////////////////////////////////////
    pendientes pe = new pendientes();
    DaoPendientes daoPendiente = new DaoPendientes();
    DefaultTableModel modeloPendiente = new DefaultTableModel();

    private void cerrarAplicacion() {
        // Guardar las filas resaltadas al cerrar la aplicación
        guardarFilasResaltadas(filasResaltadas);
    }

    /**
     * Creates new form MenuPrincipal
     */
    public MenuPrincipal() {
        initComponents();
        this.setLocationRelativeTo(null);
        listar();
        listarNotas();
        listarPiezas();
        //Se manda a llamar para que se muestre al ejecutar
        listarEmpleado();
        listarPendientes();
        // Inicializa el conjunto de filas resaltadas desde las preferencias compartidas
        filasResaltadas = cargarFilasResaltadas();
        cargarEstadoInicial();

        //Primer paso
        btnEnviarCargo.setEnabled(false);
        btnEnviarNotas.setEnabled(false);
        btnEnviarEmpleado.setEnabled(false);
        btnEnviarPieza.setEnabled(false);

        txtPiezasAcumuladas.setEnabled(false);
        txtSumatoria.setEnabled(false);
        btnAñadirPrecio.setEnabled(false);
        comboEstado_maquina.setEnabled(false);
        combotipo_Atencion.setEnabled(false);

        //
        // Configura el JDateChooser con la fecha actual
        jdcFecha.setDate(Calendar.getInstance().getTime());
        jdcFechaPendiente.setDate(Calendar.getInstance().getTime());
        jdcFechaEmpleados.setDate(Calendar.getInstance().getTime());

        // Llama al método para añadir la fecha automáticamente
        btnAñadirFechaActionPerformed(null);
        btnAñadirFechaPenActionPerformed(null);
        btnAñadirFechaEmpleadosActionPerformed(null);

    }

    ////////////////////////////////////////////
    private void cargarEstadoInicialTabla() {
    // Carga las filas resaltadas desde las preferencias compartidas
    filasResaltadas = cargarFilasResaltadas();

    // Eliminar todas las filas resaltadas para evitar que se mantengan marcadas durante una nueva búsqueda
    filasResaltadas.clear();

    // Redibujar la tabla para que se apliquen los cambios en el renderizador
    tablaNotas.repaint();
    CustomRenderer customRenderer = new CustomRenderer(filasResaltadas);
    tablaNotas.setDefaultRenderer(Object.class, customRenderer);

    // Última acción
    panel.setSelectedComponent(pPendientes);
}
    private void cargarEstadoInicial() {
        // Carga las filas resaltadas desde las preferencias compartidas
        filasResaltadas = cargarFilasResaltadas();

        // Redibujar la tabla para que se apliquen los cambios en el renderizador
        tablaNotas.repaint();
        CustomRenderer customRenderer = new CustomRenderer(filasResaltadas);
        tablaNotas.setDefaultRenderer(Object.class, customRenderer);

        // Última acción
        panel.setSelectedComponent(pPendientes);
    }

    // Método para guardar las filas resaltadas en las preferencias compartidas
    private void guardarFilasResaltadas(Set<Integer> filasResaltadas) {
        try {
            Preferences prefs = Preferences.userNodeForPackage(MenuPrincipal.class);
            String filasResaltadasStr = join(",", filasResaltadas);
            prefs.put("filasResaltadas", filasResaltadasStr);
            System.out.println("Filas resaltadas guardadas: " + filasResaltadasStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// Método para cargar las filas resaltadas desde las preferencias compartidas
    private Set<Integer> cargarFilasResaltadas() {
        Preferences prefs = Preferences.userNodeForPackage(MenuPrincipal.class);
        String filasResaltadasStr = prefs.get("filasResaltadas", "");

        Set<Integer> filasResaltadas = new HashSet<>();
        if (!filasResaltadasStr.isEmpty()) {
            String[] partes = filasResaltadasStr.split(",");
            for (String parte : partes) {
                filasResaltadas.add(Integer.parseInt(parte));
            }
        }

        return filasResaltadas;
    }

// Método para añadir este método a tu clase para emular la funcionalidad de String.join
    private String join(String separator, Set<Integer> elements) {
        StringBuilder result = new StringBuilder();
        for (Integer element : elements) {
            if (result.length() > 0) {
                result.append(separator);
            }
            result.append(element);
        }
        return result.toString();
    }

    ///////////////////////////////////////////////
    //////////////////////////////METODOS DE LISTAR/////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private void listar() {
        List<cargo> lista = daoC.Listar();
        modeloCargo = (DefaultTableModel) tablacargos.getModel();
        Object[] ob = new Object[2];
        for (int i = 0; i < lista.size(); i++) {
            ob[0] = lista.get(i).getId();
            ob[1] = lista.get(i).getNombre();
            modeloCargo.addRow(ob);
        }
        tablacargos.setModel(modeloCargo);
    }

    /////////////////////////////////////////////////////////////////////////////
    private void listarNotas() {
        List<notas> lista = daoN.ListarNotas();
        modeloNotas = (DefaultTableModel) tablaNotas.getModel();

        Object[] ob = new Object[9];
        for (int i = 0; i < lista.size(); i++) {
            ob[0] = lista.get(i).getId_notaRec();
            ob[1] = lista.get(i).getNom_Clientes();
            ob[2] = lista.get(i).getNom_maquina();
            ob[3] = lista.get(i).getMarca_Maquina();
            ob[4] = lista.get(i).getModelo_maquina();
            ob[5] = lista.get(i).getFecha();
            ob[6] = lista.get(i).getAtencion();
            ob[7] = lista.get(i).getObservaciones();
            ob[8] = lista.get(i).getEstado_Maquina();

            modeloNotas.addRow(ob);
        }
        tablaNotas.setModel(modeloNotas);
    }

    ///////////////////////////////////////////////////////////////
    private void listarPiezas() {
        List<piezas> lista = daoP.ListarPiezas();
        modeloPiezas = (DefaultTableModel) tablaPiezas.getModel();

        Object[] ob = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            ob[0] = lista.get(i).getId_Pieza();
            ob[1] = lista.get(i).getNom_Pieza();
            ob[2] = lista.get(i).getMarca_Pieza();
            ob[3] = lista.get(i).getModelo_Pieza();
            ob[4] = lista.get(i).getCantidad();
            ob[5] = lista.get(i).getPrecio_Pieza();

            modeloPiezas.addRow(ob);
        }
        tablaPiezas.setModel(modeloPiezas);
    }

    ///////////////////////////////////////////////////////////////
    private void listarEmpleado() {
        try {
            DefaultTableModel modelo;
            modelo = daoE.listarEmpleados();
            tablaEmpleado.setModel(modelo);
        } catch (Exception e) {

        }
    }

    /////////////////////////////////////////////////////////////////////////////
    private void listarPendientes() {
        try {
            DefaultTableModel modelo;
            modelo = daoPendiente.listarPendientes();
            tablaPendientes.setModel(modelo);
        } catch (Exception e) {

        }
    }

    ///////////////////////////////////////////////////////////////
    /////////////////////FINAL METODOS LISTAR/////////////////////////////////////
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnUsuarios = new javax.swing.JButton();
        btnEmpleados = new javax.swing.JButton();
        btnCargo = new javax.swing.JButton();
        btnPendientes = new javax.swing.JButton();
        btnPiezas = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        panel = new javax.swing.JTabbedPane();
        pUsuarios = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtid_notaRec = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtnom_Clientes = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtnom_maquina = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtmarca_maquina = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtmodelo_maquina = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtFecha = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        combotipo_Atencion = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        txtObservaciones = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaNotas = new javax.swing.JTable();
        jLabel19 = new javax.swing.JLabel();
        comboEstado_maquina = new javax.swing.JComboBox<>();
        btnRegistrarNotas = new javax.swing.JButton();
        btnEditarNotas = new javax.swing.JButton();
        btnEliminarNotas = new javax.swing.JButton();
        btnBuscarNotas = new javax.swing.JButton();
        txtBuscarNotas = new javax.swing.JTextField();
        jdcFecha = new com.toedter.calendar.JDateChooser();
        btnAñadirFecha = new javax.swing.JButton();
        btnEnviarNotas = new javax.swing.JButton();
        btnImprimirNota = new javax.swing.JButton();
        pEmpleados = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tablaEmpleado = new javax.swing.JTable();
        btnRegistrarEmpleado = new javax.swing.JButton();
        btnModificarEmpleado = new javax.swing.JButton();
        txtEliminarEmpleado = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        txtIdCargoEmpleado = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        txtCargoEmpleado = new javax.swing.JTextField();
        btnBuscaCargo = new javax.swing.JButton();
        btnEnviarEmpleado = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        txtid_Empleado = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txtNombreEmpleados = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txtFechaEmpleados = new javax.swing.JTextField();
        jdcFechaEmpleados = new com.toedter.calendar.JDateChooser();
        btnAñadirFechaEmpleados = new javax.swing.JButton();
        pCargos = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtidcargo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtcargo = new javax.swing.JTextField();
        btnRegistrar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablacargos = new javax.swing.JTable();
        btnEditar = new javax.swing.JButton();
        btnEliminarCargo = new javax.swing.JButton();
        btnEnviarCargo = new javax.swing.JButton();
        pPiezas = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tablaPiezas = new javax.swing.JTable();
        txtid_Pieza = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        txtNombrePieza = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        txtMarca_pieza = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        txtModelo_pieza = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        txtCantidad_Piezas = new javax.swing.JTextField();
        btnRegistrarPiezas = new javax.swing.JButton();
        btnEditarPiezas = new javax.swing.JButton();
        btneliminarPiezas = new javax.swing.JButton();
        jLabel52 = new javax.swing.JLabel();
        txt_Precio = new javax.swing.JTextField();
        btnBuscarPiezas = new javax.swing.JButton();
        txtBuscarPiezas = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel51 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        txtPiezasAcumuladas = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        txtSumatoria = new javax.swing.JTextField();
        btnAñadirPrecio = new javax.swing.JButton();
        btnEliminarSeleccion = new javax.swing.JButton();
        txtCantidadFinal = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        btnEnviarPieza = new javax.swing.JButton();
        pPendientes = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaPendientes = new javax.swing.JTable();
        btnRegistrarMaquinas = new javax.swing.JButton();
        btnEditar1 = new javax.swing.JButton();
        btnEliminarPendientes = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txtid_Registro = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtCausantePendiente = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        comboReparada = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        txtFechaPendiente = new javax.swing.JTextField();
        jdcFechaPendiente = new com.toedter.calendar.JDateChooser();
        btnAñadirFechaPen = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        txtTotalPendiente = new javax.swing.JTextField();
        txtidnotarecPendiente = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtClientePendiente = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtMaquinaPendiente = new javax.swing.JTextField();
        btnBuscaNotaPendiente = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        txtEmpleadoPendiente = new javax.swing.JTextField();
        btnBuscarEmpleadoPendiente = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        txtPiezasPendiente = new javax.swing.JTextField();
        btnBuscarPiezaPendiente = new javax.swing.JButton();
        txtidEmpleadoPendiente = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtidPiezasPendiente = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        txtBuscarPendiente = new javax.swing.JTextField();
        btnBuscarPendiente = new javax.swing.JButton();
        btnImprimirRecibo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnUsuarios.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUsuarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/notas.png"))); // NOI18N
        btnUsuarios.setText("Registrar");
        btnUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUsuariosActionPerformed(evt);
            }
        });

        btnEmpleados.setBackground(new java.awt.Color(204, 255, 255));
        btnEmpleados.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEmpleados.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/empleados.png"))); // NOI18N
        btnEmpleados.setText("Empleados");
        btnEmpleados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmpleadosActionPerformed(evt);
            }
        });

        btnCargo.setBackground(new java.awt.Color(204, 204, 0));
        btnCargo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCargo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/datos-del-usuario.png"))); // NOI18N
        btnCargo.setText("Cargos");
        btnCargo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargoActionPerformed(evt);
            }
        });

        btnPendientes.setBackground(new java.awt.Color(255, 51, 51));
        btnPendientes.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnPendientes.setForeground(new java.awt.Color(255, 255, 255));
        btnPendientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/caducado.png"))); // NOI18N
        btnPendientes.setText("Reparar");
        btnPendientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPendientesActionPerformed(evt);
            }
        });

        btnPiezas.setBackground(new java.awt.Color(51, 51, 255));
        btnPiezas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnPiezas.setForeground(new java.awt.Color(255, 255, 255));
        btnPiezas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/trabajo-en-progreso.png"))); // NOI18N
        btnPiezas.setText("Piezas");
        btnPiezas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPiezasActionPerformed(evt);
            }
        });

        btnCerrar.setBackground(new java.awt.Color(255, 0, 0));
        btnCerrar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar-sesion (1).png"))); // NOI18N
        btnCerrar.setText("Cerrar Sesión");
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCerrar)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnEmpleados, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCargo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnUsuarios, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnPendientes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnPiezas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(btnUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnPendientes, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCargo, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnPiezas, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCerrar)
                .addGap(21, 21, 21))
        );

        pUsuarios.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setText("ID Nota: ");

        txtid_notaRec.setEnabled(false);

        jLabel4.setText("Nombre Cliente:");

        jLabel6.setText("Nombre Maquina:");

        jLabel7.setText("Marca Maquina:");

        jLabel8.setText("Modelo Maquina:");

        jLabel9.setText("Fecha Llegada:");

        txtFecha.setEnabled(false);

        jLabel10.setText("Atención:");

        combotipo_Atencion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pendiente", "Reparado", " " }));

        jLabel11.setText("Observaciones:");

        tablaNotas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Nota", "Cliente", "Maquina", "Marca", "Modelo", "Fecha Llegada", "Atención", "Observaciones", "Servicio"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaNotas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaNotasMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaNotas);

        jLabel19.setText("Servicio:");

        comboEstado_maquina.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Reparacion", "Mantenimiento", "Otro..." }));

        btnRegistrarNotas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/registrar.png"))); // NOI18N
        btnRegistrarNotas.setText("Registrar");
        btnRegistrarNotas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarNotasActionPerformed(evt);
            }
        });

        btnEditarNotas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/editar.png"))); // NOI18N
        btnEditarNotas.setText("Modificar");
        btnEditarNotas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarNotasActionPerformed(evt);
            }
        });

        btnEliminarNotas.setBackground(new java.awt.Color(255, 51, 51));
        btnEliminarNotas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEliminarNotas.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarNotas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/eliminar.png"))); // NOI18N
        btnEliminarNotas.setText("Eliminar");
        btnEliminarNotas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarNotasActionPerformed(evt);
            }
        });

        btnBuscarNotas.setBackground(new java.awt.Color(102, 204, 255));
        btnBuscarNotas.setFont(new java.awt.Font("Segoe UI Emoji", 1, 14)); // NOI18N
        btnBuscarNotas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/busqueda.png"))); // NOI18N
        btnBuscarNotas.setText("Buscar:");
        btnBuscarNotas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarNotasActionPerformed(evt);
            }
        });

        jdcFecha.setEnabled(false);

        btnAñadirFecha.setBackground(new java.awt.Color(204, 255, 204));
        btnAñadirFecha.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        btnAñadirFecha.setText("Añadir");
        btnAñadirFecha.setEnabled(false);
        btnAñadirFecha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAñadirFechaActionPerformed(evt);
            }
        });

        btnEnviarNotas.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        btnEnviarNotas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/enviar-mensaje.png"))); // NOI18N
        btnEnviarNotas.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnEnviarNotas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarNotasActionPerformed(evt);
            }
        });

        btnImprimirNota.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        btnImprimirNota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/impresora.png"))); // NOI18N
        btnImprimirNota.setText("Imprimir Nota");
        btnImprimirNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirNotaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pUsuariosLayout = new javax.swing.GroupLayout(pUsuarios);
        pUsuarios.setLayout(pUsuariosLayout);
        pUsuariosLayout.setHorizontalGroup(
            pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pUsuariosLayout.createSequentialGroup()
                .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pUsuariosLayout.createSequentialGroup()
                            .addGap(14, 14, 14)
                            .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel6)
                                .addGroup(pUsuariosLayout.createSequentialGroup()
                                    .addComponent(jLabel7)
                                    .addGap(8, 8, 8))
                                .addComponent(jLabel4)
                                .addComponent(jLabel19)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pUsuariosLayout.createSequentialGroup()
                            .addGap(54, 54, 54)
                            .addComponent(jLabel3)))
                    .addComponent(btnEnviarNotas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pUsuariosLayout.createSequentialGroup()
                        .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pUsuariosLayout.createSequentialGroup()
                                .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtmarca_maquina)
                                    .addComponent(txtnom_maquina)
                                    .addComponent(txtnom_Clientes)
                                    .addComponent(txtid_notaRec))
                                .addGap(101, 101, 101))
                            .addGroup(pUsuariosLayout.createSequentialGroup()
                                .addComponent(comboEstado_maquina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pUsuariosLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtmodelo_maquina)
                                .addGap(428, 428, 428))
                            .addGroup(pUsuariosLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(combotipo_Atencion, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(pUsuariosLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jdcFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAñadirFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(pUsuariosLayout.createSequentialGroup()
                        .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pUsuariosLayout.createSequentialGroup()
                                .addComponent(btnRegistrarNotas, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEditarNotas, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminarNotas, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(117, 117, 117))
                            .addGroup(pUsuariosLayout.createSequentialGroup()
                                .addComponent(btnBuscarNotas)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscarNotas, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnImprimirNota)
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(pUsuariosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1028, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(64, Short.MAX_VALUE))
        );
        pUsuariosLayout.setVerticalGroup(
            pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pUsuariosLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtid_notaRec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtmodelo_maquina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtnom_Clientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel9))
                        .addComponent(btnAñadirFecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jdcFecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(combotipo_Atencion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(comboEstado_maquina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pUsuariosLayout.createSequentialGroup()
                        .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtnom_maquina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addGap(18, 18, 18)
                        .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtmarca_maquina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txtObservaciones))
                .addGap(18, 18, 18)
                .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEditarNotas)
                    .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnRegistrarNotas)
                        .addComponent(btnEliminarNotas)
                        .addComponent(btnEnviarNotas, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21)
                .addGroup(pUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBuscarNotas)
                    .addComponent(txtBuscarNotas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnImprimirNota))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panel.addTab("Notas", pUsuarios);

        pEmpleados.setBackground(new java.awt.Color(255, 255, 255));

        tablaEmpleado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaEmpleado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaEmpleadoMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tablaEmpleado);

        btnRegistrarEmpleado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/registrar.png"))); // NOI18N
        btnRegistrarEmpleado.setText("Registrar");
        btnRegistrarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarEmpleadoActionPerformed(evt);
            }
        });

        btnModificarEmpleado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/editar.png"))); // NOI18N
        btnModificarEmpleado.setText("Modificar");
        btnModificarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarEmpleadoActionPerformed(evt);
            }
        });

        txtEliminarEmpleado.setBackground(new java.awt.Color(255, 51, 51));
        txtEliminarEmpleado.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtEliminarEmpleado.setForeground(new java.awt.Color(255, 255, 255));
        txtEliminarEmpleado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/eliminar.png"))); // NOI18N
        txtEliminarEmpleado.setText("Dar de Baja");
        txtEliminarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEliminarEmpleadoActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Cargo"));

        jLabel21.setText("ID:");

        txtIdCargoEmpleado.setEnabled(false);

        jLabel53.setText("Cargo:");

        txtCargoEmpleado.setEnabled(false);

        btnBuscaCargo.setFont(new java.awt.Font("SimSun", 0, 14)); // NOI18N
        btnBuscaCargo.setText("Seleccionar");
        btnBuscaCargo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnBuscaCargo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscaCargoActionPerformed(evt);
            }
        });

        btnEnviarEmpleado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/enviar-mensaje.png"))); // NOI18N
        btnEnviarEmpleado.setText("Enviar");
        btnEnviarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarEmpleadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel53)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnEnviarEmpleado)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btnBuscaCargo)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCargoEmpleado, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                            .addComponent(txtIdCargoEmpleado))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtIdCargoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(txtCargoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuscaCargo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEnviarEmpleado)
                .addGap(12, 12, 12))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Empleado"));

        jLabel24.setText("ID Empleado:");

        txtid_Empleado.setEnabled(false);

        jLabel25.setText("Nombre:");

        jLabel26.setText("Fecha Registro:");

        txtFechaEmpleados.setEnabled(false);

        jdcFechaEmpleados.setEnabled(false);

        btnAñadirFechaEmpleados.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        btnAñadirFechaEmpleados.setText("...");
        btnAñadirFechaEmpleados.setEnabled(false);
        btnAñadirFechaEmpleados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAñadirFechaEmpleadosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFechaEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jdcFechaEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAñadirFechaEmpleados))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel24)
                            .addComponent(jLabel25))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtid_Empleado, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNombreEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(txtid_Empleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(txtNombreEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel26)
                        .addComponent(txtFechaEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jdcFechaEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAñadirFechaEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pEmpleadosLayout = new javax.swing.GroupLayout(pEmpleados);
        pEmpleados.setLayout(pEmpleadosLayout);
        pEmpleadosLayout.setHorizontalGroup(
            pEmpleadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4)
            .addGroup(pEmpleadosLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(pEmpleadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pEmpleadosLayout.createSequentialGroup()
                        .addComponent(btnRegistrarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModificarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEliminarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(47, 47, 47)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 449, Short.MAX_VALUE))
        );
        pEmpleadosLayout.setVerticalGroup(
            pEmpleadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pEmpleadosLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(pEmpleadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addGroup(pEmpleadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegistrarEmpleado)
                    .addComponent(btnModificarEmpleado)
                    .addComponent(txtEliminarEmpleado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                .addContainerGap())
        );

        panel.addTab("Empleados", pEmpleados);

        pCargos.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("ID:");

        txtidcargo.setEnabled(false);

        jLabel2.setText("Cargo:");

        btnRegistrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/registrar.png"))); // NOI18N
        btnRegistrar.setText("Registrar");
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        tablacargos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nombre Cargo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablacargos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablacargosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablacargos);

        btnEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/editar.png"))); // NOI18N
        btnEditar.setText("Modificar");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnEliminarCargo.setBackground(new java.awt.Color(255, 51, 51));
        btnEliminarCargo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEliminarCargo.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarCargo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/eliminar.png"))); // NOI18N
        btnEliminarCargo.setText("Eliminar");
        btnEliminarCargo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarCargoActionPerformed(evt);
            }
        });

        btnEnviarCargo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/enviar-mensaje.png"))); // NOI18N
        btnEnviarCargo.setText("Enviar");
        btnEnviarCargo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarCargoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pCargosLayout = new javax.swing.GroupLayout(pCargos);
        pCargos.setLayout(pCargosLayout);
        pCargosLayout.setHorizontalGroup(
            pCargosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pCargosLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(pCargosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGroup(pCargosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pCargosLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(txtidcargo, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pCargosLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pCargosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEnviarCargo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEliminarCargo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEditar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnRegistrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtcargo))))
                .addGap(61, 61, 61)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(506, Short.MAX_VALUE))
        );
        pCargosLayout.setVerticalGroup(
            pCargosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pCargosLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(pCargosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pCargosLayout.createSequentialGroup()
                        .addGroup(pCargosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtidcargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pCargosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtcargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(btnRegistrar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEliminarCargo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEnviarCargo)))
                .addContainerGap(301, Short.MAX_VALUE))
        );

        panel.addTab("Cargos", pCargos);

        pPiezas.setBackground(new java.awt.Color(255, 255, 255));

        jLabel30.setText("No. Pieza:");

        jLabel31.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel31.setText("Piezas");

        tablaPiezas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pieza", "Nombre", "Marca", "Modelo", "Cantidad", "Precio"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaPiezas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaPiezasMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(tablaPiezas);

        txtid_Pieza.setEnabled(false);

        jLabel32.setText("Nombre:");

        jLabel33.setText("Marca:");

        jLabel34.setText("Modelo:");

        jLabel35.setText("Cantidad:");

        btnRegistrarPiezas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/registrar.png"))); // NOI18N
        btnRegistrarPiezas.setText("Dar de Alta");
        btnRegistrarPiezas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarPiezasActionPerformed(evt);
            }
        });

        btnEditarPiezas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/editar.png"))); // NOI18N
        btnEditarPiezas.setText("Añadir");
        btnEditarPiezas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarPiezasActionPerformed(evt);
            }
        });

        btneliminarPiezas.setBackground(new java.awt.Color(255, 51, 51));
        btneliminarPiezas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btneliminarPiezas.setForeground(new java.awt.Color(255, 255, 255));
        btneliminarPiezas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/eliminar.png"))); // NOI18N
        btneliminarPiezas.setText("Dar de Baja");
        btneliminarPiezas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneliminarPiezasActionPerformed(evt);
            }
        });

        jLabel52.setText("Precio:");

        btnBuscarPiezas.setBackground(new java.awt.Color(102, 204, 255));
        btnBuscarPiezas.setFont(new java.awt.Font("Segoe UI Emoji", 1, 14)); // NOI18N
        btnBuscarPiezas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/busqueda.png"))); // NOI18N
        btnBuscarPiezas.setText("Buscar:");
        btnBuscarPiezas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarPiezasActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel14.setText("Ingresa el Nombre de la Pieza :");

        jPanel8.setBackground(new java.awt.Color(204, 255, 255));

        jLabel51.setFont(new java.awt.Font("Segoe UI Emoji", 0, 14)); // NOI18N
        jLabel51.setText("Selecciona las piezas que usaste:");

        jLabel54.setBackground(new java.awt.Color(255, 255, 255));
        jLabel54.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        jLabel54.setText("Piezas:");

        jLabel55.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        jLabel55.setText("Total:");

        btnAñadirPrecio.setBackground(new java.awt.Color(51, 255, 51));
        btnAñadirPrecio.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAñadirPrecio.setText("Añadir");
        btnAñadirPrecio.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnAñadirPrecio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAñadirPrecioActionPerformed(evt);
            }
        });

        btnEliminarSeleccion.setBackground(new java.awt.Color(255, 102, 102));
        btnEliminarSeleccion.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEliminarSeleccion.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarSeleccion.setText("Regresar");
        btnEliminarSeleccion.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnEliminarSeleccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarSeleccionActionPerformed(evt);
            }
        });

        jLabel56.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        jLabel56.setText("Cantidad:");

        btnEnviarPieza.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/enviar-mensaje.png"))); // NOI18N
        btnEnviarPieza.setText("Enviar");
        btnEnviarPieza.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnEnviarPieza.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarPiezaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel51)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel54)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPiezasAcumuladas, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel56)
                            .addComponent(jLabel55))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtSumatoria, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                                .addComponent(txtCantidadFinal, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(btnAñadirPrecio)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnEliminarSeleccion))
                                    .addComponent(btnEnviarPieza, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel54)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtPiezasAcumuladas, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCantidadFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel56))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSumatoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAñadirPrecio)
                    .addComponent(btnEliminarSeleccion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEnviarPieza)
                .addGap(11, 11, 11))
        );

        javax.swing.GroupLayout pPiezasLayout = new javax.swing.GroupLayout(pPiezas);
        pPiezas.setLayout(pPiezasLayout);
        pPiezasLayout.setHorizontalGroup(
            pPiezasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pPiezasLayout.createSequentialGroup()
                .addGroup(pPiezasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pPiezasLayout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addGroup(pPiezasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnRegistrarPiezas, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                            .addComponent(btnEditarPiezas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btneliminarPiezas, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)))
                    .addGroup(pPiezasLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(pPiezasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel32)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34)
                            .addComponent(jLabel35)
                            .addComponent(jLabel52))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pPiezasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txt_Precio)
                            .addComponent(txtCantidad_Piezas, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                            .addComponent(txtModelo_pieza)
                            .addComponent(txtMarca_pieza)
                            .addComponent(txtNombrePieza)))
                    .addGroup(pPiezasLayout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtid_Pieza, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(pPiezasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pPiezasLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscarPiezas, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscarPiezas))
                    .addGroup(pPiezasLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 595, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pPiezasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pPiezasLayout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addGap(73, 73, 73))))
        );
        pPiezasLayout.setVerticalGroup(
            pPiezasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pPiezasLayout.createSequentialGroup()
                .addGroup(pPiezasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pPiezasLayout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(pPiezasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNombrePieza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pPiezasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMarca_pieza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33))
                        .addGap(26, 26, 26)
                        .addGroup(pPiezasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtModelo_pieza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34))
                        .addGap(26, 26, 26)
                        .addGroup(pPiezasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCantidad_Piezas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35))
                        .addGap(28, 28, 28)
                        .addGroup(pPiezasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_Precio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel52))
                        .addGap(18, 18, 18)
                        .addComponent(btnRegistrarPiezas)
                        .addGap(18, 18, 18)
                        .addComponent(btnEditarPiezas)
                        .addGap(18, 18, 18)
                        .addComponent(btneliminarPiezas))
                    .addGroup(pPiezasLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pPiezasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pPiezasLayout.createSequentialGroup()
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pPiezasLayout.createSequentialGroup()
                                .addGroup(pPiezasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel30)
                                    .addComponent(txtid_Pieza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel14)
                                    .addComponent(txtBuscarPiezas, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnBuscarPiezas, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(7, 7, 7)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(218, Short.MAX_VALUE))
        );

        panel.addTab("Piezas", pPiezas);

        pPendientes.setBackground(new java.awt.Color(255, 255, 255));

        tablaPendientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaPendientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaPendientesMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tablaPendientes);

        btnRegistrarMaquinas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/registrar.png"))); // NOI18N
        btnRegistrarMaquinas.setText("Guardar");
        btnRegistrarMaquinas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarMaquinasActionPerformed(evt);
            }
        });

        btnEditar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/editar.png"))); // NOI18N
        btnEditar1.setText("Modificar");
        btnEditar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditar1ActionPerformed(evt);
            }
        });

        btnEliminarPendientes.setBackground(new java.awt.Color(255, 51, 51));
        btnEliminarPendientes.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEliminarPendientes.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarPendientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/eliminar.png"))); // NOI18N
        btnEliminarPendientes.setText("Dar de Baja");
        btnEliminarPendientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarPendientesActionPerformed(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Pendientes"));

        jLabel12.setFont(new java.awt.Font("Stencil", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 0, 0));
        jLabel12.setText("NO. Folio");

        txtid_Registro.setBackground(new java.awt.Color(0, 0, 0));
        txtid_Registro.setEnabled(false);

        jLabel20.setText("Causante:");

        txtCausantePendiente.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel17.setText("Estado Maquina:");

        comboReparada.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sin Entregar", "Entregada" }));
        comboReparada.setEnabled(false);

        jLabel23.setText("Fecha Revision:");

        txtFechaPendiente.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtFechaPendiente.setEnabled(false);

        jdcFechaPendiente.setEnabled(false);

        btnAñadirFechaPen.setFont(new java.awt.Font("Segoe UI Emoji", 0, 14)); // NOI18N
        btnAñadirFechaPen.setText("...");
        btnAñadirFechaPen.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnAñadirFechaPen.setEnabled(false);
        btnAñadirFechaPen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAñadirFechaPenActionPerformed(evt);
            }
        });

        jLabel27.setText("Total:");

        txtTotalPendiente.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTotalPendiente.setEnabled(false);

        txtidnotarecPendiente.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtidnotarecPendiente.setEnabled(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel23)
                            .addComponent(jLabel17)
                            .addComponent(jLabel27))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(comboReparada, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(txtFechaPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jdcFechaPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnAñadirFechaPen)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE))
                            .addComponent(txtTotalPendiente)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtCausantePendiente, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(txtid_Registro, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtidnotarecPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(32, 32, 32))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtid_Registro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtidnotarecPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtCausantePendiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(comboReparada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel23)
                                .addComponent(txtFechaPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jdcFechaPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27)
                            .addComponent(txtTotalPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnAñadirFechaPen))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Notas"));

        jLabel13.setText("Cliente:");

        txtClientePendiente.setEnabled(false);

        jLabel22.setText("Maquina:");

        txtMaquinaPendiente.setEnabled(false);

        btnBuscaNotaPendiente.setFont(new java.awt.Font("SimSun", 0, 14)); // NOI18N
        btnBuscaNotaPendiente.setText("Seleccionar");
        btnBuscaNotaPendiente.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnBuscaNotaPendiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscaNotaPendienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel22)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtClientePendiente, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtMaquinaPendiente, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(105, Short.MAX_VALUE)
                .addComponent(btnBuscaNotaPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(84, 84, 84))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(txtClientePendiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtMaquinaPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addComponent(btnBuscaNotaPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Empleado"));

        jLabel15.setText("Empleado:");

        txtEmpleadoPendiente.setEnabled(false);

        btnBuscarEmpleadoPendiente.setFont(new java.awt.Font("SimSun", 0, 14)); // NOI18N
        btnBuscarEmpleadoPendiente.setText("Seleccionar");
        btnBuscarEmpleadoPendiente.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnBuscarEmpleadoPendiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarEmpleadoPendienteActionPerformed(evt);
            }
        });

        jLabel16.setText("Piezas/Arreglos:");

        txtPiezasPendiente.setEnabled(false);

        btnBuscarPiezaPendiente.setFont(new java.awt.Font("SimSun", 0, 14)); // NOI18N
        btnBuscarPiezaPendiente.setText("Seleccionar");
        btnBuscarPiezaPendiente.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnBuscarPiezaPendiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarPiezaPendienteActionPerformed(evt);
            }
        });

        txtidEmpleadoPendiente.setEnabled(false);

        jLabel5.setText("No. Empleado");

        txtidPiezasPendiente.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtidPiezasPendiente.setEnabled(false);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(txtidPiezasPendiente))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addGap(24, 24, 24)
                                    .addComponent(jLabel15))
                                .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jLabel16)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtEmpleadoPendiente, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                                .addComponent(txtPiezasPendiente)))
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtidEmpleadoPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addGap(157, 157, 157)
                            .addComponent(btnBuscarPiezaPendiente))
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addGap(155, 155, 155)
                            .addComponent(btnBuscarEmpleadoPendiente))))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtidEmpleadoPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmpleadoPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuscarEmpleadoPendiente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtidPiezasPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(txtPiezasPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuscarPiezaPendiente)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jLabel29.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel29.setText("Ingresa el Folio de la Nota:");

        btnBuscarPendiente.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        btnBuscarPendiente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/busqueda.png"))); // NOI18N
        btnBuscarPendiente.setText("Buscar Nota");
        btnBuscarPendiente.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnBuscarPendiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarPendienteActionPerformed(evt);
            }
        });

        btnImprimirRecibo.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        btnImprimirRecibo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/impresora.png"))); // NOI18N
        btnImprimirRecibo.setText("Entregar Maquina");
        btnImprimirRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirReciboActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pPendientesLayout = new javax.swing.GroupLayout(pPendientes);
        pPendientes.setLayout(pPendientesLayout);
        pPendientesLayout.setHorizontalGroup(
            pPendientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pPendientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pPendientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pPendientesLayout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pPendientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pPendientesLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pPendientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(pPendientesLayout.createSequentialGroup()
                                        .addComponent(btnRegistrarMaquinas, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnEliminarPendientes, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(12, 12, 12))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pPendientesLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnImprimirRecibo)
                                .addGap(92, 92, 92)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(67, 67, 67))
                    .addGroup(pPendientesLayout.createSequentialGroup()
                        .addGap(123, 123, 123)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscarPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscarPendiente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditar1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addComponent(jScrollPane3)
        );
        pPendientesLayout.setVerticalGroup(
            pPendientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pPendientesLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(pPendientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pPendientesLayout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pPendientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnRegistrarMaquinas)
                            .addComponent(btnEliminarPendientes))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnImprimirRecibo))
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49)
                .addGroup(pPendientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(txtBuscarPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarPendiente)
                    .addComponent(btnEditar1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        panel.addTab("Pendientes", pPendientes);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 1098, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1266, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPiezasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPiezasActionPerformed
        panel.setSelectedComponent(pPiezas);
        //limpiarDatosPendientes();
        //limpiarTablaPendientes();
    }//GEN-LAST:event_btnPiezasActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        // Guardar las filas resaltadas al cerrar la aplicación
        guardarFilasResaltadas(filasResaltadas);
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void btnUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsuariosActionPerformed
        // TODO add your handling code here:
        panel.setSelectedComponent(pUsuarios);
        //limpiarDatosPendientes();
        //limpiarTablaPendientes();
    }//GEN-LAST:event_btnUsuariosActionPerformed

    private void btnPendientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPendientesActionPerformed
        // TODO add your handling code here:
        panel.setSelectedComponent(pPendientes);
        //limpiarDatosPendientes();
        //limpiarTablaPendientes();
    }//GEN-LAST:event_btnPendientesActionPerformed

    private void btnEmpleadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmpleadosActionPerformed
        // TODO add your handling code here:
        panel.setSelectedComponent(pEmpleados);
        //limpiarDatosPendientes();
        //limpiarTablaPendientes();
    }//GEN-LAST:event_btnEmpleadosActionPerformed

    private void btnCargoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargoActionPerformed
        // TODO add your handling code here:
        panel.setSelectedComponent(pCargos);
        //limpiarDatosPendientes();
        //limpiarTablaPendientes();
    }//GEN-LAST:event_btnCargoActionPerformed

    private void btnEnviarPiezaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarPiezaActionPerformed
        // Segundo Paso
        txtidPiezasPendiente.setText(txtid_Pieza.getText());
        txtPiezasPendiente.setText(txtPiezasAcumuladas.getText());
        txtTotalPendiente.setText(txtSumatoria.getText());

        btnEnviarPieza.setEnabled(false);
        btnAñadirPrecio.setEnabled(false);
        btnEliminarSeleccion.setEnabled(false);

        txtNombrePieza.setEnabled(true);
        txtMarca_pieza.setEnabled(true);
        txtModelo_pieza.setEnabled(true);
        txtCantidad_Piezas.setEnabled(true);
        txt_Precio.setEnabled(true);
        btnRegistrarPiezas.setEnabled(true);
        btnEditarPiezas.setEnabled(true);
        btneliminarPiezas.setEnabled(true);
        btnUsuarios.setEnabled(true);
        btnPendientes.setEnabled(true);
        btnEmpleados.setEnabled(true);
        btnCargo.setEnabled(true);
        btnPiezas.setEnabled(true);
        panel.setEnabled(true);

        txtid_Pieza.setText("");
        txtNombrePieza.setText("");
        txtMarca_pieza.setText("");
        txtModelo_pieza.setText("");
        txtCantidad_Piezas.setText("");
        txt_Precio.setText("");
        panel.setSelectedComponent(pPendientes);

    }//GEN-LAST:event_btnEnviarPiezaActionPerformed

    private void btnEliminarSeleccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarSeleccionActionPerformed
        // Verificar si hay cambios para revertir
        if (!cambiosRealizados.isEmpty()) {
            // Obtener el último cambio realizado
            Cambio ultimoCambio = cambiosRealizados.get(cambiosRealizados.size() - 1);

            // Revertir los cambios
            incrementarCantidadPiezas(ultimoCambio.getIdPieza(), ultimoCambio.getCantidadDecrementada());

            // Restar el precio de la última fila seleccionada del total en txtSumatoria
            restarPrecioUltimaFilaSeleccionada(ultimoCambio.getIdPieza());

            // Eliminar el cambio de la lista
            cambiosRealizados.remove(cambiosRealizados.size() - 1);

            // Eliminar el último nombre de la lista de nombresPiezasAcumuladas
            if (!nombresPiezasAcumuladas.isEmpty()) {
                nombresPiezasAcumuladas.remove(nombresPiezasAcumuladas.size() - 1);
            }
            // Actualizar el txtPiezasAcumuladas
            txtPiezasAcumuladas.setText(String.join(", ", nombresPiezasAcumuladas));

        } else {
            JOptionPane.showMessageDialog(null, "No hay cambios para revertir.");
        }
    }//GEN-LAST:event_btnEliminarSeleccionActionPerformed

    private void btnAñadirPrecioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAñadirPrecioActionPerformed

        // Obtener las filas seleccionadas
        int[] filasSeleccionadas = tablaPiezas.getSelectedRows();

        // Verificar si hay al menos una fila seleccionada
        if (filasSeleccionadas.length > 0) {
            // Obtener el precio y las piezas acumuladas actuales
            double precioActual = !txtSumatoria.getText().isEmpty() ? Double.parseDouble(txtSumatoria.getText()) : 0.0;
            String piezasAcumuladas = txtPiezasAcumuladas.getText();

            // Lista para almacenar los nombres de las piezas que se incrementaron
            List<String> nombresIncrementados = new ArrayList<>();

            // Iterar sobre las filas seleccionadas
            for (int fila : filasSeleccionadas) {
                int cantidadPieza = Integer.parseInt(tablaPiezas.getValueAt(fila, 4).toString());

                // Verificar si la cantidad es igual a 1
                if (cantidadPieza == 1) {

                    JOptionPane.showMessageDialog(null, "No se puede realizar la acción para una cantidad de 1.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;  // Salir del método si la cantidad es 1
                }

                // Obtener el nombre, precio y id de la pieza
                String nombrePieza = tablaPiezas.getValueAt(fila, 1).toString(); // Asumiendo que el nombre está en la columna 1
                double precioPieza = Double.parseDouble(tablaPiezas.getValueAt(fila, 5).toString()); // Asumiendo que el precio está en la columna 5
                int idPieza = Integer.parseInt(tablaPiezas.getValueAt(fila, 0).toString()); // Asumiendo que el id está en la columna 0

                // Verificar si el nombre ya está en la lista
                if (!nombresIncrementados.contains(nombrePieza)) {
                    // Agregar el nombre de la pieza a la lista de piezas acumuladas
                    if (!nombresPiezasAcumuladas.contains(nombrePieza)) {
                        nombresPiezasAcumuladas.add(nombrePieza);

                        // Actualizar el txtPiezasAcumuladas
                        if (!piezasAcumuladas.isEmpty()) {
                            piezasAcumuladas += ", ";
                        }
                        piezasAcumuladas += nombrePieza;
                    }

                    // Agregar el nombre a la lista de piezas incrementadas
                    nombresIncrementados.add(nombrePieza);
                }

                // Sumar el precio de la pieza al precio actual
                precioActual += precioPieza;

                // Decrementar la cantidad de piezas en -1
                decrementarCantidadPiezas(idPieza);
            }

            // Actualizar los campos txt_Precio y txtPiezasAcumuladas
            txtSumatoria.setText(String.valueOf(precioActual));
            txtPiezasAcumuladas.setText(piezasAcumuladas);

            // Almacenar los cambios realizados
            for (int fila : filasSeleccionadas) {
                String nombrePieza = tablaPiezas.getValueAt(fila, 1).toString();
                double precioPieza = Double.parseDouble(tablaPiezas.getValueAt(fila, 5).toString());
                int idPieza = Integer.parseInt(tablaPiezas.getValueAt(fila, 0).toString());

                // Almacenar el cambio realizado
                cambiosRealizados.add(new Cambio(idPieza, 1)); // Decrementar en 1
            }

            ////////////////
        }
    }//GEN-LAST:event_btnAñadirPrecioActionPerformed


    private void btnBuscarPiezasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarPiezasActionPerformed

        DaoPiezas daoP = new DaoPiezas();
        piezas pi = new piezas();

        String entradaBusqueda = txtBuscarPiezas.getText();

        if (!entradaBusqueda.isEmpty()) {
            try {
                // Intentar buscar por id
                int idBusqueda = Integer.parseInt(entradaBusqueda);
                pi.setId_Pieza(idBusqueda);

                if (daoP.BuscarPiezas(pi, true)) {
                    mostrarResultadoBusqueda(pi);
                    return;
                }
            } catch (NumberFormatException e) {
                // No es un número, continuar con la búsqueda por nombre
            }

            // Búsqueda por nombre
            pi.setNom_Pieza(entradaBusqueda);

            if (daoP.BuscarPiezas(pi, false)) {
                mostrarResultadoBusqueda(pi);
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró ninguna pieza con ese ID o nombre.", "Resultado de búsqueda", JOptionPane.INFORMATION_MESSAGE);
                limpiarDatosPiezas();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ingrese un ID o nombre de pieza para buscar.", "Error", JOptionPane.ERROR_MESSAGE);
            limpiarDatosPiezas();
        }
    }//GEN-LAST:event_btnBuscarPiezasActionPerformed

    private void btneliminarPiezasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneliminarPiezasActionPerformed
        DaoPiezas daoP = new DaoPiezas();

        if (!txtid_Pieza.getText().isEmpty()) {
            try {
                int idPieza = Integer.parseInt(txtid_Pieza.getText());

                // Preguntar al usuario cuántas piezas desea eliminar
                String cantidadEliminarStr = JOptionPane.showInputDialog(null, "¿Cuantas piezas dara de Baja?", "Dar de Baja", JOptionPane.QUESTION_MESSAGE);

                // Verificar que el usuario ingresó un número válido
                if (cantidadEliminarStr != null && !cantidadEliminarStr.isEmpty()) {
                    try {
                        int cantidadEliminar = Integer.parseInt(cantidadEliminarStr);

                        // Llamar al método de eliminación
                        int cantidadFinal = daoP.eliminarPiezas(idPieza, cantidadEliminar);

                        if (cantidadFinal != -1) {
                            // Limpieza y actualización de la interfaz
                            limpiarDatosPiezas();
                            limpiarTablaPiezas();
                            listarPiezas();
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Por favor, ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Por favor, ingrese un ID de pieza válido.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showConfirmDialog(null, e);
            }
        }
    }//GEN-LAST:event_btneliminarPiezasActionPerformed

    private void btnEditarPiezasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarPiezasActionPerformed
        // TODO add your handling code here:

        int fila = tablaPiezas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un Dato de la Tabla");
        } else {

            pi.setId_Pieza(Integer.parseInt(txtid_Pieza.getText()));
            pi.setNom_Pieza(txtNombrePieza.getText());
            pi.setMarca_Pieza(txtMarca_pieza.getText());
            pi.setModelo_Pieza(txtModelo_pieza.getText());
            pi.setCantidad(Integer.parseInt(txtCantidad_Piezas.getText()));
            pi.setPrecio_Pieza(txt_Precio.getText());

            if (daoP.editarNotas(pi)) {
                JOptionPane.showMessageDialog(null, "Modificación Realizada con Exito");
                limpiarDatosPiezas();
                limpiarTablaPiezas();
                listarPiezas();
            } else {
                JOptionPane.showMessageDialog(null, "Error!!... Al Modificar");
            }
        }
    }//GEN-LAST:event_btnEditarPiezasActionPerformed

    private void btnRegistrarPiezasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarPiezasActionPerformed
        pi.setNom_Pieza(txtNombrePieza.getText());
        pi.setMarca_Pieza(txtMarca_pieza.getText());
        pi.setModelo_Pieza(txtModelo_pieza.getText());
        pi.setPrecio_Pieza(txt_Precio.getText());

        // Convertir la cantidad de piezas a entero antes de asignarla
        try {
            int cantidadPiezas = Integer.parseInt(txtCantidad_Piezas.getText());
            pi.setCantidad(cantidadPiezas);

            if (daoP.insertarPiezas(pi)) {
                JOptionPane.showMessageDialog(null, "Pieza Registrada Exitosamente");
                limpiarDatosPiezas();
                limpiarTablaPiezas();
                listarPiezas();
            } else {
                JOptionPane.showMessageDialog(null, "ERROR!! - La Pieza no se Pudo Registrar");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error al convertir la cantidad de piezas a entero.");
        }
    }//GEN-LAST:event_btnRegistrarPiezasActionPerformed

    private void tablaPiezasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaPiezasMouseClicked

        // TODO add your handling code here:
        int fila = tablaPiezas.getSelectedRow();
        txtid_Pieza.setText(tablaPiezas.getValueAt(fila, 0).toString());
        txtNombrePieza.setText(tablaPiezas.getValueAt(fila, 1).toString());
        txtMarca_pieza.setText(tablaPiezas.getValueAt(fila, 2).toString());
        txtModelo_pieza.setText(tablaPiezas.getValueAt(fila, 3).toString());
        txtCantidad_Piezas.setText(tablaPiezas.getValueAt(fila, 4).toString());
        txt_Precio.setText(tablaPiezas.getValueAt(fila, 5).toString());
    }//GEN-LAST:event_tablaPiezasMouseClicked

    private void btnEnviarCargoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarCargoActionPerformed
        // Segundo Paso
        txtIdCargoEmpleado.setText(txtidcargo.getText());
        txtCargoEmpleado.setText(txtcargo.getText());
        btnEnviarCargo.setEnabled(false);
        panel.setSelectedComponent(pEmpleados);
    }//GEN-LAST:event_btnEnviarCargoActionPerformed

    private void btnEliminarCargoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarCargoActionPerformed

        if (!txtidcargo.getText().isEmpty()) {
            int confirmacion = JOptionPane.showConfirmDialog(rootPane, "Seguro de Eliminar el Cargo?", "Confrimar", 2);
            if (confirmacion == 0) {
                cr.setId(Integer.parseInt(txtidcargo.getText()));
                daoC.eliminar(cr);
                limpiarDatosCargo();
                limpiarTablaCargo();
                listar();
            }
        }
    }//GEN-LAST:event_btnEliminarCargoActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        // TODO add your handling code here:
        int fila = tablacargos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un Cargo");
        } else {
            cr.setId(Integer.parseInt(txtidcargo.getText()));
            cr.setNombre(txtcargo.getText());
            if (daoC.editar(cr)) {
                JOptionPane.showMessageDialog(null, "Modificación Realizada con Exito");
                limpiarDatosCargo();
                limpiarTablaCargo();
                listar();
            } else {
                JOptionPane.showMessageDialog(null, "Error!!... Al Modificar");
            }
        }
    }//GEN-LAST:event_btnEditarActionPerformed

    private void tablacargosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablacargosMouseClicked
        // TODO add your handling code here:
        int fila = tablacargos.getSelectedRow();
        txtidcargo.setText(tablacargos.getValueAt(fila, 0).toString());
        txtcargo.setText(tablacargos.getValueAt(fila, 1).toString());
    }//GEN-LAST:event_tablacargosMouseClicked

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        // TODO add your handling code here:
        cr.setNombre(txtcargo.getText());
        if (daoC.insertar(cr)) {
            JOptionPane.showMessageDialog(null, "Cargo Registrado Exitosamente");
            limpiarDatosCargo();
            limpiarTablaCargo();
            listar();
        } else {
            JOptionPane.showMessageDialog(null, "ERROR!!.. - El Cargo no se Pudo Registrar");
        }
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnAñadirFechaEmpleadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAñadirFechaEmpleadosActionPerformed
        // TODO add your handling code here:

        Date mFecha = jdcFechaEmpleados.getDate();

        if (mFecha != null) {
            // Formatear la fecha como una cadena antes de mostrarla en el JTextField
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaFormateada = sdf.format(mFecha);
            txtFechaEmpleados.setText(fechaFormateada);
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una fecha válida.");
        }
    }//GEN-LAST:event_btnAñadirFechaEmpleadosActionPerformed

    private void btnEnviarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarEmpleadoActionPerformed
        // Segundo Paso
        txtidEmpleadoPendiente.setText(txtid_Empleado.getText());
        txtEmpleadoPendiente.setText(txtNombreEmpleados.getText());

        btnEnviarEmpleado.setEnabled(false);
        txtNombreEmpleados.setEnabled(true);
        btnRegistrarEmpleado.setEnabled(true);
        btnModificarEmpleado.setEnabled(true);
        txtEliminarEmpleado.setEnabled(true);
        btnUsuarios.setEnabled(true);
        btnPendientes.setEnabled(true);
        btnEmpleados.setEnabled(true);
        btnCargo.setEnabled(true);
        btnPiezas.setEnabled(true);
        panel.setEnabled(true);

        txtid_Empleado.setText("");
        txtNombreEmpleados.setText("");
        txtFechaEmpleados.setText("");
        txtIdCargoEmpleado.setText("");
        txtCargoEmpleado.setText("");
        panel.setSelectedComponent(pPendientes);
    }//GEN-LAST:event_btnEnviarEmpleadoActionPerformed

    private void btnBuscaCargoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscaCargoActionPerformed
        //Tercer Paso boton de 3 puntos
        btnEnviarCargo.setEnabled(true);
        panel.setSelectedComponent(pCargos);
    }//GEN-LAST:event_btnBuscaCargoActionPerformed

    private void btnModificarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarEmpleadoActionPerformed
        // TODO add your handling code here:
        // TODO add your handling code here:
        int fila = tablaEmpleado.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un Empleado");
        } else {
            em.setId_empleado(Integer.parseInt(txtid_Empleado.getText()));
            em.setNom_Empleado(txtNombreEmpleados.getText());
            em.setFecha_Empleado(txtFechaEmpleados.getText());
            em.setId_cargo(Integer.parseInt(txtIdCargoEmpleado.getText()));
            em.setCargo(txtCargoEmpleado.getText());
            if (daoE.editarEmpleados(em)) {
                JOptionPane.showMessageDialog(null, "Modificación Realizada con Exito");
                limpiarDatosEmpleado();
                limpiarTablaEmpleado();
                listarEmpleado();
            } else {
                JOptionPane.showMessageDialog(null, "Error!!... Al Modificar");
            }
        }
    }//GEN-LAST:event_btnModificarEmpleadoActionPerformed

    private void btnRegistrarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarEmpleadoActionPerformed
        //Cuarto´Paso
        em.setNom_Empleado(txtNombreEmpleados.getText());
        em.setFecha_Empleado(txtFechaEmpleados.getText());
        em.setId_cargo(Integer.parseInt(txtIdCargoEmpleado.getText()));

        if (daoE.insertarEmpleados(em)) {
            JOptionPane.showMessageDialog(null, "Empleado Registrado Exitosamente");
            limpiarDatosEmpleado();
            limpiarTablaEmpleado();
            listarEmpleado();
        } else {
            JOptionPane.showMessageDialog(null, "ERROR!!.. - El Empleado no se Pudo Registrar");
        }
    }//GEN-LAST:event_btnRegistrarEmpleadoActionPerformed

    private void tablaEmpleadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaEmpleadoMouseClicked

        int fila = tablaEmpleado.getSelectedRow();
        txtid_Empleado.setText(tablaEmpleado.getValueAt(fila, 0).toString());
        txtNombreEmpleados.setText(tablaEmpleado.getValueAt(fila, 1).toString());
        txtFechaEmpleados.setText(tablaEmpleado.getValueAt(fila, 2).toString());
        txtIdCargoEmpleado.setText(tablaEmpleado.getValueAt(fila, 3).toString());
        txtCargoEmpleado.setText(tablaEmpleado.getValueAt(fila, 4).toString());
    }//GEN-LAST:event_tablaEmpleadoMouseClicked

    private void btnBuscarPiezaPendienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarPiezaPendienteActionPerformed
        //Tercer Paso boton de 3 puntos
        txtNombrePieza.setEnabled(false);
        txtMarca_pieza.setEnabled(false);
        txtModelo_pieza.setEnabled(false);
        txtCantidad_Piezas.setEnabled(false);
        txt_Precio.setEnabled(false);
        btnRegistrarPiezas.setEnabled(false);
        btnEditarPiezas.setEnabled(false);
        btneliminarPiezas.setEnabled(false);
        btnUsuarios.setEnabled(false);
        btnPendientes.setEnabled(false);
        btnEmpleados.setEnabled(false);
        btnCargo.setEnabled(false);
        btnPiezas.setEnabled(false);
        panel.setEnabled(false);

        btnEnviarPieza.setEnabled(true);
        btnAñadirPrecio.setEnabled(true);
        btnEliminarSeleccion.setEnabled(true);
        panel.setSelectedComponent(pPiezas);
    }//GEN-LAST:event_btnBuscarPiezaPendienteActionPerformed

    private void btnBuscarEmpleadoPendienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarEmpleadoPendienteActionPerformed
        //Tercer Paso boton de 3 puntos
        txtNombreEmpleados.setEnabled(false);
        btnRegistrarEmpleado.setEnabled(false);
        btnModificarEmpleado.setEnabled(false);
        txtEliminarEmpleado.setEnabled(false);
        btnUsuarios.setEnabled(false);
        btnPendientes.setEnabled(false);
        btnEmpleados.setEnabled(false);
        btnCargo.setEnabled(false);
        btnPiezas.setEnabled(false);
        panel.setEnabled(false);

        btnEnviarEmpleado.setEnabled(true);
        panel.setSelectedComponent(pEmpleados);
    }//GEN-LAST:event_btnBuscarEmpleadoPendienteActionPerformed

    private void btnBuscaNotaPendienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscaNotaPendienteActionPerformed
        //Tercer Paso boton de 3 puntos
        txtnom_Clientes.setEnabled(false);
        txtnom_maquina.setEnabled(false);
        txtmarca_maquina.setEnabled(false);
        txtmodelo_maquina.setEnabled(false);
        txtObservaciones.setEnabled(false);
        btnRegistrarNotas.setEnabled(false);
        btnEditarNotas.setEnabled(false);
        btnEliminarNotas.setEnabled(false);
        btnUsuarios.setEnabled(false);
        btnPendientes.setEnabled(false);
        btnEmpleados.setEnabled(false);
        btnCargo.setEnabled(false);
        btnPiezas.setEnabled(false);
        panel.setEnabled(false);

        comboEstado_maquina.setEnabled(true);
        combotipo_Atencion.setEnabled(true);
        btnEnviarNotas.setEnabled(true);
        panel.setSelectedComponent(pUsuarios);
    }//GEN-LAST:event_btnBuscaNotaPendienteActionPerformed

    private void btnAñadirFechaPenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAñadirFechaPenActionPerformed
        // TODO add your handling code here:

        Date mFecha = jdcFechaPendiente.getDate();

        if (mFecha != null) {
            // Formatear la fecha como una cadena antes de mostrarla en el JTextField
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaFormateada = sdf.format(mFecha);
            txtFechaPendiente.setText(fechaFormateada);
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una fecha válida.");
        }
    }//GEN-LAST:event_btnAñadirFechaPenActionPerformed

    private void btnEditar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditar1ActionPerformed
        int fila = tablaPendientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un Pendiente de la Tabla");
        } else {
            // Crear un objeto de la clase pendientes
            pendientes pe = new pendientes();

            // Configurar los valores para editar
            pe.setId_notarec(Integer.parseInt(txtidnotarecPendiente.getText()));

            pe.setCausante(txtCausantePendiente.getText());
            pe.setEstado_Maquina((String) comboReparada.getSelectedItem());
            pe.setFecha_Revision(txtFechaPendiente.getText());
            pe.setCliente(txtClientePendiente.getText());
            pe.setMaquina(txtMaquinaPendiente.getText());
            pe.setTotal(Double.parseDouble(txtTotalPendiente.getText()));
            pe.setPiezas(txtPiezasPendiente.getText());

            pe.setId_pendientes(Integer.parseInt(txtid_Registro.getText()));
            pe.setId_empleado(Integer.parseInt(txtidEmpleadoPendiente.getText()));
            pe.setEmpleado(txtEmpleadoPendiente.getText());

            // Llamar al método correcto para editar en la base de datos
            if (daoPendiente.editarPendientes(pe)) {
                JOptionPane.showMessageDialog(null, "Cambio Guardado");
                limpiarDatosPendientes();
                limpiarTablaPendientes();
                listarPendientes();
            } else {
                JOptionPane.showMessageDialog(null, "Error!!... Al Guardar");
            }
        }
    }//GEN-LAST:event_btnEditar1ActionPerformed

    private boolean camposVacios() {
        return txtid_Registro.getText().isEmpty() || txtCausantePendiente.getText().isEmpty() || txtFechaPendiente.getText().isEmpty() || txtTotalPendiente.getText().isEmpty() || txtPiezasPendiente.getText().isEmpty() || txtidnotarecPendiente.getText().isEmpty() || txtClientePendiente.getText().isEmpty() || txtMaquinaPendiente.getText().isEmpty() || txtidEmpleadoPendiente.getText().isEmpty() || txtEmpleadoPendiente.getText().isEmpty() || txtidPiezasPendiente.getText().isEmpty();
    }

    private void btnRegistrarMaquinasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarMaquinasActionPerformed

        try {
            pe.setCausante(txtCausantePendiente.getText());
            pe.setEstado_Maquina((String) comboReparada.getSelectedItem());
            pe.setFecha_Revision(txtFechaPendiente.getText());

            // Validar y convertir el campo Total a double
            String totalText = txtTotalPendiente.getText();
            if (!totalText.isEmpty()) {
                pe.setTotal(Double.parseDouble(totalText));
            } else {
                JOptionPane.showMessageDialog(null, "Error: Faltan Datos por Guardar");
                return;  // No continuar si hay un error
            }

            // Validar y convertir los campos de ID a enteros
            pe.setId_notarec(validarEntero(txtidnotarecPendiente.getText(), "idnotarec"));
            pe.setId_empleado(validarEntero(txtidEmpleadoPendiente.getText(), "idEmpleado"));
            pe.setId_Pieza(validarEntero(txtidPiezasPendiente.getText(), "idPiezas"));

            pe.setPiezas(txtPiezasPendiente.getText());

            if (daoPendiente.insertarPendientes(pe)) {
                JOptionPane.showMessageDialog(null, "Pendiente Guardado Exitosamente");
                limpiarDatosPendientes();
                limpiarTablaPendientes();
                listarPendientes();
                txtPiezasAcumuladas.setText("");
                txtCantidadFinal.setText("");
                txtSumatoria.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "ERROR!!.. - El Pendiente no se Pudo Guardar");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error: Ingresa valores numéricos válidos para los campos numéricos.");
        }


    }//GEN-LAST:event_btnRegistrarMaquinasActionPerformed

    private void tablaPendientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaPendientesMouseClicked
        int fila = tablaPendientes.getSelectedRow();

        //////////////////////
        txtidnotarecPendiente.setText(tablaPendientes.getValueAt(fila, 0).toString());
        txtCausantePendiente.setText(tablaPendientes.getValueAt(fila, 1).toString());
        comboReparada.setSelectedItem(tablaPendientes.getValueAt(fila, 2).toString());
        txtFechaEmpleados.setText(tablaPendientes.getValueAt(fila, 3).toString());
        txtClientePendiente.setText(tablaPendientes.getValueAt(fila, 4).toString());
        txtMaquinaPendiente.setText(tablaPendientes.getValueAt(fila, 5).toString());
        txtEmpleadoPendiente.setText(tablaPendientes.getValueAt(fila, 6).toString());
        txtTotalPendiente.setText(tablaPendientes.getValueAt(fila, 7).toString());
        txtPiezasPendiente.setText(tablaPendientes.getValueAt(fila, 8).toString());
        txtid_Registro.setText(tablaPendientes.getValueAt(fila, 9).toString());
        txtidEmpleadoPendiente.setText(tablaPendientes.getValueAt(fila, 10).toString());
    }//GEN-LAST:event_tablaPendientesMouseClicked

    private void btnImprimirNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirNotaActionPerformed

        // Obtener el índice de la fila seleccionada
        int selectedRow = tablaNotas.getSelectedRow();

        // Verificar si hay una fila seleccionada
        if (selectedRow != -1) {
            // Crear un objeto notas con todos los datos de la fila seleccionada
            DefaultTableModel model = (DefaultTableModel) tablaNotas.getModel();
            notas selectedNota = new notas();

            // Obtener todos los valores de la fila seleccionada y establecerlos en el objeto notas
            for (int i = 0; i < model.getColumnCount(); i++) {
                Object value = model.getValueAt(selectedRow, i);
                switch (i) {
                    case 0:
                        selectedNota.setId_notaRec((int) value);
                        break;
                    case 1:
                        selectedNota.setNom_Clientes((String) value);
                        break;
                    case 2:
                        selectedNota.setNom_maquina((String) value);
                        break;
                    case 3:
                        selectedNota.setMarca_Maquina((String) value);
                        break;
                    case 4:
                        selectedNota.setModelo_maquina((String) value);
                        break;
                    case 5:
                        selectedNota.setFecha((String) value);
                        break;
                    case 6:
                        selectedNota.setAtencion((String) value);
                        break;
                    case 7:
                        selectedNota.setObservaciones((String) value);
                        break;
                    case 8:
                        selectedNota.setEstado_Maquina((String) value);
                        break;

                }
            }

            // Luego, llama al método ImprimirNota con el objeto notas completo
            DaoNotas daoN = new DaoNotas();
            boolean impresionExitosa = false;
            try {
                impresionExitosa = daoN.ImprimirNota(selectedNota);
            } catch (BadElementException ex) {
                Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (impresionExitosa) {
                JOptionPane.showMessageDialog(null, "Nota Creada.");
            } else {
                JOptionPane.showMessageDialog(null, "Error al crear el reporte.");
            }
        } else {//Else condicion
            //JOptionPane.showMessageDialog(null, "Por favor, selecciona una fila en la tabla.");
            // Obtener la información de la última fila registrada en la base de datos
            notas lastNota = daoN.obtenerUltimaNota(); // Puedes crear este método en tu clase DaoNotas

            if (lastNota != null) {
                // Llama al método ImprimirNota con la información de la última fila
                boolean impresionExitosa = false;
                try {
                    impresionExitosa = daoN.ImprimirNota(lastNota);
                } catch (BadElementException ex) {
                    Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (impresionExitosa) {
                    JOptionPane.showMessageDialog(null, "Se guardo en el Escritorio.");
                } else {
                    JOptionPane.showMessageDialog(null, "Error al crear el reporte.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "No hay registros en la base de datos.");
            }
        }//Else final condicion

    }//GEN-LAST:event_btnImprimirNotaActionPerformed

    /*private void guardarFilasResaltadas() {
        Preferences prefs = Preferences.userNodeForPackage(MenuPrincipal.class);

        String filasResaltadasString = filasResaltadas.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

    }*/
    ////////////////////////////////////////////////////////

    private void btnEnviarNotasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarNotasActionPerformed
        // Segundo Paso
        int fila = tablaNotas.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un Dato de la Tabla");
            Object idNotaRecActual = tablaNotas.getValueAt(fila, 0);

            if (daoN.insertarFilaResaltada((int) idNotaRecActual, fila)) {
                JOptionPane.showMessageDialog(null, "Fila resaltada insertada en la base de datos.");
            } else {
                JOptionPane.showMessageDialog(null, "Error al insertar fila resaltada en la base de datos.");
            }

            filasResaltadas.add(fila);
            tablaNotas.repaint();
            CustomRenderer customRenderer = new CustomRenderer(filasResaltadas);
            tablaNotas.setDefaultRenderer(Object.class, customRenderer);

        } else {

            filasResaltadas.add(fila);
            tablaNotas.repaint();
            CustomRenderer customRenderer = new CustomRenderer(filasResaltadas);
            tablaNotas.setDefaultRenderer(Object.class, customRenderer);

            txtidnotarecPendiente.setText(txtid_notaRec.getText());
            txtClientePendiente.setText(txtnom_Clientes.getText());
            txtMaquinaPendiente.setText(txtnom_maquina.getText());

            btnEnviarNotas.setEnabled(false);
            btnImprimirNota.setEnabled(false);
            comboEstado_maquina.setEnabled(false);
            combotipo_Atencion.setEnabled(false);

            txtnom_Clientes.setEnabled(true);
            txtnom_maquina.setEnabled(true);
            txtmarca_maquina.setEnabled(true);
            txtmodelo_maquina.setEnabled(true);
            txtObservaciones.setEnabled(true);
            btnRegistrarNotas.setEnabled(true);
            btnEditarNotas.setEnabled(true);
            btnEliminarNotas.setEnabled(true);
            btnUsuarios.setEnabled(true);
            btnPendientes.setEnabled(true);
            btnEmpleados.setEnabled(true);
            btnCargo.setEnabled(true);
            btnPiezas.setEnabled(true);
            btnImprimirNota.setEnabled(true);
            panel.setEnabled(true);

            nt.setId_notaRec(Integer.parseInt(txtid_notaRec.getText()));
            nt.setNom_Clientes(txtnom_Clientes.getText());
            nt.setNom_maquina(txtnom_maquina.getText());
            nt.setMarca_Maquina(txtmarca_maquina.getText());
            nt.setModelo_maquina(txtmodelo_maquina.getText());
            nt.setFecha(txtFecha.getText());
            nt.setAtencion((String) combotipo_Atencion.getSelectedItem());
            nt.setObservaciones(txtObservaciones.getText());
            nt.setEstado_Maquina((String) comboEstado_maquina.getSelectedItem());

            if (daoN.editarNotas(nt)) {
                JOptionPane.showMessageDialog(null, "Datos Enviados");
                limpiarDatosNotas();
                limpiarTablaNotas();
                listarNotas();
            } else {
                JOptionPane.showMessageDialog(null, "Error!!... Al Enviar");
            }

            ////////////
            txtid_notaRec.setText("");
            txtnom_Clientes.setText("");
            txtnom_maquina.setText("");
            txtmarca_maquina.setText("");
            txtmodelo_maquina.setText("");
            txtObservaciones.setText("");
            panel.setSelectedComponent(pPendientes);
        }
    }//GEN-LAST:event_btnEnviarNotasActionPerformed

    private void btnAñadirFechaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAñadirFechaActionPerformed

        Date mFecha = jdcFecha.getDate();

        if (mFecha != null) {
            // Formatear la fecha como una cadena antes de mostrarla en el JTextField
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaFormateada = sdf.format(mFecha);
            txtFecha.setText(fechaFormateada);
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una fecha válida.");
        }
    }//GEN-LAST:event_btnAñadirFechaActionPerformed

    private void btnBuscarNotasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarNotasActionPerformed
      String entradaBusqueda = txtBuscarNotas.getText();

    if (!entradaBusqueda.isEmpty()) {
        DefaultTableModel modeloTabla = (DefaultTableModel) tablaNotas.getModel();
        modeloTabla.setRowCount(0); // Limpiar todas las filas de la tabla
        
        // Limpiar todas las filas resaltadas antes de la búsqueda
        tablaNotas.clearSelection();

        // Guardar las filas resaltadas antes de la búsqueda
        Set<Integer> filasResaltadasGuardadas = new HashSet<>(filasResaltadas);

        try {
            // Intentar buscar por id
            int idBusqueda = Integer.parseInt(entradaBusqueda);
            nt.setId_notaRec(idBusqueda);

            List<notas> resultados = daoN.BuscarNotas(nt, true);
            for (notas nt : resultados) {
                // Agregar las filas resultantes a la tabla
                Object[] rowData = new Object[]{
                    nt.getId_notaRec(),
                    nt.getNom_Clientes(),
                    nt.getNom_maquina(),
                    nt.getMarca_Maquina(),
                    nt.getModelo_maquina(),
                    nt.getFecha(),
                    nt.getAtencion(),
                    nt.getObservaciones(),
                    nt.getEstado_Maquina()
                };
                modeloTabla.addRow(rowData);
                
                // Si la fila estaba resaltada previamente, resaltarla nuevamente
                if (filasResaltadasGuardadas.contains(nt.getId_notaRec())) {
                    tablaNotas.addRowSelectionInterval(modeloTabla.getRowCount() - 1, modeloTabla.getRowCount() - 1);
                }
            }
        } catch (NumberFormatException e) {
            // No es un número, continuar con la búsqueda por nombre
            nt.setNom_Clientes(entradaBusqueda + "%");

            List<notas> resultados = daoN.BuscarNotas(nt, false);
            for (notas nt : resultados) {
                // Agregar las filas resultantes a la tabla
                Object[] rowData = new Object[]{
                    nt.getId_notaRec(),
                    nt.getNom_Clientes(),
                    nt.getNom_maquina(),
                    nt.getMarca_Maquina(),
                    nt.getModelo_maquina(),
                    nt.getFecha(),
                    nt.getAtencion(),
                    nt.getObservaciones(),
                    nt.getEstado_Maquina()
                };
                modeloTabla.addRow(rowData);
                
                // Si la fila estaba resaltada previamente, resaltarla nuevamente
                if (filasResaltadasGuardadas.contains(nt.getId_notaRec())) {
                    tablaNotas.addRowSelectionInterval(modeloTabla.getRowCount() - 1, modeloTabla.getRowCount() - 1);
                }
            }

            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No se encontró ningún Cliente con ese Folio o nombre.", "Resultado de búsqueda", JOptionPane.INFORMATION_MESSAGE);
                limpiarDatosNotas();
                limpiarTablaNotas();
                listarNotas();
            }
        }
    } else {
        JOptionPane.showMessageDialog(null, "Ingrese un Folio o nombre de Cliente para buscar.", "Error", JOptionPane.ERROR_MESSAGE);
        limpiarDatosNotas();
        limpiarTablaNotas();
        listarNotas();
    }
        
        /* String entradaBusqueda = txtBuscarNotas.getText();

    if (!entradaBusqueda.isEmpty()) {
        DefaultTableModel modeloTabla = (DefaultTableModel) tablaNotas.getModel();
        modeloTabla.setRowCount(0); // Limpiar todas las filas de la tabla
        
        // Guardar las filas resaltadas antes de la búsqueda
        Set<Integer> filasResaltadasGuardadas = new HashSet<>(filasResaltadas);

        try {
            // Intentar buscar por id
            int idBusqueda = Integer.parseInt(entradaBusqueda);
            nt.setId_notaRec(idBusqueda);

            List<notas> resultados = daoN.BuscarNotas(nt, true);
            for (notas nt : resultados) {
                // Agregar las filas resultantes a la tabla, manteniendo el resaltado si corresponde
                Object[] rowData = new Object[]{
                    nt.getId_notaRec(),
                    nt.getNom_Clientes(),
                    nt.getNom_maquina(),
                    nt.getMarca_Maquina(),
                    nt.getModelo_maquina(),
                    nt.getFecha(),
                    nt.getAtencion(),
                    nt.getObservaciones(),
                    nt.getEstado_Maquina()
                };
                modeloTabla.addRow(rowData);
                
                if (filasResaltadasGuardadas.contains(nt.getId_notaRec())) {
                    tablaNotas.setRowSelectionInterval(modeloTabla.getRowCount() - 1, modeloTabla.getRowCount() - 1);
                }
            }
        } catch (NumberFormatException e) {
            // No es un número, continuar con la búsqueda por nombre
            nt.setNom_Clientes(entradaBusqueda + "%");

            List<notas> resultados = daoN.BuscarNotas(nt, false);
            for (notas nt : resultados) {
                // Agregar las filas resultantes a la tabla, manteniendo el resaltado si corresponde
                Object[] rowData = new Object[]{
                    nt.getId_notaRec(),
                    nt.getNom_Clientes(),
                    nt.getNom_maquina(),
                    nt.getMarca_Maquina(),
                    nt.getModelo_maquina(),
                    nt.getFecha(),
                    nt.getAtencion(),
                    nt.getObservaciones(),
                    nt.getEstado_Maquina()
                };
                modeloTabla.addRow(rowData);
                
                if (filasResaltadasGuardadas.contains(nt.getId_notaRec())) {
                    tablaNotas.setRowSelectionInterval(modeloTabla.getRowCount() - 1, modeloTabla.getRowCount() - 1);
                }
            }

            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No se encontró ningún Cliente con ese Folio o nombre.", "Resultado de búsqueda", JOptionPane.INFORMATION_MESSAGE);
                limpiarDatosNotas();
                limpiarTablaNotas();
                listarNotas();
            }
        }
    } else {
        JOptionPane.showMessageDialog(null, "Ingrese un Folio o nombre de Cliente para buscar.", "Error", JOptionPane.ERROR_MESSAGE);
        limpiarDatosNotas();
        limpiarTablaNotas();
        listarNotas();
    }*/
      
    }//GEN-LAST:event_btnBuscarNotasActionPerformed

    private void btnEliminarNotasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarNotasActionPerformed
        if (!txtid_notaRec.getText().isEmpty()) {
            int confirmacion = JOptionPane.showConfirmDialog(rootPane, "¿Seguro de Eliminar la Nota?", "Confrimar", 2);
            if (confirmacion == 0) {
                nt.setId_notaRec(Integer.parseInt(txtid_notaRec.getText()));
                daoN.eliminarNotas(nt);
                limpiarDatosNotas();
                limpiarTablaNotas();
                listarNotas();
            }
        }
    }//GEN-LAST:event_btnEliminarNotasActionPerformed

    private void btnEditarNotasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarNotasActionPerformed

        int fila = tablaNotas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un Dato de la Tabla");
        } else {

            nt.setId_notaRec(Integer.parseInt(txtid_notaRec.getText()));
            nt.setNom_Clientes(txtnom_Clientes.getText());
            nt.setNom_maquina(txtnom_maquina.getText());
            nt.setMarca_Maquina(txtmarca_maquina.getText());
            nt.setModelo_maquina(txtmodelo_maquina.getText());
            nt.setFecha(txtFecha.getText());
            nt.setAtencion((String) combotipo_Atencion.getSelectedItem());
            nt.setObservaciones(txtObservaciones.getText());
            nt.setEstado_Maquina((String) comboEstado_maquina.getSelectedItem());

            if (daoN.editarNotas(nt)) {
                JOptionPane.showMessageDialog(null, "Modificación Realizada con Exito");
                limpiarDatosNotas();
                limpiarTablaNotas();
                listarNotas();
            } else {
                JOptionPane.showMessageDialog(null, "Error!!... Al Modificar");
            }
        }
    }//GEN-LAST:event_btnEditarNotasActionPerformed

    private void btnRegistrarNotasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarNotasActionPerformed
        //////////////////////////
        nt.setNom_Clientes(txtnom_Clientes.getText());
        nt.setNom_maquina(txtnom_maquina.getText());
        nt.setMarca_Maquina(txtmarca_maquina.getText());
        nt.setModelo_maquina(txtmodelo_maquina.getText());
        nt.setFecha(txtFecha.getText());
        nt.setAtencion((String) combotipo_Atencion.getSelectedItem());
        nt.setObservaciones(txtObservaciones.getText());
        nt.setEstado_Maquina((String) comboEstado_maquina.getSelectedItem());

        if (daoN.insertar(nt)) {
            JOptionPane.showMessageDialog(null, "Nota Registrada Exitosamente");
            limpiarDatosNotas();
            limpiarTablaNotas();
            listarNotas();
        } else {
            JOptionPane.showMessageDialog(null, "ERROR!!.. - La Nota no se Pudo Registrar");
        }
    }//GEN-LAST:event_btnRegistrarNotasActionPerformed

    private void tablaNotasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaNotasMouseClicked
        // TODO add your handling code here:
        int fila = tablaNotas.getSelectedRow();
        txtid_notaRec.setText(tablaNotas.getValueAt(fila, 0).toString());
        txtnom_Clientes.setText(tablaNotas.getValueAt(fila, 1).toString());
        txtnom_maquina.setText(tablaNotas.getValueAt(fila, 2).toString());
        txtmarca_maquina.setText(tablaNotas.getValueAt(fila, 3).toString());
        txtmodelo_maquina.setText(tablaNotas.getValueAt(fila, 4).toString());
        txtFecha.setText(tablaNotas.getValueAt(fila, 5).toString());
        combotipo_Atencion.setSelectedItem(tablaNotas.getValueAt(fila, 6).toString());
        txtObservaciones.setText(tablaNotas.getValueAt(fila, 7).toString());
        comboEstado_maquina.setSelectedItem(tablaNotas.getValueAt(fila, 8).toString());
    }//GEN-LAST:event_tablaNotasMouseClicked

    private void btnBuscarPendienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarPendienteActionPerformed
        DaoPendientes daoPendiente = new DaoPendientes();
        pendientes pen = new pendientes();

        String entradaBusqueda = txtBuscarPendiente.getText();

        if (!entradaBusqueda.isEmpty()) {
            try {
                // Intentar buscar por id
                int idBusqueda = Integer.parseInt(entradaBusqueda);
                pen.setId_notarec(idBusqueda);

                if (daoPendiente.buscarPendientePorIdONombre(pen, true)) {
                    // Mostrar resultado en la tabla
                    mostrarResultadoBusquedaPendientes(pen);

                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró ninguna Nota con ese Folio.", "Resultado de búsqueda", JOptionPane.INFORMATION_MESSAGE);
                    limpiarDatosPendientes();
                }
            } catch (NumberFormatException e) {
                // No es un número, continuar con la búsqueda por nombre
                pen.setCliente(entradaBusqueda);

                if (daoPendiente.buscarPendientePorIdONombre(pen, false)) {
                    // Mostrar resultado en la tabla
                    mostrarResultadoBusquedaPendientes(pen);

                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró ninguna Nota con ese Nombre de Cliente.", "Resultado de búsqueda", JOptionPane.INFORMATION_MESSAGE);
                    limpiarDatosPendientes();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ingrese un Folio o nombre de Cliente para buscar.", "Error", JOptionPane.ERROR_MESSAGE);
            limpiarDatosPendientes();
        }
    }//GEN-LAST:event_btnBuscarPendienteActionPerformed

    private void btnEliminarPendientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarPendientesActionPerformed
        // TODO add your handling code here:
        if (!txtidnotarecPendiente.getText().isEmpty()) {
            int confirmacion = JOptionPane.showConfirmDialog(rootPane, "¿Seguro de Dar de Baja el Registro?", "Confrimar", 2);
            if (confirmacion == 0) {
                pe.setId_pendientes(Integer.parseInt(txtidnotarecPendiente.getText()));
                daoPendiente.eliminarPendientes(pe);
                limpiarDatosPendientes();
                limpiarTablaPendientes();
                listarPendientes();
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione el Pendiente a Eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione el Pendiente a Eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnEliminarPendientesActionPerformed

    private void txtEliminarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEliminarEmpleadoActionPerformed
        // TODO add your handling code here:
        if (!txtid_Empleado.getText().isEmpty()) {
            int confirmacion = JOptionPane.showConfirmDialog(rootPane, "¿Seguro de Dar de Baja al Empleado?", "Confrimar", 2);
            if (confirmacion == 0) {
                em.setId_empleado(Integer.parseInt(txtid_Empleado.getText()));
                daoE.eliminarEmpleados(em);
                limpiarDatosEmpleado();
                limpiarTablaEmpleado();
                listarEmpleado();
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione el Empleado a Eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione el Empleado a Eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txtEliminarEmpleadoActionPerformed

    private void btnImprimirReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirReciboActionPerformed
        /////////////////////////////
        int fila = tablaPendientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un Pendiente de la Tabla");
        } else {
            // Crear un objeto de la clase pendientes
            pendientes pe = new pendientes();

            // Configurar los valores para editar
            pe.setId_pendientes(Integer.parseInt(txtid_Registro.getText()));
            pe.setEstado_Maquina("Entregada");

            // Llamar al método correcto para editar en la base de datos
            if (daoPendiente.editarEstadoMaquina(pe)) {
                JOptionPane.showMessageDialog(null, "Maquina Entregada");
                limpiarDatosPendientes();
                limpiarTablaPendientes();
                listarPendientes();
            } else {
                JOptionPane.showMessageDialog(null, "Error!!... Al Entregar");
            }
        }

        ////////////////////////////
        int confirmResult = JOptionPane.showConfirmDialog(null, "¿Desea imprimir el recibo?", "Imprimir Recibo", JOptionPane.YES_NO_OPTION);
        if (confirmResult == JOptionPane.YES_OPTION) {
// Obtener el índice de la fila seleccionada
            int selectedRow = tablaPendientes.getSelectedRow();

            // Verificar si hay una fila seleccionada
            if (selectedRow != -1) {

                // Crear un objeto pendientes con todos los datos de la fila seleccionada
                DefaultTableModel model = (DefaultTableModel) tablaPendientes.getModel();
                pendientes selectedRecibo = new pendientes();

                // Obtener todos los valores de la fila seleccionada y establecerlos en el objeto pendientes
                for (int i = 0; i < model.getColumnCount(); i++) {
                    Object value = model.getValueAt(selectedRow, i);
                    switch (i) {
                        case 0:
                            selectedRecibo.setId_notarec(Integer.parseInt((String) value));
                            break;
                        case 1:
                            selectedRecibo.setCausante((String) value);
                            break;
                        case 2:
                            selectedRecibo.setEstado_Maquina((String) value);
                            break;
                        case 3:
                            selectedRecibo.setFecha_Revision((String) value);
                            break;
                        case 4:
                            selectedRecibo.setCliente((String) value);
                            break;
                        case 5:
                            selectedRecibo.setMaquina((String) value);
                            break;
                        case 6:
                            selectedRecibo.setEmpleado((String) value);
                            break;
                        case 7:
                            if (value instanceof Double) {
                                selectedRecibo.setTotal((Double) value);
                            } else if (value instanceof String) {
                                // Intenta convertir la cadena a Double
                                try {
                                    selectedRecibo.setTotal(Double.parseDouble((String) value));
                                } catch (NumberFormatException e) {
                                    // Manejo de la excepción si la cadena no es un número válido
                                    e.printStackTrace();
                                }
                            }

                            break;
                        case 8:
                            selectedRecibo.setPiezas((String) value);
                            break;
                    }
                }

                // Luego, llama al método ImprimirRecibo con el objeto pendientes completo
                DaoPendientes daoPendientes = new DaoPendientes();
                boolean impresionExitosa = false;
                try {
                    impresionExitosa = daoPendientes.ImprimirRecibo(selectedRecibo);
                } catch (BadElementException ex) {
                    Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (impresionExitosa) {
                    JOptionPane.showMessageDialog(null, "Recibo Creado.");
                } else {
                    JOptionPane.showMessageDialog(null, "Error al crear el Recibo.");
                }
            } else {
                // Si no hay fila seleccionada, llamar al método obtenerUltimoRecibo
                DaoPendientes daoPendientes = new DaoPendientes();
                pendientes lastPendiente = daoPendientes.obtenerUltimoRecibo();

                if (lastPendiente != null) {
                    // Llama al método ImprimirRecibo con la información del último recibo
                    boolean impresionExitosa = false;
                    try {
                        impresionExitosa = daoPendientes.ImprimirRecibo(lastPendiente);
                    } catch (BadElementException ex) {
                        Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (impresionExitosa) {
                        JOptionPane.showMessageDialog(null, "Se guardó en el Escritorio.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al crear el Recibo.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No hay registros en la base de datos.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Recibo Cancelado.");
        }

    }//GEN-LAST:event_btnImprimirReciboActionPerformed
// Función para validar y convertir una cadena a un entero

    private int validarEntero(String texto, String nombreCampo) {
        try {
            if (!texto.isEmpty()) {
                return Integer.parseInt(texto);
            } else {
                JOptionPane.showMessageDialog(null, "Error: El campo " + nombreCampo + " está vacío.");
                return 0;  // Puedes devolver un valor por defecto o manejarlo de otra manera según tus necesidades
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error: Ingresa un valor numérico válido para el campo " + nombreCampo + ".");
            return 0;  // Puedes devolver un valor por defecto o manejarlo de otra manera según tus necesidades
        }
    }

    private void mostrarResultadoBusquedaNotas(notas nt) {
        txtid_notaRec.setText(Integer.toString(nt.getId_notaRec()));
        txtnom_Clientes.setText(nt.getNom_Clientes());
        txtnom_maquina.setText(nt.getNom_maquina());
        txtmarca_maquina.setText(nt.getMarca_Maquina());
        txtmodelo_maquina.setText(nt.getModelo_maquina());
        txtFecha.setText(nt.getFecha());
        combotipo_Atencion.addItem(nt.getAtencion());//falta agregar comboBox
        txtObservaciones.setText(nt.getObservaciones());
        comboEstado_maquina.addItem(nt.getEstado_Maquina());//falta agregar comboBox

        int rowIndex = encontrarFilaEnTabla(pi.getId_Pieza());
        if (rowIndex != -1) {
            tablaNotas.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
            tablaNotas.scrollRectToVisible(tablaNotas.getCellRect(rowIndex, 0, true));
        }
    }

    private void mostrarResultadoBusqueda(piezas pi) {
        txtid_Pieza.setText(Integer.toString(pi.getId_Pieza()));
        txtNombrePieza.setText(pi.getNom_Pieza());
        txtMarca_pieza.setText(pi.getMarca_Pieza());
        txtModelo_pieza.setText(pi.getModelo_Pieza());
        txtCantidad_Piezas.setText(Integer.toString(pi.getCantidad()));
        txt_Precio.setText(pi.getPrecio_Pieza());

        int rowIndex = encontrarFilaEnTablaPiezas(pi.getId_Pieza());
        if (rowIndex != -1) {
            tablaPiezas.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
            tablaPiezas.scrollRectToVisible(tablaPiezas.getCellRect(rowIndex, 0, true));
        }
    }

    private void mostrarResultadoBusquedaPendientes(pendientes pe) {
        txtid_notaRec.setText(Integer.toString(pe.getId_pendientes()));
        txtCausantePendiente.setText(pe.getCausante());

        txtFechaPendiente.setText(pe.getFecha_Revision());
        txtTotalPendiente.setText(Double.toString(pe.getTotal()));
        txtidnotarecPendiente.setText(Integer.toString(pe.getId_notarec()));
        txtClientePendiente.setText(pe.getCliente());
        txtMaquinaPendiente.setText(pe.getMaquina());
        txtidEmpleadoPendiente.setText(Integer.toString(pe.getId_empleado()));
        txtEmpleadoPendiente.setText(pe.getEmpleado());
        txtidPiezasPendiente.setText(Integer.toString(pe.getId_Pieza()));
        txtPiezasPendiente.setText(pe.getPiezas());

        ///
        // Seleccionar la fila en la tabla
        int rowIndex = encontrarFilaEnTabla(pe.getId_notarec());
        if (rowIndex != -1) {
            tablaPendientes.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
            tablaPendientes.scrollRectToVisible(tablaPendientes.getCellRect(rowIndex, 0, true));
        }
        ////
    }

    // Método para restar el precio de la última fila seleccionada del total en txtSumatoria
    private void restarPrecioUltimaFilaSeleccionada(int idPieza) {
        DaoPiezas daoP = new DaoPiezas();

        try {
            // Obtener el precio de la última fila seleccionada
            double precioUltimaFila = daoP.obtenerPrecioPieza(idPieza);

            // Obtener el precio actual en txtSumatoria
            double precioActual = !txtSumatoria.getText().isEmpty() ? Double.parseDouble(txtSumatoria.getText()) : 0.0;

            // Restar el precio de la última fila seleccionada del total en txtSumatoria
            precioActual -= precioUltimaFila;

            // Actualizar el campo txtSumatoria
            txtSumatoria.setText(String.valueOf(precioActual));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /////////////////////////////
    private void incrementarCantidadPiezas(int idPieza, int cantidad) {
        try {
            // Llamar al método para incrementar la cantidad
            int cantidadFinal = daoP.incrementarCantidadPiezas(idPieza, cantidad);

            if (cantidadFinal != -1) {
                // Limpieza y actualización de la interfaz
                limpiarTablaPiezas();
                listarPiezas();

                txtCantidadFinal.setText(String.valueOf(cantidadFinal));
            } else {
                JOptionPane.showMessageDialog(null, "Error al incrementar la cantidad de piezas.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /////////////////////////////
    private void decrementarCantidadPiezas(int idPieza) {

        if (!txtid_Pieza.getText().isEmpty()) {
            try {
                idPieza = Integer.parseInt(txtid_Pieza.getText());

                // Establecer la cantidad a dar de baja en 1
                int cantidadEliminar = 1;

                // Llamar al método de eliminación
                int cantidadFinal = daoP.eliminarPiezas(idPieza, cantidadEliminar);

                if (cantidadFinal <= cantidadEliminar) {
                    cantidadEliminar = 0;
                    JOptionPane.showMessageDialog(null, "Tu Inventario queda en 2 ,añade Inventario mas Piezas.", "ADVERTENCIA", JOptionPane.INFORMATION_MESSAGE);
                    limpiarTablaPiezas();
                    listarPiezas();
                } else {
                    if (cantidadFinal != -1) {
                        // Limpieza y actualización de la interfaz
                        limpiarTablaPiezas();
                        listarPiezas();
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Por favor, selecciona una fila.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showConfirmDialog(null, "Error");
            }
        }
    }

    //////////////////////////////////////////////////////
    private int encontrarFilaEnTabla(int idNotaRec) {
        // Obtener el modelo de la tabla
        DefaultTableModel model = (DefaultTableModel) tablaNotas.getModel();

        // Iterar a través de las filas del modelo de la tabla para encontrar la que coincide con el idNotaRec
        for (int i = 0; i < model.getRowCount(); i++) {
            if (Integer.parseInt(model.getValueAt(i, 0).toString()) == idNotaRec) {
                return i; // Devolver el índice de la fila encontrada
            }
        }
        return -1; // Si no se encuentra la fila
    }

    ///////////////////////////////////////////////////////////////
    private int encontrarFilaEnTablaPiezas(int idPiezas) {
        // Obtener el modelo de la tabla
        DefaultTableModel model = (DefaultTableModel) tablaPiezas.getModel();

        // Iterar a través de las filas del modelo de la tabla para encontrar la que coincide con el idNotaRec
        for (int i = 0; i < model.getRowCount(); i++) {
            if (Integer.parseInt(model.getValueAt(i, 0).toString()) == idPiezas) {
                return i; // Devolver el índice de la fila encontrada
            }
        }
        return -1; // Si no se encuentra la fila
    }
    //////////////////////////////////////////////////////

    private int encontrarFilaEnTablaPendientes(int idFolio) {
        // Obtener el modelo de la tabla
        DefaultTableModel model = (DefaultTableModel) tablaPendientes.getModel();

        // Iterar a través de las filas del modelo de la tabla para encontrar la que coincide con el idNotaRec
        for (int i = 0; i < model.getRowCount(); i++) {
            if (Integer.parseInt(model.getValueAt(i, 0).toString()) == idFolio) {
                return i; // Devolver el índice de la fila encontrada
            }
        }
        return -1; // Si no se encuentra la fila
    }

    /////////////////////////////////////////////////////////////////////////
    ////////////////////////Metodos Limpiar//////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    void limpiarDatosCargo() {
        txtidcargo.setText("");
        txtcargo.setText("");
    }

    void limpiarTablaCargo() {
        for (int i = 0; i < modeloCargo.getRowCount(); i++) {
            modeloCargo.removeRow(i);
            i = 0 - 1;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    void limpiarDatosNotas() {
        txtid_notaRec.setText("");
        txtnom_Clientes.setText("");
        txtnom_maquina.setText("");
        txtmarca_maquina.setText("");
        txtmodelo_maquina.setText("");
        //txtFecha.setText("");
        txtObservaciones.setText("");

    }

    void limpiarTablaNotas() {
        for (int i = 0; i < modeloNotas.getRowCount(); i++) {
            modeloNotas.removeRow(i);
            i = 0 - 1;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    void limpiarDatosPiezas() {
        txtid_Pieza.setText("");
        txtNombrePieza.setText("");
        txtMarca_pieza.setText("");
        txtModelo_pieza.setText("");
        txtCantidad_Piezas.setText("");
        txt_Precio.setText("");

    }

    void limpiarTablaPiezas() {
        for (int i = 0; i < modeloPiezas.getRowCount(); i++) {
            modeloPiezas.removeRow(i);
            i = 0 - 1;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    void limpiarDatosEmpleado() {
        txtid_Empleado.setText("");
        txtNombreEmpleados.setText("");
        txtFechaEmpleados.setText("");
        txtIdCargoEmpleado.setText("");
        txtCargoEmpleado.setText("");

    }

    void limpiarTablaEmpleado() {
        for (int i = 0; i < modeloEmpleado.getRowCount(); i++) {
            modeloEmpleado.removeRow(i);
            i = 0 - 1;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    void limpiarDatosPendientes() {
        txtidnotarecPendiente.setText("");
        txtCausantePendiente.setText("");
        //txtFechaPendiente.setText("");
        txtTotalPendiente.setText("");
        txtidnotarecPendiente.setText("");
        txtClientePendiente.setText("");
        txtMaquinaPendiente.setText("");
        txtidEmpleadoPendiente.setText("");
        txtEmpleadoPendiente.setText("");
        txtidPiezasPendiente.setText("");
        txtPiezasPendiente.setText("");
        txtid_Registro.setText("");

    }

    void limpiarTablaPendientes() {
        for (int i = 0; i < modeloPendiente.getRowCount(); i++) {
            modeloPendiente.removeRow(i);
            i = 0 - 1;
        }
    }

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
            java.util.logging.Logger.getLogger(MenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAñadirFecha;
    private javax.swing.JButton btnAñadirFechaEmpleados;
    private javax.swing.JButton btnAñadirFechaPen;
    private javax.swing.JButton btnAñadirPrecio;
    private javax.swing.JButton btnBuscaCargo;
    private javax.swing.JButton btnBuscaNotaPendiente;
    private javax.swing.JButton btnBuscarEmpleadoPendiente;
    private javax.swing.JButton btnBuscarNotas;
    private javax.swing.JButton btnBuscarPendiente;
    private javax.swing.JButton btnBuscarPiezaPendiente;
    private javax.swing.JButton btnBuscarPiezas;
    private javax.swing.JButton btnCargo;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEditar1;
    private javax.swing.JButton btnEditarNotas;
    private javax.swing.JButton btnEditarPiezas;
    private javax.swing.JButton btnEliminarCargo;
    private javax.swing.JButton btnEliminarNotas;
    private javax.swing.JButton btnEliminarPendientes;
    private javax.swing.JButton btnEliminarSeleccion;
    private javax.swing.JButton btnEmpleados;
    private javax.swing.JButton btnEnviarCargo;
    private javax.swing.JButton btnEnviarEmpleado;
    private javax.swing.JButton btnEnviarNotas;
    private javax.swing.JButton btnEnviarPieza;
    private javax.swing.JButton btnImprimirNota;
    private javax.swing.JButton btnImprimirRecibo;
    private javax.swing.JButton btnModificarEmpleado;
    private javax.swing.JButton btnPendientes;
    private javax.swing.JButton btnPiezas;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JButton btnRegistrarEmpleado;
    private javax.swing.JButton btnRegistrarMaquinas;
    private javax.swing.JButton btnRegistrarNotas;
    private javax.swing.JButton btnRegistrarPiezas;
    private javax.swing.JButton btnUsuarios;
    private javax.swing.JButton btneliminarPiezas;
    private javax.swing.JComboBox<String> comboEstado_maquina;
    private javax.swing.JComboBox<String> comboReparada;
    private javax.swing.JComboBox<String> combotipo_Atencion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private com.toedter.calendar.JDateChooser jdcFecha;
    private com.toedter.calendar.JDateChooser jdcFechaEmpleados;
    private com.toedter.calendar.JDateChooser jdcFechaPendiente;
    private javax.swing.JPanel pCargos;
    private javax.swing.JPanel pEmpleados;
    private javax.swing.JPanel pPendientes;
    private javax.swing.JPanel pPiezas;
    private javax.swing.JPanel pUsuarios;
    private javax.swing.JTabbedPane panel;
    private javax.swing.JTable tablaEmpleado;
    private javax.swing.JTable tablaNotas;
    private javax.swing.JTable tablaPendientes;
    private javax.swing.JTable tablaPiezas;
    private javax.swing.JTable tablacargos;
    private javax.swing.JTextField txtBuscarNotas;
    private javax.swing.JTextField txtBuscarPendiente;
    private javax.swing.JTextField txtBuscarPiezas;
    private javax.swing.JTextField txtCantidadFinal;
    private javax.swing.JTextField txtCantidad_Piezas;
    private javax.swing.JTextField txtCargoEmpleado;
    private javax.swing.JTextField txtCausantePendiente;
    private javax.swing.JTextField txtClientePendiente;
    private javax.swing.JButton txtEliminarEmpleado;
    private javax.swing.JTextField txtEmpleadoPendiente;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtFechaEmpleados;
    private javax.swing.JTextField txtFechaPendiente;
    private javax.swing.JTextField txtIdCargoEmpleado;
    private javax.swing.JTextField txtMaquinaPendiente;
    private javax.swing.JTextField txtMarca_pieza;
    private javax.swing.JTextField txtModelo_pieza;
    private javax.swing.JTextField txtNombreEmpleados;
    private javax.swing.JTextField txtNombrePieza;
    private javax.swing.JTextField txtObservaciones;
    private javax.swing.JTextField txtPiezasAcumuladas;
    private javax.swing.JTextField txtPiezasPendiente;
    private javax.swing.JTextField txtSumatoria;
    private javax.swing.JTextField txtTotalPendiente;
    private javax.swing.JTextField txt_Precio;
    private javax.swing.JTextField txtcargo;
    private javax.swing.JTextField txtidEmpleadoPendiente;
    private javax.swing.JTextField txtidPiezasPendiente;
    private javax.swing.JTextField txtid_Empleado;
    private javax.swing.JTextField txtid_Pieza;
    private javax.swing.JTextField txtid_Registro;
    private javax.swing.JTextField txtid_notaRec;
    private javax.swing.JTextField txtidcargo;
    private javax.swing.JTextField txtidnotarecPendiente;
    private javax.swing.JTextField txtmarca_maquina;
    private javax.swing.JTextField txtmodelo_maquina;
    private javax.swing.JTextField txtnom_Clientes;
    private javax.swing.JTextField txtnom_maquina;
    // End of variables declaration//GEN-END:variables
}
