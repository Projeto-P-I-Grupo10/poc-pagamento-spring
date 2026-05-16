package school.sptech.controller;

import com.mercadopago.resources.payment.Payment;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.sptech.DTO.CartaoResponse;
import school.sptech.DTO.CartaoResquest;
import school.sptech.DTO.PagamentoRequest;
import school.sptech.DTO.PagamentoResponse;
import school.sptech.model.Pagamento;
import school.sptech.service.PagamentoService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/pagamentos")
@CrossOrigin(origins = "*")
public class PagamentoController {

    private final PagamentoService service;
   // private final PagamentoCartaoService serviceCartao;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PostMapping("/pix")
    public ResponseEntity<PagamentoResponse> gerarPix(@RequestBody PagamentoRequest body) throws Exception {
            Pagamento pagamento = new Pagamento();
            pagamento.setIdTurma(body.getIdTurma());
            pagamento.setIdUsuario(body.getIdUsuario());
            pagamento.setMetodoPagamento(body.getMetodoPagamento());
            pagamento.setValor(body.getValor());
            pagamento.setDataPagamento(LocalDateTime.now());
            return ResponseEntity.status(200).body(service.criarPagamentoPix(pagamento, body.getEmail()));
    }

    @GetMapping("/status/{idCurso}/{idUsuario}")
    public ResponseEntity<Map<String, Object>> consultarStatus(
            @PathVariable Long idCurso,
            @PathVariable Long idUsuario) throws Exception {

        String status = service.consultarStatusPagamento(idCurso, idUsuario);

        return ResponseEntity.ok(Map.of("status", status));
    }



    @PostMapping("/cartao")
    public ResponseEntity<CartaoResponse> realizarPagamento(
            @RequestBody @Valid CartaoResquest dto) throws Exception {

        return ResponseEntity.status(200).body(service.realizarPagamentoCartao(dto));

    }


}