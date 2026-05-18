package school.sptech.service;

import org.springframework.stereotype.Service;
import school.sptech.DTO.CartaoResponse;
import school.sptech.DTO.CartaoResquest;
import school.sptech.DTO.PagamentoResponse;
import school.sptech.gateway.GatewayPagamento;
import school.sptech.model.Pagamento;
import school.sptech.repository.IPagamentoRepository;
import school.sptech.repository.IUsuarioRepository;
import java.util.Optional;

@Service
public class PagamentoService {

    private final IPagamentoRepository repository;
    private final IUsuarioRepository usuarioRepository;
    private final GatewayPagamento gatewayPagamento;

    public PagamentoService(IPagamentoRepository repository, IUsuarioRepository usuarioRepository, GatewayPagamento gatewayPagamento) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.gatewayPagamento = gatewayPagamento;
    }

    public PagamentoResponse criarPagamentoPix(Pagamento pagamento, String email) {
        Optional<Pagamento> existente = repository.findByIdUsuarioAndIdTurmaAndStatus(
                pagamento.getIdUsuario(),
                pagamento.getIdTurma(),
                "pendente"
        );

        if (existente.isPresent()) {
            throw new IllegalStateException("Já existe um pagamento PIX pendente para este curso e usuário.");
        }

        pagamento.setStatus("pendente");

        Pagamento salvo = repository.save(pagamento);

        PagamentoResponse response = gatewayPagamento.criarPagamentoPix(salvo, email);

        return response;
    }

    public Pagamento atulizarStatus(String status, Long id) {
        return gatewayPagamento.atualizarStatus(status, id);
    }

    public String consultarStatusPagamento(Long idCurso, Long idUsuario) {
        return gatewayPagamento.consultarStatusPagamento(idCurso, idUsuario);
    }

    public CartaoResponse realizarPagamentoCartao(CartaoResquest request) {
        return gatewayPagamento.realizarPagamentoCartao(request);
    }
}
