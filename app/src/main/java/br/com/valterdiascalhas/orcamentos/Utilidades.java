package br.com.valterdiascalhas.orcamentos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.StringTokenizer;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

public class Utilidades extends AppCompatActivity {

    public static String DividaPorCem(Integer cento) {
        DecimalFormatSymbols u = new DecimalFormatSymbols();
        u.setDecimalSeparator(',');
        u.setGroupingSeparator('.');
        String s = "#,##0.00";
        final DecimalFormat w = new DecimalFormat(s, u);
        return (w.format((float) cento / 100));
    }
    public static String DividaPorCemLong(Long cento) {
        DecimalFormatSymbols u = new DecimalFormatSymbols();
        u.setDecimalSeparator(',');
        u.setGroupingSeparator('.');
        String s = "#,##0.00";
        final DecimalFormat w = new DecimalFormat(s, u);
        return (w.format((float) cento / 100));
    }

    public static String DividaPorCemString(String cento) {
        int centoInt;
        try {
            centoInt =  Integer.parseInt(cento);
            DecimalFormatSymbols u = new DecimalFormatSymbols();
            u.setDecimalSeparator(',');
            u.setGroupingSeparator('.');
            String s = "#,##0.00";
            DecimalFormat w = new DecimalFormat(s, u);
            float totalfloat = (float) centoInt / 100;
            return w.format(totalfloat);
        } catch (Exception e) {
            return "0";
        }
    }


    public static String parteInteira(String s) {
        String r = s.substring(0, s.indexOf(","));
        if (r.equals("")) return "0";
        else return r;
    }

    public static String parteDecimal(String s) {
        String r = s.substring(s.indexOf(",") + 1, s.length());
        if (r.equals("")) return "0";
        else return r;
    }

    /**
     * From Dialog Frament with a ',' to a valid DecimalNumber AsString, XML android:digits="0123456789,"
     **/
    public static String decNumStr(String s1, Context context) {
        String resultado;
        String s = s1.replace(".", "");
        int virgula = countOccurrence(s, ',');


        if (virgula < 2) {
            if (virgula == 1) {
                /** determinar os digitos a direita de virgula **/
                if (s.length() - s.indexOf(',') < 4) {
                    resultado = parteInteira(s) + "." + parteDecimal(s);//"0.0"
                    return resultado;
                } else {
                    L.t(context, "Não use mais de dois digitos apos a virgula");
                    return "Error";
                }
            } else {
                //não tem virgula, mas é um numero inteiro válido?
                if (isDouble(s)) {
                    resultado = s;
                    return resultado;
                } else {
                    L.t(context, "Não é um Número válido " + s);
                    return "Error";
                }
            }
        } else {
            L.t(context, "Não use mais de uma virgula");
            return "Error";
        }
    }

    public static int countOccurrence(String s, char c) {
        int counter = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                counter++;
            }
        }
        return counter;
    }

    public static boolean isDouble(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isInteger(String str) {
        try {
            int d = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String calculaTempoBatismo(String t) {
        //transformar string em data
        //calcular a diferenca e retorna anos
        Date first;
        Date last = Calendar.getInstance().getTime();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            first = sdf.parse(t);
            Calendar startCalendar = new GregorianCalendar();
            startCalendar.setTime(first);
            Calendar endCalendar = new GregorianCalendar();
            endCalendar.setTime(last);
            int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
            int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
            //fim alternativa

            if (diffYear < 3) {
                return diffMonth + " meses";
            } else {
                return diffYear + " anos";
            }
        } catch (ParseException ex) {
            return "Nao foi possivel calcular";
        }
    }

    public static String calculaTempoAnos(String t) {
        Date first;
        Date last = Calendar.getInstance().getTime();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            first = sdf.parse(t);
            return String.valueOf(getDiffYears(first, last));
        } catch (ParseException ex) {
            return "Nao foi possivel calcular";
        }
    }

    public static String agora() {
        String timestamp = DateFormat.getDateTimeInstance().format(new Date());
        return timestamp;
    }

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * Returns a psuedo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimim value
     * @param max Maximim value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static boolean isOnline(ConnectivityManager connMgr) {
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static String comparaData(String local, String baixada) {
        //transformar string em data
        //calcular a diferenca e retorna anos
        Date dataLocal;
        Date dataBaixada;
        Date agora = Calendar.getInstance().getTime();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            dataLocal = sdf.parse(local);
            dataBaixada = sdf.parse(baixada);

            Calendar localCalendar = new GregorianCalendar();
            localCalendar.setTime(dataLocal);

            Calendar baixadaCalendar = new GregorianCalendar();
            baixadaCalendar.setTime(dataBaixada);

            if (baixadaCalendar.get(Calendar.YEAR) - localCalendar.get(Calendar.YEAR) == 0) {
                if (baixadaCalendar.get(Calendar.MONTH) - localCalendar.get(Calendar.MONTH) == 0) {
                    if (baixadaCalendar.get(Calendar.DAY_OF_MONTH) - localCalendar.get(Calendar.DAY_OF_MONTH) == 0) {
                        return "mesma data";
                    } else {
                        return "Dias Diferentes: " + (baixadaCalendar.get(Calendar.DAY_OF_MONTH) - localCalendar.get(Calendar.DAY_OF_MONTH));
                    }
                } else {
                    return "Meses Diferentes: " + (baixadaCalendar.get(Calendar.MONTH) - localCalendar.get(Calendar.MONTH));
                }
            } else {
                return "Anos Diferentes: " + (baixadaCalendar.get(Calendar.YEAR) - localCalendar.get(Calendar.YEAR));
            }

        } catch (ParseException ex) {
            return "Nao foi possivel calcular";
        }
    }

    public static boolean existeTabela(String tabela, Context context) {
        DBAdapter dbAdapter = new DBAdapter(context);
        SQLiteDatabase sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tabela + "' ", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public static boolean temDadosNoBanco(Context context) {
        DBAdapter dbAdapter;
        dbAdapter = new DBAdapter(context);
        /**
         if (dbAdapter.findFirstRelatorio().contentEquals("No Records")) {
         //L.m("No Records in RELATORIOS");
         return false;
         } else if (dbAdapter.findFirstPublicador().contentEquals("No Records")) {
         //L.m("No Records in PUBLICADORES");
         return false;
         } else {
         return true;
         }

         **/
        return true;
    }

    public static boolean findLocalFiles(String name) {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, name);
        if (file.exists()) return true;
        else return false;
    }

    public static int[] diaMesAno(String data) {
        int[] result = {1, 1, 2016};
        String dia;
        String mes;
        String ano;
        StringTokenizer token = new StringTokenizer(data, "/");

        dia = token.nextToken();
        mes = token.nextToken();
        ano = token.nextToken();

        result[0] = Integer.parseInt(dia);
        result[1] = Integer.parseInt(mes);
        result[2] = Integer.parseInt(ano);
        return result;
    }


    public static boolean validarData(String data) {
        String dia;
        String mes;
        String ano;
        StringTokenizer token = new StringTokenizer(data, "/");
        try {
            dia = token.nextToken();
            mes = token.nextToken();
            ano = token.nextToken();

            if (dia.length() < 1 || dia.length() > 2)
                return false;
            if (mes.length() < 1 || mes.length() > 2)
                return false;
            if (ano.length() != 4)
                return false;


            int intDia = Integer.parseInt(dia);
            int intMes = Integer.parseInt(mes);
            int intAno = Integer.parseInt(ano);
            if (intMes < 1 || intMes > 12)
                return false;

            if (intMes == 1 || intMes == 3 || intMes == 5 || intMes == 7 || intMes == 8 || intMes == 10 || intMes == 12) {
                if (intDia < 1 || intDia > 31) return false;

            } else if (intMes == 4 || intMes == 6 || intMes == 9 || intMes == 11) {
                if (intDia < 1 || intDia > 30) return false;

            } else if (intMes == 2) {
                if (new GregorianCalendar().isLeapYear(intAno)) {
                    if (intDia < 1 || intDia > 29) return false;

                } else {
                    if (intDia < 1 || intDia > 28) return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
