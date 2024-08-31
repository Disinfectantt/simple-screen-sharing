package com.cringee.simplescreensharing.controllerTest;

import com.cringee.simplescreensharing.controllers.RoutesController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class RoutesTest {
    @Mock
    private Model model;

    @InjectMocks
    private RoutesController routesController;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void testIndex() {
        String result = routesController.index();
        assertThat(result).isEqualTo("index");
    }

    @Test
    void testLoginForm_NonHxRequest() {
        String result = routesController.loginForm(request, response);

        assertThat(result).isEqualTo("login");
        assertThat(response.getHeader("HX-Redirect")).isNull();
    }

    @Test
    void testLoginForm_HxRequest() {
        request.addHeader("HX-Request", "true");

        String result = routesController.loginForm(request, response);

        assertThat(result).isNull();
        assertThat(response.getHeader("HX-Redirect")).isEqualTo("/login");
    }

    @Test
    void testErrorPage() {
        String result = routesController.errorPage();

        assertThat(result).isEqualTo("error");
    }
}
