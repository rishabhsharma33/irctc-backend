package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserBookingService {
    private final User user;
    private List<User> userList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String USERS_PATH = "app/src/main/java/ticket/booking/localDb/users.json";
    public UserBookingService(User user) throws IOException{
        this.user = user;
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        loadUsers();
    }
    private void loadUsers() throws IOException{
        userList = objectMapper.readValue(new File(USERS_PATH), new TypeReference<List<User>>() {});
    }

    public Boolean loginUser() {
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equalsIgnoreCase(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user1) {
        try {
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        } catch (IOException e) {
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File userFile = new File(USERS_PATH);
        objectMapper.writeValue(userFile, userList);
    }

    private void fetchBooking() {
        user.printTickets();
    }

    private Boolean cancelBooking(String ticketId) throws IOException {
        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("Enter a valid ticked id");
            return Boolean.FALSE;
        }

        boolean isRemoved = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(ticketId));
        if (isRemoved) {
            saveUserListToFile();
            System.out.println("Ticket with id " + ticketId + " has been cancelled");
            return true;
        } else {
            System.out.println("No ticket found with ticketId: " + ticketId);
            return false;
        }
    }


}
