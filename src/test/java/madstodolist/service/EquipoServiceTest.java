package madstodolist.service;

import madstodolist.model.Equipo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class EquipoServiceTest {

    @Autowired
    private EquipoService equipoService;

    @Test
    @Transactional
    public void crearNuevoEquipoDevuelveEquipo() {
        // WHEN
        Equipo equipo = equipoService.crearEquipo("Proyecto P1");

        // THEN
        assertThat(equipo).isNotNull();
        assertThat(equipo.getId()).isNotNull();
        assertThat(equipo.getNombre()).isEqualTo("Proyecto P1");
    }

    @Test
    @Transactional
    public void recuperarEquipoPorId() {
        // GIVEN
        Equipo equipo = equipoService.crearEquipo("Proyecto P1");

        // WHEN
        Equipo equipoRecuperado = equipoService.recuperarEquipo(equipo.getId());

        // THEN
        assertThat(equipoRecuperado).isNotNull();
        assertThat(equipoRecuperado.getId()).isEqualTo(equipo.getId());
        assertThat(equipoRecuperado.getNombre()).isEqualTo("Proyecto P1");
    }
}