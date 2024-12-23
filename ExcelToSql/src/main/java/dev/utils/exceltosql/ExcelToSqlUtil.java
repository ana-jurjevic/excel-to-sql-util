package dev.utils.exceltosql;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.opencsv.CSVReader;

public class ExcelToSqlUtil {
	private static Logger logger = Logger.getLogger(ExcelToSqlUtil.class.getName());

	public static void main(String[] args) {
		List<String> fileNames = Arrays.asList("Amazon", "Apple", "Facebook", "Google", "Netflix");
		for (String fileName : fileNames) {
			createSqlFromExcel(fileName);
		}
	}

	private static void createSqlFromExcel(String fileName) {
		StringBuilder sql = new StringBuilder();
		String baseInsertStatement = "INSERT INTO stock_history (id, \"date\", \"open\", high, low, \"close\", adjusted_close, volume, stock_id) VALUES ";
		sql.append(baseInsertStatement);
		sql.append("\n");
		try (FileOutputStream outputStream = new FileOutputStream(fileName + ".sql");
				FileReader filereader = new FileReader(
						Paths.get(ExcelToSqlUtil.class.getResource("/" + fileName + ".csv").toURI()).toFile());
				CSVReader csvReader = new CSVReader(filereader);) {
			String[] nextRecord = csvReader.readNext();
			nextRecord = csvReader.readNext(); // skip header
			while (nextRecord != null) {
				sql.append(handleData(nextRecord, getStockId(fileName)));
				nextRecord = csvReader.readNext();
				if (nextRecord != null) {
					sql.append(",");
					sql.append("\n");
				}
			}
			sql.append(";");
			String content = sql.toString();
			outputStream.write(content.getBytes());
		} catch (IOException | URISyntaxException e) {
			logger.severe(e.getMessage());
		}
	}

	private static int getStockId(String sheetName) {
		return switch (sheetName) {
		case "Apple" -> 1;
		case "Amazon" -> 2;
		case "Facebook" -> 3;
		case "Google" -> 4;
		case "Netflix" -> 5;
		default -> 0;
		};
	}

	private static String handleData(String[] row, int stockId) {
		return "(nextval('stock_history_id_seq'), '" + row[0] + "'," + row[1] + "," + row[2] + "," + row[3] + "," + row[4]
				+ "," + row[5] + "," + row[6] + ", " + stockId + ")";
	}
}