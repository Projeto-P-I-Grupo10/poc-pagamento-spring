package school.sptech.gateway;

import school.sptech.DTO.PagamentoRequest;
import school.sptech.DTO.PagamentoResponse;

public interface GatewayPagamento {

    PagamentoResponse criarPix(PagamentoRequest request) throws Exception;

    String consultarStatus(Long idPagamentoExterno) throws Exception;
}
