// import java.io.*;
// import java.nio.ByteBuffer;
// import java.util.Date;

// import static Constants.*;

// public class dbquery {

// private String text;
// private int pageSize;
// private File file;

// public static void main(String[] args) {
// dbquery query = new dbquery();
// query.validateInputs(args);
// query.search();
// }

// /**
// * Validates the command line arguments with text and page size
// *
// * @param args The command line arguments
// */
// private void validateInputs(String[] args) {
// if (args.length != 2) {
// displayUsageMessage();
// }

// String pSize = args[1];
// text = args[0];

// try {
// pageSize = Integer.parseInt(pSize);
// } catch (NumberFormatException e) {
// System.err.println("Enter numeric value for the page size");
// displayUsageMessage();
// }
// file = new File("heap." + pageSize);
// if (!file.exists() || !file.isFile()) {
// System.err.println("The file heap." + pageSize + " does not exist");
// displayUsageMessage();
// }
// }

// private void displayUsageMessage() {
// System.err.println("usage: dbquery [<text>] [<page_size>]");
// System.exit(1);
// }

// /**
// * This method reads an array of bytes from the heap file corresponding to the
// * page size, hence it reads one page
// * at a time. This will continue until all pages are searched.
// */
// private void search() {
// long startTime = System.currentTimeMillis();

// try (FileInputStream fileInputStream = new FileInputStream(file)) {
// int bytesRead = 0;
// while (bytesRead != -1) {
// byte[] page = new byte[pageSize];
// bytesRead = fileInputStream.read(page);
// if (bytesRead != -1) {
// searchPage(page);
// }
// }
// } catch (Exception e) {
// }

// long endTime = System.currentTimeMillis();
// long timeInMilliseconds = endTime - startTime;
// System.out.println("Time taken to search (milliseconds): " +
// timeInMilliseconds);
// System.out.println("Time taken to search (seconds): " + (timeInMilliseconds /
// 1000));
// }

// /**
// * This method reads records from the page and then fields from the records,
// if
// * the first field DA_NAME is equal
// * to the search text then it print out the full contents of that record. This
// * will go on until all records in the
// * page are searched.
// */
// private void searchPage(byte[] page) {
// // Declare variables for each field in the csv
// byte[] bpersonName = new byte[PERSON_NAME_SIZE];
// byte[] bbirthDate = new byte[BIRTH_DATE_SIZE];
// byte[] bbirthPlace = new byte[BIRTH_PLACE_SIZE];
// byte[] bdeathDate = new byte[DEATH_DATE_SIZE];
// byte[] bfieldLabel = new byte[FIELD_SIZE];
// byte[] bgenreLabel = new byte[GENRE_SIZE];
// byte[] binstrument = new byte[INSTRUMENT_SIZE];
// byte[] bnationality = new byte[NATIONALITY_SIZE];
// byte[] bthumbnail = new byte[THUMBNAIL_SIZE];
// byte[] bwikiPageID = new byte[WIKIPAGE_ID_SIZE];
// byte[] bdescription = new byte[DESCRIPTION_SIZE];

// byte[][] byteArrays = { bpersonName, bbirthDate, bbirthPlace, bdeathDate,
// bfieldLabel, bgenreLabel,
// binstrument, bnationality, bthumbnail, bwikiPageID, bdescription };

// ByteArrayInputStream pageInputStream = new ByteArrayInputStream(page);

// int bytesRead;
// while (true) {
// byte[] record = new byte[RECORD_SIZE];
// bytesRead = 0;
// try {
// bytesRead = pageInputStream.read(record);
// } catch (Exception e) {
// }

// if (bytesRead != RECORD_SIZE) {
// break;
// }
// ByteArrayInputStream recordInputStream = new ByteArrayInputStream(record);

// for (byte[] byteArray : byteArrays) {
// try {
// recordInputStream.read(byteArray);
// } catch (Exception e) {
// }
// }

// // After converting to string, need to trim to remove empty spaces that comes
// // after (from the padding)
// String recordID = new String(bpersonName).trim();
// if (recordID.equals(text)) {
// System.out.println("Name: " + new String(bpersonName));
// System.out.println("Birth Date: " + new
// Date(ByteBuffer.wrap(bbirthDate).getLong() * 1000));
// System.out.println("Birth Place " + new String(bbirthPlace));
// System.out.println("Death Date: " + new
// Date(ByteBuffer.wrap(bdeathDate).getLong() * 1000));
// System.out.println("Field Label: " + new String(bfieldLabel));
// System.out.println("Genre Label: " + new String(bgenreLabel));
// System.out.println("Instrument: " + new String(binstrument));
// System.out.println("Nationality: " + new String(bnationality));
// System.out.println("Thumbnail: " + new String(bthumbnail));
// System.out.println("Wiki Page ID: " + new String(bwikiPageID));
// System.out.println("Description: " + new String(bdescription) + "\n");
// }
// }
// }
// }
