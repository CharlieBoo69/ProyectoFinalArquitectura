package com.example.Frontend.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Route("event")
public class EventView extends VerticalLayout {

    @Autowired
    private RestTemplate restTemplate;
    private String participantId;

    public EventView() {
        participantId = Optional.ofNullable(VaadinSession.getCurrent().getAttribute("participantId"))
                .map(Object::toString)
                .orElse(null);

        if (participantId == null) {
            Notification.show("Participant ID is not set.");
            return;
        }

        H1 title = new H1("Crear Evento");
        FormLayout formLayout = new FormLayout();

        TextField idEvent = new TextField("Event ID");
        TextField name = new TextField("Name");
        TextField description = new TextField("Description");
        TextField place = new TextField("Place");
        TextField type = new TextField("Type");
        TextField price = new TextField("Price");
        DateTimePicker eventTime = new DateTimePicker("Event Time");

        Button createButton = new Button("Create", event -> {
            if (idEvent.isEmpty() || name.isEmpty() || description.isEmpty() || place.isEmpty() || type.isEmpty() || price.isEmpty() || eventTime.isEmpty()) {
                Notification.show("All fields must be filled");
                return;
            }

            double parsedPrice;
            try {
                parsedPrice = Double.parseDouble(price.getValue());
            } catch (NumberFormatException e) {
                Notification.show("Price must be a valid number");
                return;
            }

            LocalDateTime parsedEventTime = eventTime.getValue();
            if (parsedEventTime == null) {
                Notification.show("Event time must be set");
                return;
            }

            String url = "http://localhost:8084/api/events";

            Map<String, Object> request = new HashMap<>();
            request.put("idEvent", idEvent.getValue());
            request.put("idOwner", participantId);
            request.put("name", name.getValue());
            request.put("description", description.getValue());
            request.put("place", place.getValue());
            request.put("type", type.getValue());
            request.put("price", parsedPrice);
            request.put("eventTime", parsedEventTime.toString());
            request.put("guests", new String[]{});
            request.put("notifications", new String[]{});

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    Notification.show("Event created");
                    getUI().ifPresent(ui -> ui.navigate("main"));
                } else {
                    Notification.show("Failed to create event: " + response.getBody());
                }
            } catch (Exception e) {
                Notification.show("Error creating event: " + e.getMessage());
            }
        });

        formLayout.add(idEvent, name, description, place, type, price, eventTime);
        add(title, formLayout, createButton);
    }
}