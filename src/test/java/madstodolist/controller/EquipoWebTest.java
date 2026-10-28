package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.model.Equipo;
import madstodolist.service.EquipoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import madstodolist.model.Usuario;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
public class EquipoWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipoService equipoService;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Test
    public void getEquiposDevuelveListaOrdenada() throws Exception {
        Equipo equipo1 = new Equipo("Proyecto A");
        equipo1.setId(1L); // <-- Asegúrate de que tenga ID

        Equipo equipo2 = new Equipo("Proyecto B");
        equipo2.setId(2L); // <-- Asegúrate de que tenga ID

        when(equipoService.findAllOrderedByName()).thenReturn(Arrays.asList(equipo1, equipo2));

        this.mockMvc.perform(get("/equipos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Proyecto A")))
                .andExpect(content().string(containsString("Proyecto B")));
    }

    @Test
    public void getDetalleEquipoDevuelveNombreYMiembros() throws Exception {
        // GIVEN
        Equipo equipo = new Equipo("Proyecto Alfa");
        Usuario user1 = new Usuario("ana@ua");
        Usuario user2 = new Usuario("juan@ua");

        when(equipoService.recuperarEquipo(1L)).thenReturn(equipo);
        when(equipoService.usuariosEquipo(1L)).thenReturn(Arrays.asList(user1, user2));

        // WHEN, THEN
        this.mockMvc.perform(get("/equipos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Proyecto Alfa")))
                .andExpect(content().string(containsString("ana@ua")))
                .andExpect(content().string(containsString("juan@ua")));
    }

    @Test
    public void getDetalleEquipoNoExistenteDevuelve404() throws Exception {
        // GIVEN
        when(equipoService.recuperarEquipo(999L)).thenReturn(null);

        // WHEN, THEN
        this.mockMvc.perform(get("/equipos/999"))
                .andExpect(status().isNotFound());
    }
}