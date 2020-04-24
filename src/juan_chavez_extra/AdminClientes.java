package juan_chavez_extra;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

public class AdminClientes extends javax.swing.JFrame {

    int idCliente;

    public AdminClientes() {
        initComponents();
        Dimension size = getToolkit().getScreenSize();
        setLocation((size.width - getWidth()) / 2, (size.height - getHeight()) / 2);
        llenarTablaCliente();
        cbOrden.addItem("ordenar por");
        cbOrden.setSelectedItem("ordenar por");
        idCliente = 0;
    }

    private void llenarTablaCliente() {
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "SELECT codigocliente AS id,\n"
                    + "	nombrecliente AS nombre,\n"
                    + "	ncarnet AS carnet,\n"
                    + "	telefono, \n"
                    + "	count(prestamo.CODIGOPRESTAMO) AS prestamos\n"
                    + "from cliente left join prestamo on codigocliente = codigoclientep " + getCondicionWhere() + " GROUP BY 1\n"
                    + getOrden();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            DefaultTableModel table = (DefaultTableModel) DbUtils.resultSetToTableModel(rs);
            MisUtiles.acomodarTabla(table, tablaClientes, new int[]{35, 250, 70, 70});
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void llenarTablaPrestamos() {
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "SELECT codigoprestamo AS id,\n"
                    + " codigoempp AS idEm, \n"
                    + " codigocopia AS idcopia, \n"
                    + "	titulo,\n"
                    + "	fechaprestamo AS \'prestamo\',\n"
                    + "	fechadevolucion AS devolucion,\n"
                    + "	fechalimite AS limite,\n"
                    + "	preciorenta AS \'renta(Bs.)\',\n"
                    + "	preciocompra AS \'compra(Bs.)\',\n"
                    + "	monto AS \'pago extra\' \n"
                    + "FROM  ((video JOIN copia ON codigovideo = codigov) JOIN prestamo ON codigocopiap = codigocopia) left JOIN multa ON CODIGOPRESTAMOM = codigoprestamo\n"
                    + "WHERE codigoclienteP = " + idCliente + getCondicion();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            DefaultTableModel table = (DefaultTableModel) DbUtils.resultSetToTableModel(rs);
            MisUtiles.acomodarTabla(table, tablaPrestamos, new int[]{35, 38, 0, 225, 74, 74, 74, 65, 80});
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void llenarTodosPrestamos() {
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "SELECT codigoprestamo AS id,\n"
                    + " codigoempp AS idEm, \n"
                    + " codigocopia AS idcopia, \n"
                    + "	nombrecliente AS 'cliente',\n"
                    + "	titulo,\n"
                    + "	fechaprestamo AS 'prestamo',\n"
                    + "	fechadevolucion AS devolucion,\n"
                    + "	fechalimite AS limite,\n"
                    + "	preciorenta AS 'renta(Bs.)',\n"
                    + "	preciocompra AS 'compra(Bs.)',\n"
                    + "	monto AS 'pago extra' \n"
                    + "FROM ((video\n"
                    + "JOIN copia ON codigovideo = codigov)\n"
                    + "JOIN prestamo ON codigocopiap = codigocopia)\n"
                    + "LEFT JOIN multa ON CODIGOPRESTAMOM = codigoprestamo\n"
                    + "JOIN cliente ON codigocliente = codigoclientep\n"
                    + getCondicionWhere() + "\n";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            DefaultTableModel table = (DefaultTableModel) DbUtils.resultSetToTableModel(rs);
            MisUtiles.acomodarTabla(table, tablaPrestamos, new int[]{35, 38, 0, 100, 150, 74, 74, 74, 65, 80});
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private String getOrden() {
        String tipoOrden = cbOrden.getSelectedItem().toString();
        if (tipoOrden.equals("ordenar por")) {
            tipoOrden = "id";
        }
        return "ORDER BY " + tipoOrden + (jRadioButton2.isSelected() ? " DESC" : " ASC");
    }

    private String getCondicion() {
        if (btnRangoEspecifico.isFocusOwner()) {
            String rangoEspecifico = " AND " + getRango();
            return rangoEspecifico;
        }
        switch (cbTiempo.getSelectedItem().toString()) {
            case "ultima semana":
                return " AND fechaprestamo > " + MisUtiles.obtenerFechaDiasAntes(7);
            case "ultimo mes":
                return " AND fechaprestamo > " + MisUtiles.obtenerFechaDiasAntes(31);
            default:
                return "";
        }
    }

    private String getRango() {
        return "fechaprestamo > " + desdeAnio.getText() + desdeMes.getText() + desdeDia.getText()
                + " AND fechaprestamo < " + hastaAnio.getText() + hastaMes.getText() + hastaDia.getText();
    }

    private String getCondicionWhere() {
        if (btnRangoEspecifico.isFocusOwner()) {
            String rangoEspecifico = "Where " + getRango();
            return rangoEspecifico;
        }
        switch (cbTiempo.getSelectedItem().toString()) {
            case "ultima semana":
                return "WHERE fechaprestamo > " + MisUtiles.obtenerFechaDiasAntes(7);
            case "ultimo mes":
                return "WHERE fechaprestamo > " + MisUtiles.obtenerFechaDiasAntes(31);
            default:
                return "";
        }
    }

    private void eliminar() {
        Connection con = MisUtiles.getConection();
        try {
            PreparedStatement ps;
            ps = con.prepareStatement("DELETE from cliente where CODIGOCLIENTE = " + idCliente + ";");
            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Cliente eliminado exitosamente");
                refrescar();
            } else {
                JOptionPane.showMessageDialog(null, "Error al eliminar cliente");
            }
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e + "\n posiblemente necesita borrar otros"
                    + "elementos relacionados a este");
        }

    }

    private void refrescar() {
        idCliente = 0;
        llenarTablaCliente();
        llenarTablaPrestamos();
    }

    private void seleccionarTodo(JTextField componente) {
        componente.select(0, componente.getText().length());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaClientes = new javax.swing.JTable();
        btnEliminarCliente = new javax.swing.JButton();
        btnRefrescar = new javax.swing.JButton();
        cbOrden = new javax.swing.JComboBox<>();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        cbTiempo = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaPrestamos = new javax.swing.JTable();
        desdeAnio = new javax.swing.JTextField();
        desdeMes = new javax.swing.JTextField();
        hastaAnio = new javax.swing.JTextField();
        hastaMes = new javax.swing.JTextField();
        desdeDia = new javax.swing.JTextField();
        hastaDia = new javax.swing.JTextField();
        btnRangoEspecifico = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnMostrarTodo = new javax.swing.JButton();
        btnRegistrar = new javax.swing.JButton();
        codigoCopia = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btnCerrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Administrar clientes");
        setResizable(false);

        tablaClientes.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaClientes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tablaClientes.setFocusable(false);
        tablaClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaClientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaClientes);

        btnEliminarCliente.setText("Eliminar cliente");
        btnEliminarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarClienteActionPerformed(evt);
            }
        });

        btnRefrescar.setText("refrescar");
        btnRefrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefrescarActionPerformed(evt);
            }
        });

        cbOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "id", "nombre", "carnet", "telefono", "prestamos" }));
        cbOrden.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cbOrdenFocusGained(evt);
            }
        });

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Ascendente");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Descendente");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Clientes");

        cbTiempo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "siempre", "ultima semana", "ultimo mes" }));

        jLabel3.setText("prestamos desde:");

        tablaPrestamos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "id", "titulo", "prestamo", "devolucion", "limite", "renta(Bs.)", "compra(Bs.)", "pago extra"
            }
        ));
        tablaPrestamos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tablaPrestamos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaPrestamosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaPrestamos);

        desdeAnio.setText("AAAA");
        desdeAnio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                desdeAnioFocusGained(evt);
            }
        });

        desdeMes.setText("MM");
        desdeMes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                desdeMesFocusGained(evt);
            }
        });

        hastaAnio.setText("AAAA");
        hastaAnio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                hastaAnioFocusGained(evt);
            }
        });

        hastaMes.setText("MM");
        hastaMes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                hastaMesFocusGained(evt);
            }
        });

        desdeDia.setText("DD");
        desdeDia.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                desdeDiaFocusGained(evt);
            }
        });

        hastaDia.setText("DD");
        hastaDia.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                hastaDiaFocusGained(evt);
            }
        });

        btnRangoEspecifico.setText("Fecha Especifica");
        btnRangoEspecifico.setToolTipText("no inclusivo");
        btnRangoEspecifico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRangoEspecificoActionPerformed(evt);
            }
        });

        jLabel2.setText("desde:");

        jLabel4.setText("hasta:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Prestamos");

        btnMostrarTodo.setText("mostrar todos");
        btnMostrarTodo.setToolTipText("");
        btnMostrarTodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMostrarTodoActionPerformed(evt);
            }
        });

        btnRegistrar.setText("registrar cliente");
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        codigoCopia.setEditable(false);

        jLabel6.setText("Codigo copia");

        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(codigoCopia, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCerrar))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 785, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 519, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(298, 298, 298)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cbOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton2))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnRefrescar)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cbTiempo, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(77, 77, 77)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(hastaAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(desdeAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(hastaMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(hastaDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(desdeMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(desdeDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45))
                            .addComponent(btnEliminarCliente, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnRangoEspecifico)
                                    .addComponent(btnRegistrar)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(149, 149, 149)
                                .addComponent(btnMostrarTodo)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cbTiempo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3)
                                    .addComponent(btnRefrescar))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cbOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jRadioButton1)
                                    .addComponent(jRadioButton2)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(btnRangoEspecifico)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(65, 65, 65)
                                .addComponent(btnRegistrar))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(desdeAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(hastaAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(desdeMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(desdeDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(hastaMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(hastaDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminarCliente)
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(btnMostrarTodo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(codigoCopia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(btnCerrar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tablaClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaClientesMouseClicked
        int fila = tablaClientes.getSelectedRow();
        idCliente = Integer.parseInt(fila > -1 ? tablaClientes.getValueAt(fila, 0).toString() : "0");
        llenarTablaPrestamos();
    }//GEN-LAST:event_tablaClientesMouseClicked

    private void btnEliminarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarClienteActionPerformed
        if (idCliente != 0) {
            String mensaje = "esto eliminara todos los elementos relacionados a este cliente(" + tablaClientes.getValueAt(tablaClientes.getSelectedRow(), 1) + ")";
            if (JOptionPane.showConfirmDialog(null, mensaje) == JOptionPane.YES_OPTION) {
                eliminar();
                llenarTablaCliente();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un cliente desde \n"
                    + "la tabla para que este sea eliminado");
        }
    }//GEN-LAST:event_btnEliminarClienteActionPerformed

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarActionPerformed
        refrescar();
    }//GEN-LAST:event_btnRefrescarActionPerformed

    private void cbOrdenFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbOrdenFocusGained
        cbOrden.removeItem("ordenar por");
    }//GEN-LAST:event_cbOrdenFocusGained

    private void btnRangoEspecificoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRangoEspecificoActionPerformed
        refrescar();
        //llenarTablaCliente();
    }//GEN-LAST:event_btnRangoEspecificoActionPerformed

    private void desdeAnioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_desdeAnioFocusGained
        seleccionarTodo(desdeAnio);
    }//GEN-LAST:event_desdeAnioFocusGained

    private void desdeMesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_desdeMesFocusGained
        seleccionarTodo(desdeMes);
    }//GEN-LAST:event_desdeMesFocusGained

    private void desdeDiaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_desdeDiaFocusGained
        seleccionarTodo(desdeDia);
    }//GEN-LAST:event_desdeDiaFocusGained

    private void hastaAnioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_hastaAnioFocusGained
        seleccionarTodo(hastaAnio);
    }//GEN-LAST:event_hastaAnioFocusGained

    private void hastaMesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_hastaMesFocusGained
        seleccionarTodo(hastaMes);
    }//GEN-LAST:event_hastaMesFocusGained

    private void hastaDiaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_hastaDiaFocusGained
        seleccionarTodo(hastaDia);
    }//GEN-LAST:event_hastaDiaFocusGained

    private void btnMostrarTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMostrarTodoActionPerformed
        llenarTodosPrestamos();
    }//GEN-LAST:event_btnMostrarTodoActionPerformed

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        new RegistroCliente().setVisible(true);
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void tablaPrestamosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaPrestamosMouseClicked
        int fila = tablaPrestamos.getSelectedRow();
        codigoCopia.setText(fila > -1 ? tablaPrestamos.getValueAt(fila, 2).toString() : "");
    }//GEN-LAST:event_tablaPrestamosMouseClicked

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

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
            java.util.logging.Logger.getLogger(AdminClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminClientes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnEliminarCliente;
    private javax.swing.JButton btnMostrarTodo;
    private javax.swing.JButton btnRangoEspecifico;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbOrden;
    private javax.swing.JComboBox<String> cbTiempo;
    private javax.swing.JTextField codigoCopia;
    private javax.swing.JTextField desdeAnio;
    private javax.swing.JTextField desdeDia;
    private javax.swing.JTextField desdeMes;
    private javax.swing.JTextField hastaAnio;
    private javax.swing.JTextField hastaDia;
    private javax.swing.JTextField hastaMes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tablaClientes;
    private javax.swing.JTable tablaPrestamos;
    // End of variables declaration//GEN-END:variables
}
