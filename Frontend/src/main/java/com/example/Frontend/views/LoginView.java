package com.example.Frontend.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

@Route("")
public class LoginView extends VerticalLayout {

    private final RestTemplate restTemplate;

    @Autowired
    public LoginView(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        H1 title = new H1("Login");
        LoginForm loginForm = new LoginForm();
        Button registerButton = new Button("Register", event -> getUI().ifPresent(ui -> ui.navigate("register")));

        loginForm.addLoginListener(e -> {
            String username = e.getUsername();
            String password = e.getPassword();

            String url = "http://localhost:8084/api/participants/{participantId}";
            Map<String, String> params = new HashMap<>();
            params.put("participantId", username);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, null, Map.class, params);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && password.equals(response.getBody().get("password"))) {
                VaadinSession.getCurrent().setAttribute("participantId", username);
                getUI().ifPresent(ui -> ui.navigate("main"));
            } else {
                Notification.show("Login failed");
            }
        });

        add(title, loginForm, registerButton);
    }
}