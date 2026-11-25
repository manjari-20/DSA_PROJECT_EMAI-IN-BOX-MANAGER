import java.util.*;
import java.io.*;

class Email {
    String id, sender, subject, priority, date;
    int arrivalTime;
    boolean isRead;

    Email(String id, String sender, String subject,
          String priority, String date, int arrivalTime,
          boolean isRead) {

        this.id = id;
        this.sender = sender;
        this.subject = subject;
        this.priority = priority;
        this.date = date;
        this.arrivalTime = arrivalTime;
        this.isRead = isRead;
    }
}

public class EmailInboxManager {

    static Scanner sc = new Scanner(System.in);

    // PriorityQueue: High priority first, then arrival time
    static PriorityQueue<Email> inbox = new PriorityQueue<>(
        (a, b) -> {
            int p = Integer.compare(
                priorityValue(b.priority),
                priorityValue(a.priority)
            );

            if (p != 0) return p;

            return Integer.compare(a.arrivalTime, b.arrivalTime);
        }
    );

    // Stack: Recently opened emails
    static Stack<Email> recentEmails = new Stack<>();

    // HashMap: Email ID lookup
    static HashMap<String, Email> emailMap = new HashMap<>();

    // Stores all emails for sorting and display
    static ArrayList<Email> allEmails = new ArrayList<>();

    static int nextId = 101;
    static int arrivalCounter = 1;

    static final String FILE_NAME = "inbox.txt";

    static int priorityValue(String priority) {
        if (priority.equalsIgnoreCase("High")) return 3;
        if (priority.equalsIgnoreCase("Normal")) return 2;
        return 1;
    }

    // Receive a new email
    static void receiveEmail() {

        System.out.print("Enter Sender: ");
        String sender = sc.nextLine().trim();

        System.out.print("Enter Subject: ");
        String subject = sc.nextLine().trim();

        if (sender.isEmpty() || subject.isEmpty()) {
            System.out.println("Sender and Subject cannot be empty.");
            return;
        }

        System.out.print("Enter Priority (High/Normal/Low): ");
        String priority = sc.nextLine().trim();

        if (!priority.equalsIgnoreCase("High") &&
            !priority.equalsIgnoreCase("Normal") &&
            !priority.equalsIgnoreCase("Low")) {

            System.out.println("Invalid priority!");
            return;
        }

        priority = priority.substring(0, 1).toUpperCase()
                   + priority.substring(1).toLowerCase();

        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = sc.nextLine().trim();

        String id = "EM" + nextId++;

        Email email = new Email(
            id, sender, subject, priority,
            date, arrivalCounter++, false
        );

        inbox.add(email);
        emailMap.put(id, email);
        allEmails.add(email);

        System.out.println(
            "Email received: " + id + " | "
            + sender + " | " + priority
        );
    }

    // Open next highest-priority email
    static void openNextEmail() {

        if (inbox.isEmpty()) {
            System.out.println("Inbox is empty!");
            return;
        }

        Email email = inbox.poll();

        email.isRead = true;
        recentEmails.push(email);

        System.out.println(
            "Opened: " + email.id + " | " + email.sender
        );
    }

    // Search using HashMap
    static void searchEmail() {

        System.out.print("Enter Email ID: ");
        String id = sc.nextLine().trim().toUpperCase();

        Email email = emailMap.get(id);

        if (email == null) {
            System.out.println("Email " + id + " not found.");
        } else {
            displayEmail(email);
        }
    }

    // Display email details
    static void displayEmail(Email email) {

        System.out.println("\nEmail ID: " + email.id);
        System.out.println("Sender: " + email.sender);
        System.out.println("Subject: " + email.subject);
        System.out.println("Priority: " + email.priority);
        System.out.println("Date: " + email.date);
        System.out.println("Status: " +
            (email.isRead ? "Read" : "Unread"));
    }

    // Sort emails by sender
    static void sortBySender() {

        ArrayList<Email> sorted = new ArrayList<>(allEmails);

        sorted.sort(
            Comparator.comparing(
                e -> e.sender.toLowerCase()
            )
        );

        System.out.println("\n===== SORTED BY SENDER =====");

        for (Email e : sorted) {
            System.out.println(
                e.id + " | " + e.sender + " | " + e.subject
            );
        }
    }

    // Sort emails by date
    static void sortByDate() {

        ArrayList<Email> sorted = new ArrayList<>(allEmails);

        sorted.sort(
            Comparator.comparing(e -> e.date)
        );

        System.out.println("\n===== SORTED BY DATE =====");

        for (Email e : sorted) {
            System.out.println(
                e.id + " | " + e.sender + " | " + e.date
            );
        }
    }

    // Mark email as read
    static void markAsRead() {

        System.out.print("Enter Email ID: ");
        String id = sc.nextLine().trim().toUpperCase();

        Email email = emailMap.get(id);

        if (email == null) {
            System.out.println("Email " + id + " not found.");
            return;
        }

        email.isRead = true;

        // Remove from pending inbox
        inbox.remove(email);

        System.out.println("Email marked as read: " + id);
    }

    // Recently opened emails
    static void showRecentEmails() {

        if (recentEmails.isEmpty()) {
            System.out.println("No recently opened emails!");
            return;
        }

        System.out.println("\n===== RECENTLY OPENED EMAILS =====");

        for (int i = recentEmails.size() - 1; i >= 0; i--) {
            Email e = recentEmails.get(i);

            System.out.println(
                "Opened: " + e.id + " | " + e.sender
            );
        }
    }

    // Display all emails
    static void displayAllEmails() {

        if (allEmails.isEmpty()) {
            System.out.println("Inbox is empty!");
            return;
        }

        System.out.println("\n===== ALL EMAILS =====");

        for (Email e : allEmails) {
            System.out.println(
                e.id + " | " + e.sender + " | "
                + e.priority + " | "
                + (e.isRead ? "Read" : "Unread")
            );
        }
    }

    // Save emails into inbox.txt
    static void saveEmails() {

        try (PrintWriter writer =
             new PrintWriter(new FileWriter(FILE_NAME))) {

            for (Email e : allEmails) {

                writer.println(
                    e.id + "|" + e.sender + "|" +
                    e.subject + "|" + e.priority + "|" +
                    e.date + "|" + e.arrivalTime + "|" +
                    e.isRead
                );
            }

            System.out.println("Inbox saved successfully!");

        } catch (IOException e) {
            System.out.println("Error saving inbox: " + e.getMessage());
        }
    }

    // Load emails when program starts
    static void loadEmails() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("New inbox created.");
            return;
        }

        try (BufferedReader reader =
             new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split("\\|", -1);

                if (data.length != 7) continue;

                Email email = new Email(
                    data[0], data[1], data[2],
                    data[3], data[4],
                    Integer.parseInt(data[5]),
                    Boolean.parseBoolean(data[6])
                );

                allEmails.add(email);
                emailMap.put(email.id, email);

                if (!email.isRead) {
                    inbox.add(email);
                }

                nextId = Math.max(
                    nextId,
                    Integer.parseInt(email.id.substring(2)) + 1
                );

                arrivalCounter = Math.max(
                    arrivalCounter, email.arrivalTime + 1
                );
            }

            System.out.println("Inbox loaded successfully!");

        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading inbox: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        loadEmails();

        while (true) {

            System.out.println("\n===== EMAIL INBOX MANAGER =====");
            System.out.println("1. Receive Email");
            System.out.println("2. Open Next Email");
            System.out.println("3. Search Email");
            System.out.println("4. Sort By Sender");
            System.out.println("5. Sort By Date");
            System.out.println("6. Mark Email As Read");
            System.out.println("7. Recently Opened Emails");
            System.out.println("8. Display All Emails");
            System.out.println("0. Save and Exit");

            System.out.print("Enter Your Choice: ");

            String input = sc.nextLine();

            switch (input) {

                case "1":
                    receiveEmail();
                    break;

                case "2":
                    openNextEmail();
                    break;

                case "3":
                    searchEmail();
                    break;

                case "4":
                    sortBySender();
                    break;

                case "5":
                    sortByDate();
                    break;

                case "6":
                    markAsRead();
                    break;

                case "7":
                    showRecentEmails();
                    break;

                case "8":
                    displayAllEmails();
                    break;

                case "0":
                    saveEmails();
                    System.out.println("Thank you for using Email Inbox Manager!");
                    return;

                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }
}
