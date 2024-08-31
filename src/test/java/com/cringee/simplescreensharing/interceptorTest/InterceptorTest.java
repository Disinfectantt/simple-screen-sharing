package com.cringee.simplescreensharing.interceptorTest;

import com.cringee.simplescreensharing.controllers.UserController;
import com.cringee.simplescreensharing.interseptors.HxRequestInterceptor;
import com.cringee.simplescreensharing.services.RoleService;
import com.cringee.simplescreensharing.services.SseEmitterService;
import com.cringee.simplescreensharing.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(HxRequestInterceptor.class)
class InterceptorTest implements WebMvcConfigurer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private SseEmitterService sseEmitterService;

    @Autowired
    private HxRequestInterceptor hxRequestInterceptor;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(hxRequestInterceptor);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void Present() throws Exception {
        mockMvc.perform(get("/users/user_form")
                        .header("HX-Request", "true")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void redirect() throws Exception {
        mockMvc.perform(get("/users/user_form"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
