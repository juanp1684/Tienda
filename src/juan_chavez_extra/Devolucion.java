package juan_chavez_extra;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;


public class Devolucion extends javax.swing.JFrame {

    int idCliente;
    int idPrestamo;

    public Devolucion() {
        initComponents();
        Dimension size = getToolkit().getScreenSize();
        setLocation((size.width - getWidth()) / 2, (size.height - getHeight()) / 2);
        llenarTablaCliente();
        idCliente = 0;
        idPrestamo = 0;
    }

    private void llenarTablaCliente() {
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "SELECT codigocliente AS id,\n"
                    + "	nombrecliente AS nombre,\n"
                    + "	ncarnet AS carnet,\n"
                    + "	telefono \n"
                    + "from cliente \n"
                    + "where nombrecliente like ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + buscador.getText() + "%");
            ResultSet rs = ps.executeQuery();
            DefaultTableModel table = (DefaultTableModel) DbUtils.resultSetToTableModel(rs);
            MisUtiles.acomodarTabla(table, tablaClientes, new int[]{35, 250, 70});
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void llenarTablaPrestamos() {
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "SELECT codigoprestamo AS id,\n"
                    + "codigocopia AS idCo,\n"
                    + " titulo,\n"
                    + " fechaprestamo AS 'prestamo',\n"
                    + " fechalimite AS limite,\n"
                    + " estado,\n"
                    + " preciocompra AS 'compra(Bs.)'\n"
                    + "FROM (video\n"
                    + "JOIN copia ON codigovideo = codigov)\n"
                    + "JOIN prestamo ON codigocopiap = codigocopia\n"
                    + "WHERE codigoclienteP = " + idCliente + " AND fechalimite IS NOT NULL "
                    + " AND fechadevolucion is null;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            DefaultTableModel table = (DefaultTableModel) DbUtils.resultSetToTableModel(rs);
            MisUtiles.acomodarTabla(table, tablaDeudas, new int[]{35, 45, 225, 74, 74, 74});
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void habilitarCopia(){
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "UPDATE copia \n"
                    + "SET disponibilidad = 1, \n"
                    + " estado = ? \n"
                    + "WHERE codigocopia = " + idCopia.getText();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, estado.getText());
            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Copia de regreso en la tienda");
            } else {
                JOptionPane.showMessageDialog(null, "Error al reponer la copia");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private String getIdPrestamo(){
        int fila = tablaDeudas.getSelectedRow();
        if(fila > -1){
            return tablaDeudas.getValueAt(fila, 0).toString();
        }
        return "0";
    }
    
    private int getMultaRetraso(){
        int fila = tablaDeudas.getSelectedRow();
        String fecha = tablaDeudas.getValueAt(fila, 4).toString();
        int anio = Integer.parseInt(fecha.substring(0, 4));
        int mes = Integer.parseInt(fecha.substring(5, 7));
        int dia = Integer.parseInt(fecha.substring(8));
        int diferencia = MisUtiles.diferenciaConHoy(anio, mes, dia);
        if(diferencia > 0){
            return diferencia * 2;
        }
        return 0;
    }
    
    private String getEstado(){
        int fila = tablaDeudas.getSelectedRow();
        if(fila > -1){
            return tablaDeudas.getValueAt(fila, 5).toString();
        }
        return "0"; 
    }
    
    private String getCosto(){
        int fila = tablaDeudas.getSelectedRow();
        if(fila > -1){
            return tablaDeudas.getValueAt(fila, 6).toString();
        }
        return "0"; 
    }
    
    private double getMultaEstado(){
        double res = 0;
        if(!estado.getText().equals(getEstado())){
            double costoCompra = Integer.parseInt(getCosto());
            res = costoCompra/100*75 ;
           
        }
        return res;
    }
    
    private void multar(){
        Connection conn = MisUtiles.getConection();
        double monto = getMultaRetraso() + getMultaEstado();
        try{
            String sql = "INSERT INTO multa (codigoprestamom, monto) values(?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(getIdPrestamo()));
            ps.setDouble(2, monto);
            int res = ps.executeUpdate();
            if(res > 0){
                JOptionPane.showMessageDialog(null, "multa total es " + monto + "Bs.");
            }else{
                JOptionPane.showMessageDialog(null, "multa no registrada, pero el monto es " + monto);
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void realizarDevolucion() {
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "UPDATE prestamo\n"
                    + "SET fechadevolucion = " + MisUtiles.getToday() + " \n"
                    + "WHERE codigoprestamo = " + getIdPrestamo();
            PreparedStatement ps = conn.prepareStatement(sql);
            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Prestamo actualizado");
                multar();
                habilitarCopia();
            } else {
                JOptionPane.showMessageDialog(null, "Error en la devolucion");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tablaClientes = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaDeudas = new javax.swing.JTable();
        buscador = new javax.swing.JTextField();
        btnDevolucion = new javax.swing.JButton();
        estado = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        idCopia = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        titulo = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnCerrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Devoluciones");
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

        tablaDeudas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "idCo", "titulo", "prestamo", "limite", "estado", "compra(Bs.)"
            }
        ));
        tablaDeudas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tablaDeudas.getTableHeader().setReorderingAllowed(false);
        tablaDeudas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaDeudasMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaDeudas);

        buscador.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscadorKeyReleased(evt);
            }
        });

        btnDevolucion.setText("completar devolucion");
        btnDevolucion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDevolucionActionPerformed(evt);
            }
        });

        jLabel1.setText("Estado de la devolucion:");

        idCopia.setEditable(false);

        jLabel2.setText("idCopia:");

        titulo.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titulo.setText("Devoluciones");

        jLabel4.setText("Buscar:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Prestamos pendientes");

        btnCerrar.setText("cerrar");
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 612, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buscador, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(193, 193, 193))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(idCopia, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(estado, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnDevolucion)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCerrar)))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(214, 214, 214)
                        .addComponent(titulo, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titulo)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(buscador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(idCopia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btnDevolucion)
                    .addComponent(estado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btnCerrar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tablaClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaClientesMouseClicked
        int fila = tablaClientes.getSelectedRow();
        idCliente = Integer.parseInt(fila > -1 ? tablaClientes.getValueAt(fila, 0).toString() : "0");
        llenarTablaPrestamos();
        estado.setText("");
    }//GEN-LAST:event_tablaClientesMouseClicked

    private void buscadorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscadorKeyReleased
        llenarTablaCliente();
        idCliente = 0;
    }//GEN-LAST:event_buscadorKeyReleased

    private void tablaDeudasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaDeudasMouseClicked
        int fila = tablaDeudas.getSelectedRow();
        boolean filaValida = fila > -1;
        idPrestamo = Integer.parseInt(filaValida ? tablaDeudas.getValueAt(fila, 0).toString() : "0");
        estado.setText(filaValida ? tablaDeudas.getValueAt(fila, 5).toString() : "");
        idCopia.setText(filaValida ? tablaDeudas.getValueAt(fila, 1).toString() : "");
    }//GEN-LAST:event_tablaDeudasMouseClicked

    private void btnDevolucionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDevolucionActionPerformed
        if(!idCopia.getText().equals("")){
            realizarDevolucion();
        }else{
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un prestamo desde \n"
                    + "la tabla antes de realizar la devolucion");
        }
    }//GEN-LAST:event_btnDevolucionActionPerformed

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
            java.util.logging.Logger.getLogger(Devolucion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Devolucion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Devolucion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Devolucion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Devolucion().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnDevolucion;
    private javax.swing.JTextField buscador;
    private javax.swing.JTextField estado;
    private javax.swing.JTextField idCopia;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tablaClientes;
    private javax.swing.JTable tablaDeudas;
    private javax.swing.JLabel titulo;
    // End of variables declaration//GEN-END:variables
}
