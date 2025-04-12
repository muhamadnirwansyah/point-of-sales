package com.be.pos.backend_app.projection;

import java.math.BigDecimal;

public interface ReportProjection {

    Long getTransactionid();
    String getCreatedat();
    BigDecimal getTotal();
    BigDecimal getItemprice();
    Integer getQuantity();
    BigDecimal getTotalprice();
    String getProductname();
    String getTransactioncode();
}
