package org.jumbune.datavalidation.dsc;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.jumbune.common.beans.dsc.DataSourceCompMapperInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DataSourceCompMapper extends Mapper<LongWritable, Text, Text, DataSourceCompMapValueWritable> {

	private static final String _1 = "1";
	private DataSourceCompMapperInfo mapperInfo;
	private Map<String, String> filesMap;
	private MultipleOutputs<NullWritable, Text> multipleOutputs;

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setup(Mapper.Context context) {
		Gson gson = new Gson();
		Configuration conf = context.getConfiguration();
		Type type = new TypeToken<Map<String, String>>() {
		}.getType();

		mapperInfo = gson.fromJson(conf.get("mapperInfoJson"), DataSourceCompMapperInfo.class);
		filesMap = gson.fromJson(conf.get("filesMap"), type);
		multipleOutputs = new MultipleOutputs<NullWritable, Text>(context);

	}

	public void map(LongWritable key, Text row, Context context) throws IOException, InterruptedException {
		DataSourceCompMapValueWritable mapOutputValue = new DataSourceCompMapValueWritable();
		mapOutputValue.setRow(row);

		FileSplit fileSplit = (FileSplit) context.getInputSplit();
		String filePath = fileSplit.getPath().toUri().getPath();

		mapOutputValue.setFilePath(new Text(filePath));

		String fieldSeparator = mapperInfo.getFieldSeparator();
		
		StringBuffer str = new StringBuffer();
		try {
			if (filePath.contains(mapperInfo.getSourcePath())) {
				String[] fields = getSplits(row.toString(), fieldSeparator);
				if (mapperInfo.getNoOfFieldsInSource() != null) {
					if (fields.length != mapperInfo.getNoOfFieldsInSource()) {
						writeNoOfFieldsViolation(filePath);
						return;
					}
				}
				for (int fieldNumber : mapperInfo.getSourcePrimaryKey()) {
					str.append(fields[fieldNumber - 1]).append(DataSourceCompConstants.PIPE_SEPARATOR);
				}
				str.deleteCharAt(str.length() - 1);

				mapOutputValue.setIsSource(new BooleanWritable(true));
				context.write(new Text(str.toString()), mapOutputValue);

			} else {
				String[] fields = getSplits(row.toString(), fieldSeparator);
				if (mapperInfo.getNoOfFieldsInDestination() != null) {
					if (fields.length != mapperInfo.getNoOfFieldsInDestination()) {
						writeNoOfFieldsViolation(filePath);
						return;
					}
				}
				for (int fieldNumber : mapperInfo.getDestinationPrimaryKey()) {
					str.append(fields[fieldNumber - 1]).append(DataSourceCompConstants.PIPE_SEPARATOR);
				}
				str.deleteCharAt(str.length() - 1);
				mapOutputValue.setIsSource(new BooleanWritable(false));
				context.write(new Text(str.toString()), mapOutputValue);
			}
		} catch (IndexOutOfBoundsException e) {
			writeNoOfFieldsViolation(filePath);
		}

	}

	private void writeNoOfFieldsViolation(String outputPath) throws IOException, InterruptedException {
		writeInvalidRowsViolation(outputPath, DataSourceCompConstants.NO_OF_FIELDS_VIOLATION);
		StringBuilder sb = new StringBuilder(DataSourceCompConstants.NO_OF_FIELDS_VIOLATION)
				.append(DataSourceCompConstants.SLASH).append(filesMap.get(outputPath))
				.append(DataSourceCompConstants.SLASH);
		multipleOutputs.write(NullWritable.get(), new Text(_1), sb.toString());
	}

	private void writeInvalidRowsViolation(String outputPath, String dueTo) throws IOException, InterruptedException {
		StringBuilder sb = new StringBuilder(DataSourceCompConstants.INVALID_ROWS)
				.append(DataSourceCompConstants.SLASH).append(dueTo).append(DataSourceCompConstants.SLASH)
				.append(filesMap.get(outputPath)).append(DataSourceCompConstants.SLASH);
		multipleOutputs.write(NullWritable.get(), new Text(_1), sb.toString());
	}

	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		multipleOutputs.close();
	}
	
	private String[] getSplits(String line, String character) {
		int from = 0, to = 0, charLength = character.length();
		List<String> splits = new ArrayList<>();
		while (true) {
			to = line.indexOf(character, from);
			// if this is the last split
			if (to == -1) {
				splits.add(line.substring(from));
				break;
			}
			// if line ends with character
			if (to == line.length() - 1) {
				splits.add(line.substring(from, to));
				splits.add("");
				break;
			}
			splits.add(line.substring(from, to));
			from = to + charLength;
		}
		return splits.toArray(new String[splits.size()]);
	}

}
