import java.io.*;
import java.util.*;

class Book implements Comparable<Book> {
    int id;
    String title, author, category;
    boolean issued;

    Book(int id, String title, String author, String category, boolean issued) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.issued = issued;
    }

    public void display() {
        System.out.println("ID: " + id + " | " + title + " | " + author +
                " | " + category + " | Issued: " + issued);
    }

    public String toFile() {
        return id + "," + title + "," + author + "," + category + "," + issued;
    }

    @Override
    public int compareTo(Book b) {
        return title.compareToIgnoreCase(b.title);
    }
}

class Member {
    int id;
    String name, email;
    List<Integer> issuedBooks = new ArrayList<>();

    Member(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public void display() {
        System.out.println("ID: " + id + " | " + name + " | " + email +
                " | Books: " + issuedBooks);
    }

    public String toFile() {
        return id + "," + name + "," + email + "," +
                String.join(";", issuedBooks.stream().map(String::valueOf).toList());
    }
}

public class City_Library_Digital_Management_System {

    Map<Integer, Book> books = new HashMap<>();
    Map<Integer, Member> members = new HashMap<>();
    Set<String> categories = new HashSet<>();

    Scanner sc = new Scanner(System.in);
    final String BOOK_FILE = "books.txt", MEMBER_FILE = "members.txt";

    public City_Library_Digital_Management_System() {
        load();
    }

    // ------------------ FILE HANDLING ------------------

    void load() {
        try (BufferedReader br = new BufferedReader(new FileReader(BOOK_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] a = line.split(",");
                Book b = new Book(
                        Integer.parseInt(a[0]), a[1], a[2], a[3], Boolean.parseBoolean(a[4])
                );
                books.put(b.id, b);
                categories.add(b.category);
            }
        } catch (Exception ignored) {}

        try (BufferedReader br = new BufferedReader(new FileReader(MEMBER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] a = line.split(",");
                Member m = new Member(Integer.parseInt(a[0]), a[1], a[2]);
                if (a.length > 3 && !a[3].isEmpty())
                    for (String s : a[3].split(";")) m.issuedBooks.add(Integer.parseInt(s));
                members.put(m.id, m);
            }
        } catch (Exception ignored) {}
    }

    void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOK_FILE))) {
            for (Book b : books.values()) bw.write(b.toFile() + "\n");
        } catch (Exception ignored) {}

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MEMBER_FILE))) {
            for (Member m : members.values()) bw.write(m.toFile() + "\n");
        } catch (Exception ignored) {}
    }

    // ------------------ OPERATIONS ------------------

    void addBook() {
        System.out.print("Title: ");
        String t = sc.nextLine();
        System.out.print("Author: ");
        String a = sc.nextLine();
        System.out.print("Category: ");
        String c = sc.nextLine();

        int id = books.size() + 101;
        Book b = new Book(id, t, a, c, false);
        books.put(id, b);
        categories.add(c);

        save();
        System.out.println("Book Added. ID: " + id);
    }

    void addMember() {
        System.out.print("Name: ");
        String n = sc.nextLine();
        System.out.print("Email: ");
        String e = sc.nextLine();

        int id = members.size() + 501;
        members.put(id, new Member(id, n, e));

        save();
        System.out.println("Member Added. ID: " + id);
    }

    void issueBook() {
        System.out.print("Book ID: ");
        int bid = Integer.parseInt(sc.nextLine());
        System.out.print("Member ID: ");
        int mid = Integer.parseInt(sc.nextLine());

        if (!books.containsKey(bid) || !members.containsKey(mid)) return;

        Book b = books.get(bid);
        Member m = members.get(mid);

        if (b.issued) { System.out.println("Already issued."); return; }

        b.issued = true;
        m.issuedBooks.add(bid);

        save();
        System.out.println("Book Issued.");
    }

    void returnBook() {
        System.out.print("Book ID: ");
        int bid = Integer.parseInt(sc.nextLine());
        System.out.print("Member ID: ");
        int mid = Integer.parseInt(sc.nextLine());

        if (!books.containsKey(bid) || !members.containsKey(mid)) return;

        Book b = books.get(bid);
        Member m = members.get(mid);

        if (!b.issued) { System.out.println("Not issued."); return; }

        b.issued = false;
        m.issuedBooks.remove(Integer.valueOf(bid));

        save();
        System.out.println("Book Returned.");
    }

    void searchBooks() {
        System.out.print("Search keyword: ");
        String key = sc.nextLine().toLowerCase();

        books.values().stream()
                .filter(b -> b.title.toLowerCase().contains(key)
                          || b.author.toLowerCase().contains(key)
                          || b.category.toLowerCase().contains(key))
                .forEach(Book::display);
    }

    void sortBooks() {
        List<Book> list = new ArrayList<>(books.values());

        System.out.println("1. By Title\n2. By Author");
        int c = Integer.parseInt(sc.nextLine());

        if (c == 1) Collections.sort(list);
        else list.sort(Comparator.comparing(b -> b.author.toLowerCase()));

        list.forEach(Book::display);
    }

    // ------------------ MENU ------------------

    void menu() {
        while (true) {
            System.out.println("\n---- City Library Digital Management System ----");
            System.out.println("1. Add Book\n2. Add Member\n3. Issue Book\n4. Return Book");
            System.out.println("5. Search Books\n6. Sort Books\n7. Exit");
            System.out.print("Choice: ");

            switch (Integer.parseInt(sc.nextLine())) {
                case 1 -> addBook();
                case 2 -> addMember();
                case 3 -> issueBook();
                case 4 -> returnBook();
                case 5 -> searchBooks();
                case 6 -> sortBooks();
                case 7 -> { save(); return; }
            }
        }
    }

    public static void main(String[] args) {
        new City_Library_Digital_Management_System().menu();
    }
}