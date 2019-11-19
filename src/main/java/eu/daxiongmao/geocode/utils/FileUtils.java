package eu.daxiongmao.geocode.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class to perform file operations
 * @author guillaume diaz
 * @version 1.0 - 2019/07
 */
public class FileUtils {

	/** File header constant for ZIP files, see https://en.wikipedia.org/wiki/List_of_file_signatures */
	private static final long ZIP_FILE_HEADER = 0x504B0304;

	private FileUtils() {
		// private factory
	}


	/**
	 * To check if a given file path exists and is ZIP file or not
	 * @param geoFile path of the file to analyse
	 * @return boolean - 'true' if the file exists and is ZIP ; 'false' if the file exists but is not ZIP
	 * @throws IOException something went wrong: either the file does not exists or it is empty
	 */
	public static boolean isZipFile(final Path geoFile) throws IOException {
		// arg check
		if (geoFile == null || !Files.isRegularFile(geoFile) || Files.notExists(geoFile)) {
			throw new IllegalArgumentException("You must provide a valid GeoName file in ZIP or TXT format");
		}		

		// Open file and read the header in HEXA
		long fileHeader = 0;
		try (final RandomAccessFile raf = new RandomAccessFile(geoFile.toFile(), "r")) {
			fileHeader = raf.readInt();
		}

		// check if zip
		return fileHeader == ZIP_FILE_HEADER;
	}


}
