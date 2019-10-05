package org.sanjose.util;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.jsoup.Jsoup;
import org.sanjose.model.ScpTipocambio;
import org.sanjose.model.ScpTipocambioPK;
import org.sanjose.repo.ScpTipocambioRep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.sanjose.util.GenUtil.EUR;
import static org.sanjose.util.GenUtil.USD;


public class TipoCambio {
    private static final Logger log = LoggerFactory.getLogger(TipoCambio.class);

    final static String EXCHANGE_RATE_URL_API = "http://www.sbs.gob.pe/app/stats/seriesH-tipo_cambio_moneda_excel.asp?fecha1={0}&fecha2={0}&moneda={1}&cierre=";

    private static Map<Character, String> monedaNombres = new HashMap<>();
    private static Map<Character, String> monedaSimbolos = new HashMap<>();

    private Character moneda;
    private BigDecimal compra;
    private BigDecimal venta;
    private Date fecha;

    public TipoCambio(Date fecha, Character moneda) throws TipoCambioNoExiste {
        this.moneda = moneda;
        this.fecha = fecha;
        monedaNombres.put(USD, "lar de N.A.");
        monedaNombres.put(EUR, "Euro");
        monedaSimbolos.put(USD, "02");
        monedaSimbolos.put(EUR, "66");
        get();
    }

    public static Map<Character, TipoCambio> actualizaTipoCambioSBS(Date fecha) throws TipoCambioNoExiste {
        Map<Character, TipoCambio> tipos = new HashMap<>();
        tipos.put(USD, new TipoCambio(fecha, USD));
        tipos.put(EUR, new TipoCambio(fecha, EUR));
        return tipos;
    }

    public static void checkTipoCambio(Date fecha, ScpTipocambioRep tipocambioRep) throws TipoCambioNoExiste {
        boolean existsUSD = ProcUtil.existeTipoDeCambio(fecha, USD, tipocambioRep);
        boolean existsEUR = ProcUtil.existeTipoDeCambio(fecha, EUR, tipocambioRep);
        if (!existsEUR || !existsUSD) {
            Map<Character, TipoCambio> rates = actualizaTipoCambioSBS(fecha);
            List<ScpTipocambio> tipocambios = tipocambioRep.findById_FecFechacambio(
                    GenUtil.getBeginningOfDay(fecha));
            if (!tipocambios.isEmpty()) {
                ScpTipocambio tipocambio = tipocambios.get(0);
                if (tipocambio.getNumTcvdolar().equals(new BigDecimal(0)))
                    tipocambio.setNumTcvdolar(rates.get(USD).getVenta());
                if (tipocambio.getNumTccdolar().equals(new BigDecimal(0)))
                    tipocambio.setNumTccdolar(rates.get(USD).getCompra());
                if (tipocambio.getNumTcveuro().equals(new BigDecimal(0)))
                    tipocambio.setNumTcveuro(rates.get(EUR).getVenta());
                if (tipocambio.getNumTcceuro().equals(new BigDecimal(0)))
                    tipocambio.setNumTcceuro(rates.get(EUR).getCompra());
                tipocambioRep.save(tipocambio);
            } else {
                ScpTipocambio tipocambio = new ScpTipocambio();
                tipocambio.prepareToSave();
                ScpTipocambioPK tipocambioId = new ScpTipocambioPK();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                tipocambioId.setTxtAnoproceso(sdf.format(fecha));
                tipocambioId.setFecFechacambio(GenUtil.getBeginningOfDay(fecha));
                tipocambio.setId(tipocambioId);
                tipocambio.setNumTcvdolar(rates.get(USD).getVenta());
                tipocambio.setNumTccdolar(rates.get(USD).getCompra());
                tipocambio.setNumTcveuro(rates.get(EUR).getVenta());
                tipocambio.setNumTcceuro(rates.get(EUR).getCompra());
                tipocambioRep.save(tipocambio);
            }
        }
    }


    public void get() throws TipoCambioNoExiste {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
            String stUrl = EXCHANGE_RATE_URL_API.replace("{0}", sdf.format(fecha));
            stUrl = stUrl.replace("{1}", monedaSimbolos.get(moneda));
            URL url = new URL(stUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line = "";
            StringBuffer sb = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            org.jsoup.nodes.Document doc = Jsoup.parse(sb.toString());
            org.jsoup.select.Elements rows = doc.select("tr");
            for (org.jsoup.nodes.Element row : rows) {
                org.jsoup.select.Elements columns = row.select("td");
                for (org.jsoup.nodes.Element column : columns) {
                    if ("FECHA".equals(column.text()))
                        continue;
                    if (sdf.format(fecha).equalsIgnoreCase(column.text()) && columns.get(1).text() != null && columns.get(1).text().contains(monedaNombres.get(moneda))) {
                        setCompra(new BigDecimal(columns.get(2).text()));
                        setVenta(new BigDecimal(columns.get(3).text()));
                        break;
                    }
                }
            }
            if (compra == null || venta == null)
                throw new TipoCambioNoExiste("Tipo de cambio no existe para esta fecha: " + sdf.format(fecha));
        } catch (IOException e) {
            throw new TipoCambioNoExiste("No se podia connectar al : " + e.getMessage());
        }
    }

    public Character getMoneda() {
        return moneda;
    }

    public void setMoneda(Character moneda) {
        this.moneda = moneda;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getCompra() {
        return compra;
    }

    public void setCompra(BigDecimal compra) {
        this.compra = compra;
    }

    public BigDecimal getVenta() {
        return venta;
    }

    public void setVenta(BigDecimal venta) {
        this.venta = venta;
    }

    @Override
    public String toString() {
        return "TipoCambio{" +
                "moneda=" + moneda +
                ", compra=" + compra +
                ", venta=" + venta +
                ", fecha=" + fecha +
                '}';
    }

    public class TipoCambioNoExiste extends Exception {
        public TipoCambioNoExiste(String message) {
            super(message);
        }
    }


    public static void main(String[] args) throws TipoCambioNoExiste, IOException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        TipoCambio tc = new TipoCambio(sdf.parse("01/10/2019"), EUR);
        System.out.println(tc);
    }

}
