package lorenz.lab05;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SingleLineFormatter extends Formatter {

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final int CLS_NAME_LENGTH = 24;
	private static final int MTD_NAME_LENGTH = 16;
	
	private DateFormat dateFormat;
	private int clsNameLength;
	private int mtdNameLength;
	private String messageFormat;

	public SingleLineFormatter() {
		dateFormat = new SimpleDateFormat(DATE_FORMAT);
		clsNameLength = CLS_NAME_LENGTH;
		mtdNameLength = MTD_NAME_LENGTH;
		messageFormat = "%" + DATE_FORMAT.length() + "s %08d %-6s %"
				+ clsNameLength + "s %-" + mtdNameLength + "s %s";
	}

	@Override
	public String format(LogRecord rec) {
		StringWriter w1 = new StringWriter();
		PrintWriter w2 = new PrintWriter(w1);
		w2.printf(messageFormat,
				dateFormat.format(new Date(rec.getMillis())),
				rec.getThreadID(),
				String.valueOf(rec.getLevel()),
				fixClsName(rec.getSourceClassName()),
				fixMtdName(rec.getSourceMethodName()),
				rec.getMessage());
		w2.println();
		w2.close();
		return w1.toString();
	}

	// ===============================
	// Private
	// ===============================

	private String fixClsName(String clsName) {
		int len = clsName.length();
		if (len <= clsNameLength)
			return clsName;
		else {
			return clsName.substring(len - clsNameLength);
		}
	}

	private String fixMtdName(String mtdName) {
		int len = mtdName.length();
		if (len < mtdNameLength)
			return mtdName;
		else
			return mtdName.substring(0, mtdNameLength);
	}
}
