package com.example.Frontend.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Route("register")
public class RegisterView extends VerticalLayout {

    @Autowired
    private RestTemplate restTemplate;

    public RegisterView() {
        H1 title = new H1("Registero");
        FormLayout formLayout = new FormLayout();

        TextField participantId = new TextField("Participant ID");
        PasswordField password = new PasswordField("Password");
        TextField firstName = new TextField("First Name");
        TextField lastName = new TextField("Last Name");
        EmailField email = new EmailField("Email");
        TextField phoneNumber = new TextField("Phone Number");

        Button registerButton = new Button("Register", event -> {
            String url = "http://localhost:8084/api/participants";

            Map<String, Object> request = new HashMap<>();
            request.put("participantId", participantId.getValue());
            request.put("password", password.getValue());
            request.put("firstName", firstName.getValue());
            request.put("lastName", lastName.getValue());
            request.put("email", email.getValue());
            request.put("phoneNumber", phoneNumber.getValue());
            request.put("events", new String[]{});
            request.put("notifications", new String[]{});

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Notification.show("Registration successful");
                getUI().ifPresent(ui -> ui.navigate(""));
            } else {
                Notification.show("Registration failed");
            }
        });

        formLayout.add(participantId, password, firstName, lastName, email, phoneNumber);
        add(title, formLayout, registerButton);
    }
}
