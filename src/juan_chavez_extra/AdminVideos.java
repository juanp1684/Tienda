package juan_chavez_extra;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

public class AdminVideos extends javax.swing.JFrame {

    int idVideo;
    int idCopia;

    public AdminVideos() {
        initComponents();
        Dimension size = getToolkit().getScreenSize();
        setLocation((size.width - getWidth()) / 2, (size.height - getHeight()) / 2);
        llenarTablaVideos();
        llenarTablaCopias();        
        btnAniadirCopias.setVisible(false);
        orden.addItem("ordenar por");
        orden.setSelectedItem("ordenar por");
        idVideo = 0;
        idCopia = 0;    
    }

    private void llenarTablaVideos() {
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "select codigov AS id, titulo, duracion, genero , "
                    + "preciorenta AS \'renta(Bs.)\', preciocompra AS \'compra(Bs.)\', "
                    + "COUNT(case disponibilidad when 1 then 1 END) AS copias \n"
                    + " from video left join copia on codigov = codigovideo \n"
                    + "group by 1 \n"
                    + getOrden();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            DefaultTableModel table = (DefaultTableModel) DbUtils.resultSetToTableModel(rs);
            MisUtiles.acomodarTabla(table, tablaVideos, new int[]{35, 200, 65, 75, 65, 80});
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private String getidVideo(){
        if(idVideo == 0){
            return "";
        }
        return "where codigov = " + idVideo;
    }

    private void llenarTablaCopias() {
        Connection conn = MisUtiles.getConection();
        try {
            String sql = "SELECT codigocopia AS id,\n"
                    + "	titulo,\n"
                    + "	estado,\n"
                    + "	if(disponibilidad = 1, 'si', 'no') AS 'en tienda?'\n"
                    + "FROM video JOIN copia ON codigov = codigovideo\n"
                    + getidVideo() + "\n"
                    + "GROUP BY 1\n"
                    + "ORDER BY id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            DefaultTableModel table = (DefaultTableModel) DbUtils.resultSetToTableModel(rs);
            MisUtiles.acomodarTabla(table, tablaCopias, new int[]{35, 200, 65});
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private String getOrden() {
        String tipoOrden = orden.getSelectedItem().toString();
        if (tipoOrden.equals("ordenar por")) {
            tipoOrden = "id";
        }
        return "ORDER BY " + tipoOrden + (jRadioButton2.isSelected() ? " DESC" : " ASC");
    }

    private void crear() {
        Connection conn = MisUtiles.getConection();
        try {
            PreparedStatement ps;

            ps = conn.prepareStatement("INSERT INTO video(TITULO, DURACION, GENERO, PRECIORENTA, PRECIOCOMPRA)VALUES(?,?,?,?,?) ");
            llenarConsulta(ps);
            int res = ps.executeUpdate();

            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Video registrado exitosamente");
                crearCopias(Integer.parseInt(nroCopias.getText()));
                limpiarCajas();
            } else {
                JOptionPane.showMessageDialog(null, "Error al registrar video");
            }
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void modificar() {
        Connection conn = MisUtiles.getConection();
        try {
            PreparedStatement ps;
            ps = conn.prepareStatement("UPDATE video\n"
                    + "SET \n"
                    + "	titulo = ?,\n"
                    + "	duracion = ?,\n"
                    + "	genero = ?,\n"
                    + "	preciorenta = ?, \n"
                    + "	preciocompra = ?\n"
                    + "WHERE codigoV = " + idVideo + ";");
            llenarConsulta(ps);
            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Video actualizado exitosamente");
                limpiarCajas();
            } else {
                JOptionPane.showMessageDialog(null, "Error al actualizar video");
            }
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void eliminarCopia(){
        Connection con = MisUtiles.getConection();
        try {
            PreparedStatement ps;
            ps = con.prepareStatement("DELETE from copia where codigocopia = " + idCopia + ";");
            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Copia eliminado exitosamente");
            } else {
                JOptionPane.showMessageDialog(null, "Error al eliminar copia");
            }
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e + "\n posiblemente necesita borrar otros"
                    + "elementos relacionados a este");
        }
    }

    private void eliminarVideo() {
        Connection conn = MisUtiles.getConection();
        try {
            PreparedStatement ps;
            ps = conn.prepareStatement("DELETE from video where CODIGOV = " + idVideo + ";");
            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Video eliminado exitosamente");
                limpiarCajas();
            } else {
                JOptionPane.showMessageDialog(null, "Error al eliminar video");
            }
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e + "\n posiblemente necesita borrar otros"
                    + "elementos relacionados a este");
        }
    }

    private void llenarConsulta(PreparedStatement ps) {
        try {
            ps.setString(1, tituloVideo.getText());
            ps.setInt(2, Integer.parseInt(duracion.getText()));
            ps.setString(3, genero.getText());
            ps.setInt(4, Integer.parseInt(renta.getText()));
            ps.setInt(5, Integer.parseInt(compra.getText()));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void crearCopias(int cantidad) {
        int copias = 0;
        String id = Integer.toString(idVideo);
        if (idVideo == 0) {
            id = "(SELECT MAX(codigoV) FROM video)";
        }
        try {
            for (int i = 0; i < cantidad; i++) {
                Connection conn = MisUtiles.getConection();
                PreparedStatement ps;

                ps = conn.prepareStatement("INSERT INTO copia (CODIGOVIDEO) VALUES( " + id + "); ");
                copias += ps.executeUpdate();
                conn.close();
            }
            JOptionPane.showMessageDialog(null, "Se han creado " + copias + " copias exitosamente");
            llenarTablaCopias();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar copias");
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void refrescar() {
        btnAniadirCopias.setVisible(false);
        idVideo = 0;
        idCopia = 0;
        llenarTablaVideos();
        llenarTablaCopias();
    }

    private void limpiarCajas() {
        tituloVideo.setText("");
        genero.setText("");
        duracion.setText("");
        renta.setText("");
        compra.setText("");
        nroCopias.setText("");
        btnAniadirCopias.setVisible(false);
        idVideo = 0;
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
        tablaVideos = new javax.swing.JTable();
        tituloVideo = new javax.swing.JTextField();
        genero = new javax.swing.JTextField();
        duracion = new javax.swing.JTextField();
        renta = new javax.swing.JTextField();
        compra = new javax.swing.JTextField();
        btnCrear = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        nroCopias = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btnAniadirCopias = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnRefrescar = new javax.swing.JButton();
        orden = new javax.swing.JComboBox<>();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        tituloVentana = new javax.swing.JLabel();
        titulo2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaCopias = new javax.swing.JTable();
        btnEliminarCopia = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Administrar Videos");
        setResizable(false);

        tablaVideos.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaVideos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tablaVideos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaVideosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaVideos);

        btnCrear.setText("Crear");
        btnCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearActionPerformed(evt);
            }
        });

        btnModificar.setText("Modificar");
        btnModificar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnModificar.setFocusable(false);
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        jLabel1.setText("Titulo");

        jLabel2.setText("Duracion(min)");

        jLabel3.setText("Genero");

        jLabel4.setText("costo renta");

        jLabel5.setText("costo compra");

        nroCopias.setText("0");

        jLabel6.setText("Nro. Copias");

        btnAniadirCopias.setText("solo añadir copias");
        btnAniadirCopias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAniadirCopiasActionPerformed(evt);
            }
        });

        btnLimpiar.setText("limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        btnRefrescar.setText("refrescar");
        btnRefrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefrescarActionPerformed(evt);
            }
        });

        orden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "id", "titulo", "duracion", "genero", "copias" }));
        orden.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ordenFocusGained(evt);
            }
        });

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Ascendente");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Descendente");

        tituloVentana.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        tituloVentana.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tituloVentana.setText("Videos");

        titulo2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        titulo2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titulo2.setText("Copias");

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
        tablaCopias.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaCopiasMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaCopias);

        btnEliminarCopia.setText("eliminar seleccion");
        btnEliminarCopia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarCopiaActionPerformed(evt);
            }
        });

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
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(btnRefrescar)
                        .addGap(18, 18, 18)
                        .addComponent(orden, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jRadioButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton2))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(titulo2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEliminarCopia)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCerrar)
                        .addContainerGap())))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(nroCopias, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnAniadirCopias)
                                .addGap(18, 18, 18)
                                .addComponent(btnLimpiar))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tituloVideo, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(genero)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(duracion, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel5))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(renta, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(compra, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(72, 72, 72)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnModificar)
                                    .addComponent(btnCrear, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnEliminar, javax.swing.GroupLayout.Alignment.LEADING)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(231, 231, 231)
                        .addComponent(tituloVentana, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 603, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tituloVentana)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tituloVideo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCrear)
                    .addComponent(jLabel1))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(duracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(renta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnModificar)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(compra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nroCopias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(btnAniadirCopias)
                    .addComponent(btnLimpiar))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRefrescar)
                    .addComponent(orden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(titulo2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(btnEliminarCopia))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnCerrar)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tablaVideosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaVideosMouseClicked
        int fila = tablaVideos.getSelectedRow();
        idVideo = Integer.parseInt(fila > -1 ? tablaVideos.getValueAt(fila, 0).toString() : "0");
        llenarTablaCopias();

        tituloVideo.setText(tablaVideos.getValueAt(fila, 1).toString());
        duracion.setText(tablaVideos.getValueAt(fila, 2).toString());
        genero.setText(tablaVideos.getValueAt(fila, 3).toString());
        renta.setText(tablaVideos.getValueAt(fila, 4).toString());
        compra.setText(tablaVideos.getValueAt(fila, 5).toString());
        btnAniadirCopias.setVisible(true);
    }//GEN-LAST:event_tablaVideosMouseClicked

    private void btnCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearActionPerformed
        crear();
        llenarTablaVideos();
    }//GEN-LAST:event_btnCrearActionPerformed

    private void btnAniadirCopiasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAniadirCopiasActionPerformed
        crearCopias(Integer.parseInt(nroCopias.getText()));
    }//GEN-LAST:event_btnAniadirCopiasActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        if (idVideo != 0) {
            modificar();
            llenarTablaVideos();
        } else {
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un video desde \n"
                    + "la tabla para que este sea modificado");
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarCajas();
        llenarTablaVideos();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        if (idVideo != 0) {
            if (JOptionPane.showConfirmDialog(null, "esto eliminara todos los elementos relacionados a este video") == JOptionPane.YES_OPTION) {
                eliminarVideo();
                llenarTablaVideos();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un video desde \n"
                    + "la tabla para que este sea eliminado");
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarActionPerformed
        refrescar();
    }//GEN-LAST:event_btnRefrescarActionPerformed

    private void ordenFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ordenFocusGained
        orden.removeItem("ordenar por");
    }//GEN-LAST:event_ordenFocusGained

    private void tablaCopiasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaCopiasMouseClicked
        int fila = tablaCopias.getSelectedRow();
        idCopia = Integer.parseInt(fila > -1 ? tablaCopias.getValueAt(fila, 0).toString() : "0");
    }//GEN-LAST:event_tablaCopiasMouseClicked

    private void btnEliminarCopiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarCopiaActionPerformed
        if (idCopia != 0) {
            if (JOptionPane.showConfirmDialog(null, "esto eliminara todos los elementos relacionados a esta copia") == JOptionPane.YES_OPTION) {
                eliminarCopia();
                llenarTablaCopias();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un video desde \n"
                    + "la tabla para que este sea eliminado");
        }
    }//GEN-LAST:event_btnEliminarCopiaActionPerformed

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
            java.util.logging.Logger.getLogger(AdminVideos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminVideos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminVideos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminVideos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminVideos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAniadirCopias;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnCrear;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEliminarCopia;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField compra;
    private javax.swing.JTextField duracion;
    private javax.swing.JTextField genero;
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
    private javax.swing.JTextField nroCopias;
    private javax.swing.JComboBox<String> orden;
    private javax.swing.JTextField renta;
    private javax.swing.JTable tablaCopias;
    private javax.swing.JTable tablaVideos;
    private javax.swing.JLabel titulo2;
    private javax.swing.JLabel tituloVentana;
    private javax.swing.JTextField tituloVideo;
    // End of variables declaration//GEN-END:variables
}
