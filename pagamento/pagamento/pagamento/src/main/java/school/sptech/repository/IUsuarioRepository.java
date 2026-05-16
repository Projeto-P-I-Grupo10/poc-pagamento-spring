package school.sptech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.model.Usuario;

public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
}
