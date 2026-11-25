# Vaccination Slot Booking

A Java console application for PS No. 6 that manages a vaccination session using a circular array queue of three slots. Bookings are served in first-in, first-out (FIFO) order, and each released slot can be reused.

## Google Drive Link

Add the link to your project report, demonstration or submission folder below.

**Google Drive link**

---

## Student Details

| Field              | Details                          |
| ------------------ | -------------------------------- |
| Student name       | **************\_\_************** |
| Roll / ERP number  | **************\_\_************** |
| Program / branch   | **************\_\_************** |
| Semester / section | **************\_\_************** |
| Academic session   | **************\_\_************** |
| Faculty / guide    | **************\_\_************** |

## Features

- Book a slot for an applicant aged 18 or older.
- Generate booking IDs beginning with `V101`.
- Limit the pending queue to three bookings.
- Vaccinate the oldest pending booking and free its slot.
- Display pending bookings in FIFO order.
- Find an active booking by ID using a `HashMap`.
- Load pending bookings from `bookings.txt` at startup.
- Save the queue and next booking number on exit or end-of-input.

## Concepts Used

| Concept             | Use in the project                                   |
| ------------------- | ---------------------------------------------------- |
| Array               | Stores three pending `Booking` objects.              |
| Circular queue      | Advances `front` and `rear` using modulo arithmetic. |
| FIFO                | Serves bookings in their arrival order.              |
| HashMap             | Retrieves active bookings by ID.                     |
| Classes and methods | Organize booking data and application operations.    |
| File handling       | Preserves the queue and ID sequence between runs.    |
| Input validation    | Checks names, ages and menu selections.              |

The `front` index identifies the next person to vaccinate. The `rear` index identifies the next insertion position. The `count` variable distinguishes a full queue from an empty queue when both indices are equal.

## Project Files

| File                                                                           | Purpose                                                           |
| ------------------------------------------------------------------------------ | ----------------------------------------------------------------- |
| [VaccinationSlotBooking.java](VaccinationSlotBooking.java)                     | Complete application source.                                      |
| [Vaccination_Slot_Booking_Synopsis.pdf](Vaccination_Slot_Booking_Synopsis.pdf) | College project report with diagrams, test cases and source code. |
| `bookings.txt`                                                                 | Data file created when the application saves.                     |
| `README.md`                                                                    | Project overview and execution instructions.                      |

## Requirements

- JDK 11 or later.
- A terminal on Windows, macOS or Linux.
- Read and write access to the folder used to run the application.

## Compile and Run

Open a terminal in the folder containing `VaccinationSlotBooking.java`.

```bash
javac VaccinationSlotBooking.java
java VaccinationSlotBooking
```

## Menu

```text
1. Book slot
2. Vaccinate next
3. View queue
4. Find booking by ID
5. Save and exit
```

Choose option `1` and enter the applicant's name and integer age. Choose option `2` to serve the oldest booking. Option `4` accepts an ID such as `V101`, including lowercase input such as `v101`.

## Validation

- Applicants must be at least 18 years old.
- Names must contain text and must be free of tab characters.
- A full queue rejects additional bookings.
- Invalid menu choices and non-integer ages produce a message.
- Vaccinated bookings are removed from the queue and ID lookup map.
- Invalid saved data produces an error and the existing file is preserved.

## File Persistence

The application reads and writes `bookings.txt` in its current working folder. A missing file starts an empty queue with the next ID set to `V101`.

The first line stores the next numeric booking ID. Each remaining line stores the booking number, age and name, separated by tabs, in FIFO order.

```text
104
102	30	Dev
103	30	Meera
```

In this example, `V102` and `V103` are pending, and the next booking receives `V104`. The saved counter preserves the ID sequence even when the queue becomes empty.

## Test Cases

All four assignment cases passed after compilation for Java 11 and execution on JDK 26.

| Case              | Initial state and action                                                                        | Expected result                                                                                  |
| ----------------- | ----------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| 1. Normal input   | Empty queue. Book Riya, age 25.                                                                 | `Slot booked. ID: V101 \| Riya \| Position: 1`                                                   |
| 2. Full queue     | Three pending bookings. Attempt to book Dev, age 30.                                            | `Slots full. Please try the next session.`                                                       |
| 3. Invalid input  | Empty queue. Attempt to book Aman, age -5.                                                      | `Invalid age. Booking not done.`                                                                 |
| 4. FIFO and reuse | Queue contains V101 Riya and V102 Dev. Vaccinate next, book Meera aged 30, then view the queue. | Riya is vaccinated. Meera receives V103 at position 2. Queue order is V102 Dev, then V103 Meera. |

Additional checks passed for age 18, invalid names and ages, empty-queue operations, circular wraparound, ID lookup, restart, UTF-8 names and preservation of malformed saved data.

## Complexity

For `n` pending bookings, adding, vaccinating and ID lookup take expected `O(1)` time. Displaying, loading and saving take `O(n)` time. The array has a fixed capacity of three, and the map contains at most three active entries. This analysis treats individual booking fields as bounded-size values.
