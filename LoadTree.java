
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

// import static org.rmit.student.tree.Constants.*;

public class LoadTree {

    private String loadOption;
    private int pageSize;
    private int branchingFactor;
    private File file;

    public static void main(String[] args) {
        LoadTree loadTree = new LoadTree();
        // loadTree.validateInputs(args);
        loadTree.validateInputs();
        loadTree.load();
    }

    /**
     * Validates the command line arguments with branching factor and page size
     *
     * @param args The command line arguments
     */
    // private void validateInputs(String[] args) {
    private void validateInputs() {
        // if (args.length != 3) {
        // displayUsageMessage();
        // }
        // loadOption = args[0];
        loadOption = "load";
        // String pSize = args[1];
        String pSize = "4096";
        // String mEntries = args[2];
        String mEntries = "3";
        try {
            pageSize = Integer.parseInt(pSize);
        } catch (NumberFormatException e) {
            System.err.println("Enter numeric value for the page size");
            displayUsageMessage();
        }
        System.out.println("the file is found");
        file = new File("heap." + pageSize);
        if (!file.exists() || !file.isFile()) {
            System.out.println("the file is found");
            System.err.println("The file heap." + pageSize + " does not exist");
            displayUsageMessage();
        }
        try {
            branchingFactor = Integer.parseInt(mEntries);
        } catch (NumberFormatException e) {
            System.err.println("Enter numeric value for the max entries per node, must be greater than 3");
            displayUsageMessage();
        }
        if (branchingFactor < 3) {
            displayUsageMessage();
        }
    }

    /**
     * The records in the heap file are entered one at a time from the
     * root/top of the tree.
     *
     * Pages are read from the file one at a time and read into the b+ tree.
     *
     * The time taken will be displayed once it is finished loading.
     */
    private void load() {
        BPlusTree<Long, byte[]> tree = new BPlusTree(branchingFactor);
        long startTime = System.currentTimeMillis();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int bytesRead = 0;
            while (bytesRead != -1) {
                byte[] page = new byte[pageSize];
                bytesRead = fileInputStream.read(page);
                if (bytesRead != -1) {
                    readPageIntoTree(tree, page);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (loadOption.equals("bulk-load")) {
            tree.resetRoot();
        }

        long endTime = System.currentTimeMillis();
        long timeInMilliseconds = endTime - startTime;
        System.out.println("Time taken to load file into tree (milliseconds): " + timeInMilliseconds);
        System.out.println("Time taken to load file into tree (seconds): " + (timeInMilliseconds / 1000));

        displayOptions(tree);
    }

    private void displayOptions(BPlusTree<Long, byte[]> tree) {
        boolean repeat = true;
        while (repeat) {
            Scanner keyboard = new Scanner(System.in);
            int optionNum;
            System.out.println("Selection option:");
            System.out.println("1. Search");
            System.out.println("2. Range search");
            System.out.println("3. Exit");

            String option = keyboard.nextLine();

            try {
                optionNum = Integer.parseInt(option);
            } catch (Exception e) {
                System.out.println("Invalid input");
                continue;
            }

            switch (optionNum) {
                case 1:
                    search(tree);
                    break;
                case 2:
                    rangeSearch(tree);
                    break;
                case 3:
                    repeat = false;
            }
        }
    }

    /**
     * The Records in each page is read into a byte array and the key to each
     * record is retrieved to be used in the insertion.
     */
    private void readPageIntoTree(BPlusTree<Long, byte[]> tree, byte[] page) {
        ByteArrayInputStream pageInputStream = new ByteArrayInputStream(page);

        int bytesRead;
        while (true) {
            // THE RECORD
            byte[] record = new byte[constants.RECORD_SIZE];
            bytesRead = 0;
            try {
                bytesRead = pageInputStream.read(record);
            } catch (Exception e) {
            }
            // If bytes read does not match record size then all records are done for this
            // page
            if (bytesRead != constants.RECORD_SIZE) {
                break;
            }
            try (ByteArrayInputStream recordInputStream = new ByteArrayInputStream(record)) {
                // Read the bytes into the byte array from the stream
                recordInputStream.read(bpersonName);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // THE KEY
            String recordID = new String(bpersonName).trim();

            if (!recordID.equals("")) {
                long key = Long.parseLong(recordID);
                if (loadOption.equals("load")) {
                    tree.insert(key, record);
                }
                if (loadOption.equals("bulk-load")) {
                    tree.bulkInsert(key, record);
                }
                // System.out.println(recordID + " has been added");
            }
        }
    }

    /**
     * Range search of the B+ Tree
     */
    private void rangeSearch(BPlusTree<Long, byte[]> tree) {
        List<byte[]> rangeResult;
        long startTime;
        Scanner keyboard = new Scanner(System.in);
        System.out.println();

        long keyLowerBound;
        long keyUpperBound;
        try {
            System.out.println("Enter a search key (bottom range): ");
            String searchKeyBot = keyboard.nextLine();
            keyLowerBound = Long.parseLong(searchKeyBot);

            System.out.println("Enter a search key (top range): ");
            String searchKeyTop = keyboard.nextLine();
            keyUpperBound = Long.parseLong(searchKeyTop);
        } catch (Exception e) {
            System.err.println("Invalid search key");
            return;
        }

        System.out.println("Now searching between: " + keyLowerBound + " & " + keyUpperBound);
        startTime = System.currentTimeMillis();
        rangeResult = tree.rangeSearch(keyLowerBound, keyUpperBound);

        for (byte[] result : rangeResult) {
            ByteArrayInputStream recordInputStream = new ByteArrayInputStream(result);
            for (byte[] byteArray : byteArrays) {
                try {
                    recordInputStream.read(byteArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            printRecord();
        }

        long endTime = System.currentTimeMillis();
        long timeInMilliseconds = endTime - startTime;
        System.out.println("Time taken to search tree (milliseconds): " + timeInMilliseconds);
        System.out.println("Time taken to search tree (seconds): " + (timeInMilliseconds / 1000));
    }

    /**
     * Prompts the user for a search key to search the b+ tree.
     *
     * Will display the time taken to retrieve the entry.
     */
    private void search(BPlusTree<Long, byte[]> tree) {
        byte[] result;
        long startTime;
        Scanner keyboard = new Scanner(System.in);
        System.out.println();

        System.out.println("Enter a search key: ");
        String searchKey = keyboard.nextLine();
        long key = Long.parseLong(searchKey);
        System.out.println("Now searching for: " + searchKey);
        startTime = System.currentTimeMillis();
        result = tree.search(key);

        if (result == null) {
            System.err.println("Search key not found, please try again");
            return;
        }

        ByteArrayInputStream recordInputStream = new ByteArrayInputStream(result);
        for (byte[] byteArray : byteArrays) {
            try {
                recordInputStream.read(byteArray);
            } catch (Exception e) {
            }
        }

        printRecord();

        long endTime = System.currentTimeMillis();
        long timeInMilliseconds = endTime - startTime;
        System.out.println("Time taken to range search tree (milliseconds): " + timeInMilliseconds);
        System.out.println("Time taken to range search tree (seconds): " + (timeInMilliseconds / 1000));
    }

    private void displayUsageMessage() {
        System.err.println("usage: LoadTree [<load || bulk-load>] [<page_size>] [<branching_factor> >= 3]");
        System.exit(1);
    }

    // private void printRecord() {
    // System.out.println("Name: " + ByteBuffer.wrap(deviceId).getInt());
    // System.out.println("Arrival time: " + new
    // Date(ByteBuffer.wrap(arrivalTime).getLong() * 1000));
    // System.out.println("Departure time: " + new
    // Date(ByteBuffer.wrap(departureTime).getLong() * 1000));
    // System.out.println("Duration (seconds): " +
    // ByteBuffer.wrap(durationSeconds).getLong());
    // System.out.println("Street marker: " + new String(streetMarker));
    // System.out.println("Parking sign: " + new String(parkingSign));
    // System.out.println("Area: " + new String(area));
    // System.out.println("Street ID: " + ByteBuffer.wrap(streetId).getInt());
    // System.out.println("Street Name: " + new String(streetName));
    // System.out.println("Between street 1: " + new String(betweenStreet1));
    // System.out.println("Between street 2: " + new String(betweenStreet2));
    // System.out.println("Side of street: " +
    // ByteBuffer.wrap(sideOfStreet).getInt());
    // System.out.println("In violation: " + (Boolean)
    // (ByteBuffer.wrap(inViolation).get() != 0) + "\n");
    // System.out.println();
    // }

    private void printRecord() {
        System.out.println("Name: " + new String(bpersonName));
        System.out.println("Birth Date: " + new String(bbirthDate));
        // System.out.println("Birth Date: " + new
        // Date(ByteBuffer.wrap(bbirthDate).getLong() * 1000));
        System.out.println("Birth Place " + new String(bbirthPlace));
        System.out.println("Death Date: " + new Date(ByteBuffer.wrap(bdeathDate).getLong() * 1000));
        System.out.println("Field Label: " + new String(bfieldLabel));
        System.out.println("Genre Label: " + new String(bgenreLabel));
        System.out.println("Instrument: " + new String(binstrument));
        System.out.println("Nationality: " + new String(bnationality));
        System.out.println("Thumbnail: " + new String(bthumbnail));
        System.out.println("Wiki Page ID: " + new String(bwikiPageID));
        System.out.println("Description: " + new String(bdescription) + "\n");
        System.out.println();
    }

    // Declare variables for each field in the csv
    private byte[] bpersonName = new byte[constants.PERSON_NAME_SIZE];
    private byte[] bbirthDate = new byte[constants.BIRTH_DATE_SIZE];
    private byte[] bbirthPlace = new byte[constants.BIRTH_PLACE_SIZE];
    private byte[] bdeathDate = new byte[constants.DEATH_DATE_SIZE];
    private byte[] bfieldLabel = new byte[constants.FIELD_SIZE];
    private byte[] bgenreLabel = new byte[constants.GENRE_SIZE];
    private byte[] binstrument = new byte[constants.INSTRUMENT_SIZE];
    private byte[] bnationality = new byte[constants.NATIONALITY_SIZE];
    private byte[] bthumbnail = new byte[constants.THUMBNAIL_SIZE];
    private byte[] bwikiPageID = new byte[constants.WIKIPAGE_ID_SIZE];
    private byte[] bdescription = new byte[constants.DESCRIPTION_SIZE];

    private byte[][] byteArrays = { bpersonName, bbirthDate, bbirthPlace, bdeathDate, bfieldLabel, bgenreLabel,
            binstrument, bnationality, bthumbnail, bwikiPageID, bdescription };
}
