package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//
// A diferencia de los tests web de tarea, donde usábamos los datos
// de prueba de la base de datos, aquí vamos a practicar otro enfoque:
// moquear el usuarioService.
public class UsuarioWebTest {

    @Autowired
    private MockMvc mockMvc;

    // Moqueamos el usuarioService.
    // En los tests deberemos proporcionar el valor devuelto por las llamadas
    // a los métodos de usuarioService que se van a ejecutar cuando se realicen
    // las peticiones a los endpoint.
    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private madstodolist.authentication.ManagerUserSession managerUserSession;

    @Test
    public void servicioLoginUsuarioOK() throws Exception {
        // GIVEN
        // Moqueamos la llamada a usuarioService.login para que
        // devuelva un LOGIN_OK y la llamada a usuarioServicie.findByEmail
        // para que devuelva un usuario determinado.

        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setId(1L);

        when(usuarioService.login("ana.garcia@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        when(usuarioService.findByEmail("ana.garcia@gmail.com"))
                .thenReturn(anaGarcia);

        // WHEN, THEN
        // Realizamos una petición POST al login pasando los datos
        // esperados en el mock, la petición devolverá una redirección a la
        // URL con las tareas del usuario

        this.mockMvc.perform(post("/login")
                        .param("eMail", "ana.garcia@gmail.com")
                        .param("password", "12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/1/tareas"));
    }

    @Test
    public void servicioLoginUsuarioNotFound() throws Exception {
        // GIVEN
        // Moqueamos el método usuarioService.login para que devuelva
        // USER_NOT_FOUND
        when(usuarioService.login("pepito.perez@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.USER_NOT_FOUND);

        // WHEN, THEN
        // Realizamos una petición POST con los datos del usuario mockeado y
        // se debe devolver una página que contenga el mensaja "No existe usuario"
        this.mockMvc.perform(post("/login")
                        .param("eMail","pepito.perez@gmail.com")
                        .param("password","12345678"))
                .andExpect(content().string(containsString("No existe usuario")));
    }

    @Test
    public void servicioLoginUsuarioErrorPassword() throws Exception {
        // GIVEN
        // Moqueamos el método usuarioService.login para que devuelva
        // ERROR_PASSWORD
        when(usuarioService.login("ana.garcia@gmail.com", "000"))
                .thenReturn(UsuarioService.LoginStatus.ERROR_PASSWORD);

        // WHEN, THEN
        // Realizamos una petición POST con los datos del usuario mockeado y
        // se debe devolver una página que contenga el mensaja "Contraseña incorrecta"
        this.mockMvc.perform(post("/login")
                        .param("eMail","ana.garcia@gmail.com")
                        .param("password","000"))
                .andExpect(content().string(containsString("Contraseña incorrecta")));
    }

    @Test
    public void getRegistradosSinLoginLanzaExcepcion() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        this.mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/registrados"))
                .andExpect(status().is4xxClientError());
    }



    

    @Test
    public void loginAdminRedirigeARegistrados() throws Exception {
        UsuarioData admin = new UsuarioData();
        admin.setId(1L);
        admin.setEmail("admin@ua");
        admin.setAdmin(true);

        when(usuarioService.login("admin@ua", "123")).thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        when(usuarioService.findByEmail("admin@ua")).thenReturn(admin);

        this.mockMvc.perform(post("/login")
                        .param("eMail", "admin@ua")
                        .param("password", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registrados"));
    }

    // tests para comprobar que solo admin vea la lista de usuarios

    @Test
    public void getRegistradosSinLoginDevuelveNoAutorizado() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        this.mockMvc.perform(get("/registrados"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getRegistradosUsuarioNoAdminDevuelveNoAutorizado() throws Exception {
        Long idNoAdmin = 2L;
        UsuarioData noAdmin = new UsuarioData();
        noAdmin.setId(idNoAdmin);
        noAdmin.setEmail("noadmin@ua");
        noAdmin.setAdmin(false);

        when(managerUserSession.usuarioLogeado()).thenReturn(idNoAdmin);
        when(usuarioService.findById(idNoAdmin)).thenReturn(noAdmin);

        this.mockMvc.perform(get("/registrados"))
                .andExpect(status().isUnauthorized());
    }



    @Test
    public void getDescripcionUsuarioNoAdminDevuelveNoAutorizado() throws Exception {
        Long idNoAdmin = 2L;
        UsuarioData noAdmin = new UsuarioData();
        noAdmin.setId(idNoAdmin);
        noAdmin.setEmail("noadmin@ua");
        noAdmin.setAdmin(false);

        when(managerUserSession.usuarioLogeado()).thenReturn(idNoAdmin);
        when(usuarioService.findById(idNoAdmin)).thenReturn(noAdmin);

        this.mockMvc.perform(get("/registrados/3"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getRegistradosConAdminMuestraListaUsuarios() throws Exception {
        Long idAdmin = 1L;
        when(managerUserSession.usuarioLogeado()).thenReturn(idAdmin);

        UsuarioData admin = new UsuarioData();
        admin.setId(idAdmin);
        admin.setNombre("Admin");
        admin.setEmail("admin@ua");
        admin.setAdmin(true);

        UsuarioData usuario2 = new UsuarioData();
        usuario2.setId(2L);
        usuario2.setEmail("pedro@ua");

        java.util.List<UsuarioData> listaUsuarios = java.util.Arrays.asList(admin, usuario2);

        // Simulamos la llamada de comprobarAdmin() y la de allUsuarios()
        when(usuarioService.findById(idAdmin)).thenReturn(admin);
        when(usuarioService.allUsuarios()).thenReturn(listaUsuarios);

        this.mockMvc.perform(get("/registrados"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("Listado de usuarios registrados"),
                        containsString("admin@ua"),
                        containsString("pedro@ua")
                )));
    }

    @Test
    public void getDescripcionUsuarioConAdminMuestraDatosSinPassword() throws Exception {
        Long idAdmin = 1L;
        when(managerUserSession.usuarioLogeado()).thenReturn(idAdmin);

        UsuarioData admin = new UsuarioData();
        admin.setId(idAdmin);
        admin.setNombre("Admin");
        admin.setEmail("admin@ua");
        admin.setAdmin(true);

        UsuarioData usuarioDetalle = new UsuarioData();
        usuarioDetalle.setId(2L);
        usuarioDetalle.setNombre("Carlos Ruiz");
        usuarioDetalle.setEmail("carlos@ua");
        usuarioDetalle.setPassword("secreto123");

        // comprobarAdmin() buscará el id 1L
        when(usuarioService.findById(idAdmin)).thenReturn(admin);
        // La consulta de la ruta /registrados/2 buscará el id 2L
        when(usuarioService.findById(2L)).thenReturn(usuarioDetalle);

        this.mockMvc.perform(get("/registrados/2"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("Carlos Ruiz"),
                        containsString("carlos@ua"),
                        not(containsString("secreto123"))
                )));
    }

    @Test
    public void loginUsuarioBloqueadoMuestraError() throws Exception {
        when(usuarioService.login("bloqueado@ua", "123"))
                .thenReturn(UsuarioService.LoginStatus.USER_BLOCKED);

        this.mockMvc.perform(post("/login")
                        .param("eMail", "bloqueado@ua")
                        .param("password", "123"))
                .andExpect(content().string(containsString("El usuario tiene bloqueado el acceso")));
    }

    @Test
    public void postBloquearUsuarioRedirigeARegistrados() throws Exception {
        Long idAdmin = 1L;
        when(managerUserSession.usuarioLogeado()).thenReturn(idAdmin);

        UsuarioData admin = new UsuarioData();
        admin.setId(idAdmin);
        admin.setAdmin(true);

        when(usuarioService.findById(idAdmin)).thenReturn(admin);

        this.mockMvc.perform(post("/registrados/2/bloquear"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registrados"));
    }
}
