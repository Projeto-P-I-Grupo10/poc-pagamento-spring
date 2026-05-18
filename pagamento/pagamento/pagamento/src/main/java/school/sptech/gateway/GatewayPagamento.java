package school.sptech.gateway;

import school.sptech.DTO.CartaoResponse;
import school.sptech.DTO.CartaoResquest;
import school.sptech.DTO.PagamentoResponse;
import school.sptech.model.Pagamento;

public interface GatewayPagamento {

    PagamentoResponse criarPagamentoPix(Pagamento pagamento, String email);

    Pagamento atualizarStatus(String status, Long id);

    String consultarStatusPagamento(Long idCurso, Long idUsuario);

    CartaoResponse realizarPagamentoCartao(CartaoResquest request);
}
