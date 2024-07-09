package com.example.Frontend.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Route("user")
public class UserView extends VerticalLayout {

    private final RestTemplate restTemplate;
    private String participantId;

    private TextField participantIdField = new TextField("Participant ID");
    private PasswordField passwordField = new PasswordField("Password");
    private TextField firstNameField = new TextField("First Name");
    private TextField lastNameField = new TextField("Last Name");
    private EmailField emailField = new EmailField("Email");
    private TextField phoneNumberField = new TextField("Phone Number");

    private Map<String, Object> userData = new HashMap<>();

    @Autowired
    public UserView(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.participantId = VaadinSession.getCurrent().getAttribute("participantId").toString();

        H1 title = new H1("User Info");
        participantIdField.setValue(participantId);
        participantIdField.setReadOnly(true);

        loadUserData();

        Button editButton = new Button("Edit", event -> setFieldsEditable(true));
        Button saveButton = new Button("Save", event -> updateUserData());
        Button logoutButton = new Button("Logout", event -> {
            VaadinSession.getCurrent().setAttribute("participantId", null);
            getUI().ifPresent(ui -> ui.navigate(""));
        });

        add(title, participantIdField, passwordField, firstNameField, lastNameField, emailField, phoneNumberField, editButton, saveButton, logoutButton);
    }

    private void loadUserData() {
        String url = "http://localhost:8084/api/participants/" + participantId;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            userData = response.getBody();
            passwordField.setValue((String) userData.get("password"));
            firstNameField.setValue((String) userData.get("firstName"));
            lastNameField.setValue((String) userData.get("lastName"));
            emailField.setValue((String) userData.get("email"));
            phoneNumberField.setValue((String) userData.get("phoneNumber"));

            setFieldsEditable(false);
        } else {
            Notification.show("Failed to load user data");
        }
    }

    private void setFieldsEditable(boolean editable) {
        passwordField.setReadOnly(!editable);
        firstNameField.setReadOnly(!editable);
        lastNameField.setReadOnly(!editable);
        emailField.setReadOnly(!editable);
        phoneNumberField.setReadOnly(!editable);
    }

    private void updateUserData() {
        String url = "http://localhost:8084/api/participants/" + participantId;

        Map<String, Object> request = new HashMap<>(userData);
        request.put("password", passwordField.getValue());
        request.put("firstName", firstNameField.getValue());
        request.put("lastName", lastNameField.getValue());
        request.put("email", emailField.getValue());
        request.put("phoneNumber", phoneNumberField.getValue());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Notification.show("User data updated");
            setFieldsEditable(false);
        } else {
            Notification.show("Failed to update user data");
        }
    }
}