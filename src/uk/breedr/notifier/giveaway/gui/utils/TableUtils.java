/**
 * TableUtils.java
 * GiveawayGrowl
 *
 * Created by Ed George on 2 Jan 2015
 *
 */
package uk.breedr.notifier.giveaway.gui.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import uk.breedr.notifier.giveaway.Giveaway;
import uk.breedr.notifier.giveaway.gui.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author edgeorge
 *
 */
public class TableUtils {



	private TableUtils(){}

	public static void setJTableColumnsPercentage(JTable table, int tablePreferredWidth,
			double... percentages) {
		double total = 0;
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			total += percentages[i];
		}

		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth((int)
					(tablePreferredWidth * (percentages[i] / total)));
		}
	}

	public static void setJTableColumnsWidth(JTable table, int... widths) {
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth((int) widths[i]);
		}
	}

	public static void setJTableSingleColumnWidth(JTable table, int column, int width) {
		TableColumn tc = table.getColumnModel().getColumn(column);
		tc.setPreferredWidth(width);
	}

	public static String get_duration(Date date1, Date date2)
	{                   
		TimeUnit timeUnit = TimeUnit.SECONDS;

		long diffInMilli = date2.getTime() - date1.getTime();
		long s = timeUnit.convert(diffInMilli, TimeUnit.MILLISECONDS);

		long days = s / (24 * 60 * 60);
		long rest = s - (days * 24 * 60 * 60);
		long hrs = rest / (60 * 60);
		long rest1 = rest - (hrs * 60 * 60);
		long min = rest1 / 60;      
		long sec = s % 60;

		String dates = "";
		if (days > 0) dates = days + "d ";

		dates += fill2((int) hrs) + "h ";
		dates += fill2((int) min) + "m ";
		dates += fill2((int) sec) + "s ";
		dates += "ago";
		return dates;
	}

	public static String fill2(int value)
	{
		String ret = String.valueOf(value);

		if (ret.length() < 2)
			ret = "0" + ret;            
		return ret;
	}

	/**
	 * @param file
	 * @param giveaways
	 */
	public static boolean export(File file, List<Giveaway> giveaways, Logger logger) {
		if(giveaways != null){
			Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
			String json = gson.toJson(giveaways);
			logger.write("Writing to file: " + file.getAbsolutePath());
			logger.write(json);
			return stringToFile(file, json, logger);
		}else{
			return stringToFile(file, "[]", logger);
		}
	}

	public static boolean stringToFile(File file, String string, Logger logger){
		try
		{
			if (!file.exists()){
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(string);
			bw.close();
			return true;
		}catch(IOException e){
			logger.write(e.getMessage(), Logger.Level.ERROR);
		}
		return false;
	}

}
