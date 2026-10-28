package madstodolist.service;

import madstodolist.controller.exception.EquipoServiceException;
import madstodolist.model.Equipo;
import madstodolist.repository.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import madstodolist.model.Usuario;
import madstodolist.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@Service
public class EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Equipo crearEquipo(String nombre) {
        Equipo equipo = new Equipo(nombre);
        equipoRepository.save(equipo);
        return equipo;
    }

    @Transactional(readOnly = true)
    public Equipo recuperarEquipo(Long id) {
        return equipoRepository.findById(id).orElse(null);
    }

    // Dentro de EquipoService:
    @Transactional(readOnly = true)
    public List<Equipo> findAllOrderedByName() {
        return equipoRepository.findAllOrderedByName();
    }

    @Transactional
    public void anadirUsuarioAEquipo(Long idEquipo, Long idUsuario) {
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);

        if (equipo != null && usuario != null) {
            equipo.addUsuario(usuario);
        }
    }

    @Transactional(readOnly = true)
    public List<Usuario> usuariosEquipo(Long idEquipo) {
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        if (equipo == null) {
            return Collections.emptyList();
        }
        List<Usuario> usuarios = new ArrayList<>(equipo.getUsuarios());
        usuarios.sort(Comparator.comparing(Usuario::getNombre, Comparator.nullsLast(String::compareTo)));
        return usuarios;
    }

    @Transactional(readOnly = true)
    public List<Equipo> equiposUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            return Collections.emptyList();
        }
        List<Equipo> equipos = new ArrayList<>(usuario.getEquipos());
        equipos.sort(Comparator.comparing(Equipo::getNombre, Comparator.nullsLast(String::compareTo)));
        return equipos;
    }

    @Transactional
    public void eliminarUsuarioDeEquipo(Long idEquipo, Long idUsuario) {
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        if (equipo == null) {
            throw new EquipoServiceException("Equipo no encontrado");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new EquipoServiceException("Usuario no encontrado");
        }

        // Eliminamos la relación en ambos lados
        equipo.getUsuarios().remove(usuario);
        usuario.getEquipos().remove(equipo);
    }

    @Transactional
    public Equipo modificarEquipo(Long idEquipo, String nuevoNombre) {
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        if (equipo == null) {
            throw new EquipoServiceException("Equipo no encontrado");
        }
        equipo.setNombre(nuevoNombre);
        return equipoRepository.save(equipo);
    }
}