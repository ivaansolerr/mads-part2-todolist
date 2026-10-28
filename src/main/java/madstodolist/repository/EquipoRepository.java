package madstodolist.repository;

import madstodolist.model.Equipo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EquipoRepository extends CrudRepository<Equipo, Long> {
    @Query("SELECT e FROM Equipo e ORDER BY e.nombre ASC")
    List<Equipo> findAllOrderedByName();
}