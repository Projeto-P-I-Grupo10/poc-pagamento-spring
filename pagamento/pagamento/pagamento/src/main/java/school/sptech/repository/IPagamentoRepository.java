package school.sptech.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.model.Pagamento;

import java.util.Optional;

public interface IPagamentoRepository extends JpaRepository<Pagamento,Long> {
    Optional<Pagamento> findByIdUsuarioAndIdCursoAndStatus(Long idUsuario, Long idCurso, String status);
    Optional<Pagamento> findByIdCursoAndIdUsuario(Long idCurso, Long idUsuario);
 
}
