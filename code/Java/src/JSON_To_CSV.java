import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSON_To_CSV {

	final static char COMMA_REPLACMENT=';';
	final static char DOUBLE_QUOTES_REPLACMENT=' ';
	final static char SINGLE_QUOTES_REPLACMENT=' ';
	
	final static String INTEGER ="INTEGER";
	final static String LONG ="LONG";

	final static String ATT_NAME ="name";
	final static String ATT_TYPE ="type";
	
	final static String TABLE ="table";
	final static String TABLE_NAME ="name";
	final static String ATTRIBUTES ="attributes";
	final static String DESCRIPTION ="description";
	final static String ID_ATTRIBUTES ="idAttribute";
	final static String TUPLES ="tuples";
	final static String BODY ="body";
	
	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {

		if (args.length != 2) {
			System.out.println("ERROR: (usage) JSON_to_CSV "
					+ "<json-file> <output-as-csv-filename>");
			System.exit(-1);
		}
		String inFileName = args[0];
		String outDirName = args[1];
		File outFolder = new File(outDirName);

		if (!outFolder.exists()) {
			if (outFolder.mkdir()) {
				System.out.println("out directory is created at " + outDirName);
			} else {
				System.exit(-1);
				System.out.println("Failed to create out directory "
						+ outDirName);
			}
		}

		JSONParser parser = new JSONParser();
		try {

			JSONObject jsonData = (JSONObject) parser.parse(new FileReader(
					inFileName));

			JSONObject jsonTable = (JSONObject) jsonData.get(TABLE);
			JSONArray attributeList = (JSONArray) jsonTable.get(ATTRIBUTES);
			String description = (String) jsonTable.get(DESCRIPTION);
			String name = (String) jsonTable.get(TABLE_NAME);
			JSONObject idAttribute = (JSONObject) jsonTable.get(ID_ATTRIBUTES);
			JSONArray tupleSet = (JSONArray) jsonTable.get(TUPLES);

			String outFile = outDirName + name + ".csv";
			System.out.println("Writing to file " + outFile);
			PrintWriter writer = new PrintWriter(outFile);

			String headerLine = getRowOne(attributeList, idAttribute);
			//System.out.println(headerLine);
			writer.println(headerLine);

			for (Object aRow : tupleSet) {
				JSONObject aTuple = (JSONObject) aRow;

				StringBuffer aLine = new StringBuffer();

				String id = "";
				String idAttName = (String) idAttribute.get(ATT_NAME);
				String idAttType = (String) idAttribute.get(ATT_TYPE);

				id = getValue(aTuple, idAttName, idAttType);
				aLine.append(id);
				
				for (Object aAtt : attributeList) {
					JSONObject anAttribute = (JSONObject) aAtt;

					String aValue = "";

					String attName = (String) anAttribute.get(ATT_NAME);
					String attType = (String) anAttribute.get(ATT_TYPE);
					if (attName.compareTo(BODY) == 0) {
						aValue = "body removed";
					} else if (attName.compareTo(idAttName) == 0) {
						// no need to print id again
						continue;
					} else {
						aValue = getValue(aTuple, attName, attType);
					}
					// System.out.println(attName + " --> " + aValue);
					aLine.append("," + aValue);
				}
				String oneRowStr = aLine.toString();
				//System.out.println(oneRowStr);
				writer.println(oneRowStr);
			}
			writer.close();
			System.out.println("parsed!!!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	public static String getRowOne(JSONArray attributeList,
			JSONObject idAttribute) {
		StringBuffer aLineBff = new StringBuffer();

		String idAttName = (String) idAttribute.get("name");
		String idAttType = (String) idAttribute.get("type");

		aLineBff.append(idAttName + ":" + idAttType);

		for (Object aAtt : attributeList) {
			JSONObject anAttribute = (JSONObject) aAtt;
			String attName = (String) anAttribute.get("name");
			String attType = (String) anAttribute.get("type");
			if (attName.compareTo(idAttName) == 0) {
				// no need to print id again
				continue;
			}
			aLineBff.append("," + attName + ":" + attType);
		}
		return aLineBff.toString();
	}

	public static String getValue(JSONObject aTuple, String attName,
			String attType) {
		String aValue = "";
		if (attType.compareTo(INTEGER) == 0 || attType.compareTo(LONG) == 0) {
			Long intVal = (Long) aTuple.get(attName);
			if (intVal == null) {
				aValue = "";
			} else {
				aValue = String.valueOf(intVal);
			}
		} else {
			aValue = (String) aTuple.get(attName);
		}
		if (aValue != null) {
			aValue = aValue.replace(',', COMMA_REPLACMENT);
			aValue = aValue.replace('\"', DOUBLE_QUOTES_REPLACMENT);
			aValue = aValue.replace('\'', SINGLE_QUOTES_REPLACMENT);
		} else {
			aValue = "";
		}
		return aValue;
	}

}
