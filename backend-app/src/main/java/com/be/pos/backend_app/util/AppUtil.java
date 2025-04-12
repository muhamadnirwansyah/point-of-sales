package com.be.pos.backend_app.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AppUtil {

    public static String formatRupiah(BigDecimal price){
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("id","ID"));
        return numberFormat.format(price);
    }

    public static String formatDateTime(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return localDateTime.format(formatter);
    }
}
