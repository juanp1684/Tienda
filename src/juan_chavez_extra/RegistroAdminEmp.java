package juan_chavez_extra;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

public class RegistroAdminEmp extends javax.swing.JFrame {

    boolean registroEmp;
    String idAdmin;

    /**
     * @param tipoRegistro true = empleado false = admin
     */
    public RegistroAdminEmp(boolean tipoRegistro, String idAdmin) {
        initComponents();
        this.idAdmin = idAdmin;
        Dimension size = getToolkit().getScreenSize();
        setLocation((size.width - getWidth()) / 2, (size.height - getHeight()) / 2);
        registroEmp = tipoRegistro;
        setLabel();
        llenarTabla();
    }

    private void llenarTabla() {
        if (registroEmp) {
            llenarTablaEmp();
        } else {
            llenarTablaAdmin();
        }
    }

    private void llenarTablaEmp() {
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "SELECT codigoemp AS id, nombreadmin AS empleador, nombreemp AS nombre, passwordemp AS contraseña\n"
                    + "FROM administrador JOIN empleado ON codigoadmine = codigoadmin;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            DefaultTableModel table = (DefaultTableModel) DbUtils.resultSetToTableModel(rs);
            MisUtiles.acomodarTabla(table, tablaRegistro, new int[]{35});
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void llenarTablaAdmin() {
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "SELECT codigoadmin AS id, nombreadmin AS nombre, passwordadmin AS contraseña\n"
                    + " FROM administrador;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            DefaultTableModel table = (DefaultTableModel) DbUtils.resultSetToTableModel(rs);
            MisUtiles.acomodarTabla(table, tablaRegistro, new int[]{35});
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void setLabel() {
        if (registroEmp) {
            titulo.setText("Registrar Empleado");
        } else {
            titulo.setText("Registrar Administrador");
        }
    }

    private String getColumnas() {
        if (registroEmp) {
            return "empleado \n" + "(NOMBREEMP, PASSWORDEMP, codigoadmine) ";
        }
        return "administrador \n" + "(NOMBREADMIN, PASSWORDADMIN) ";
    }

    private String getConsulta() {
        if (registroEmp) {
            return "SELECT MAX(codigoemp) FROM empleado";
        }
        return "SELECT MAX(codigoadmin) FROM administrador";
    }

    private void limpiarCajas() {
        nombre.setText("");
        password.setText("");
        codigo.setText("");
    }

    private String getCredenciales() {
        Connection conn = MisUtiles.getConection();
        try {
            String sql = getConsulta();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            int codigo = 0;
            if (rs.next()) {
                codigo = rs.getInt(1);
            }
            return "\ncodigo: " + codigo
                    + "\n contraseña: " + password.getText();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return "";
    }

    private String getCodigoAdmin() {
        if (registroEmp) {
            return ", " + idAdmin;
        }
        return "";
    }

    private void registrar() {
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "INSERT INTO "
                    + getColumnas()
                    + " VALUES(?, ?" + getCodigoAdmin() + ")";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombre.getText());
            ps.setString(2, password.getText());
            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Registro exitoso " + getCredenciales());
                limpiarCajas();
                llenarTabla();
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo registrar");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void eliminar() {
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "DELETE FROM "
                    + titulo.getText().substring(10)
                    + " WHERE " + getTipoCodigo() +" = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, codigo.getText());
            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Se elimino de forma exitosa");
                llenarTabla();
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo eliminar");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private String getTipoCodigo(){
        if(registroEmp){
            return "codigoemp";
        }
        return "codigoadmin";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nombre = new javax.swing.JTextField();
        password = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        titulo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaRegistro = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        codigo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        btnEliminar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registrar Personal");
        setResizable(false);

        jButton1.setText("Registrar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        titulo.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titulo.setText("Registro ??");

        tablaRegistro.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaRegistro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaRegistroMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaRegistro);

        jLabel1.setText("Nombre:");

        jLabel3.setText("Contraseña:");

        codigo.setEditable(false);

        jLabel4.setText("ID");

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(105, 105, 105)
                        .addComponent(titulo, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nombre)
                            .addComponent(password, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(codigo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCerrar)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titulo)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(36, 36, 36)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(codigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(btnEliminar)
                    .addComponent(btnCerrar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        registrar();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tablaRegistroMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaRegistroMouseClicked
        int fila = tablaRegistro.getSelectedRow();
        String id = fila > -1 ? tablaRegistro.getValueAt(fila, 0).toString() : "";
        codigo.setText(id);
    }//GEN-LAST:event_tablaRegistroMouseClicked

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        if (!codigo.getText().equals("")) {
            if (JOptionPane.showConfirmDialog(null, "esto eliminara todos los elementos relacionados a este trabajador") == JOptionPane.YES_OPTION) {
                eliminar();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un trabajador desde \n"
                    + "la tabla para que este sea eliminado");
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

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
            java.util.logging.Logger.getLogger(RegistroAdminEmp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroAdminEmp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroAdminEmp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroAdminEmp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistroAdminEmp(true, "1").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JTextField codigo;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField nombre;
    private javax.swing.JTextField password;
    private javax.swing.JTable tablaRegistro;
    private javax.swing.JLabel titulo;
    // End of variables declaration//GEN-END:variables
}
