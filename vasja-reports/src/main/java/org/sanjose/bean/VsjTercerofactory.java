package org.sanjose.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VsjTercerofactory {
    public static java.util.Collection genereateCollection() {

        List<VsjOperaciontercero> terc = new ArrayList<>();
        BigDecimal sumSaldosol = new BigDecimal(34);
        BigDecimal sumSaldodolar = new BigDecimal(588);
        BigDecimal sumSaldomo = new BigDecimal(969);


        terc.add(new VsjOperaciontercero(
                null,
                "",
                new Timestamp(new Date().getTime()),
                "",
                "000000",
                "Saldo inicial SOLES",
                "",
                "",
                "",
                '0',
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                sumSaldosol,
                null,
                null,
                "",
                true
        ));

        terc.add(new VsjOperaciontercero(
                null,
                "",
                new Timestamp(new Date().getTime()),
                "",
                "000001",
                "Saldo inicial DOLARES",
                "",
                "",
                "",
                '1',
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                sumSaldodolar,
                null,
                "",
                true
        ));

        terc.add(new VsjOperaciontercero(
                null,
                "",
                new Timestamp(new Date().getTime()),
                "",
                "000002",
                "Saldo inicial EUROS",
                "",
                "",
                "",
                '0',
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                sumSaldomo,
                "",
                true
        ));


        VsjTerceroreporte terceroreporte = new VsjTerceroreporte(
                "102444",
                "AB",
                new Date().toString(),
                new Date().toString(),
                terc
                );


        //List<VsjTerceroreporte> reportes = new ArrayList<>();
        //reportes.add(terceroreporte);
        return terc;
    }

}
