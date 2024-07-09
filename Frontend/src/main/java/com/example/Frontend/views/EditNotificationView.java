package com.example.Frontend.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Route("editNotification")
public class EditNotificationView extends VerticalLayout implements HasUrlParameter<String> {

    @Autowired
    private RestTemplate restTemplate;
    private String notificationId;
    private String eventIdValue;
    private Map<String, Object> existingNotificationData;

    private TextField notificationIdField = new TextField("Notification ID");
    private TextField eventIdField = new TextField("Event ID");
    private TextField typeField = new TextField("Type");
    private TextField messageField = new TextField("Message");
    private DateTimePicker sendDateField = new DateTimePicker("Send Date");

    public EditNotificationView() {
        H1 title = new H1("Edit Notification");
        FormLayout formLayout = new FormLayout();

        notificationIdField.setReadOnly(true);
        eventIdField.setReadOnly(true);
        sendDateField.setReadOnly(true);

        Button saveButton = new Button("Save", event -> updateNotificationData());

        formLayout.add(notificationIdField, eventIdField, typeField, messageField, sendDateField);
        add(title, formLayout, saveButton);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        this.notificationId = parameter;
        loadNotificationData();
    }

    private void loadNotificationData() {
        String url = "http://localhost:8084/api/notifications/" + notificationId;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            existingNotificationData = response.getBody();
            notificationIdField.setValue((String) existingNotificationData.get("notificationId"));
            eventIdField.setValue((String) existingNotificationData.get("eventId"));
            eventIdValue = (String) existingNotificationData.get("eventId"); // Store eventId for later use
            typeField.setValue((String) existingNotificationData.get("type"));
            messageField.setValue((String) existingNotificationData.get("message"));
            sendDateField.setValue(LocalDateTime.parse((String) existingNotificationData.get("sendDate")));
        } else {
            Notification.show("Failed to load notification data");
        }
    }

    private void updateNotificationData() {
        String url = "http://localhost:8084/api/notifications/" + notificationId;

        Map<String, Object> request = new HashMap<>(existingNotificationData); // Use the existing data as a base
        request.put("type", typeField.getValue());
        request.put("message", messageField.getValue());
        request.put("sendDate", sendDateField.getValue().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Notification.show("Notification updated");
            getUI().ifPresent(ui -> ui.navigate("notifications/" + eventIdValue));
        } else {
            Notification.show("Failed to update notification");
        }
    }
}