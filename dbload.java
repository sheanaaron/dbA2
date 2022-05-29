// import java.io.BufferedReader;
// import java.io.ByteArrayOutputStream;
// import java.io.DataOutputStream;
// import java.io.FileNotFoundException;
// import java.io.FileOutputStream;
// import java.io.FileReader;
// import java.io.IOException;
// import java.text.ParseException;
// import java.text.SimpleDateFormat;
// import java.util.Date;

// public class dbload {
//     /*
//      * Loads data from an input CSV into fixed-length records. Record fields are:
//      * personname = 70 bytes
//      * birthdate = 8 bytes
//      * birthPlace_label = 354 bytes
//      * deathDate 8 bytes
//      * field_label 242 bytes
//      * genre_label 386 bytes
//      * instrument_abel 541 bytes
//      * nationality_label 119 bytes
//      * thumbnail 292 bytes
//      * wikiPageID 4 bytes
//      * description 466 bytes
//      *
//      * Outputs a binary file called heap.pagesize
//      */
//     public static void main(String[] args) throws IOException {

//         // check for correct number of arguments
//         if (args.length != constants.DBLOAD_ARG_COUNT) {
//             System.out.println("Error: Incorrect number of arguments were input");
//             return;
//         }

//         int pageSize = Integer.parseInt(args[constants.DBLOAD_PAGE_SIZE_ARG]);
//         String datafile = args[constants.DATAFILE_ARG];
//         String outputFileName = "heap." + pageSize;
//         int numRecordsLoaded = 0;
//         int numberOfPagesUsed = 0;
//         long startTime = 0;
//         long finishTime = 0;
//         boolean exceptionOccurred = false;
//         final int numBytesFixedLengthRecord = constants.TOTAL_SIZE;
//         int numRecordsPerPage = pageSize/numBytesFixedLengthRecord;
//         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

//         BufferedReader reader = null;
//         FileOutputStream outputStream = null;
//         ByteArrayOutputStream byteOutputStream = null;
//         DataOutputStream dataOutput = null;

//         try {
//             reader = new BufferedReader(new FileReader(datafile));
//             outputStream = new FileOutputStream(outputFileName);
//             byteOutputStream = new ByteArrayOutputStream();
//             dataOutput = new DataOutputStream(byteOutputStream);

//             startTime = System.nanoTime();

//             // read in the header line (not processed further, as datafile fieldnames are known)
//             String line = reader.readLine();

//             // read in lines while not the end of file
//             while ((line = reader.readLine()) != null) {

//                 String[] valuesAsStrings = line.split(",");

//                 // Convert data into relevant data types
//                 String name = valuesAsStrings[constants.PERSON_NAME_POS];
//                 String birthDateStr = valuesAsStrings[constants.BIRTH_DATE_POS];
//                 String birthPlace = valuesAsStrings[constants.BIRTH_PLACE_POS];
//                 String deathDateStr = valuesAsStrings[constants.DEATH_DATE_POS];
//                 String field = valuesAsStrings[constants.FIELD_POS];
//                 String genre = valuesAsStrings[constants.GENRE_POS];
//                 String instrument = valuesAsStrings[constants.INSTRUMENT_POS];
//                 String nationality = valuesAsStrings[constants.NATIONALITY_POS];
//                 String thumbnail = valuesAsStrings[constants.THUMBNAIL_POS];
//                 int wikipageId = Integer.parseInt(valuesAsStrings[constants.WIKIPAGE_ID_POS]);
//                 String description = valuesAsStrings[constants.DESCRIPTION_POS];

//                 // parse datetime field into a date object, then get long datatype representation
//                 long birthDateLong = 0;
//                 if (!birthDateStr.equals("NULL")) {
//                   Date birthDate = dateFormat.parse(birthDateStr);
//                   birthDateLong = birthDate.getTime();
//                 }
//                 long deathDateLong = 0;
//                 if (!deathDateStr.equals("NULL")) {
//                   Date deathDate = dateFormat.parse(deathDateStr);
//                   deathDateLong = deathDate.getTime();
//                 }

//                 // Write bytes to data output stream
//                 dataOutput.writeBytes(getStringOfLength(name, constants.PERSON_NAME_SIZE));
//                 dataOutput.writeLong(birthDateLong);
//                 dataOutput.writeBytes(getStringOfLength(birthPlace, constants.BIRTH_PLACE_SIZE));
//                 dataOutput.writeLong(deathDateLong);
//                 dataOutput.writeBytes(getStringOfLength(field, constants.FIELD_SIZE));
//                 dataOutput.writeBytes(getStringOfLength(genre, constants.GENRE_SIZE));
//                 dataOutput.writeBytes(getStringOfLength(instrument, constants.INSTRUMENT_SIZE));
//                 dataOutput.writeBytes(getStringOfLength(nationality, constants.NATIONALITY_SIZE));
//                 dataOutput.writeBytes(getStringOfLength(thumbnail, constants.THUMBNAIL_SIZE));
//                 dataOutput.writeInt(wikipageId);
//                 dataOutput.writeBytes(getStringOfLength(description, constants.DESCRIPTION_SIZE));

//                 numRecordsLoaded++;
//                 // check if a new page is needed
//                 if (numRecordsLoaded % numRecordsPerPage == 0) {
//                     dataOutput.flush();
//                     // Get the byte array of loaded records, copy to an empty page and writeout
//                     byte[] page = new byte[pageSize];
//                     byte[] records = byteOutputStream.toByteArray();
//                     int numberBytesToCopy = byteOutputStream.size();
//                     System.arraycopy(records, 0, page, 0, numberBytesToCopy);
//                     writeOut(outputStream, page);
//                     numberOfPagesUsed++;
//                     byteOutputStream.reset();
//                 }
//             }

//             // At end of csv, check if there are records in the current page to be written out
//             if (numRecordsLoaded % numRecordsPerPage != 0) {
//                 dataOutput.flush();
//                 byte[] page = new byte[pageSize];
//                 byte[] records = byteOutputStream.toByteArray();
//                 int numberBytesToCopy = byteOutputStream.size();
//                 System.arraycopy(records, 0, page, 0, numberBytesToCopy);
//                 writeOut(outputStream, page);
//                 numberOfPagesUsed++;
//                 byteOutputStream.reset();
//             }

//             finishTime = System.nanoTime();
//         }
//         catch (FileNotFoundException e) {
//             System.err.println("Error: File not present " + e.getMessage());
//             exceptionOccurred = true;
//         }
//         catch (IOException e) {
//             System.err.println("Error: IOExeption " + e.getMessage());
//             exceptionOccurred = true;
//         }
//         catch (ParseException e) {
//             System.err.println("Parse error when parsing date: " + e.getMessage());
//         }
//         finally {
//             // close input/output streams
//             if (reader != null) {
//                 reader.close();
//             }
//             if (dataOutput != null) {
//                 dataOutput.close();
//             }
//             if (byteOutputStream != null) {
//                 byteOutputStream.close();
//             }
//             if (outputStream != null) {
//                 outputStream.close();
//             }
//         }

//         // print out stats if all operations succeeded
//         if (exceptionOccurred == false) {

//             System.out.println("The number of records loaded: " + numRecordsLoaded);
//             System.out.println("The number of pages used: " + numberOfPagesUsed);
//             long timeInMilliseconds = (finishTime - startTime)/constants.MILLISECONDS_PER_SECOND;
//             System.out.println("Time taken: " + timeInMilliseconds + " ms");
//         }
//     }

//     // Writes out a byte array to file using a FileOutputStream
//     public static void writeOut(FileOutputStream stream, byte[] byteArray)
//             throws FileNotFoundException, IOException {

//         stream.write(byteArray);
//     }

//     // Returns a whitespace padded string of the same length as parameter int length
//     public static String getStringOfLength(String original, int length) {

//         int lengthDiff = length - original.length();

//         // Check difference in string lengths
//         if (lengthDiff == 0) {
//             return original;
//         }
//         else if (lengthDiff > 0) {
//             // if original string is too short, pad end with whitespace
//             StringBuilder string = new StringBuilder(original);
//             for (int i = 0; i < lengthDiff; i++) {
//                 string.append(" ");
//             }
//             return string.toString();
//         }
//         else {
//             // if original string is too long, shorten to required length
//             return original.substring(0, length);
//         }
//     }
// }

import java.io.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class dbload {

    // Constants
    private File file;
    private static final String SEPARATOR = ",";
    private static final int SHORT_STRING_BYTES = 10;
    private static final int MEDIUM_STRING_BYTES = 30;
    private static final int LONG_STRING_BYTES = 50;

    public static void main(String[] args) {

        int pageSize = 0;
        String datafile = null;
        try {
            pageSize = Integer.parseInt(args[1]);
            datafile = args[2];
        } catch (Exception e) {
            System.out.println("Error! Ex:java dbload -p pagesize datafile");
            System.exit(0);
        }

        heap(pageSize, datafile);

        // Testing out
        // System.out.println("You have requested: " + pageSize + " and " + datafile);

    }

    public static void heap(int pageSize, String datafile) {

        // Declaring all fields name from the csv
        String personName;
        String birthDate;
        String birthPlace_label;
        String deathDate;
        String field_label;
        String genre_label;
        String instrument;
        String nationality;
        String thumbnail;
        String wikiPageID;
        String description;

        // Counting the time taken
        long startTime = System.currentTimeMillis();
        int totalRecords = 0;
        int totalPages = 0;

        ByteArrayOutputStream pageOutputStream = new ByteArrayOutputStream();
        BufferedReader bufferedReader = null;
        DataOutputStream dataOutputStream = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(datafile));
            dataOutputStream = new DataOutputStream(new FileOutputStream("heap." + pageSize));
            String row;
            while ((row = bufferedReader.readLine()) != null) {
                String[] columns = row.split(SEPARATOR);
                if (columns[0] != null) {
                    personName = columns[1];
                    birthDate = columns[23];
                    birthPlace_label = columns[25];
                    deathDate = columns[40];
                    field_label = columns[50];
                    genre_label = columns[53];
                    instrument = columns[63];
                    nationality = columns[74];
                    thumbnail = columns[124];
                    wikiPageID = columns[133];
                    description = columns[137];

                    byte[] bpersonName = Arrays.copyOf(personName.getBytes(), LONG_STRING_BYTES);
                    byte[] bbirthDate = Arrays.copyOf(birthDate.getBytes(), SHORT_STRING_BYTES);
                    byte[] bbirthPlace = Arrays.copyOf(birthPlace_label.getBytes(), MEDIUM_STRING_BYTES);
                    byte[] bdeathDate = Arrays.copyOf(deathDate.getBytes(), SHORT_STRING_BYTES);
                    byte[] bfieldLabel = Arrays.copyOf(field_label.getBytes(), MEDIUM_STRING_BYTES);
                    byte[] bgenreLabel = Arrays.copyOf(genre_label.getBytes(), MEDIUM_STRING_BYTES);
                    byte[] binstrument = Arrays.copyOf(instrument.getBytes(), MEDIUM_STRING_BYTES);
                    byte[] bnationality = Arrays.copyOf(nationality.getBytes(), MEDIUM_STRING_BYTES);
                    byte[] bthumbnail = Arrays.copyOf(thumbnail.getBytes(), MEDIUM_STRING_BYTES);
                    byte[] bwikiPageID = Arrays.copyOf(wikiPageID.getBytes(), MEDIUM_STRING_BYTES);
                    byte[] bdescription = Arrays.copyOf(description.getBytes(), LONG_STRING_BYTES);
                    ByteArrayOutputStream recordOutputStream = new ByteArrayOutputStream();
                    byte[][] byteArrays = { bpersonName, bbirthDate, bbirthPlace, bdeathDate, bfieldLabel, bgenreLabel,
                            binstrument, bnationality, bthumbnail, bwikiPageID, bdescription };
                    for (byte[] byteArray : byteArrays) {
                        recordOutputStream.write(byteArray);
                    }
                    byte[] record = recordOutputStream.toByteArray();

                    if (record.length + pageOutputStream.size() > pageSize) {
                        byte[] page = Arrays.copyOf(pageOutputStream.toByteArray(), pageSize);
                        dataOutputStream.write(page);
                        totalPages++;
                        pageOutputStream.reset();
                    }

                    pageOutputStream.write(record);

                    totalRecords++;
                }
            }
            byte[] page = Arrays.copyOf(pageOutputStream.toByteArray(), pageSize);
            dataOutputStream.write(page);
            totalPages++;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        long timeInMilliseconds = endTime - startTime;
        System.out.println("Time taken to load data (milliseconds): " + timeInMilliseconds);
        System.out.println("Time taken to load data (seconds): " + (timeInMilliseconds / 1000));
        System.out.println("Records added: " + totalRecords);
        System.out.println("Pages added: " + totalPages);
    }

    // For Testing Purposes
    // String line = "";

    // try {
    // BufferedReader br = new BufferedReader(new FileReader(datafile));
    // byte[] buffer = new byte[0];

    // while((line = br.readLine()) != null) {
    // String[] values = line.split(","); //header

    // //System.out.println("Name " + values[1] + " BirthDate " + values[23] + "
    // BirthPlace " + values[26] + " Genre " + values[53]);
    // }
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }

}