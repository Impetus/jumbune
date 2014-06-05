package org.jumbune.profiling.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.jumbune.profiling.utils.ProfileJMXPropsBean;
import org.junit.*;


import static org.junit.Assert.*;

public class ProfileJMXPropsBeanTest {
	private ProfileJMXPropsBean fixture1;

	private ProfileJMXPropsBean fixture2;

	{
		fixture2 = new ProfileJMXPropsBean();
		fixture2.setBlocksRead("");
		fixture2.setBlocksTotal("");
		fixture2.setBytesWritten("");
		fixture2.setHotties(new ArrayList());
		fixture2.setReadsFromLocalClient("");
		fixture2.setReadsFromRemoteClient("");
		fixture2.setRpcProcessingTimeMaxTime("");
		fixture2.setRpcQueueTimeMaxTime("");
		fixture2.setWriteBlockOpAvgTime("");
	}

	private ProfileJMXPropsBean fixture3;

	{
		fixture3 = new ProfileJMXPropsBean();
		fixture3.setBlocksRead("");
		fixture3.setBlocksTotal("0123456789");
		fixture3.setBytesWritten("");
		fixture3.setHotties(new LinkedList());
		fixture3.setReadsFromLocalClient("0123456789");
		fixture3.setReadsFromRemoteClient("");
		fixture3.setRpcProcessingTimeMaxTime("0123456789");
		fixture3.setRpcQueueTimeMaxTime("0123456789");
		fixture3.setWriteBlockOpAvgTime("0123456789");
	}

	private ProfileJMXPropsBean fixture4;

	{
		fixture4 = new ProfileJMXPropsBean();
		fixture4.setBlocksRead("");
		fixture4.setBlocksTotal("0123456789");
		fixture4.setBytesWritten("");
		fixture4.setHotties(new LinkedList());
		fixture4.setReadsFromLocalClient("0123456789");
		fixture4.setReadsFromRemoteClient("0123456789");
		fixture4.setRpcProcessingTimeMaxTime("0123456789");
		fixture4.setRpcQueueTimeMaxTime("0123456789");
		fixture4.setWriteBlockOpAvgTime("0123456789");
	}

	private ProfileJMXPropsBean fixture5;

	{
		fixture5 = new ProfileJMXPropsBean();
		fixture5.setBlocksRead("");
		fixture5.setBlocksTotal("0123456789");
		fixture5.setBytesWritten("");
		fixture5.setHotties(new LinkedList());
		fixture5.setReadsFromLocalClient("0123456789");
		fixture5.setReadsFromRemoteClient("An��t-1.0.txt");
		fixture5.setRpcProcessingTimeMaxTime("0123456789");
		fixture5.setRpcQueueTimeMaxTime("0123456789");
		fixture5.setWriteBlockOpAvgTime("0123456789");
	}

	private ProfileJMXPropsBean fixture6;

	{
		fixture6 = new ProfileJMXPropsBean();
		fixture6.setBlocksRead("");
		fixture6.setBlocksTotal("0123456789");
		fixture6.setBytesWritten("0123456789");
		fixture6.setHotties(new LinkedList());
		fixture6.setReadsFromLocalClient("0123456789");
		fixture6.setReadsFromRemoteClient("");
		fixture6.setRpcProcessingTimeMaxTime("0123456789");
		fixture6.setRpcQueueTimeMaxTime("0123456789");
		fixture6.setWriteBlockOpAvgTime("0123456789");
	}

	private ProfileJMXPropsBean fixture7;

	{
		fixture7 = new ProfileJMXPropsBean();
		fixture7.setBlocksRead("");
		fixture7.setBlocksTotal("0123456789");
		fixture7.setBytesWritten("0123456789");
		fixture7.setHotties(new LinkedList());
		fixture7.setReadsFromLocalClient("0123456789");
		fixture7.setReadsFromRemoteClient("0123456789");
		fixture7.setRpcProcessingTimeMaxTime("0123456789");
		fixture7.setRpcQueueTimeMaxTime("0123456789");
		fixture7.setWriteBlockOpAvgTime("0123456789");
	}

	private ProfileJMXPropsBean fixture8;

	{
		fixture8 = new ProfileJMXPropsBean();
		fixture8.setBlocksRead("");
		fixture8.setBlocksTotal("0123456789");
		fixture8.setBytesWritten("0123456789");
		fixture8.setHotties(new LinkedList());
		fixture8.setReadsFromLocalClient("0123456789");
		fixture8.setReadsFromRemoteClient("An��t-1.0.txt");
		fixture8.setRpcProcessingTimeMaxTime("0123456789");
		fixture8.setRpcQueueTimeMaxTime("0123456789");
		fixture8.setWriteBlockOpAvgTime("0123456789");
	}

	private ProfileJMXPropsBean fixture9;

	{
		fixture9 = new ProfileJMXPropsBean();
		fixture9.setBlocksRead("");
		fixture9.setBlocksTotal("0123456789");
		fixture9.setBytesWritten("An��t-1.0.txt");
		fixture9.setHotties(new LinkedList());
		fixture9.setReadsFromLocalClient("0123456789");
		fixture9.setReadsFromRemoteClient("");
		fixture9.setRpcProcessingTimeMaxTime("0123456789");
		fixture9.setRpcQueueTimeMaxTime("0123456789");
		fixture9.setWriteBlockOpAvgTime("0123456789");
	}

	private ProfileJMXPropsBean fixture10;

	{
		fixture10 = new ProfileJMXPropsBean();
		fixture10.setBlocksRead("");
		fixture10.setBlocksTotal("0123456789");
		fixture10.setBytesWritten("An��t-1.0.txt");
		fixture10.setHotties(new LinkedList());
		fixture10.setReadsFromLocalClient("0123456789");
		fixture10.setReadsFromRemoteClient("0123456789");
		fixture10.setRpcProcessingTimeMaxTime("0123456789");
		fixture10.setRpcQueueTimeMaxTime("0123456789");
		fixture10.setWriteBlockOpAvgTime("0123456789");
	}

	private ProfileJMXPropsBean fixture11;

	{
		fixture11 = new ProfileJMXPropsBean();
		fixture11.setBlocksRead("");
		fixture11.setBlocksTotal("0123456789");
		fixture11.setBytesWritten("An��t-1.0.txt");
		fixture11.setHotties(new LinkedList());
		fixture11.setReadsFromLocalClient("0123456789");
		fixture11.setReadsFromRemoteClient("An��t-1.0.txt");
		fixture11.setRpcProcessingTimeMaxTime("0123456789");
		fixture11.setRpcQueueTimeMaxTime("0123456789");
		fixture11.setWriteBlockOpAvgTime("0123456789");
	}

	private ProfileJMXPropsBean fixture12;

	{
		fixture12 = new ProfileJMXPropsBean();
		fixture12.setBlocksRead("0123456789");
		fixture12.setBlocksTotal("0123456789");
		fixture12.setBytesWritten("");
		fixture12.setHotties(new LinkedList());
		fixture12.setReadsFromLocalClient("0123456789");
		fixture12.setReadsFromRemoteClient("");
		fixture12.setRpcProcessingTimeMaxTime("0123456789");
		fixture12.setRpcQueueTimeMaxTime("0123456789");
		fixture12.setWriteBlockOpAvgTime("0123456789");
	}

	private ProfileJMXPropsBean fixture13;

	{
		fixture13 = new ProfileJMXPropsBean();
		fixture13.setBlocksRead("0123456789");
		fixture13.setBlocksTotal("0123456789");
		fixture13.setBytesWritten("");
		fixture13.setHotties(new LinkedList());
		fixture13.setReadsFromLocalClient("0123456789");
		fixture13.setReadsFromRemoteClient("0123456789");
		fixture13.setRpcProcessingTimeMaxTime("0123456789");
		fixture13.setRpcQueueTimeMaxTime("0123456789");
		fixture13.setWriteBlockOpAvgTime("0123456789");
	}

	private ProfileJMXPropsBean fixture14;

	{
		fixture14 = new ProfileJMXPropsBean();
		fixture14.setBlocksRead("0123456789");
		fixture14.setBlocksTotal("0123456789");
		fixture14.setBytesWritten("0123456789");
		fixture14.setHotties(new LinkedList());
		fixture14.setReadsFromLocalClient("0123456789");
		fixture14.setReadsFromRemoteClient("");
		fixture14.setRpcProcessingTimeMaxTime("0123456789");
		fixture14.setRpcQueueTimeMaxTime("0123456789");
		fixture14.setWriteBlockOpAvgTime("0123456789");
	}

	private ProfileJMXPropsBean fixture15;

	{
		fixture15 = new ProfileJMXPropsBean();
		fixture15.setBlocksRead("0123456789");
		fixture15.setBlocksTotal("0123456789");
		fixture15.setBytesWritten("0123456789");
		fixture15.setHotties(new LinkedList());
		fixture15.setReadsFromLocalClient("0123456789");
		fixture15.setReadsFromRemoteClient("0123456789");
		fixture15.setRpcProcessingTimeMaxTime("0123456789");
		fixture15.setRpcQueueTimeMaxTime("0123456789");
		fixture15.setWriteBlockOpAvgTime("0123456789");
	}

	private ProfileJMXPropsBean fixture16;

	{
		fixture16 = new ProfileJMXPropsBean();
		fixture16.setBlocksRead("0123456789");
		fixture16.setBlocksTotal("0123456789");
		fixture16.setBytesWritten("An��t-1.0.txt");
		fixture16.setHotties(new LinkedList());
		fixture16.setReadsFromLocalClient("0123456789");
		fixture16.setReadsFromRemoteClient("");
		fixture16.setRpcProcessingTimeMaxTime("0123456789");
		fixture16.setRpcQueueTimeMaxTime("0123456789");
		fixture16.setWriteBlockOpAvgTime("0123456789");
	}

	private ProfileJMXPropsBean fixture17;

	{
		fixture17 = new ProfileJMXPropsBean();
		fixture17.setBlocksRead("0123456789");
		fixture17.setBlocksTotal("An��t-1.0.txt");
		fixture17.setBytesWritten("");
		fixture17.setHotties(new Vector());
		fixture17.setReadsFromLocalClient("An��t-1.0.txt");
		fixture17.setReadsFromRemoteClient("An��t-1.0.txt");
		fixture17.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
		fixture17.setRpcQueueTimeMaxTime("An��t-1.0.txt");
		fixture17.setWriteBlockOpAvgTime("An��t-1.0.txt");
	}

	private ProfileJMXPropsBean fixture18;

	{
		fixture18 = new ProfileJMXPropsBean();
		fixture18.setBlocksRead("0123456789");
		fixture18.setBlocksTotal("An��t-1.0.txt");
		fixture18.setBytesWritten("0123456789");
		fixture18.setHotties(new Vector());
		fixture18.setReadsFromLocalClient("An��t-1.0.txt");
		fixture18.setReadsFromRemoteClient("0123456789");
		fixture18.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
		fixture18.setRpcQueueTimeMaxTime("An��t-1.0.txt");
		fixture18.setWriteBlockOpAvgTime("An��t-1.0.txt");
	}

	private ProfileJMXPropsBean fixture19;

	{
		fixture19 = new ProfileJMXPropsBean();
		fixture19.setBlocksRead("0123456789");
		fixture19.setBlocksTotal("An��t-1.0.txt");
		fixture19.setBytesWritten("0123456789");
		fixture19.setHotties(new Vector());
		fixture19.setReadsFromLocalClient("An��t-1.0.txt");
		fixture19.setReadsFromRemoteClient("An��t-1.0.txt");
		fixture19.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
		fixture19.setRpcQueueTimeMaxTime("An��t-1.0.txt");
		fixture19.setWriteBlockOpAvgTime("An��t-1.0.txt");
	}

	private ProfileJMXPropsBean fixture20;

	{
		fixture20 = new ProfileJMXPropsBean();
		fixture20.setBlocksRead("0123456789");
		fixture20.setBlocksTotal("An��t-1.0.txt");
		fixture20.setBytesWritten("An��t-1.0.txt");
		fixture20.setHotties(new Vector());
		fixture20.setReadsFromLocalClient("An��t-1.0.txt");
		fixture20.setReadsFromRemoteClient("0123456789");
		fixture20.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
		fixture20.setRpcQueueTimeMaxTime("An��t-1.0.txt");
		fixture20.setWriteBlockOpAvgTime("An��t-1.0.txt");
	}

	private ProfileJMXPropsBean fixture21;

	{
		fixture21 = new ProfileJMXPropsBean();
		fixture21.setBlocksRead("0123456789");
		fixture21.setBlocksTotal("An��t-1.0.txt");
		fixture21.setBytesWritten("An��t-1.0.txt");
		fixture21.setHotties(new Vector());
		fixture21.setReadsFromLocalClient("An��t-1.0.txt");
		fixture21.setReadsFromRemoteClient("An��t-1.0.txt");
		fixture21.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
		fixture21.setRpcQueueTimeMaxTime("An��t-1.0.txt");
		fixture21.setWriteBlockOpAvgTime("An��t-1.0.txt");
	}

	private ProfileJMXPropsBean fixture22;

	{
		fixture22 = new ProfileJMXPropsBean();
		fixture22.setBlocksRead("An��t-1.0.txt");
		fixture22.setBlocksTotal("An��t-1.0.txt");
		fixture22.setBytesWritten("");
		fixture22.setHotties(new Vector());
		fixture22.setReadsFromLocalClient("An��t-1.0.txt");
		fixture22.setReadsFromRemoteClient("");
		fixture22.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
		fixture22.setRpcQueueTimeMaxTime("An��t-1.0.txt");
		fixture22.setWriteBlockOpAvgTime("An��t-1.0.txt");
	}

	private ProfileJMXPropsBean fixture23;

	{
		fixture23 = new ProfileJMXPropsBean();
		fixture23.setBlocksRead("An��t-1.0.txt");
		fixture23.setBlocksTotal("An��t-1.0.txt");
		fixture23.setBytesWritten("");
		fixture23.setHotties(new Vector());
		fixture23.setReadsFromLocalClient("An��t-1.0.txt");
		fixture23.setReadsFromRemoteClient("0123456789");
		fixture23.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
		fixture23.setRpcQueueTimeMaxTime("An��t-1.0.txt");
		fixture23.setWriteBlockOpAvgTime("An��t-1.0.txt");
	}

	private ProfileJMXPropsBean fixture24;

	{
		fixture24 = new ProfileJMXPropsBean();
		fixture24.setBlocksRead("An��t-1.0.txt");
		fixture24.setBlocksTotal("An��t-1.0.txt");
		fixture24.setBytesWritten("");
		fixture24.setHotties(new Vector());
		fixture24.setReadsFromLocalClient("An��t-1.0.txt");
		fixture24.setReadsFromRemoteClient("An��t-1.0.txt");
		fixture24.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
		fixture24.setRpcQueueTimeMaxTime("An��t-1.0.txt");
		fixture24.setWriteBlockOpAvgTime("An��t-1.0.txt");
	}

	private ProfileJMXPropsBean fixture25;

	{
		fixture25 = new ProfileJMXPropsBean();
		fixture25.setBlocksRead("An��t-1.0.txt");
		fixture25.setBlocksTotal("An��t-1.0.txt");
		fixture25.setBytesWritten("0123456789");
		fixture25.setHotties(new Vector());
		fixture25.setReadsFromLocalClient("An��t-1.0.txt");
		fixture25.setReadsFromRemoteClient("");
		fixture25.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
		fixture25.setRpcQueueTimeMaxTime("An��t-1.0.txt");
		fixture25.setWriteBlockOpAvgTime("An��t-1.0.txt");
	}

	private ProfileJMXPropsBean fixture26;

	{
		fixture26 = new ProfileJMXPropsBean();
		fixture26.setBlocksRead("An��t-1.0.txt");
		fixture26.setBlocksTotal("An��t-1.0.txt");
		fixture26.setBytesWritten("0123456789");
		fixture26.setHotties(new Vector());
		fixture26.setReadsFromLocalClient("An��t-1.0.txt");
		fixture26.setReadsFromRemoteClient("0123456789");
		fixture26.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
		fixture26.setRpcQueueTimeMaxTime("An��t-1.0.txt");
		fixture26.setWriteBlockOpAvgTime("An��t-1.0.txt");
	}

	private ProfileJMXPropsBean fixture27;

	{
		fixture27 = new ProfileJMXPropsBean();
		fixture27.setBlocksRead("An��t-1.0.txt");
		fixture27.setBlocksTotal("An��t-1.0.txt");
		fixture27.setBytesWritten("0123456789");
		fixture27.setHotties(new Vector());
		fixture27.setReadsFromLocalClient("An��t-1.0.txt");
		fixture27.setReadsFromRemoteClient("An��t-1.0.txt");
		fixture27.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
		fixture27.setRpcQueueTimeMaxTime("An��t-1.0.txt");
		fixture27.setWriteBlockOpAvgTime("An��t-1.0.txt");
	}

	private ProfileJMXPropsBean fixture28;

	{
		fixture28 = new ProfileJMXPropsBean();
		fixture28.setBlocksRead("An��t-1.0.txt");
		fixture28.setBlocksTotal("An��t-1.0.txt");
		fixture28.setBytesWritten("An��t-1.0.txt");
		fixture28.setHotties(new Vector());
		fixture28.setReadsFromLocalClient("An��t-1.0.txt");
		fixture28.setReadsFromRemoteClient("");
		fixture28.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
		fixture28.setRpcQueueTimeMaxTime("An��t-1.0.txt");
		fixture28.setWriteBlockOpAvgTime("An��t-1.0.txt");
	}

	private ProfileJMXPropsBean fixture29;

	{
		fixture29 = new ProfileJMXPropsBean();
		fixture29.setBlocksRead("An��t-1.0.txt");
		fixture29.setBlocksTotal("An��t-1.0.txt");
		fixture29.setBytesWritten("An��t-1.0.txt");
		fixture29.setHotties(new Vector());
		fixture29.setReadsFromLocalClient("An��t-1.0.txt");
		fixture29.setReadsFromRemoteClient("0123456789");
		fixture29.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
		fixture29.setRpcQueueTimeMaxTime("An��t-1.0.txt");
		fixture29.setWriteBlockOpAvgTime("An��t-1.0.txt");
	}

	private ProfileJMXPropsBean fixture30;

	{
		fixture30 = new ProfileJMXPropsBean();
		fixture30.setBlocksRead("An��t-1.0.txt");
		fixture30.setBlocksTotal("An��t-1.0.txt");
		fixture30.setBytesWritten("An��t-1.0.txt");
		fixture30.setHotties(new Vector());
		fixture30.setReadsFromLocalClient("An��t-1.0.txt");
		fixture30.setReadsFromRemoteClient("An��t-1.0.txt");
		fixture30.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
		fixture30.setRpcQueueTimeMaxTime("An��t-1.0.txt");
		fixture30.setWriteBlockOpAvgTime("An��t-1.0.txt");
	}

	public ProfileJMXPropsBean getFixture1() throws Exception {
		return fixture1;
	}

	public ProfileJMXPropsBean getFixture2() throws Exception {
		if (fixture2 == null) {
			fixture2 = new ProfileJMXPropsBean();
			fixture2.setBlocksRead("");
			fixture2.setBlocksTotal("");
			fixture2.setBytesWritten("");
			fixture2.setHotties(new ArrayList());
			fixture2.setReadsFromLocalClient("");
			fixture2.setReadsFromRemoteClient("");
			fixture2.setRpcProcessingTimeMaxTime("");
			fixture2.setRpcQueueTimeMaxTime("");
			fixture2.setWriteBlockOpAvgTime("");
		}
		return fixture2;
	}

	public ProfileJMXPropsBean getFixture3() throws Exception {
		if (fixture3 == null) {
			fixture3 = new ProfileJMXPropsBean();
			fixture3.setBlocksRead("");
			fixture3.setBlocksTotal("0123456789");
			fixture3.setBytesWritten("");
			fixture3.setHotties(new LinkedList());
			fixture3.setReadsFromLocalClient("0123456789");
			fixture3.setReadsFromRemoteClient("");
			fixture3.setRpcProcessingTimeMaxTime("0123456789");
			fixture3.setRpcQueueTimeMaxTime("0123456789");
			fixture3.setWriteBlockOpAvgTime("0123456789");
		}
		return fixture3;
	}

	public ProfileJMXPropsBean getFixture4() throws Exception {
		if (fixture4 == null) {
			fixture4 = new ProfileJMXPropsBean();
			fixture4.setBlocksRead("");
			fixture4.setBlocksTotal("0123456789");
			fixture4.setBytesWritten("");
			fixture4.setHotties(new LinkedList());
			fixture4.setReadsFromLocalClient("0123456789");
			fixture4.setReadsFromRemoteClient("0123456789");
			fixture4.setRpcProcessingTimeMaxTime("0123456789");
			fixture4.setRpcQueueTimeMaxTime("0123456789");
			fixture4.setWriteBlockOpAvgTime("0123456789");
		}
		return fixture4;
	}

	public ProfileJMXPropsBean getFixture5() throws Exception {
		if (fixture5 == null) {
			fixture5 = new ProfileJMXPropsBean();
			fixture5.setBlocksRead("");
			fixture5.setBlocksTotal("0123456789");
			fixture5.setBytesWritten("");
			fixture5.setHotties(new LinkedList());
			fixture5.setReadsFromLocalClient("0123456789");
			fixture5.setReadsFromRemoteClient("An��t-1.0.txt");
			fixture5.setRpcProcessingTimeMaxTime("0123456789");
			fixture5.setRpcQueueTimeMaxTime("0123456789");
			fixture5.setWriteBlockOpAvgTime("0123456789");
		}
		return fixture5;
	}

	public ProfileJMXPropsBean getFixture6() throws Exception {
		if (fixture6 == null) {
			fixture6 = new ProfileJMXPropsBean();
			fixture6.setBlocksRead("");
			fixture6.setBlocksTotal("0123456789");
			fixture6.setBytesWritten("0123456789");
			fixture6.setHotties(new LinkedList());
			fixture6.setReadsFromLocalClient("0123456789");
			fixture6.setReadsFromRemoteClient("");
			fixture6.setRpcProcessingTimeMaxTime("0123456789");
			fixture6.setRpcQueueTimeMaxTime("0123456789");
			fixture6.setWriteBlockOpAvgTime("0123456789");
		}
		return fixture6;
	}

	public ProfileJMXPropsBean getFixture7() throws Exception {
		if (fixture7 == null) {
			fixture7 = new ProfileJMXPropsBean();
			fixture7.setBlocksRead("");
			fixture7.setBlocksTotal("0123456789");
			fixture7.setBytesWritten("0123456789");
			fixture7.setHotties(new LinkedList());
			fixture7.setReadsFromLocalClient("0123456789");
			fixture7.setReadsFromRemoteClient("0123456789");
			fixture7.setRpcProcessingTimeMaxTime("0123456789");
			fixture7.setRpcQueueTimeMaxTime("0123456789");
			fixture7.setWriteBlockOpAvgTime("0123456789");
		}
		return fixture7;
	}

	public ProfileJMXPropsBean getFixture8() throws Exception {
		if (fixture8 == null) {
			fixture8 = new ProfileJMXPropsBean();
			fixture8.setBlocksRead("");
			fixture8.setBlocksTotal("0123456789");
			fixture8.setBytesWritten("0123456789");
			fixture8.setHotties(new LinkedList());
			fixture8.setReadsFromLocalClient("0123456789");
			fixture8.setReadsFromRemoteClient("An��t-1.0.txt");
			fixture8.setRpcProcessingTimeMaxTime("0123456789");
			fixture8.setRpcQueueTimeMaxTime("0123456789");
			fixture8.setWriteBlockOpAvgTime("0123456789");
		}
		return fixture8;
	}

	public ProfileJMXPropsBean getFixture9() throws Exception {
		if (fixture9 == null) {
			fixture9 = new ProfileJMXPropsBean();
			fixture9.setBlocksRead("");
			fixture9.setBlocksTotal("0123456789");
			fixture9.setBytesWritten("An��t-1.0.txt");
			fixture9.setHotties(new LinkedList());
			fixture9.setReadsFromLocalClient("0123456789");
			fixture9.setReadsFromRemoteClient("");
			fixture9.setRpcProcessingTimeMaxTime("0123456789");
			fixture9.setRpcQueueTimeMaxTime("0123456789");
			fixture9.setWriteBlockOpAvgTime("0123456789");
		}
		return fixture9;
	}

	public ProfileJMXPropsBean getFixture10() throws Exception {
		if (fixture10 == null) {
			fixture10 = new ProfileJMXPropsBean();
			fixture10.setBlocksRead("");
			fixture10.setBlocksTotal("0123456789");
			fixture10.setBytesWritten("An��t-1.0.txt");
			fixture10.setHotties(new LinkedList());
			fixture10.setReadsFromLocalClient("0123456789");
			fixture10.setReadsFromRemoteClient("0123456789");
			fixture10.setRpcProcessingTimeMaxTime("0123456789");
			fixture10.setRpcQueueTimeMaxTime("0123456789");
			fixture10.setWriteBlockOpAvgTime("0123456789");
		}
		return fixture10;
	}

	public ProfileJMXPropsBean getFixture11() throws Exception {
		if (fixture11 == null) {
			fixture11 = new ProfileJMXPropsBean();
			fixture11.setBlocksRead("");
			fixture11.setBlocksTotal("0123456789");
			fixture11.setBytesWritten("An��t-1.0.txt");
			fixture11.setHotties(new LinkedList());
			fixture11.setReadsFromLocalClient("0123456789");
			fixture11.setReadsFromRemoteClient("An��t-1.0.txt");
			fixture11.setRpcProcessingTimeMaxTime("0123456789");
			fixture11.setRpcQueueTimeMaxTime("0123456789");
			fixture11.setWriteBlockOpAvgTime("0123456789");
		}
		return fixture11;
	}

	public ProfileJMXPropsBean getFixture12() throws Exception {
		if (fixture12 == null) {
			fixture12 = new ProfileJMXPropsBean();
			fixture12.setBlocksRead("0123456789");
			fixture12.setBlocksTotal("0123456789");
			fixture12.setBytesWritten("");
			fixture12.setHotties(new LinkedList());
			fixture12.setReadsFromLocalClient("0123456789");
			fixture12.setReadsFromRemoteClient("");
			fixture12.setRpcProcessingTimeMaxTime("0123456789");
			fixture12.setRpcQueueTimeMaxTime("0123456789");
			fixture12.setWriteBlockOpAvgTime("0123456789");
		}
		return fixture12;
	}

	public ProfileJMXPropsBean getFixture13() throws Exception {
		if (fixture13 == null) {
			fixture13 = new ProfileJMXPropsBean();
			fixture13.setBlocksRead("0123456789");
			fixture13.setBlocksTotal("0123456789");
			fixture13.setBytesWritten("");
			fixture13.setHotties(new LinkedList());
			fixture13.setReadsFromLocalClient("0123456789");
			fixture13.setReadsFromRemoteClient("0123456789");
			fixture13.setRpcProcessingTimeMaxTime("0123456789");
			fixture13.setRpcQueueTimeMaxTime("0123456789");
			fixture13.setWriteBlockOpAvgTime("0123456789");
		}
		return fixture13;
	}

	public ProfileJMXPropsBean getFixture14() throws Exception {
		if (fixture14 == null) {
			fixture14 = new ProfileJMXPropsBean();
			fixture14.setBlocksRead("0123456789");
			fixture14.setBlocksTotal("0123456789");
			fixture14.setBytesWritten("0123456789");
			fixture14.setHotties(new LinkedList());
			fixture14.setReadsFromLocalClient("0123456789");
			fixture14.setReadsFromRemoteClient("");
			fixture14.setRpcProcessingTimeMaxTime("0123456789");
			fixture14.setRpcQueueTimeMaxTime("0123456789");
			fixture14.setWriteBlockOpAvgTime("0123456789");
		}
		return fixture14;
	}

	public ProfileJMXPropsBean getFixture15() throws Exception {
		if (fixture15 == null) {
			fixture15 = new ProfileJMXPropsBean();
			fixture15.setBlocksRead("0123456789");
			fixture15.setBlocksTotal("0123456789");
			fixture15.setBytesWritten("0123456789");
			fixture15.setHotties(new LinkedList());
			fixture15.setReadsFromLocalClient("0123456789");
			fixture15.setReadsFromRemoteClient("0123456789");
			fixture15.setRpcProcessingTimeMaxTime("0123456789");
			fixture15.setRpcQueueTimeMaxTime("0123456789");
			fixture15.setWriteBlockOpAvgTime("0123456789");
		}
		return fixture15;
	}

	public ProfileJMXPropsBean getFixture16() throws Exception {
		if (fixture16 == null) {
			fixture16 = new ProfileJMXPropsBean();
			fixture16.setBlocksRead("0123456789");
			fixture16.setBlocksTotal("0123456789");
			fixture16.setBytesWritten("An��t-1.0.txt");
			fixture16.setHotties(new LinkedList());
			fixture16.setReadsFromLocalClient("0123456789");
			fixture16.setReadsFromRemoteClient("");
			fixture16.setRpcProcessingTimeMaxTime("0123456789");
			fixture16.setRpcQueueTimeMaxTime("0123456789");
			fixture16.setWriteBlockOpAvgTime("0123456789");
		}
		return fixture16;
	}

	public ProfileJMXPropsBean getFixture17() throws Exception {
		if (fixture17 == null) {
			fixture17 = new ProfileJMXPropsBean();
			fixture17.setBlocksRead("0123456789");
			fixture17.setBlocksTotal("An��t-1.0.txt");
			fixture17.setBytesWritten("");
			fixture17.setHotties(new Vector());
			fixture17.setReadsFromLocalClient("An��t-1.0.txt");
			fixture17.setReadsFromRemoteClient("An��t-1.0.txt");
			fixture17.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
			fixture17.setRpcQueueTimeMaxTime("An��t-1.0.txt");
			fixture17.setWriteBlockOpAvgTime("An��t-1.0.txt");
		}
		return fixture17;
	}

	public ProfileJMXPropsBean getFixture18() throws Exception {
		if (fixture18 == null) {
			fixture18 = new ProfileJMXPropsBean();
			fixture18.setBlocksRead("0123456789");
			fixture18.setBlocksTotal("An��t-1.0.txt");
			fixture18.setBytesWritten("0123456789");
			fixture18.setHotties(new Vector());
			fixture18.setReadsFromLocalClient("An��t-1.0.txt");
			fixture18.setReadsFromRemoteClient("0123456789");
			fixture18.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
			fixture18.setRpcQueueTimeMaxTime("An��t-1.0.txt");
			fixture18.setWriteBlockOpAvgTime("An��t-1.0.txt");
		}
		return fixture18;
	}

	public ProfileJMXPropsBean getFixture19() throws Exception {
		if (fixture19 == null) {
			fixture19 = new ProfileJMXPropsBean();
			fixture19.setBlocksRead("0123456789");
			fixture19.setBlocksTotal("An��t-1.0.txt");
			fixture19.setBytesWritten("0123456789");
			fixture19.setHotties(new Vector());
			fixture19.setReadsFromLocalClient("An��t-1.0.txt");
			fixture19.setReadsFromRemoteClient("An��t-1.0.txt");
			fixture19.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
			fixture19.setRpcQueueTimeMaxTime("An��t-1.0.txt");
			fixture19.setWriteBlockOpAvgTime("An��t-1.0.txt");
		}
		return fixture19;
	}

	public ProfileJMXPropsBean getFixture20() throws Exception {
		if (fixture20 == null) {
			fixture20 = new ProfileJMXPropsBean();
			fixture20.setBlocksRead("0123456789");
			fixture20.setBlocksTotal("An��t-1.0.txt");
			fixture20.setBytesWritten("An��t-1.0.txt");
			fixture20.setHotties(new Vector());
			fixture20.setReadsFromLocalClient("An��t-1.0.txt");
			fixture20.setReadsFromRemoteClient("0123456789");
			fixture20.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
			fixture20.setRpcQueueTimeMaxTime("An��t-1.0.txt");
			fixture20.setWriteBlockOpAvgTime("An��t-1.0.txt");
		}
		return fixture20;
	}

	public ProfileJMXPropsBean getFixture21() throws Exception {
		if (fixture21 == null) {
			fixture21 = new ProfileJMXPropsBean();
			fixture21.setBlocksRead("0123456789");
			fixture21.setBlocksTotal("An��t-1.0.txt");
			fixture21.setBytesWritten("An��t-1.0.txt");
			fixture21.setHotties(new Vector());
			fixture21.setReadsFromLocalClient("An��t-1.0.txt");
			fixture21.setReadsFromRemoteClient("An��t-1.0.txt");
			fixture21.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
			fixture21.setRpcQueueTimeMaxTime("An��t-1.0.txt");
			fixture21.setWriteBlockOpAvgTime("An��t-1.0.txt");
		}
		return fixture21;
	}

	public ProfileJMXPropsBean getFixture22() throws Exception {
		if (fixture22 == null) {
			fixture22 = new ProfileJMXPropsBean();
			fixture22.setBlocksRead("An��t-1.0.txt");
			fixture22.setBlocksTotal("An��t-1.0.txt");
			fixture22.setBytesWritten("");
			fixture22.setHotties(new Vector());
			fixture22.setReadsFromLocalClient("An��t-1.0.txt");
			fixture22.setReadsFromRemoteClient("");
			fixture22.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
			fixture22.setRpcQueueTimeMaxTime("An��t-1.0.txt");
			fixture22.setWriteBlockOpAvgTime("An��t-1.0.txt");
		}
		return fixture22;
	}

	public ProfileJMXPropsBean getFixture23() throws Exception {
		if (fixture23 == null) {
			fixture23 = new ProfileJMXPropsBean();
			fixture23.setBlocksRead("An��t-1.0.txt");
			fixture23.setBlocksTotal("An��t-1.0.txt");
			fixture23.setBytesWritten("");
			fixture23.setHotties(new Vector());
			fixture23.setReadsFromLocalClient("An��t-1.0.txt");
			fixture23.setReadsFromRemoteClient("0123456789");
			fixture23.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
			fixture23.setRpcQueueTimeMaxTime("An��t-1.0.txt");
			fixture23.setWriteBlockOpAvgTime("An��t-1.0.txt");
		}
		return fixture23;
	}

	public ProfileJMXPropsBean getFixture24() throws Exception {
		if (fixture24 == null) {
			fixture24 = new ProfileJMXPropsBean();
			fixture24.setBlocksRead("An��t-1.0.txt");
			fixture24.setBlocksTotal("An��t-1.0.txt");
			fixture24.setBytesWritten("");
			fixture24.setHotties(new Vector());
			fixture24.setReadsFromLocalClient("An��t-1.0.txt");
			fixture24.setReadsFromRemoteClient("An��t-1.0.txt");
			fixture24.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
			fixture24.setRpcQueueTimeMaxTime("An��t-1.0.txt");
			fixture24.setWriteBlockOpAvgTime("An��t-1.0.txt");
		}
		return fixture24;
	}

	public ProfileJMXPropsBean getFixture25() throws Exception {
		if (fixture25 == null) {
			fixture25 = new ProfileJMXPropsBean();
			fixture25.setBlocksRead("An��t-1.0.txt");
			fixture25.setBlocksTotal("An��t-1.0.txt");
			fixture25.setBytesWritten("0123456789");
			fixture25.setHotties(new Vector());
			fixture25.setReadsFromLocalClient("An��t-1.0.txt");
			fixture25.setReadsFromRemoteClient("");
			fixture25.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
			fixture25.setRpcQueueTimeMaxTime("An��t-1.0.txt");
			fixture25.setWriteBlockOpAvgTime("An��t-1.0.txt");
		}
		return fixture25;
	}

	public ProfileJMXPropsBean getFixture26() throws Exception {
		if (fixture26 == null) {
			fixture26 = new ProfileJMXPropsBean();
			fixture26.setBlocksRead("An��t-1.0.txt");
			fixture26.setBlocksTotal("An��t-1.0.txt");
			fixture26.setBytesWritten("0123456789");
			fixture26.setHotties(new Vector());
			fixture26.setReadsFromLocalClient("An��t-1.0.txt");
			fixture26.setReadsFromRemoteClient("0123456789");
			fixture26.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
			fixture26.setRpcQueueTimeMaxTime("An��t-1.0.txt");
			fixture26.setWriteBlockOpAvgTime("An��t-1.0.txt");
		}
		return fixture26;
	}

	public ProfileJMXPropsBean getFixture27() throws Exception {
		if (fixture27 == null) {
			fixture27 = new ProfileJMXPropsBean();
			fixture27.setBlocksRead("An��t-1.0.txt");
			fixture27.setBlocksTotal("An��t-1.0.txt");
			fixture27.setBytesWritten("0123456789");
			fixture27.setHotties(new Vector());
			fixture27.setReadsFromLocalClient("An��t-1.0.txt");
			fixture27.setReadsFromRemoteClient("An��t-1.0.txt");
			fixture27.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
			fixture27.setRpcQueueTimeMaxTime("An��t-1.0.txt");
			fixture27.setWriteBlockOpAvgTime("An��t-1.0.txt");
		}
		return fixture27;
	}

	public ProfileJMXPropsBean getFixture28() throws Exception {
		if (fixture28 == null) {
			fixture28 = new ProfileJMXPropsBean();
			fixture28.setBlocksRead("An��t-1.0.txt");
			fixture28.setBlocksTotal("An��t-1.0.txt");
			fixture28.setBytesWritten("An��t-1.0.txt");
			fixture28.setHotties(new Vector());
			fixture28.setReadsFromLocalClient("An��t-1.0.txt");
			fixture28.setReadsFromRemoteClient("");
			fixture28.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
			fixture28.setRpcQueueTimeMaxTime("An��t-1.0.txt");
			fixture28.setWriteBlockOpAvgTime("An��t-1.0.txt");
		}
		return fixture28;
	}

	public ProfileJMXPropsBean getFixture29() throws Exception {
		if (fixture29 == null) {
			fixture29 = new ProfileJMXPropsBean();
			fixture29.setBlocksRead("An��t-1.0.txt");
			fixture29.setBlocksTotal("An��t-1.0.txt");
			fixture29.setBytesWritten("An��t-1.0.txt");
			fixture29.setHotties(new Vector());
			fixture29.setReadsFromLocalClient("An��t-1.0.txt");
			fixture29.setReadsFromRemoteClient("0123456789");
			fixture29.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
			fixture29.setRpcQueueTimeMaxTime("An��t-1.0.txt");
			fixture29.setWriteBlockOpAvgTime("An��t-1.0.txt");
		}
		return fixture29;
	}

	public ProfileJMXPropsBean getFixture30() throws Exception {
		if (fixture30 == null) {
			fixture30 = new ProfileJMXPropsBean();
			fixture30.setBlocksRead("An��t-1.0.txt");
			fixture30.setBlocksTotal("An��t-1.0.txt");
			fixture30.setBytesWritten("An��t-1.0.txt");
			fixture30.setHotties(new Vector());
			fixture30.setReadsFromLocalClient("An��t-1.0.txt");
			fixture30.setReadsFromRemoteClient("An��t-1.0.txt");
			fixture30.setRpcProcessingTimeMaxTime("An��t-1.0.txt");
			fixture30.setRpcQueueTimeMaxTime("An��t-1.0.txt");
			fixture30.setWriteBlockOpAvgTime("An��t-1.0.txt");
		}
		return fixture30;
	}

	@Test
	public void testGetBlocksRead_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();

		String result = fixture.getBlocksRead();

		assertEquals(null, result);
	}

	@Test
	public void testGetBlocksRead_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();

		String result = fixture.getBlocksRead();

		assertEquals("", result);
	}

	@Test
	public void testGetBlocksRead_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();

		String result = fixture.getBlocksRead();

		assertEquals("", result);
	}

	@Test
	public void testGetBlocksRead_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();

		String result = fixture.getBlocksRead();

		assertEquals("", result);
	}

	@Test
	public void testGetBlocksRead_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();

		String result = fixture.getBlocksRead();

		assertEquals("", result);
	}

	@Test
	public void testGetBlocksRead_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();

		String result = fixture.getBlocksRead();

		assertEquals("", result);
	}

	@Test
	public void testGetBlocksRead_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();

		String result = fixture.getBlocksRead();

		assertEquals("", result);
	}

	@Test
	public void testGetBlocksRead_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();

		String result = fixture.getBlocksRead();

		assertEquals("", result);
	}

	@Test
	public void testGetBlocksRead_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();

		String result = fixture.getBlocksRead();

		assertEquals("", result);
	}

	@Test
	public void testGetBlocksRead_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();

		String result = fixture.getBlocksRead();

		assertEquals("", result);
	}

	@Test
	public void testGetBlocksRead_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();

		String result = fixture.getBlocksRead();

		assertEquals("", result);
	}

	@Test
	public void testGetBlocksRead_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();

		String result = fixture.getBlocksRead();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksRead_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();

		String result = fixture.getBlocksRead();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksRead_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();

		String result = fixture.getBlocksRead();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksRead_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();

		String result = fixture.getBlocksRead();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksRead_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();

		String result = fixture.getBlocksRead();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksRead_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();

		String result = fixture.getBlocksRead();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksRead_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();

		String result = fixture.getBlocksRead();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksRead_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();

		String result = fixture.getBlocksRead();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksRead_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();

		String result = fixture.getBlocksRead();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksRead_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();

		String result = fixture.getBlocksRead();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksRead_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();

		String result = fixture.getBlocksRead();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksRead_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();

		String result = fixture.getBlocksRead();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksRead_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();

		String result = fixture.getBlocksRead();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksRead_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();

		String result = fixture.getBlocksRead();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksRead_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();

		String result = fixture.getBlocksRead();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksRead_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();

		String result = fixture.getBlocksRead();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksRead_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();

		String result = fixture.getBlocksRead();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksRead_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();

		String result = fixture.getBlocksRead();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksRead_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();

		String result = fixture.getBlocksRead();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksTotal_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();

		String result = fixture.getBlocksTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetBlocksTotal_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();

		String result = fixture.getBlocksTotal();

		assertEquals("", result);
	}

	@Test
	public void testGetBlocksTotal_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();

		String result = fixture.getBlocksTotal();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksTotal_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();

		String result = fixture.getBlocksTotal();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksTotal_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();

		String result = fixture.getBlocksTotal();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksTotal_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();

		String result = fixture.getBlocksTotal();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksTotal_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();

		String result = fixture.getBlocksTotal();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksTotal_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();

		String result = fixture.getBlocksTotal();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksTotal_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();

		String result = fixture.getBlocksTotal();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksTotal_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();

		String result = fixture.getBlocksTotal();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksTotal_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();

		String result = fixture.getBlocksTotal();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksTotal_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();

		String result = fixture.getBlocksTotal();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksTotal_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();

		String result = fixture.getBlocksTotal();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksTotal_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();

		String result = fixture.getBlocksTotal();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksTotal_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();

		String result = fixture.getBlocksTotal();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksTotal_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();

		String result = fixture.getBlocksTotal();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBlocksTotal_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();

		String result = fixture.getBlocksTotal();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksTotal_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();

		String result = fixture.getBlocksTotal();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksTotal_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();

		String result = fixture.getBlocksTotal();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksTotal_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();

		String result = fixture.getBlocksTotal();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksTotal_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();

		String result = fixture.getBlocksTotal();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksTotal_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();

		String result = fixture.getBlocksTotal();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksTotal_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();

		String result = fixture.getBlocksTotal();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksTotal_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();

		String result = fixture.getBlocksTotal();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksTotal_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();

		String result = fixture.getBlocksTotal();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksTotal_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();

		String result = fixture.getBlocksTotal();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksTotal_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();

		String result = fixture.getBlocksTotal();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksTotal_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();

		String result = fixture.getBlocksTotal();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksTotal_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();

		String result = fixture.getBlocksTotal();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBlocksTotal_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();

		String result = fixture.getBlocksTotal();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBytesWritten_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();

		String result = fixture.getBytesWritten();

		assertEquals(null, result);
	}

	@Test
	public void testGetBytesWritten_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();

		String result = fixture.getBytesWritten();

		assertEquals("", result);
	}

	@Test
	public void testGetBytesWritten_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();

		String result = fixture.getBytesWritten();

		assertEquals("", result);
	}

	@Test
	public void testGetBytesWritten_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();

		String result = fixture.getBytesWritten();

		assertEquals("", result);
	}

	@Test
	public void testGetBytesWritten_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();

		String result = fixture.getBytesWritten();

		assertEquals("", result);
	}

	@Test
	public void testGetBytesWritten_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();

		String result = fixture.getBytesWritten();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBytesWritten_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();

		String result = fixture.getBytesWritten();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBytesWritten_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();

		String result = fixture.getBytesWritten();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBytesWritten_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();

		String result = fixture.getBytesWritten();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBytesWritten_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();

		String result = fixture.getBytesWritten();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBytesWritten_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();

		String result = fixture.getBytesWritten();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBytesWritten_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();

		String result = fixture.getBytesWritten();

		assertEquals("", result);
	}

	@Test
	public void testGetBytesWritten_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();

		String result = fixture.getBytesWritten();

		assertEquals("", result);
	}

	@Test
	public void testGetBytesWritten_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();

		String result = fixture.getBytesWritten();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBytesWritten_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();

		String result = fixture.getBytesWritten();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBytesWritten_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();

		String result = fixture.getBytesWritten();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBytesWritten_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();

		String result = fixture.getBytesWritten();

		assertEquals("", result);
	}

	@Test
	public void testGetBytesWritten_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();

		String result = fixture.getBytesWritten();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBytesWritten_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();

		String result = fixture.getBytesWritten();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBytesWritten_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();

		String result = fixture.getBytesWritten();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBytesWritten_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();

		String result = fixture.getBytesWritten();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBytesWritten_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();

		String result = fixture.getBytesWritten();

		assertEquals("", result);
	}

	@Test
	public void testGetBytesWritten_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();

		String result = fixture.getBytesWritten();

		assertEquals("", result);
	}

	@Test
	public void testGetBytesWritten_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();

		String result = fixture.getBytesWritten();

		assertEquals("", result);
	}

	@Test
	public void testGetBytesWritten_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();

		String result = fixture.getBytesWritten();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBytesWritten_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();

		String result = fixture.getBytesWritten();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBytesWritten_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();

		String result = fixture.getBytesWritten();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetBytesWritten_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();

		String result = fixture.getBytesWritten();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBytesWritten_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();

		String result = fixture.getBytesWritten();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetBytesWritten_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();

		String result = fixture.getBytesWritten();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetCapacityTotal_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetCapacityTotal_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();

		String result = fixture.getCapacityTotal();

		assertEquals(null, result);
	}

	@Test
	public void testGetHotties_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();

		List result = fixture.getHotties();

		assertEquals(null, result);
	}

	@Test
	public void testGetHotties_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetHotties_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();

		List result = fixture.getHotties();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetReadsFromLocalClient_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();

		String result = fixture.getReadsFromLocalClient();

		assertEquals(null, result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromLocalClient_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();

		String result = fixture.getReadsFromLocalClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals(null, result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetReadsFromRemoteClient_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();

		String result = fixture.getReadsFromRemoteClient();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals(null, result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcProcessingTimeMaxTime_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();

		String result = fixture.getRpcProcessingTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals(null, result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetRpcQueueTimeMaxTime_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();

		String result = fixture.getRpcQueueTimeMaxTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals(null, result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetWriteBlockOpAvgTime_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();

		String result = fixture.getWriteBlockOpAvgTime();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testSetBlocksRead_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();
		String blocksRead = "";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksRead_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();
		String blocksRead = "0123456789";

		fixture.setBlocksRead(blocksRead);

	}

	@Test
	public void testSetBlocksTotal_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();
		String blocksTotal = "";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBlocksTotal_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();
		String blocksTotal = "0123456789";

		fixture.setBlocksTotal(blocksTotal);

	}

	@Test
	public void testSetBytesWritten_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();
		String bytesWritten = "";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetBytesWritten_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();
		String bytesWritten = "0123456789";

		fixture.setBytesWritten(bytesWritten);

	}

	@Test
	public void testSetCapacityTotal_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetCapacityTotal_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();
		String capacityTotal = "2.3";

		fixture.setCapacityTotal(capacityTotal);

	}

	@Test
	public void testSetHotties_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();
		List hotties = new ArrayList();

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();
		List hotties = new LinkedList();

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();
		List hotties = new Vector();

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetHotties_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();
		List hotties = null;

		fixture.setHotties(hotties);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();
		String readsFromLocalClient = "";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromLocalClient_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();
		String readsFromLocalClient = "0123456789";

		fixture.setReadsFromLocalClient(readsFromLocalClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();
		String readsFromRemoteClient = "";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetReadsFromRemoteClient_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();
		String readsFromRemoteClient = "0123456789";

		fixture.setReadsFromRemoteClient(readsFromRemoteClient);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();
		String rpcProcessingTimeMaxTime = "";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcProcessingTimeMaxTime_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();
		String rpcProcessingTimeMaxTime = "0123456789";

		fixture.setRpcProcessingTimeMaxTime(rpcProcessingTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();
		String rpcQueueTimeMaxTime = "";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetRpcQueueTimeMaxTime_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();
		String rpcQueueTimeMaxTime = "0123456789";

		fixture.setRpcQueueTimeMaxTime(rpcQueueTimeMaxTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();
		String writeBlockOpAvgTime = "";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testSetWriteBlockOpAvgTime_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();
		String writeBlockOpAvgTime = "0123456789";

		fixture.setWriteBlockOpAvgTime(writeBlockOpAvgTime);

	}

	@Test
	public void testToString_fixture1_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture1();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=null, reads_from_remote_client=null, writeBlockOpAvgTime=null, blocks_read=null, bytes_written=null, rpcProcessingTimeAvgTime=null, rpcQueueTimeAvgTime=null, capacityTotal=null, blocksTotal=null]",
				result);
	}

	@Test
	public void testToString_fixture2_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture2();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=, reads_from_remote_client=, writeBlockOpAvgTime=, blocks_read=, bytes_written=, rpcProcessingTimeAvgTime=, rpcQueueTimeAvgTime=, capacityTotal=null, blocksTotal=]",
				result);
	}

	@Test
	public void testToString_fixture3_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture3();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=0123456789, reads_from_remote_client=, writeBlockOpAvgTime=0123456789, blocks_read=, bytes_written=, rpcProcessingTimeAvgTime=0123456789, rpcQueueTimeAvgTime=0123456789, capacityTotal=null, blocksTotal=0123456789]",
				result);
	}

	@Test
	public void testToString_fixture4_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture4();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=0123456789, reads_from_remote_client=0123456789, writeBlockOpAvgTime=0123456789, blocks_read=, bytes_written=, rpcProcessingTimeAvgTime=0123456789, rpcQueueTimeAvgTime=0123456789, capacityTotal=null, blocksTotal=0123456789]",
				result);
	}

	@Test
	public void testToString_fixture5_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture5();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=0123456789, reads_from_remote_client=An��t-1.0.txt, writeBlockOpAvgTime=0123456789, blocks_read=, bytes_written=, rpcProcessingTimeAvgTime=0123456789, rpcQueueTimeAvgTime=0123456789, capacityTotal=null, blocksTotal=0123456789]",
				result);
	}

	@Test
	public void testToString_fixture6_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture6();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=0123456789, reads_from_remote_client=, writeBlockOpAvgTime=0123456789, blocks_read=, bytes_written=0123456789, rpcProcessingTimeAvgTime=0123456789, rpcQueueTimeAvgTime=0123456789, capacityTotal=null, blocksTotal=0123456789]",
				result);
	}

	@Test
	public void testToString_fixture7_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture7();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=0123456789, reads_from_remote_client=0123456789, writeBlockOpAvgTime=0123456789, blocks_read=, bytes_written=0123456789, rpcProcessingTimeAvgTime=0123456789, rpcQueueTimeAvgTime=0123456789, capacityTotal=null, blocksTotal=0123456789]",
				result);
	}

	@Test
	public void testToString_fixture8_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture8();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=0123456789, reads_from_remote_client=An��t-1.0.txt, writeBlockOpAvgTime=0123456789, blocks_read=, bytes_written=0123456789, rpcProcessingTimeAvgTime=0123456789, rpcQueueTimeAvgTime=0123456789, capacityTotal=null, blocksTotal=0123456789]",
				result);
	}

	@Test
	public void testToString_fixture9_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture9();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=0123456789, reads_from_remote_client=, writeBlockOpAvgTime=0123456789, blocks_read=, bytes_written=An��t-1.0.txt, rpcProcessingTimeAvgTime=0123456789, rpcQueueTimeAvgTime=0123456789, capacityTotal=null, blocksTotal=0123456789]",
				result);
	}

	@Test
	public void testToString_fixture10_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture10();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=0123456789, reads_from_remote_client=0123456789, writeBlockOpAvgTime=0123456789, blocks_read=, bytes_written=An��t-1.0.txt, rpcProcessingTimeAvgTime=0123456789, rpcQueueTimeAvgTime=0123456789, capacityTotal=null, blocksTotal=0123456789]",
				result);
	}

	@Test
	public void testToString_fixture11_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture11();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=0123456789, reads_from_remote_client=An��t-1.0.txt, writeBlockOpAvgTime=0123456789, blocks_read=, bytes_written=An��t-1.0.txt, rpcProcessingTimeAvgTime=0123456789, rpcQueueTimeAvgTime=0123456789, capacityTotal=null, blocksTotal=0123456789]",
				result);
	}

	@Test
	public void testToString_fixture12_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture12();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=0123456789, reads_from_remote_client=, writeBlockOpAvgTime=0123456789, blocks_read=0123456789, bytes_written=, rpcProcessingTimeAvgTime=0123456789, rpcQueueTimeAvgTime=0123456789, capacityTotal=null, blocksTotal=0123456789]",
				result);
	}

	@Test
	public void testToString_fixture13_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture13();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=0123456789, reads_from_remote_client=0123456789, writeBlockOpAvgTime=0123456789, blocks_read=0123456789, bytes_written=, rpcProcessingTimeAvgTime=0123456789, rpcQueueTimeAvgTime=0123456789, capacityTotal=null, blocksTotal=0123456789]",
				result);
	}

	@Test
	public void testToString_fixture14_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture14();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=0123456789, reads_from_remote_client=, writeBlockOpAvgTime=0123456789, blocks_read=0123456789, bytes_written=0123456789, rpcProcessingTimeAvgTime=0123456789, rpcQueueTimeAvgTime=0123456789, capacityTotal=null, blocksTotal=0123456789]",
				result);
	}

	@Test
	public void testToString_fixture15_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture15();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=0123456789, reads_from_remote_client=0123456789, writeBlockOpAvgTime=0123456789, blocks_read=0123456789, bytes_written=0123456789, rpcProcessingTimeAvgTime=0123456789, rpcQueueTimeAvgTime=0123456789, capacityTotal=null, blocksTotal=0123456789]",
				result);
	}

	@Test
	public void testToString_fixture16_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture16();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=0123456789, reads_from_remote_client=, writeBlockOpAvgTime=0123456789, blocks_read=0123456789, bytes_written=An��t-1.0.txt, rpcProcessingTimeAvgTime=0123456789, rpcQueueTimeAvgTime=0123456789, capacityTotal=null, blocksTotal=0123456789]",
				result);
	}

	@Test
	public void testToString_fixture17_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture17();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=An��t-1.0.txt, reads_from_remote_client=An��t-1.0.txt, writeBlockOpAvgTime=An��t-1.0.txt, blocks_read=0123456789, bytes_written=, rpcProcessingTimeAvgTime=An��t-1.0.txt, rpcQueueTimeAvgTime=An��t-1.0.txt, capacityTotal=null, blocksTotal=An��t-1.0.txt]",
				result);
	}

	@Test
	public void testToString_fixture18_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture18();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=An��t-1.0.txt, reads_from_remote_client=0123456789, writeBlockOpAvgTime=An��t-1.0.txt, blocks_read=0123456789, bytes_written=0123456789, rpcProcessingTimeAvgTime=An��t-1.0.txt, rpcQueueTimeAvgTime=An��t-1.0.txt, capacityTotal=null, blocksTotal=An��t-1.0.txt]",
				result);
	}

	@Test
	public void testToString_fixture19_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture19();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=An��t-1.0.txt, reads_from_remote_client=An��t-1.0.txt, writeBlockOpAvgTime=An��t-1.0.txt, blocks_read=0123456789, bytes_written=0123456789, rpcProcessingTimeAvgTime=An��t-1.0.txt, rpcQueueTimeAvgTime=An��t-1.0.txt, capacityTotal=null, blocksTotal=An��t-1.0.txt]",
				result);
	}

	@Test
	public void testToString_fixture20_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture20();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=An��t-1.0.txt, reads_from_remote_client=0123456789, writeBlockOpAvgTime=An��t-1.0.txt, blocks_read=0123456789, bytes_written=An��t-1.0.txt, rpcProcessingTimeAvgTime=An��t-1.0.txt, rpcQueueTimeAvgTime=An��t-1.0.txt, capacityTotal=null, blocksTotal=An��t-1.0.txt]",
				result);
	}

	@Test
	public void testToString_fixture21_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture21();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=An��t-1.0.txt, reads_from_remote_client=An��t-1.0.txt, writeBlockOpAvgTime=An��t-1.0.txt, blocks_read=0123456789, bytes_written=An��t-1.0.txt, rpcProcessingTimeAvgTime=An��t-1.0.txt, rpcQueueTimeAvgTime=An��t-1.0.txt, capacityTotal=null, blocksTotal=An��t-1.0.txt]",
				result);
	}

	@Test
	public void testToString_fixture22_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture22();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=An��t-1.0.txt, reads_from_remote_client=, writeBlockOpAvgTime=An��t-1.0.txt, blocks_read=An��t-1.0.txt, bytes_written=, rpcProcessingTimeAvgTime=An��t-1.0.txt, rpcQueueTimeAvgTime=An��t-1.0.txt, capacityTotal=null, blocksTotal=An��t-1.0.txt]",
				result);
	}

	@Test
	public void testToString_fixture23_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture23();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=An��t-1.0.txt, reads_from_remote_client=0123456789, writeBlockOpAvgTime=An��t-1.0.txt, blocks_read=An��t-1.0.txt, bytes_written=, rpcProcessingTimeAvgTime=An��t-1.0.txt, rpcQueueTimeAvgTime=An��t-1.0.txt, capacityTotal=null, blocksTotal=An��t-1.0.txt]",
				result);
	}

	@Test
	public void testToString_fixture24_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture24();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=An��t-1.0.txt, reads_from_remote_client=An��t-1.0.txt, writeBlockOpAvgTime=An��t-1.0.txt, blocks_read=An��t-1.0.txt, bytes_written=, rpcProcessingTimeAvgTime=An��t-1.0.txt, rpcQueueTimeAvgTime=An��t-1.0.txt, capacityTotal=null, blocksTotal=An��t-1.0.txt]",
				result);
	}

	@Test
	public void testToString_fixture25_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture25();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=An��t-1.0.txt, reads_from_remote_client=, writeBlockOpAvgTime=An��t-1.0.txt, blocks_read=An��t-1.0.txt, bytes_written=0123456789, rpcProcessingTimeAvgTime=An��t-1.0.txt, rpcQueueTimeAvgTime=An��t-1.0.txt, capacityTotal=null, blocksTotal=An��t-1.0.txt]",
				result);
	}

	@Test
	public void testToString_fixture26_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture26();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=An��t-1.0.txt, reads_from_remote_client=0123456789, writeBlockOpAvgTime=An��t-1.0.txt, blocks_read=An��t-1.0.txt, bytes_written=0123456789, rpcProcessingTimeAvgTime=An��t-1.0.txt, rpcQueueTimeAvgTime=An��t-1.0.txt, capacityTotal=null, blocksTotal=An��t-1.0.txt]",
				result);
	}

	@Test
	public void testToString_fixture27_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture27();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=An��t-1.0.txt, reads_from_remote_client=An��t-1.0.txt, writeBlockOpAvgTime=An��t-1.0.txt, blocks_read=An��t-1.0.txt, bytes_written=0123456789, rpcProcessingTimeAvgTime=An��t-1.0.txt, rpcQueueTimeAvgTime=An��t-1.0.txt, capacityTotal=null, blocksTotal=An��t-1.0.txt]",
				result);
	}

	@Test
	public void testToString_fixture28_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture28();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=An��t-1.0.txt, reads_from_remote_client=, writeBlockOpAvgTime=An��t-1.0.txt, blocks_read=An��t-1.0.txt, bytes_written=An��t-1.0.txt, rpcProcessingTimeAvgTime=An��t-1.0.txt, rpcQueueTimeAvgTime=An��t-1.0.txt, capacityTotal=null, blocksTotal=An��t-1.0.txt]",
				result);
	}

	@Test
	public void testToString_fixture29_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture29();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=An��t-1.0.txt, reads_from_remote_client=0123456789, writeBlockOpAvgTime=An��t-1.0.txt, blocks_read=An��t-1.0.txt, bytes_written=An��t-1.0.txt, rpcProcessingTimeAvgTime=An��t-1.0.txt, rpcQueueTimeAvgTime=An��t-1.0.txt, capacityTotal=null, blocksTotal=An��t-1.0.txt]",
				result);
	}

	@Test
	public void testToString_fixture30_1() throws Exception {
		ProfileJMXPropsBean fixture = getFixture30();

		String result = fixture.toString();

		assertEquals(
				"ProfileJMXPropsBean [reads_from_local_client=An��t-1.0.txt, reads_from_remote_client=An��t-1.0.txt, writeBlockOpAvgTime=An��t-1.0.txt, blocks_read=An��t-1.0.txt, bytes_written=An��t-1.0.txt, rpcProcessingTimeAvgTime=An��t-1.0.txt, rpcQueueTimeAvgTime=An��t-1.0.txt, capacityTotal=null, blocksTotal=An��t-1.0.txt]",
				result);
	}

	@Before
	public void setUp() throws Exception {
		fixture1 = new ProfileJMXPropsBean();
	}

	@After
	public void tearDown() throws Exception {
	}
}