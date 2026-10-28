package madstodolist.service;

import madstodolist.model.Equipo;
import madstodolist.model.Usuario;
import madstodolist.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class EquipoServiceTest {

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

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


    @Test
    @Transactional
    public void recuperarTodosLosEquiposOrdenadosAlfabeticamente() {
        // GIVEN
        equipoService.crearEquipo("Beta");
        equipoService.crearEquipo("Alfa");
        equipoService.crearEquipo("Gamma");

        // WHEN
        List<Equipo> equipos = equipoService.findAllOrderedByName();

        // THEN
        assertThat(equipos).hasSize(3);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Alfa");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Beta");
        assertThat(equipos.get(2).getNombre()).isEqualTo("Gamma");
    }

    @Test
    @Transactional
    public void anadirUsuarioAEquipo() {
        // GIVEN
        Equipo equipo = equipoService.crearEquipo("Proyecto P1");
        madstodolist.model.Usuario usuario = new madstodolist.model.Usuario("ana.lopez@gmail.com");
        usuarioRepository.save(usuario);

        // WHEN
        equipoService.anadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        // THEN
        List<madstodolist.model.Usuario> usuarios = equipoService.usuariosEquipo(equipo.getId());
        assertThat(usuarios).hasSize(1);
        assertThat(usuarios.get(0).getEmail()).isEqualTo("ana.lopez@gmail.com");
    }

    @Test
    @Transactional
    public void equiposDeUnUsuario() {
        // GIVEN
        Usuario usuario = new Usuario("ana.lopez@gmail.com");
        usuarioRepository.save(usuario);

        Equipo equipo1 = equipoService.crearEquipo("Proyecto P1");
        Equipo equipo2 = equipoService.crearEquipo("Proyecto P2");

        equipoService.anadirUsuarioAEquipo(equipo1.getId(), usuario.getId());
        equipoService.anadirUsuarioAEquipo(equipo2.getId(), usuario.getId());

        // WHEN
        List<Equipo> equipos = equipoService.equiposUsuario(usuario.getId());

        // THEN
        assertThat(equipos).hasSize(2);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Proyecto P1");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Proyecto P2");
    }

    @Test
    @Transactional
    public void eliminarUsuarioDeEquipo() {
        // GIVEN
        Equipo equipo = equipoService.crearEquipo("Proyecto P1");
        Usuario usuario = new Usuario("ana.lopez@gmail.com");
        usuarioRepository.save(usuario);
        equipoService.anadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        // WHEN
        equipoService.eliminarUsuarioDeEquipo(equipo.getId(), usuario.getId());

        // THEN
        List<Usuario> usuarios = equipoService.usuariosEquipo(equipo.getId());
        assertThat(usuarios).isEmpty();

        List<Equipo> equipos = equipoService.equiposUsuario(usuario.getId());
        assertThat(equipos).isEmpty();
    }

    @Test
    @Transactional
    public void modificarEquipoCambiaElNombre() {
        // GIVEN
        Equipo equipo = equipoService.crearEquipo("Equipo Original");

        // WHEN
        Equipo equipoModificado = equipoService.modificarEquipo(equipo.getId(), "Equipo Renombrado");

        // THEN
        assertThat(equipoModificado.getNombre()).isEqualTo("Equipo Renombrado");

        Equipo equipoRecuperado = equipoService.recuperarEquipo(equipo.getId());
        assertThat(equipoRecuperado.getNombre()).isEqualTo("Equipo Renombrado");
    }

    @Test
    @Transactional
    public void eliminarEquipoLoBorraDeLaBaseDeDatos() {
        // GIVEN
        Equipo equipo = equipoService.crearEquipo("Equipo A Borrar");

        // WHEN
        equipoService.eliminarEquipo(equipo.getId());

        // THEN
        Equipo equipoRecuperado = equipoService.recuperarEquipo(equipo.getId());
        assertThat(equipoRecuperado).isNull();
    }
}

