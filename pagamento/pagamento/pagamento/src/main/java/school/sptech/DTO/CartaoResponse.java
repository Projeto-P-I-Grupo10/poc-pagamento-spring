package school.sptech.DTO;

import java.math.BigDecimal;

public class CartaoResponse {

     private String status;
     private String statusDetalhe;
     private BigDecimal valor;
     private Integer parcelas;
     private String bandeira;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDetalhe() {
        return statusDetalhe;
    }

    public void setStatusDetalhe(String statusDetalhe) {
        this.statusDetalhe = statusDetalhe;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getParcelas() {
        return parcelas;
    }

    public void setParcelas(Integer parcelas) {
        this.parcelas = parcelas;
    }

    public String getBandeira() {
        return bandeira;
    }

    public void setBandeira(String bandeira) {
        this.bandeira = bandeira;
    }
}
