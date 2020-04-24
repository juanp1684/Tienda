package juan_chavez_extra;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

public class AlquilerCompra extends javax.swing.JFrame {

    boolean esEmpleado;
    int idCopia;
    String idEmp;

    public AlquilerCompra(boolean esEmpleado, String idCliente, String idEmp) {
        this.esEmpleado = esEmpleado;
        this.idEmp = idEmp;
        initComponents();
        codigoCliente.setText(idCliente);
        ocultarParaCliente();
        idCopia = 0;
        buscarNombre();
        Dimension size = getToolkit().getScreenSize();
        setLocation((size.width - getWidth()) / 2, (size.height - getHeight()) / 2);
        llenarTablaCopias();
    }

    private void ocultarParaCliente() {
        codigoCliente.setEditable(esEmpleado);
        nroDias.setEditable(esEmpleado);
        btnRegCliente.setVisible(esEmpleado);
        btnDevolucion.setVisible(esEmpleado);
    }

    private void llenarTablaCopias() {
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "SELECT codigocopia AS id,\n"
                    + "	titulo,\n"
                    + " duracion, \n"
                    + " genero, \n"
                    + "	estado\n"
                    + "FROM video JOIN copia ON codigov = codigovideo\n"
                    + "where disponibilidad = 1 AND (titulo like ?)\n"
                    + "order by titulo";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + buscador.getText() + "%");
            ResultSet rs = ps.executeQuery();
            DefaultTableModel table = (DefaultTableModel) DbUtils.resultSetToTableModel(rs);
            MisUtiles.acomodarTabla(table, tablaCopias, new int[]{0, 200, 65, 75});
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private String getLimite() {
        if (btnComprar.isFocusOwner()) {
            return null;
        }
        return MisUtiles.obtenerFechaDiasAntes(-1 * Integer.parseInt(nroDias.getText()));
    }

    private void realizarTransaccion() {
        if (idCopia < 1) {
            JOptionPane.showMessageDialog(null, "Seleccionar la copia deseada en la tabla por favor");
            return;
        }
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "INSERT INTO prestamo(codigocopiap, codigoclientep, codigoempp, fechaprestamo, "
                    + "fechalimite) \n"
                    + "values (?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCopia);
            ps.setString(2, codigoCliente.getText());
            ps.setString(3, idEmp);
            ps.setString(4, MisUtiles.getToday());
            ps.setString(5, getLimite());
            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Transaccion exitosa de \n"
                        + "una copia de " + tablaCopias.getValueAt(tablaCopias.getSelectedRow(), 1));
                retirarCopia();
                llenarTablaCopias();
                idCopia = 0;
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo realizar la transaccion");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void retirarCopia() {
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "UPDATE copia SET DISPONIBILIDAD = 0 WHERE  CODIGOCOPIA = " + idCopia;
            PreparedStatement ps = conn.prepareStatement(sql);
            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "la copia ha sido retirada efectivamente");
            } else {
                JOptionPane.showMessageDialog(null, "la copia no pudo ser retirada del sistema \n"
                        + "se recomeinda consultar con un tecnico");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void buscarNombre() {
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "select nombrecliente AS nombre from cliente \n"
                    + "where codigocliente = ? ;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, codigoCliente.getText());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                labelNombre.setText(rs.getString("nombre"));
            } else {
                labelNombre.setText("");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        codigoCliente = new javax.swing.JTextField();
        titulo = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaCopias = new javax.swing.JTable();
        buscador = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        titulo1 = new javax.swing.JLabel();
        nroDias = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        btnAlquilar = new javax.swing.JButton();
        btnComprar = new javax.swing.JButton();
        labelNombre = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnRegCliente = new javax.swing.JButton();
        btnDevolucion = new javax.swing.JButton();
        codigoCopia = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btnAtras = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Venta y Alquiler");
        setResizable(false);

        codigoCliente.setText("ID");
        codigoCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                codigoClienteKeyReleased(evt);
            }
        });

        titulo.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titulo.setText("Venta y Alquiler");

        tablaCopias.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaCopias.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tablaCopias.setOpaque(false);
        tablaCopias.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaCopiasMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaCopias);

        buscador.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscadorKeyReleased(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("buscar :");

        titulo1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        titulo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titulo1.setText("Copias disponibles");

        nroDias.setText("7");

        jLabel4.setText("dias para devolver");

        btnAlquilar.setText("alquilar");
        btnAlquilar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlquilarActionPerformed(evt);
            }
        });

        btnComprar.setText("comprar");
        btnComprar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComprarActionPerformed(evt);
            }
        });

        labelNombre.setText("nombre cliente");

        jLabel5.setText("codigo de cliente");

        btnRegCliente.setText("registrar cliente");
        btnRegCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegClienteActionPerformed(evt);
            }
        });

        btnDevolucion.setText("recibir devolucion");
        btnDevolucion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDevolucionActionPerformed(evt);
            }
        });

        codigoCopia.setEditable(false);

        jLabel6.setText("idCopia");

        btnAtras.setText("atras");
        btnAtras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtrasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(titulo, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(126, 126, 126))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(codigoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(labelNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(96, 96, 96))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(titulo1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(92, 92, 92))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(nroDias, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnAlquilar)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnComprar)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(codigoCopia, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addContainerGap()))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buscador, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnDevolucion, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnRegCliente, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(54, 54, 54))))
            .addGroup(layout.createSequentialGroup()
                .addGap(199, 199, 199)
                .addComponent(btnAtras)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(codigoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelNombre)
                    .addComponent(jLabel5))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buscador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRegCliente)
                        .addGap(18, 18, 18)
                        .addComponent(btnDevolucion)))
                .addGap(18, 18, 18)
                .addComponent(titulo1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nroDias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(btnAlquilar)
                    .addComponent(btnComprar)
                    .addComponent(codigoCopia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(btnAtras)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tablaCopiasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaCopiasMouseClicked
        int fila = tablaCopias.getSelectedRow();
        idCopia = Integer.parseInt(fila > -1 ? tablaCopias.getValueAt(fila, 0).toString() : "0");
        codigoCopia.setText(Integer.toString(idCopia));
    }//GEN-LAST:event_tablaCopiasMouseClicked

    private void btnAlquilarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlquilarActionPerformed
        realizarTransaccion();
    }//GEN-LAST:event_btnAlquilarActionPerformed

    private void codigoClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_codigoClienteKeyReleased
        buscarNombre();
    }//GEN-LAST:event_codigoClienteKeyReleased

    private void btnComprarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComprarActionPerformed
        realizarTransaccion();
    }//GEN-LAST:event_btnComprarActionPerformed

    private void btnRegClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegClienteActionPerformed
        new RegistroCliente().setVisible(true);
    }//GEN-LAST:event_btnRegClienteActionPerformed

    private void btnDevolucionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDevolucionActionPerformed
        new Devolucion().setVisible(true);
    }//GEN-LAST:event_btnDevolucionActionPerformed

    private void buscadorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscadorKeyReleased
        llenarTablaCopias();
        idCopia = 0;
    }//GEN-LAST:event_buscadorKeyReleased

    private void btnAtrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtrasActionPerformed
        dispose();
        if (esEmpleado) {
            new LoginEmpleado().setVisible(true);
        } else {
            new LoginCliente().setVisible(true);
        }
    }//GEN-LAST:event_btnAtrasActionPerformed

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
            java.util.logging.Logger.getLogger(AlquilerCompra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AlquilerCompra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AlquilerCompra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AlquilerCompra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AlquilerCompra(true, "0", "1").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlquilar;
    private javax.swing.JButton btnAtras;
    private javax.swing.JButton btnComprar;
    private javax.swing.JButton btnDevolucion;
    private javax.swing.JButton btnRegCliente;
    private javax.swing.JTextField buscador;
    private javax.swing.JTextField codigoCliente;
    private javax.swing.JTextField codigoCopia;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelNombre;
    private javax.swing.JTextField nroDias;
    private javax.swing.JTable tablaCopias;
    private javax.swing.JLabel titulo;
    private javax.swing.JLabel titulo1;
    // End of variables declaration//GEN-END:variables
}
