package com.cringee.simplescreensharing.handlerTest;

import com.cringee.simplescreensharing.controllers.UserController;
import com.cringee.simplescreensharing.handlers.ValidateExceptionsHandler;
import com.cringee.simplescreensharing.services.RoleService;
import com.cringee.simplescreensharing.services.SseEmitterService;
import com.cringee.simplescreensharing.services.UserService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@WebMvcTest({UserController.class, ValidateExceptionsHandler.class})
public class ValidateExceptionsHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private SseEmitterService sseEmitterService;

    @BeforeEach
    void setUp() {

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void badRequest() throws Exception {
        when(userService.findAll()).thenThrow(new ConstraintViolationException("Test Constraint Violation", null));

        mockMvc.perform(get("/users/"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Test Constraint Violation"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void badRequest2() throws Exception {
        mockMvc.perform(get("/users/user_form/qwerty")
                        .header("HX-Request", "true"))
                .andExpect(status().isBadRequest());
    }

}
