package madstodolist.service;

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
}