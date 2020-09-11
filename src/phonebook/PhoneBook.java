package phonebook;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class PhoneBook {
    private final PhoneRecord[] records;

    public PhoneBook(PhoneRecord[] records) {
        this.records = records;
    }

    public PhoneBook(PhoneBook other) {
        this.records = other.records.clone();
    }

    public int indexOfByLinearSearch(Person person) {
        for (int i = 0; i < records.length; i++) {
            if (records[i].getPerson().equals(person)) {
                return i;
            }
        }
        return -1;
    }

    public int indexOfByJumpSearch(Person person) {
        if (records.length == 0) {
            return -1;
        }
        int right = 0;
        if (records[right].getPerson().equals(person)) {
            return right;
        }
        int jumpSize = (int) Math.sqrt(records.length);
        int prevRight = right;
        while (right < records.length - 1) {
            right = Math.min(right + jumpSize, records.length - 1);
            if (records[right].getPerson().compareTo(person) >= 0) {
                break;
            }
            prevRight = right;
        }
        if (right == records.length - 1 && records[right].getPerson().compareTo(person) < 0) {
            return -1;
        }
        for (int i = right; i > prevRight; i--) {
            final int compare = records[i].getPerson().compareTo(person);
            if (compare <= 0) {
                return compare == 0 ? i : -1;
            }
        }
        return -1;
    }

    public List<PhoneRecord> linearSearch(Person[] searchPeople) {
        final List<PhoneRecord> phoneRecords = new ArrayList<>(searchPeople.length);
        for (Person searchPerson : searchPeople) {
            final int i = indexOfByLinearSearch(searchPerson);
            if (i >= 0) {
                phoneRecords.add(records[i]);
            }
        }
        return phoneRecords;
    }

    public List<PhoneRecord> jumpSearch(Person[] searchPeople) {
        final List<PhoneRecord> phoneRecords = new ArrayList<>(searchPeople.length);
        for (Person searchPerson : searchPeople) {
            final int i = indexOfByJumpSearch(searchPerson);
            if (i >= 0) {
                phoneRecords.add(records[i]);
            }
        }
        return phoneRecords;
    }

    public List<PhoneRecord> binarySearch(Person[] searchPeople) {
        final List<PhoneRecord> phoneRecords = new ArrayList<>(searchPeople.length);
        MAIN:
        for (Person searchPerson : searchPeople) {
            int left = 0;
            int right = records.length - 1;
            while (left <= right) {
                int mid = left + (right - left) / 2;
                final int compare = searchPerson.compareTo(records[mid].getPerson());
                if (compare == 0) {
                    phoneRecords.add(records[mid]);
                    continue MAIN;
                } else if (compare < 0) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }
        }
        return phoneRecords;
    }

    public boolean bubbleSortWithTimeout(long timeout) {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<?> future = executor.submit(this::bubbleSort);
        executor.shutdown();
        try {
            future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        } catch (InterruptedException | TimeoutException e) {
            return !future.cancel(true);
        }
        return true;
    }

    private void bubbleSort() {
        for (int i = 0; i < records.length - 1; i++) {
            for (int j = 0; j < records.length - i - 1; j++) {
                if (records[j].getPerson().compareTo(records[j + 1].getPerson()) > 0) {
                    final PhoneRecord temp = records[j];
                    records[j] = records[j + 1];
                    records[j + 1] = temp;
                }
                if (Thread.interrupted()) {
                    return;
                }
            }
        }
    }

    public void quickSort() {
        int left = 0;
        int right = records.length - 1;
        final Deque<Integer> deque = new ArrayDeque<>();
        MAIN:
        while (left < right) {
            int pivotIndex = partition(left, right);
            if (left < pivotIndex - 1) {
                deque.add(pivotIndex + 1);
                deque.add(right);
                right = pivotIndex - 1;
            } else if (pivotIndex + 1 < right) {
                left = pivotIndex + 1;
            } else {
                while (!deque.isEmpty()) {
                    right = deque.removeLast();
                    left = deque.removeLast();
                    if (left < right) {
                        continue MAIN;
                    }
                }
                left = 0;
                right = 0;
            }
        }
    }

    private int partition(int left, int right) {
        int partitionIndex = left;
        PhoneRecord pivot = records[right];
        for (int i = left; i < right; i++) {
            if (records[i].getPerson().compareTo(pivot.getPerson()) <= 0) {
                swap(i, partitionIndex++);
            }
        }
        swap(partitionIndex, right);
        return partitionIndex;
    }

    private void swap(int i, int j) {
        final PhoneRecord temp = records[i];
        records[i] = records[j];
        records[j] = temp;
    }

    @Override
    public String toString() {
        return Arrays.stream(records)
                .map(record -> record.getNumber() + " " + record.getPerson())
                .collect(Collectors.joining("\n"));
    }
}
