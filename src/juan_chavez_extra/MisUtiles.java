package juan_chavez_extra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import net.proteanit.sql.DbUtils;

public class MisUtiles {

    public static final String URL = "JDBC:mysql://localhost:3306/tiendavideo?serverTimezone=UTC";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "";

    static Connection getConection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = (Connection) DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            System.out.println(e);
        }
        return con;
    }
    
    static String getToday(){
        String res;
        Date hoy = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("YYYYMMdd");
        res = formato.format(hoy);
        return res;
    }

    static String obtenerFechaDiasAntes(int n) {
        long diaMS = 1000 * 60 * 60 * 24;
        Date fechaAnterior = new Date(System.currentTimeMillis() - (n * diaMS));
        SimpleDateFormat formato = new SimpleDateFormat("YYYYMMdd");
        String res = formato.format(fechaAnterior);
        return res;
    }

    static void acomodarTabla(DefaultTableModel table, JTable objetivo, int[] medidas) {
        /*acomoda los resultados en una table ineditable*/
        Vector columnas = new Vector();
        objetivo.getTableHeader().setReorderingAllowed(false);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            columnas.addElement(table.getColumnName(i));
        }
        DefaultTableModel table2 = new DefaultTableModel(table.getDataVector(), columnas) {

            @Override
            public boolean isCellEditable(int row, int column
            ) {
                //ninguna celda es editable
                return false;
            }
        };
        int i = 0;
        objetivo.setModel(table2);
        for (int ancho : medidas) {
            TableColumn columna = objetivo.getColumnModel().getColumn(i++);
            columna.setMinWidth(ancho);
            columna.setMaxWidth(ancho);
            columna.setPreferredWidth(ancho);
        }
    }

    static int diferenciaConHoy(int anio, int mes, int dia) {
        LocalDate otroDia = LocalDate.of(anio, mes, dia);
        LocalDate hoy = LocalDate.now();
        Period diferencia = Period.between(otroDia, hoy);
        int meses = diferencia.getMonths();
        int mesesAbs = Math.abs(meses);
        int mesesFinal = 0;
        if (mesesAbs != 0) {
            mesesFinal = (conseguirMeses(mesesAbs)) * ((meses) / (mesesAbs));
        }
        return diferencia.getDays() + mesesFinal + diferencia.getYears() * 365;
    }

    private static int conseguirMeses(int n) {
        int res = 0;
        for (int i = 1; i <= n; i++) {
            res += i % 2 == 0 ? 30 : 31;
        }
        return res;
    }
}
