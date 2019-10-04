package org.sanjose.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.sanjose.model.ScpTipocambio;
import org.sanjose.model.ScpTipocambioPK;
import org.sanjose.repo.ScpTipocambioRep;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;


public class TipoCambioUtil {
    private static final Logger log = LoggerFactory.getLogger(TipoCambioUtil.class);

    final static String EXCHANGE_RATE_URL_API =  "https://api.exchangerate-api.com/v4/latest/PEN";

    final static String EXCHANGE_RATE_URL =  "http://www.nubedeideas.pe/peru/index.php/api/tipo-cambio";

    public static void checkTipoCambio(Date fecha, ScpTipocambioRep tipocambioRep) {
        try {
            if (fecha.before(GenUtil.getBeginningOfWorkingDay(new Date()))) {
                log.info("Todavia no hay nuevos tipos de cambio para hoy dia");
                return;
            }
            boolean existsUSD = ProcUtil.existeTipoDeCambio(fecha, GenUtil.USD, tipocambioRep);
            boolean existsEUR = ProcUtil.existeTipoDeCambio(fecha, GenUtil.EUR, tipocambioRep);
            if (!existsEUR || !existsUSD) {
               Map<Character, TipoCambio> rates = actualizaTipoCambioNubes();
               List<ScpTipocambio> tipocambios = tipocambioRep.findById_FecFechacambio(
                       GenUtil.getBeginningOfDay(fecha));
               if (!tipocambios.isEmpty()) {
                   ScpTipocambio tipocambio = tipocambios.get(0);
                   if (tipocambio.getNumTcvdolar().equals(new BigDecimal(0)))
                       tipocambio.setNumTcvdolar(rates.get(GenUtil.USD).getVenta());
                   if (tipocambio.getNumTccdolar().equals(new BigDecimal(0)))
                       tipocambio.setNumTccdolar(rates.get(GenUtil.USD).getCompra());
                   if (tipocambio.getNumTcveuro().equals(new BigDecimal(0)))
                       tipocambio.setNumTcveuro(rates.get(GenUtil.EUR).getVenta());
                   if (tipocambio.getNumTcceuro().equals(new BigDecimal(0)))
                       tipocambio.setNumTcceuro(rates.get(GenUtil.EUR).getCompra());
                   tipocambioRep.save(tipocambio);
               } else {
                   ScpTipocambio tipocambio = new ScpTipocambio();
                   tipocambio.prepareToSave();
                   ScpTipocambioPK tipocambioId = new ScpTipocambioPK();
                   SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                   tipocambioId.setTxtAnoproceso(sdf.format(fecha));
                   tipocambioId.setFecFechacambio(GenUtil.getBeginningOfDay(fecha));
                   tipocambio.setId(tipocambioId);
                   tipocambio.setNumTcvdolar(rates.get(GenUtil.USD).getVenta());
                   tipocambio.setNumTccdolar(rates.get(GenUtil.USD).getCompra());
                   tipocambio.setNumTcveuro(rates.get(GenUtil.EUR).getVenta());
                   tipocambio.setNumTcceuro(rates.get(GenUtil.EUR).getCompra());
                   tipocambioRep.save(tipocambio);
               }
            }
            log.info("Tipo cambio esta actual");
        } catch (NumberFormatException | IOException e) {
            log.info("Problema al conseguir tipo de cambio: " + e.getMessage());
        }
    }

    public static Map<Character, BigDecimal> actualizaTipoCambioExchangeOpen(Date curDate) {
        Map<Character, BigDecimal> rates = new HashMap<>();
        try {
            HttpURLConnection request = (HttpURLConnection) new URL(EXCHANGE_RATE_URL_API).openConnection();
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject jsonobj = root.getAsJsonObject();

            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
            String fecha = jsonobj.get("date").getAsString();
            // If the rate is for a different date than the requested then ignore
            if (!(sdf.format(curDate).equals(fecha)))
                return rates;
            JsonElement reqResult = jsonobj.get("rates");
            rates.put(GenUtil.USD, extractTipoCambio(reqResult, "USD"));
            rates.put(GenUtil.EUR, extractTipoCambio(reqResult, "EUR"));
        } catch (NumberFormatException | IOException e) {
            log.info("Problema al conseguir tipo de cambio");
        }
        return rates;
    }

    private static BigDecimal extractTipoCambio(JsonElement ratesJson, String moneda) {
        String rates = ratesJson.toString();
        rates = rates.substring(rates.indexOf(moneda));
        rates = rates.substring(rates.indexOf(":")+1, rates.indexOf(","));

        BigDecimal b = new BigDecimal(1d);
        return b.divide(new BigDecimal(rates), 3, BigDecimal.ROUND_HALF_EVEN);
    }

    public static Map<Character, TipoCambio> actualizaTipoCambioNubes() throws NumberFormatException, IOException {
        Map<Character, TipoCambio> rates = new HashMap<>();
        HttpURLConnection request = (HttpURLConnection) new URL(EXCHANGE_RATE_URL).openConnection();
        request.connect();

        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject jsonobj = root.getAsJsonObject();
        JsonElement reqResult = jsonobj.get("datos");
        Gson gson = new Gson();
        for (JsonElement el : reqResult.getAsJsonArray()) {
            TipoCambio tc = gson.fromJson(el, TipoCambio.class);
            if (tc.getMoneda().equals("DÓLAR DE N.A."))
                rates.put(GenUtil.USD, tc);
            if (tc.getMoneda().equals("EURO"))
                rates.put(GenUtil.EUR, tc);
        }
        return rates;
    }

    private class TipoCambio {

        String moneda;
        BigDecimal compra;
        BigDecimal venta;

        public String getMoneda() {
            return moneda;
        }

        public void setMoneda(String moneda) {
            this.moneda = moneda;
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
    }
}
