package org.jumbune.datavalidation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * A customized class for file-based {@link InputFormat}s.
 * 
 * <p>
 * <code>DataValidationInputFormat</code> is the customized class for file-based
 * <code>InputFormat</code>s. This provides a generic implementation of
 * {@link #getSplits(JobContext)}.
 * 
 * 
 */
public class DataValidationInputFormat extends
		FileInputFormat<LongWritable, Text> {

	/** The Constant SPLIT_SLOP. */
	private static final double SPLIT_SLOP = 1.1;
	
	/** The record separator. */
	private byte[] recordSeparator;
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager
			.getLogger(DataValidationInputFormat.class);

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.InputFormat#createRecordReader(org.apache.hadoop.mapreduce.InputSplit, org.apache.hadoop.mapreduce.TaskAttemptContext)
	 */
	@Override
	public RecordReader<LongWritable, Text> createRecordReader(
			InputSplit split, TaskAttemptContext context) {
		return new DataValidationRecordReader();
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.lib.input.FileInputFormat#isSplitable(org.apache.hadoop.mapreduce.JobContext, org.apache.hadoop.fs.Path)
	 */
	@Override
	protected boolean isSplitable(JobContext context, Path file) {
		
		return true;
	}

	/**
	 * Generate the list of files and make them into DataValidationFileSplit.
	 *
	 * @param job the job
	 * @return the splits
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public List<InputSplit> getSplits(JobContext job) throws IOException {
		long minSize = Math.max(getFormatMinSplitSize(), getMinSplitSize(job));
		long maxSize = getMaxSplitSize(job);
		// generate splits
		List<InputSplit> splits = new ArrayList<InputSplit>();
		setData(job,minSize,maxSize,splits, listStatus(job));
		LOGGER.debug("Total # of splits: " + splits.size());
		return splits;
	}
	
	/**
	 *  Finds files inside directories recusively and add to  fileStatusList
	 * @param job refers to JobContext that is being used to read the configurations of the job that ran
	 * @param minSize refers to the minimum file block size.
	 * @param maxSize refers to the maximum file block size.
	 * @param splits refers  to a list of splits that are being generated.
	 * @param fileStatusList list of FileStatus
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void setData(JobContext job, long minSize, long maxSize,
			List<InputSplit> splits, List<FileStatus> fileStatusList) throws IOException {
		for(FileStatus file:fileStatusList) {
			if (file.isDir()) {
				Path dirPath = file.getPath();
				FileStatus [] fileArray = dirPath.getFileSystem(job.getConfiguration()).listStatus(dirPath);
				setData(job, minSize, maxSize, splits, Arrays.asList(fileArray));
			} else {
				//Checking whether file is empty or not
				Path path  = file.getPath();
				FileSystem fs = path.getFileSystem(job.getConfiguration());
				ContentSummary cs = fs.getContentSummary(path);
				if (cs.getLength() > 0) {
					generateSplits(job, minSize, maxSize, splits, file);	
				} 
		    }
		}
	}

	/**
	 * Generate splits.
	 *
	 * @param job refers to JobContext that is being used to read the configurations of the job that ran
	 * @param minSize refers to the minimum file block size.
	 * @param maxSize refers to the maximum file block size.
	 * @param splits refers  to a list of splits that are being generated.
	 * @param file refers to the FileStatus required to determine block size,length,allocations.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void generateSplits(JobContext job, long minSize, long maxSize,
			List<InputSplit> splits, FileStatus file) throws IOException {
		Path path = file.getPath();
		int numOfRecordsInCurrentSplit = 0;
		int numOfRecordsInPreviousSplit = 0;
		FileSystem fs = path.getFileSystem(job.getConfiguration());
		long length = file.getLen();
		BlockLocation[] blkLocations = fs.getFileBlockLocations(file, 0,
				length);
		if ((length != 0) && isSplitable(job, path)) {
			long blockSize = file.getBlockSize();
			long splitSize = computeSplitSize(blockSize, minSize, maxSize);
			long bytesRemaining = length;

			// checking the occurrences of the record separator in current
			// split
			recordSeparator = job.getConfiguration()
					.get(DataValidationConstants.RECORD_SEPARATOR)
					.getBytes();
			while (((double) bytesRemaining) / splitSize > SPLIT_SLOP) {
				int blkIndex = getBlockIndex(blkLocations, length
						- bytesRemaining);
				long start = length - bytesRemaining;
				long end = start + splitSize;
				FSDataInputStream fsin = fs.open(path);
				fsin.seek(start);
				long pos = start;
				int b = 0;
				int bufferPos = 0;
				while (true) {
					b = fsin.read();
					pos = fsin.getPos();
					if (b == -1) {
						break;}
					if (b == recordSeparator[bufferPos]) {
						bufferPos++;
						if (bufferPos == recordSeparator.length) {
							numOfRecordsInCurrentSplit++;
							bufferPos = 0;
							if (pos > end) {
								break;
							}
						}
					} else {
						// reset the value of buffer position to zero
						bufferPos = 0;

					}

				}

				splits.add(new DataValidationFileSplit(path, start,
						splitSize, numOfRecordsInPreviousSplit,
						blkLocations[blkIndex].getHosts()));
				bytesRemaining -= splitSize;
				numOfRecordsInPreviousSplit = numOfRecordsInCurrentSplit;
				numOfRecordsInCurrentSplit = 0;
			}

			addSplitIfBytesRemaining(splits, path, numOfRecordsInPreviousSplit,
					length, blkLocations, bytesRemaining);
		} else if (length != 0) {
			splits.add(new DataValidationFileSplit(path, 0, length,
					numOfRecordsInPreviousSplit, blkLocations[0].getHosts()));
		} else {
			splits.add(new DataValidationFileSplit(path, 0, length,
					numOfRecordsInPreviousSplit, new String[0]));
		}
	}

	/**
	 * Adds the split if bytes remaining.
	 *
	 * @param splits refers  to a list of splits that are being generated.
	 * @param path refers to  the file name.
	 * @param numOfRecordsInPreviousSplit refers to the number of records in the last split.
	 * @param length denotes  the number of bytes in the file to process
	 * @param blkLocations refers to the block allocations on HDFS.
	 * @param bytesRemaining refers to the number of bytes that are remaining in the file to be processed.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void addSplitIfBytesRemaining(List<InputSplit> splits, Path path,
			int numOfRecordsInPreviousSplit, long length,
			BlockLocation[] blkLocations, long bytesRemaining)
			throws IOException {
		if (bytesRemaining != 0) {

			splits.add(new DataValidationFileSplit(path, length
					- bytesRemaining, bytesRemaining,
					numOfRecordsInPreviousSplit,
					blkLocations[blkLocations.length - 1].getHosts()));
		}
	}

}
