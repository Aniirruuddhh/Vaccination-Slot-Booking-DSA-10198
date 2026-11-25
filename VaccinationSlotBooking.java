import java.io.IOException;
import static java.lang.System.out;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;
import java.util.*;

public class VaccinationSlotBooking {
    private static final int CAPACITY = 3;
    private static final int MIN_AGE = 18;
    private static final int FIRST_ID = 101;
    private static final Path STORE = Path.of("bookings.txt");
    private static final String MENU = String.join(System.lineSeparator(), "",
            "1. Book slot", "2. Vaccinate next", "3. View queue",
            "4. Find booking by ID", "5. Save and exit", "Choose an option: ");

    private static final class Booking {
        final int number;
        final String name;
        final int age;

        Booking(int number, String name, int age) {
            this.number = number;
            this.name = name;
            this.age = age;
        }

        String id() {
            return "V" + number;
        }
    }

    private final Booking[] queue = new Booking[CAPACITY];
    private final HashMap<String, Booking> byId = new HashMap<>();
    private int front, rear, count; // rear is the next insertion index.
    private int nextId = FIRST_ID;

    private static String readLine(Scanner input, String prompt) {
        out.print(prompt);
        return input.hasNextLine() ? input.nextLine().strip() : null;
    }

    private void enqueue(Booking booking) {
        queue[rear] = booking;
        rear = (rear + 1) % CAPACITY;
        count++;
        byId.put(booking.id(), booking);
    }

    private void bookSlot(Scanner input) {
        String name = readLine(input, "Name: ");
        String ageText = name == null ? null : readLine(input, "Age: ");
        if (ageText == null) {
            return;
        }
        if (count == CAPACITY) {
            out.println("Slots full. Please try the next session.");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            age = -1;
        }
        if (age < MIN_AGE) {
            out.println("Invalid age. Booking not done.");
        } else if (!validName(name)) {
            out.println("Invalid name. Booking not done.");
        } else if (nextId == Integer.MAX_VALUE) {
            out.println("Booking IDs exhausted. Booking not done.");
        } else {
            Booking booking = new Booking(nextId++, name, age);
            enqueue(booking);
            out.printf("Slot booked. ID: %s | %s | Position: %d%n",
                    booking.id(), name, count);
        }
    }

    private static boolean validName(String name) {
        return !name.isBlank() && !name.contains("\t");
    }

    private void vaccinateNext() {
        if (count == 0) {
            out.println("No bookings to vaccinate.");
            return;
        }

        Booking booking = queue[front];
        queue[front] = null;
        front = (front + 1) % CAPACITY;
        count--;
        byId.remove(booking.id());
        out.println(booking.name + " vaccinated. Slot freed.");
    }

    private void viewQueue() {
        if (count == 0) {
            out.println("Queue is empty.");
        }
        for (int i = 0; i < count; i++) {
            Booking booking = queue[(front + i) % CAPACITY];
            out.printf("%d. %s | %s%n", i + 1, booking.id(), booking.name);
        }
    }

    private void findBooking(Scanner input) {
        String id = readLine(input, "Booking ID: ");
        if (id == null) {
            return;
        }
        Booking booking = byId.get(id.toUpperCase(Locale.ROOT));
        if (booking == null) {
            out.println("Booking not found.");
        } else {
            out.printf("%s | %s | Age: %d%n", booking.id(), booking.name, booking.age);
        }
    }

    private void load() throws IOException {
        try {
            List<String> lines = Files.readAllLines(STORE);
            if (lines.isEmpty() || lines.size() > CAPACITY + 1) {
                throw new IOException("Invalid file header or booking count.");
            }
            nextId = Integer.parseInt(lines.get(0));
            if (nextId < FIRST_ID) {
                throw new IOException("Invalid next booking ID.");
            }

            int previousId = FIRST_ID - 1;
            for (int i = 1; i < lines.size(); i++) {
                String[] fields = lines.get(i).split("\t", -1);
                if (fields.length != 3) {
                    throw new IOException(
                            "Invalid booking record on line " + (i + 1) + ".");
                }
                int number = Integer.parseInt(fields[0]);
                int age = Integer.parseInt(fields[1]);
                String name = fields[2];
                if (number <= previousId || number >= nextId
                        || age < MIN_AGE || !validName(name)) {
                    throw new IOException(
                            "Invalid booking data on line " + (i + 1) + ".");
                }
                enqueue(new Booking(number, name, age));
                previousId = number;
            }
        } catch (NoSuchFileException e) {
            // First run starts with an empty queue.
        } catch (NumberFormatException e) {
            throw new IOException("A saved booking number or age is invalid.", e);
        }
    }

    private void save() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(Integer.toString(nextId));
        for (int i = 0; i < count; i++) {
            Booking booking = queue[(front + i) % CAPACITY];
            lines.add(booking.number + "\t" + booking.age + "\t" + booking.name);
        }

        Path temporary = Files.createTempFile(Path.of("."), "bookings-", ".tmp");
        try {
            Files.write(temporary, lines);
            Files.move(temporary, STORE, REPLACE_EXISTING, ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporary, STORE, REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private void run(Scanner input) {
        while (true) {
            String option = readLine(input, MENU);
            if (option == null || option.equals("5")) {
                return;
            }
            switch (option) {
                case "1":
                    bookSlot(input);
                    break;
                case "2":
                    vaccinateNext();
                    break;
                case "3":
                    viewQueue();
                    break;
                case "4":
                    findBooking(input);
                    break;
                default:
                    out.println("Invalid option. Enter 1 to 5.");
            }
        }
    }

    public static void main(String[] args) {
        VaccinationSlotBooking app = new VaccinationSlotBooking();
        try (Scanner input = new Scanner(System.in, StandardCharsets.UTF_8)) {
            app.load();
            app.run(input);
            app.save();
            out.println("Bookings saved to bookings.txt.");
        } catch (IOException e) {
            System.err.println("Error with bookings.txt: " + e.getMessage());
            System.exit(1);
        }
    }
}
