package com.example.Frontend.views;

import com.example.Frontend.models.Event;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Route("main")
public class MainView extends VerticalLayout {

    private final RestTemplate restTemplate;
    private String participantId;

    @Autowired
    public MainView(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.participantId = Optional.ofNullable(VaadinSession.getCurrent().getAttribute("participantId"))
                .map(Object::toString)
                .orElse(null);

        if (this.participantId == null) {
            Notification.show("Participant ID is not set.");
            return;
        }

        H1 title = new H1("Bienvenido");
        Button userButton = new Button("Informacion del Usuario", event -> getUI().ifPresent(ui -> ui.navigate("user")));

        H1 title1 = new H1("Lista de eventos a los que estoy invitado");
        Grid<Event> guestEventsGrid = new Grid<>(Event.class);

        H1 title2 = new H1("Mis Eventos");
        Button createEventButton = new Button("Crear Nuevo Evento", event -> getUI().ifPresent(ui -> ui.navigate("event")));
        Grid<Event> ownerEventsGrid = new Grid<>(Event.class);

        H1 title3 = new H1("Eventos para inscribirme");
        Grid<Event> publicEventsGrid = new Grid<>(Event.class);

        loadGuestEvents(guestEventsGrid);
        loadOwnerEvents(ownerEventsGrid);
        loadPublicEvents(publicEventsGrid);

        add(title, userButton, title1, guestEventsGrid, title2, createEventButton, ownerEventsGrid, title3, publicEventsGrid);
    }

    private void loadGuestEvents(Grid<Event> grid) {
        String url = "http://localhost:8084/api/events";

        ResponseEntity<Event[]> response = restTemplate.getForEntity(url, Event[].class);
        List<Event> events = Arrays.asList(response.getBody());

        grid.setItems(events.stream()
                .filter(event -> event.getGuests() != null && Arrays.asList(event.getGuests()).contains(participantId))
                .collect(Collectors.toList()));
        grid.setColumns("idEvent", "name", "eventTime", "place");

        grid.addComponentColumn(event -> {
            Button infoButton = new Button("Info", e -> Notification.show("Event info: " +event.getName()+"\nDescripcion: "+ event.getDescription()+"\nFecha: "+ event.getEventTime()+"\nLugar: "+event.getPlace()));
            Button notificationsButton = new Button("Notifications", e -> Notification.show("Event notifications: " + Arrays.toString(event.getNotifications())));
            return new VerticalLayout(infoButton, notificationsButton);
        }).setHeader("Actions");
    }

    private void loadOwnerEvents(Grid<Event> grid) {
        String url = "http://localhost:8084/api/events";

        ResponseEntity<Event[]> response = restTemplate.getForEntity(url, Event[].class);
        List<Event> events = Arrays.asList(response.getBody());

        grid.setItems(events.stream()
                .filter(event -> event.getIdOwner() != null && event.getIdOwner().equals(participantId))
                .collect(Collectors.toList()));
        grid.setColumns("idEvent", "name", "eventTime", "place");

        grid.addComponentColumn(event -> {
            Button infoButton = new Button("Info", e -> Notification.show("Info:\nNombre del evento: " +event.getName()+"\nDescripcion: "+ event.getDescription()+"\nFecha: "+ event.getEventTime()+"\nLugar: "+event.getPlace()));
            Button editButton = new Button("Edit", e -> getUI().ifPresent(ui -> ui.navigate("editEvent/" + event.getIdEvent())));
            TextField guestIdField = new TextField("Guest ID");
            Button addGuestButton = new Button("Add Guest", e -> addGuestToMyEvent(event.getIdEvent(), guestIdField.getValue()));
            Button deleteButton = new Button("Delete", e -> deleteEvent(event.getIdEvent(), guestIdField.getValue()));
            Button notificationsButton = new Button("Notifications", e -> getUI().ifPresent(ui -> ui.navigate("notifications/" + event.getIdEvent())));
            return new VerticalLayout(infoButton, editButton,guestIdField, addGuestButton, deleteButton, notificationsButton);
        }).setHeader("Actions");
    }

    private void loadPublicEvents(Grid<Event> grid) {
        String url = "http://localhost:8084/api/events";

        ResponseEntity<Event[]> response = restTemplate.getForEntity(url, Event[].class);
        List<Event> events = Arrays.asList(response.getBody());

        grid.setItems(events.stream()
                .filter(event -> "public".equals(event.getType()) && event.getGuests() != null && !Arrays.asList(event.getGuests()).contains(participantId))
                .collect(Collectors.toList()));
        grid.setColumns("idEvent", "name", "eventTime", "place");

        grid.addComponentColumn(event -> {
            Button joinButton = new Button("Join", e -> addGuestToEvent(event.getIdEvent(), event.getIdOwner()));
            return joinButton;
        }).setHeader("Actions");
    }

    private void deleteEvent(String eventId, String guestId) {
        String url = "http://localhost:8084/api/events/" + eventId + "/guests/" + guestId+"?userId="+ participantId;
        restTemplate.delete(url);
        Notification.show("Invitado eliminado");
        getUI().ifPresent(ui -> ui.getPage().reload());
    }
    private void addGuestToMyEvent(String eventId, String guestId) {
        String url = "http://localhost:8084/api/events/" + eventId + "/guests/" + guestId+"?userId="+ participantId;
        restTemplate.put(url, null);
        Notification.show("Invitado agregado");
        getUI().ifPresent(ui -> ui.getPage().reload());
    }

    private void addGuestToEvent(String eventId, String idOwnwer) {

        String url = "http://localhost:8084/api/events/" + eventId + "/guests/" + participantId+"?userId="+ idOwnwer;
        restTemplate.put(url, null);
        Notification.show("Joined event");
        getUI().ifPresent(ui -> ui.getPage().reload());
    }
}