package school.sptech.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.model.Pagamento;

import java.util.Optional;

public interface IPagamentoRepository extends JpaRepository<Pagamento,Long> {
    Optional<Pagamento> findByIdUsuarioAndIdTurmaAndStatus(Long idUsuario, Long idTurma, String status);
    Optional<Pagamento> findByIdTurmaAndIdUsuario(Long idTurma, Long idUsuario);
}
