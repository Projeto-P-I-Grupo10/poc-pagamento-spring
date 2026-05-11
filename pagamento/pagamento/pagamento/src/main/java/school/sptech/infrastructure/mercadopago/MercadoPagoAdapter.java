package school.sptech.infrastructure.mercadopago;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Component;
import school.sptech.DTO.PagamentoRequest;
import school.sptech.DTO.PagamentoResponse;
import school.sptech.gateway.GatewayPagamento;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class MercadoPagoAdapter implements GatewayPagamento {

    @Override
    public PagamentoResponse criarPix(PagamentoRequest request) throws Exception {

        PaymentClient client = new PaymentClient();

        PaymentPayerRequest payer =
                PaymentPayerRequest.builder()
                        .email(request.getEmail())
                        .build();

        OffsetDateTime expiracao =
                OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(10);

        PaymentCreateRequest paymentRequest =
                PaymentCreateRequest.builder()
                        .transactionAmount(request.getValor())
                        .paymentMethodId("pix")
                        .payer(payer)
                        .description(request.getMetodoPagamento())
                        .dateOfExpiration(expiracao)
                        .externalReference(request.getStatus())
                        .build();

        Payment resposta = client.create(paymentRequest);

        var transactionData =
                resposta.getPointOfInteraction().getTransactionData();

        PagamentoResponse response =
                new PagamentoResponse();

        response.get(resposta.getId());
        response.setQrCode(transactionData.getQrCode());
        response.setQrCode(transactionData.getQrCodeBase64());
        response.setStatus(resposta.getStatus());

        return response;
    }

    @Override
    public String consultarStatus(Long idPagamentoExterno) throws Exception {

        PaymentClient client = new PaymentClient();

        Payment payment = client.get(idPagamentoExterno);

        return payment.getStatus();
    }
}
