package school.sptech.adapter;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.sptech.DTO.CartaoResponse;
import school.sptech.DTO.CartaoResquest;
import school.sptech.DTO.PagamentoResponse;
import school.sptech.gateway.GatewayPagamento;
import school.sptech.model.Pagamento;
import school.sptech.model.Usuario;
import school.sptech.repository.IPagamentoRepository;
import school.sptech.repository.IUsuarioRepository;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class MercadoPagoAdapter implements GatewayPagamento {

    private final IPagamentoRepository repository;

    private final IUsuarioRepository usuarioRepository;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    public MercadoPagoAdapter(IPagamentoRepository repository, IUsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    @Override
    public PagamentoResponse criarPagamentoPix(Pagamento pagamento, String email) {
        try {
            PaymentClient client = new PaymentClient();

            PaymentPayerRequest payer = PaymentPayerRequest.builder()
                    .email(email)
                    .build();

            OffsetDateTime expiracao = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(10);

            PaymentCreateRequest request = PaymentCreateRequest.builder()
                    .transactionAmount(pagamento.getValor())
                    .paymentMethodId("pix")
                    .payer(payer)
                    .dateOfExpiration(expiracao)
                    .description("Pagamento PIX")
                    .externalReference(pagamento.getId().toString())
                    .build();

            Payment resposta = client.create(request);

            var transactionData = resposta.getPointOfInteraction().getTransactionData();

            PagamentoResponse pagamentoResponse = new PagamentoResponse(
                    transactionData.getQrCodeBase64(),
                    transactionData.getQrCode()
            );

            System.out.println("==== RESPOSTA DO MP ====");
            System.out.println("ID: " + resposta.getId());
            System.out.println("Status: " + resposta.getStatus());
            System.out.println("Expiração MP: " + resposta.getDateOfExpiration());

            pagamento.setIdMercadoPago(resposta.getId());
            repository.save(pagamento);

            return pagamentoResponse;

        } catch (MPApiException e) {
            System.out.println("STATUS: " + e.getStatusCode());
            System.out.println("RESPONSE: " + e.getApiResponse().getContent());
        } catch (MPException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Pagamento atualizarStatus(String status, Long id) {
        Pagamento pagamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        pagamento.setStatus(status);
        return repository.save(pagamento);
    }

    @Override
    public String consultarStatusPagamento(Long idCurso, Long idUsuario) {
        try {
            Pagamento pagamento = repository.findByIdTurmaAndIdUsuario(idCurso, idUsuario)
                    .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

            PaymentClient client = new PaymentClient();
            Payment resposta = client.get(pagamento.getIdMercadoPago());

            pagamento.setStatus(resposta.getStatus());
            repository.save(pagamento);

            return resposta.getStatus();

        } catch (MPApiException e) {
            throw new RuntimeException("Erro na API do Mercado Pago ao consultar status", e);
        } catch (MPException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CartaoResponse realizarPagamentoCartao(CartaoResquest request) {
        try {
            Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            PaymentClient client = new PaymentClient();

            PaymentPayerRequest payer = PaymentPayerRequest.builder()
                    .email(usuario.getEmail())
                    .identification(
                            IdentificationRequest.builder()
                                    .type(request.getTipoDocumento())
                                    .number(request.getNumeroDocumento())
                                    .build()
                    )
                    .build();

            PaymentCreateRequest paymentRequest = PaymentCreateRequest.builder()
                    .transactionAmount(request.getValor())
                    .token(request.getToken())
                    .installments(request.getParcelas())
                    .paymentMethodId(request.getPaymentMethodId())
                    .issuerId(request.getIssuerId())
                    .description("Pagamento curso")
                    .payer(payer)
                    .build();

            Pagamento pagamento = new Pagamento();
            pagamento.setIdTurma(request.getIdTurma());
            pagamento.setIdUsuario(request.getIdUsuario());
            pagamento.setValor(request.getValor());
            pagamento.setMetodoPagamento("Cartao");
            pagamento.setStatus("pendente");

            pagamento = repository.save(pagamento);

            Payment resposta = client.create(paymentRequest);

            pagamento.setIdMercadoPago(resposta.getId());
            pagamento.setStatus(resposta.getStatus());
            repository.save(pagamento);

            CartaoResponse response = new CartaoResponse();
            response.setValor(pagamento.getValor());
            response.setBandeira(request.getPaymentMethodId());
            response.setParcelas(request.getParcelas());
            response.setStatus(resposta.getStatus());
            response.setStatusDetalhe(resposta.getStatusDetail());

            return response;

        } catch (MPApiException e) {
            System.out.println("STATUS: " + e.getStatusCode());
            System.out.println("RESPONSE: " + e.getApiResponse().getContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
