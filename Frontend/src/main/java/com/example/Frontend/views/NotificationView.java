package com.example.Frontend.views;

import com.example.Frontend.models.Notification;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

@Route("notifications")
public class NotificationView extends VerticalLayout implements HasUrlParameter<String> {

    private final RestTemplate restTemplate;
    private String eventId;
    private Grid<Notification> notificationsGrid;

    private TextField notificationIdField = new TextField("Notification ID");
    private TextField typeField = new TextField("Type");
    private TextField messageField = new TextField("Message");
    private DateTimePicker sendDateField = new DateTimePicker("Send Date");

    @Autowired
    public NotificationView(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        H1 title = new H1("Notifications");

        notificationsGrid = new Grid<>(Notification.class);

        FormLayout formLayout = new FormLayout();
        Button createButton = new Button("Create Notification", event -> createNotification());

        formLayout.add(notificationIdField, typeField, messageField, sendDateField, createButton);

        add(title, notificationsGrid, formLayout);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        this.eventId = parameter;
        loadNotifications();
    }

    private void loadNotifications() {
        String url = "http://localhost:8084/api/notifications";

        ResponseEntity<Notification[]> response = restTemplate.getForEntity(url, Notification[].class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<Notification> notifications = Arrays.asList(response.getBody());

            notificationsGrid.setItems(notifications.stream()
                    .filter(notification -> notification.getEventId().equals(eventId))
                    .collect(Collectors.toList()));
            notificationsGrid.setColumns("notificationId", "type", "message", "sendDate");

            notificationsGrid.addComponentColumn(notification -> {
                Button editButton = new Button("Edit", e -> getUI().ifPresent(ui -> ui.navigate("editNotification/" + notification.getNotificationId())));
                return editButton;
            }).setHeader("Actions");
        } else {
            com.vaadin.flow.component.notification.Notification.show("Failed to load notifications");
        }
    }

    private void createNotification() {
        String url = "http://localhost:8084/api/notifications";

        Map<String, Object> request = new HashMap<>();
        request.put("notificationId", notificationIdField.getValue());
        request.put("eventId", eventId);
        request.put("type", typeField.getValue());
        request.put("message", messageField.getValue());
        request.put("sendDate", sendDateField.getValue().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            com.vaadin.flow.component.notification.Notification.show("Notification created");
            loadNotifications(); // Reload notifications to include the newly created one
        } else {
            com.vaadin.flow.component.notification.Notification.show("Failed to create notification");
        }
    }
}