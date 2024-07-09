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

@Route("editEvent")
public class EditEventView extends VerticalLayout implements HasUrlParameter<String> {

    @Autowired
    private RestTemplate restTemplate;
    private String eventId;

    private TextField idEvent = new TextField("Event ID");
    private TextField idOwner = new TextField("Owner ID");
    private TextField name = new TextField("Name");
    private TextField description = new TextField("Description");
    private TextField place = new TextField("Place");
    private TextField type = new TextField("Type");
    private TextField price = new TextField("Price");
    private DateTimePicker eventTime = new DateTimePicker("Event Time");

    private Map<String, Object> originalEventData = new HashMap<>();

    public EditEventView() {
        H1 title = new H1("Edit Event");
        FormLayout formLayout = new FormLayout();

        idEvent.setReadOnly(true);
        idOwner.setReadOnly(true);

        Button saveButton = new Button("Save", event -> updateEventData());

        formLayout.add(idEvent, idOwner, name, description, place, type, price, eventTime);
        add(title, formLayout, saveButton);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        this.eventId = parameter;
        loadEventData();
    }

    private void loadEventData() {
        String url = "http://localhost:8084/api/events/" + eventId;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            originalEventData = response.getBody();
            idEvent.setValue((String) originalEventData.get("idEvent"));
            idOwner.setValue((String) originalEventData.get("idOwner"));
            name.setValue((String) originalEventData.get("name"));
            description.setValue((String) originalEventData.get("description"));
            place.setValue((String) originalEventData.get("place"));
            type.setValue((String) originalEventData.get("type"));
            price.setValue(originalEventData.get("price").toString());
            eventTime.setValue(LocalDateTime.parse((String) originalEventData.get("eventTime")));
        } else {
            Notification.show("Failed to load event data");
        }
    }

    private void updateEventData() {
        String url = "http://localhost:8084/api/events/" + eventId;

        Map<String, Object> request = new HashMap<>();
        request.put("idEvent", idEvent.getValue());
        request.put("idOwner", idOwner.getValue());
        request.put("name", name.getValue());
        request.put("description", description.getValue());
        request.put("place", place.getValue());
        request.put("type", type.getValue());
        request.put("price", Double.parseDouble(price.getValue()));
        request.put("eventTime", eventTime.getValue().toString());
        request.put("guests", originalEventData.get("guests"));
        request.put("notifications", originalEventData.get("notifications"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Notification.show("Event updated");
                getUI().ifPresent(ui -> ui.navigate("main"));
            } else {
                Notification.show("Failed to update event: " + response.getBody());
            }
        } catch (Exception e) {
            Notification.show("Error updating event: " + e.getMessage());
        }
    }
}
