package phonebook;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        final PhoneRecord[] phoneRecords = loadRecords("directory.txt", PhoneRecord::parse)
                .toArray(PhoneRecord[]::new);
        final PhoneBook phoneBook = new PhoneBook(phoneRecords);
        final Person[] searchPeople = loadRecords("find.txt", Person::parse)
                .toArray(Person[]::new);
        final long linearSearchTime = linearSearch(phoneBook, searchPeople);
        bubbleSortAndJumpSearch(new PhoneBook(phoneBook), searchPeople, linearSearchTime);
        quickSortAndBinarySearch(new PhoneBook(phoneBook), searchPeople);
        hashTableSearch(new PhoneBook(phoneBook), searchPeople);
    }

    private static void hashTableSearch(PhoneBook phoneBook, Person[] searchPeople) {
        System.out.println("\nStart searching (hash table)...");
        final long startCreate = System.currentTimeMillis();
        final PhoneRecord[] records = phoneBook.getRecords();
        final HashTable<Person, PhoneRecord> hashTable = new HashTable<>(records.length * 2);
        for (PhoneRecord record : records) {
            hashTable.put(record.getPerson(), record);
        }
        final long createTime = System.currentTimeMillis() - startCreate;
        final long startSearch = System.currentTimeMillis();
        final List<PhoneRecord> searchResults = new ArrayList<>(searchPeople.length);
        for (Person searchPerson : searchPeople) {
            final PhoneRecord phoneRecord = hashTable.get(searchPerson);
            if (phoneRecord != null) {
                searchResults.add(phoneRecord);
            }
        }
        final long searchTime = System.currentTimeMillis() - startSearch;
        System.out.printf("Found %d / %d entries. Time taken: %s.%n",
                searchResults.size(), searchPeople.length, formatTime(createTime + searchTime));
        System.out.printf("Creating time: %s.%n", formatTime(createTime));
        System.out.printf("Searching time: %s.%n", formatTime(searchTime));
    }

    private static void quickSortAndBinarySearch(PhoneBook phoneBook, Person[] searchPeople) {
        System.out.println("\nStart searching (quick sort + binary search)...");
        final long startSort = System.currentTimeMillis();
        phoneBook.quickSort();
        final long sortTime = System.currentTimeMillis() - startSort;
        final long startSearch = System.currentTimeMillis();
        final List<PhoneRecord> searchResults = phoneBook.binarySearch(searchPeople);
        final long searchTime = System.currentTimeMillis() - startSearch;
        System.out.printf("Found %d / %d entries. Time taken: %s.%n",
                searchResults.size(), searchPeople.length, formatTime(sortTime + searchTime));
        System.out.printf("Sorting time: %s.%n", formatTime(sortTime));
        System.out.printf("Searching time: %s.%n", formatTime(searchTime));
    }

    private static void bubbleSortAndJumpSearch(PhoneBook phoneBook, Person[] searchPeople, long searchTime) {
        System.out.println("\nStart searching (bubble sort + jump search)...");
        final long startSort = System.currentTimeMillis();
        final boolean sortComplete = phoneBook.bubbleSortWithTimeout(10 * searchTime);
        final long sortTime = System.currentTimeMillis() - startSort;
        final long secondStartSearch = System.currentTimeMillis();
        final List<PhoneRecord> secondSearchResults = sortComplete ? phoneBook.jumpSearch(searchPeople) :
                phoneBook.linearSearch(searchPeople);
        final long secondSearchTime = System.currentTimeMillis() - secondStartSearch;
        System.out.printf("Found %d / %d entries. Time taken: %s.%n",
                secondSearchResults.size(), searchPeople.length, formatTime(sortTime + secondSearchTime));
        System.out.printf("Sorting time: %s.%s%n", formatTime(sortTime),
                sortComplete ? "" : " - STOPPED, moved to linear search");
        System.out.printf("Searching time: %s.%n", formatTime(secondSearchTime));
    }

    private static long linearSearch(PhoneBook phoneBook, Person[] searchPeople) {
        System.out.println("Start searching (linear search)...");
        final long startSearch = System.currentTimeMillis();
        final List<PhoneRecord> searchResults = phoneBook.linearSearch(searchPeople);
        final long searchTime = System.currentTimeMillis() - startSearch;
        System.out.printf("Found %d / %d entries. Time taken: %s.%n",
                searchResults.size(), searchPeople.length, formatTime(searchTime));
        return searchTime;
    }

    private static <T> List<T> loadRecords(String fileName, Function<String, T> parser) {
        final List<T> records = new ArrayList<>();
        final File file = Path.of(System.getProperty("user.home"), fileName).toFile();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                records.add(parser.apply(scanner.nextLine()));
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File " + file.getAbsolutePath() + " not found", e);
        }
        return records;
    }

    private static String formatTime(long time) {
        final long minutes = TimeUnit.MINUTES.convert(time, TimeUnit.MILLISECONDS);
        final long seconds = TimeUnit.SECONDS.convert(time, TimeUnit.MILLISECONDS) % 60;
        final long milliseconds = time % 1000;
        return String.format("%d min. %d sec. % d ms", minutes, seconds, milliseconds);
    }

}

