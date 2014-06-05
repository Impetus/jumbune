package org.jumbune.debugger.instrumentation.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipInputStream;

import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.debugger.instrumentation.adapter.BaseAdapter;
import org.jumbune.debugger.instrumentation.adapter.BlockLogAdapter;
import org.jumbune.debugger.instrumentation.adapter.CaseAdapter;
import org.jumbune.debugger.instrumentation.adapter.ChainedTaskClassAdapter;
import org.jumbune.debugger.instrumentation.adapter.ConfigureMapReduceAdapter;
import org.jumbune.debugger.instrumentation.adapter.ContextWriteLogAdapter;
import org.jumbune.debugger.instrumentation.adapter.ContextWriteValidationAdapter;
import org.jumbune.debugger.instrumentation.adapter.DoNotDisturbAdapter;
import org.jumbune.debugger.instrumentation.adapter.InstrumentFinalizer;
import org.jumbune.debugger.instrumentation.utils.Environment;
import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.jumbune.utils.beans.LogInfoBean;
import org.junit.*;
import static org.junit.Assert.*;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class InstrumentUtilTest {
	

	@Test
	public void testAddChainLoggerInfo_1()
		throws Exception {
		int sequence = 0;
		String className = "";

		InsnList result = InstrumentUtil.addChainLoggerInfo(sequence, className);

		assertNotNull(result);
		assertEquals(4, result.size());
	}

	@Test
	public void testAddChainLoggerInfo_2()
		throws Exception {
		int sequence = 1;
		String className = "0123456789";

		InsnList result = InstrumentUtil.addChainLoggerInfo(sequence, className);

		assertNotNull(result);
		assertEquals(4, result.size());
	}

	@Test
	public void testAddChainLoggerInfo_3()
		throws Exception {
		int sequence = 7;
		String className = "0123456789";

		InsnList result = InstrumentUtil.addChainLoggerInfo(sequence, className);

		assertNotNull(result);
		assertEquals(4, result.size());
	}

	@Test
	public void testAddChainLoggerInfo_4()
		throws Exception {
		int sequence = 1;
		String className = "";

		InsnList result = InstrumentUtil.addChainLoggerInfo(sequence, className);

		assertNotNull(result);
		assertEquals(4, result.size());
	}

	@Test
	public void testAddChainLoggerInfo_5()
		throws Exception {
		int sequence = 7;
		String className = "";

		InsnList result = InstrumentUtil.addChainLoggerInfo(sequence, className);

		assertNotNull(result);
		assertEquals(4, result.size());
	}

	@Test
	public void testAddChainLoggerInfo_6()
		throws Exception {
		int sequence = 0;
		String className = "0123456789";

		InsnList result = InstrumentUtil.addChainLoggerInfo(sequence, className);

		assertNotNull(result);
		assertEquals(4, result.size());
	}

	@Test
	public void testAddClassMember_1()
		throws Exception {
		ArrayList<FieldNode> fieldList = new ArrayList<FieldNode>();
		fieldList.add(new FieldNode(-1, -1, (String) null, (String) null, (String) null, (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = new Object();

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_2()
		throws Exception {
		ArrayList<FieldNode> fieldList = new ArrayList<FieldNode>();
		fieldList.add(new FieldNode(-1, -1, (String) null, (String) null, (String) null, (Object) null));
		fieldList.add(new FieldNode(-1, (String) null, (String) null, (String) null, (Object) null));
		fieldList.add(new FieldNode(0, "", "", "", new Object()));
		fieldList.add(new FieldNode(0, 0, "", "", "", new Object()));
		fieldList.add(new FieldNode(0, 0, "An��t-1.0.txt", "An��t-1.0.txt", "", (Object) null));
		fieldList.add(new FieldNode(1, "0123456789", "0123456789", "0123456789", (Object) null));
		fieldList.add(new FieldNode(1, 1, "0123456789", "0123456789", "0123456789", (Object) null));
		fieldList.add(new FieldNode(7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", (Object) null));
		fieldList.add(new FieldNode(7, 7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", (Object) null));
		fieldList.add(new FieldNode(Integer.MAX_VALUE, Integer.MIN_VALUE, "0123456789", "0123456789", "", (Object) null));
		fieldList.add(new FieldNode(Integer.MIN_VALUE, "0123456789", "0123456789", "", (Object) null));
		int access = 1;
		String name = "0123456789";
		String desc = "0123456789";
		String signature = "0123456789";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_3()
		throws Exception {
		ArrayList<FieldNode> fieldList = new ArrayList<FieldNode>();
		fieldList.add(new FieldNode(0, "", "", "", new Object()));
		int access = 0;
		String name = "0123456789";
		String desc = "0123456789";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_4()
		throws Exception {
		ArrayList<FieldNode> fieldList = new ArrayList<FieldNode>();
		fieldList.add(new FieldNode(0, 0, "", "", "", new Object()));
		int access = 7;
		String name = "0123456789";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_5()
		throws Exception {
		ArrayList<FieldNode> fieldList = new ArrayList<FieldNode>();
		fieldList.add(new FieldNode(1, "0123456789", "0123456789", "0123456789", (Object) null));
		int access = 1;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_6()
		throws Exception {
		ArrayList<FieldNode> fieldList = new ArrayList<FieldNode>();
		fieldList.add(new FieldNode(1, 1, "0123456789", "0123456789", "0123456789", (Object) null));
		int access = 7;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_7()
		throws Exception {
		ArrayList<FieldNode> fieldList = new ArrayList<FieldNode>();
		fieldList.add(new FieldNode(7, 7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_8()
		throws Exception {
		ArrayList<FieldNode> fieldList = new ArrayList<FieldNode>();
		fieldList.add(new FieldNode(Integer.MAX_VALUE, Integer.MIN_VALUE, "0123456789", "0123456789", "", (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_9()
		throws Exception {
		ArrayList<FieldNode> fieldList = new ArrayList<FieldNode>();
		fieldList.add(new FieldNode(Integer.MIN_VALUE, "0123456789", "0123456789", "", (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_10()
		throws Exception {
		LinkedList<FieldNode> fieldList = new LinkedList<FieldNode>();
		fieldList.add(new FieldNode(-1, -1, (String) null, (String) null, (String) null, (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_11()
		throws Exception {
		LinkedList<FieldNode> fieldList = new LinkedList<FieldNode>();
		fieldList.add(new FieldNode(-1, -1, (String) null, (String) null, (String) null, (Object) null));
		fieldList.add(new FieldNode(-1, (String) null, (String) null, (String) null, (Object) null));
		fieldList.add(new FieldNode(0, "", "", "", new Object()));
		fieldList.add(new FieldNode(0, 0, "", "", "", new Object()));
		fieldList.add(new FieldNode(0, 0, "An��t-1.0.txt", "An��t-1.0.txt", "", (Object) null));
		fieldList.add(new FieldNode(1, "0123456789", "0123456789", "0123456789", (Object) null));
		fieldList.add(new FieldNode(1, 1, "0123456789", "0123456789", "0123456789", (Object) null));
		fieldList.add(new FieldNode(7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", (Object) null));
		fieldList.add(new FieldNode(7, 7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", (Object) null));
		fieldList.add(new FieldNode(Integer.MAX_VALUE, Integer.MIN_VALUE, "0123456789", "0123456789", "", (Object) null));
		fieldList.add(new FieldNode(Integer.MIN_VALUE, "0123456789", "0123456789", "", (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_12()
		throws Exception {
		LinkedList<FieldNode> fieldList = new LinkedList<FieldNode>();
		fieldList.add(new FieldNode(-1, (String) null, (String) null, (String) null, (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_13()
		throws Exception {
		LinkedList<FieldNode> fieldList = new LinkedList<FieldNode>();
		fieldList.add(new FieldNode(0, "", "", "", new Object()));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_14()
		throws Exception {
		LinkedList<FieldNode> fieldList = new LinkedList<FieldNode>();
		fieldList.add(new FieldNode(0, 0, "", "", "", new Object()));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_15()
		throws Exception {
		LinkedList<FieldNode> fieldList = new LinkedList<FieldNode>();
		fieldList.add(new FieldNode(0, 0, "An��t-1.0.txt", "An��t-1.0.txt", "", (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_16()
		throws Exception {
		LinkedList<FieldNode> fieldList = new LinkedList<FieldNode>();
		fieldList.add(new FieldNode(1, "0123456789", "0123456789", "0123456789", (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_17()
		throws Exception {
		LinkedList<FieldNode> fieldList = new LinkedList<FieldNode>();
		fieldList.add(new FieldNode(1, 1, "0123456789", "0123456789", "0123456789", (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_18()
		throws Exception {
		LinkedList<FieldNode> fieldList = new LinkedList<FieldNode>();
		fieldList.add(new FieldNode(7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_19()
		throws Exception {
		LinkedList<FieldNode> fieldList = new LinkedList<FieldNode>();
		fieldList.add(new FieldNode(7, 7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_20()
		throws Exception {
		LinkedList<FieldNode> fieldList = new LinkedList<FieldNode>();
		fieldList.add(new FieldNode(Integer.MAX_VALUE, Integer.MIN_VALUE, "0123456789", "0123456789", "", (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_21()
		throws Exception {
		LinkedList<FieldNode> fieldList = new LinkedList<FieldNode>();
		fieldList.add(new FieldNode(Integer.MIN_VALUE, "0123456789", "0123456789", "", (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_22()
		throws Exception {
		Vector<FieldNode> fieldList = new Vector<FieldNode>();
		fieldList.add(new FieldNode(-1, -1, (String) null, (String) null, (String) null, (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_23()
		throws Exception {
		Vector<FieldNode> fieldList = new Vector<FieldNode>();
		fieldList.add(new FieldNode(-1, -1, (String) null, (String) null, (String) null, (Object) null));
		fieldList.add(new FieldNode(-1, (String) null, (String) null, (String) null, (Object) null));
		fieldList.add(new FieldNode(0, "", "", "", new Object()));
		fieldList.add(new FieldNode(0, 0, "", "", "", new Object()));
		fieldList.add(new FieldNode(0, 0, "An��t-1.0.txt", "An��t-1.0.txt", "", (Object) null));
		fieldList.add(new FieldNode(1, "0123456789", "0123456789", "0123456789", (Object) null));
		fieldList.add(new FieldNode(1, 1, "0123456789", "0123456789", "0123456789", (Object) null));
		fieldList.add(new FieldNode(7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", (Object) null));
		fieldList.add(new FieldNode(7, 7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", (Object) null));
		fieldList.add(new FieldNode(Integer.MAX_VALUE, Integer.MIN_VALUE, "0123456789", "0123456789", "", (Object) null));
		fieldList.add(new FieldNode(Integer.MIN_VALUE, "0123456789", "0123456789", "", (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_24()
		throws Exception {
		Vector<FieldNode> fieldList = new Vector<FieldNode>();
		fieldList.add(new FieldNode(-1, (String) null, (String) null, (String) null, (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_25()
		throws Exception {
		Vector<FieldNode> fieldList = new Vector<FieldNode>();
		fieldList.add(new FieldNode(0, "", "", "", new Object()));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_26()
		throws Exception {
		Vector<FieldNode> fieldList = new Vector<FieldNode>();
		fieldList.add(new FieldNode(0, 0, "", "", "", new Object()));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_27()
		throws Exception {
		Vector<FieldNode> fieldList = new Vector<FieldNode>();
		fieldList.add(new FieldNode(0, 0, "An��t-1.0.txt", "An��t-1.0.txt", "", (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_28()
		throws Exception {
		Vector<FieldNode> fieldList = new Vector<FieldNode>();
		fieldList.add(new FieldNode(1, "0123456789", "0123456789", "0123456789", (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_29()
		throws Exception {
		Vector<FieldNode> fieldList = new Vector<FieldNode>();
		fieldList.add(new FieldNode(1, 1, "0123456789", "0123456789", "0123456789", (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddClassMember_30()
		throws Exception {
		Vector<FieldNode> fieldList = new Vector<FieldNode>();
		fieldList.add(new FieldNode(7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", (Object) null));
		int access = 0;
		String name = "";
		String desc = "";
		String signature = "";
		Object value = null;

		InstrumentUtil.addClassMember(fieldList, access, name, desc, signature, value);

	}

	@Test
	public void testAddCounterLogging_1()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMsgPrefix = "";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLogging(className, methodName, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLogging_2()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMsgPrefix = "0123456789";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLogging(className, methodName, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLogging_3()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMsgPrefix = "0123456789";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLogging(className, methodName, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLogging_4()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMsgPrefix = "";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLogging(className, methodName, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLogging_5()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMsgPrefix = "";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLogging(className, methodName, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLogging_6()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMsgPrefix = "0123456789";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLogging(className, methodName, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLogging_7()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMsgPrefix = "0123456789";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLogging(className, methodName, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLogging_8()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMsgPrefix = "";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLogging(className, methodName, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLogging_9()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMsgPrefix = "";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLogging(className, methodName, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLogging_10()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMsgPrefix = "0123456789";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLogging(className, methodName, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLogging_11()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMsgPrefix = "0123456789";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLogging(className, methodName, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLogging_12()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMsgPrefix = "";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLogging(className, methodName, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLogging_13()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMsgPrefix = "";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLogging(className, methodName, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLogging_14()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMsgPrefix = "0123456789";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLogging(className, methodName, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLogging_15()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMsgPrefix = "0123456789";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLogging(className, methodName, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLogging_16()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMsgPrefix = "";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLogging(className, methodName, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_1()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMethod = "";
		String logMsgPrefix = "";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_2()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMethod = "0123456789";
		String logMsgPrefix = "0123456789";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_3()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMethod = "0123456789";
		String logMsgPrefix = "0123456789";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_4()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMethod = "0123456789";
		String logMsgPrefix = "";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_5()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMethod = "0123456789";
		String logMsgPrefix = "";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_6()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMethod = "";
		String logMsgPrefix = "0123456789";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_7()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMethod = "";
		String logMsgPrefix = "0123456789";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_8()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMethod = "";
		String logMsgPrefix = "";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_9()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMethod = "";
		String logMsgPrefix = "";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_10()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMethod = "0123456789";
		String logMsgPrefix = "0123456789";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_11()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMethod = "0123456789";
		String logMsgPrefix = "0123456789";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_12()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMethod = "0123456789";
		String logMsgPrefix = "";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_13()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMethod = "0123456789";
		String logMsgPrefix = "";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_14()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMethod = "";
		String logMsgPrefix = "0123456789";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_15()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMethod = "";
		String logMsgPrefix = "0123456789";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_16()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMethod = "";
		String logMsgPrefix = "";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_17()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMethod = "";
		String logMsgPrefix = "";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_18()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMethod = "0123456789";
		String logMsgPrefix = "0123456789";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_19()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMethod = "0123456789";
		String logMsgPrefix = "0123456789";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_20()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMethod = "0123456789";
		String logMsgPrefix = "";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_21()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMethod = "0123456789";
		String logMsgPrefix = "";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_22()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMethod = "";
		String logMsgPrefix = "0123456789";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_23()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMethod = "";
		String logMsgPrefix = "0123456789";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_24()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMethod = "";
		String logMsgPrefix = "";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_25()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMethod = "";
		String logMsgPrefix = "";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_26()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMethod = "0123456789";
		String logMsgPrefix = "0123456789";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_27()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMethod = "0123456789";
		String logMsgPrefix = "0123456789";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_28()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMethod = "0123456789";
		String logMsgPrefix = "";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_29()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMethod = "0123456789";
		String logMsgPrefix = "";
		String field = "";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddCounterLoggingOldApi_30()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMethod = "";
		String logMsgPrefix = "0123456789";
		String field = "0123456789";

		InsnList result = InstrumentUtil.addCounterLoggingOldApi(className, methodName, logMethod, logMsgPrefix, field);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddLogHeader_1()
		throws Exception {
		String className = "";

		InsnList result = InstrumentUtil.addLogHeader(className);

		assertNotNull(result);
		assertEquals(4, result.size());
	}

	@Test
	public void testAddLogHeader_2()
		throws Exception {
		String className = "0123456789";

		InsnList result = InstrumentUtil.addLogHeader(className);

		assertNotNull(result);
		assertEquals(4, result.size());
	}

	@Test
	public void testAddLogHeaderOldApi_1()
		throws Exception {
		String className = "";

		InsnList result = InstrumentUtil.addLogHeaderOldApi(className);

		assertNotNull(result);
		assertEquals(4, result.size());
	}

	@Test
	public void testAddLogHeaderOldApi_2()
		throws Exception {
		String className = "0123456789";

		InsnList result = InstrumentUtil.addLogHeaderOldApi(className);

		assertNotNull(result);
		assertEquals(4, result.size());
	}

	@Test
	public void testAddLogMessage_1()
		throws Exception {
		Object object1 = null;
		Object object2 = null;
		Object object3 = null;
		Object object4 = null;
		Object object5 = null;
		Object object6 = null;
		Object object7 = null;
		Object object8 = null;
		Object object9 = null;
		Object object10 = null;

		InsnList result = InstrumentUtil.addLogMessage(object1, object2, object3, object4, object5, object6, object7, object8, object9, object10);

		assertNotNull(result);
		assertEquals(12, result.size());
	}

	@Test
	public void testAddLogMessage_2()
		throws Exception {

		InsnList result = InstrumentUtil.addLogMessage();

		assertNotNull(result);
		assertEquals(2, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_1()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 0;
		String validatorFieldName = "";
		String validatorClass = "";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_2()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");
		int variableIndex = 1;
		String validatorFieldName = "0123456789";
		String validatorClass = "0123456789";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_3()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 0;
		String validatorFieldName = "0123456789";
		String validatorClass = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(9, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_4()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 7;
		String validatorFieldName = "";
		String validatorClass = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_5()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 1;
		String validatorFieldName = "";
		String validatorClass = "";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_6()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 7;
		String validatorFieldName = "0123456789";
		String validatorClass = "0123456789";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(9, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_7()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");
		int variableIndex = 1;
		String validatorFieldName = "";
		String validatorClass = "0123456789";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_8()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 0;
		String validatorFieldName = "";
		String validatorClass = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(9, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_9()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 1;
		String validatorFieldName = "0123456789";
		String validatorClass = "0123456789";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_10()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 0;
		String validatorFieldName = "";
		String validatorClass = "0123456789";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_11()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 7;
		String validatorFieldName = "";
		String validatorClass = "0123456789";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(9, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_12()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");
		int variableIndex = 1;
		String validatorFieldName = "";
		String validatorClass = "";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_13()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 7;
		String validatorFieldName = "0123456789";
		String validatorClass = "";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_14()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 1;
		String validatorFieldName = "";
		String validatorClass = "0123456789";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_15()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 0;
		String validatorFieldName = "";
		String validatorClass = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_16()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 1;
		String validatorFieldName = "0123456789";
		String validatorClass = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(9, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_17()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");
		int variableIndex = 0;
		String validatorFieldName = "0123456789";
		String validatorClass = "";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_18()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 7;
		String validatorFieldName = "";
		String validatorClass = "0123456789";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_19()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 0;
		String validatorFieldName = "0123456789";
		String validatorClass = "0123456789";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_20()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");
		int variableIndex = 7;
		String validatorFieldName = "0123456789";
		String validatorClass = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_21()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 1;
		String validatorFieldName = "";
		String validatorClass = "0123456789";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(9, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_22()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 7;
		String validatorFieldName = "0123456789";
		String validatorClass = "0123456789";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_23()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 1;
		String validatorFieldName = "0123456789";
		String validatorClass = "0123456789";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_24()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 0;
		String validatorFieldName = "0123456789";
		String validatorClass = "";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_25()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");
		int variableIndex = 7;
		String validatorFieldName = "";
		String validatorClass = "";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_26()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 0;
		String validatorFieldName = "0123456789";
		String validatorClass = "0123456789";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(9, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_27()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 7;
		String validatorFieldName = "0123456789";
		String validatorClass = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_28()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 1;
		String validatorFieldName = "";
		String validatorClass = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_29()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 0;
		String validatorFieldName = "";
		String validatorClass = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggerWithClassMethodCall_30()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");
		int variableIndex = 7;
		String validatorFieldName = "";
		String validatorClass = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addLoggerWithClassMethodCall(logBean, variableIndex, validatorFieldName, validatorClass, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddLoggingForPartitioner_1()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");

		InsnList result = InstrumentUtil.addLoggingForPartitioner(logBean);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddLoggingForPartitioner_2()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");

		InsnList result = InstrumentUtil.addLoggingForPartitioner(logBean);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddLoggingForPartitioner_3()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");

		InsnList result = InstrumentUtil.addLoggingForPartitioner(logBean);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddLoggingForPartitioner_4()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);

		InsnList result = InstrumentUtil.addLoggingForPartitioner(logBean);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddMapReduceContextWriteLogging_1()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMessage = "";

		InsnList result = InstrumentUtil.addMapReduceContextWriteLogging(className, methodName, logMessage);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testAddMapReduceContextWriteLogging_2()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMessage = "0123456789";

		InsnList result = InstrumentUtil.addMapReduceContextWriteLogging(className, methodName, logMessage);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testAddMapReduceContextWriteLogging_3()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMessage = "";

		InsnList result = InstrumentUtil.addMapReduceContextWriteLogging(className, methodName, logMessage);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testAddMapReduceContextWriteLogging_4()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMessage = "0123456789";

		InsnList result = InstrumentUtil.addMapReduceContextWriteLogging(className, methodName, logMessage);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testAddMapReduceContextWriteLogging_5()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMessage = "";

		InsnList result = InstrumentUtil.addMapReduceContextWriteLogging(className, methodName, logMessage);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testAddMapReduceContextWriteLogging_6()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMessage = "0123456789";

		InsnList result = InstrumentUtil.addMapReduceContextWriteLogging(className, methodName, logMessage);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testAddMapReduceContextWriteLogging_7()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMessage = "";

		InsnList result = InstrumentUtil.addMapReduceContextWriteLogging(className, methodName, logMessage);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testAddMapReduceContextWriteLogging_8()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMessage = "0123456789";

		InsnList result = InstrumentUtil.addMapReduceContextWriteLogging(className, methodName, logMessage);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_1()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 0;

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_2()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");
		int variableIndex = 1;

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_3()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 7;

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_4()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 7;

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_5()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");
		int variableIndex = 0;

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_6()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 1;

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_7()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 1;

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_8()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 7;

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_9()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 0;

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_10()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 0;

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_11()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 1;

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_12()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");
		int variableIndex = 7;

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_13()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 0;
		String pattern = "";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_14()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");
		int variableIndex = 1;
		String pattern = "0123456789";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_15()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 0;
		String pattern = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(9, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_16()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 1;
		String pattern = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_17()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 0;
		String pattern = "";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_18()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 1;
		String pattern = "0123456789";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(9, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_19()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 7;
		String pattern = "0123456789";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_20()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 1;
		String pattern = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_21()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 7;
		String pattern = "0123456789";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(9, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_22()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");
		int variableIndex = 0;
		String pattern = "0123456789";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_23()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 7;
		String pattern = "0123456789";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_24()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 1;
		String pattern = "";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_25()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");
		int variableIndex = 7;
		String pattern = "";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_26()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 0;
		String pattern = "0123456789";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(9, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_27()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 7;
		String pattern = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_28()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 0;
		String pattern = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_29()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 7;
		String pattern = "";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(9, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_30()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");
		int variableIndex = 0;
		String pattern = "0123456789";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_31()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 1;
		String pattern = "0123456789";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_32()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 0;
		String pattern = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_33()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");
		int variableIndex = 7;
		String pattern = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_34()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 0;
		String pattern = "0123456789";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(9, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_35()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 7;
		String pattern = "0123456789";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_36()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 1;
		String pattern = "0123456789";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_37()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 0;
		String pattern = "0123456789";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_38()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");
		int variableIndex = 7;
		String pattern = "0123456789";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_39()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 1;
		String pattern = "";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(9, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_40()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");
		int variableIndex = 7;
		String pattern = "";
		String classQualifiedName = "";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_41()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");
		int variableIndex = 0;
		String pattern = "0123456789";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(10, result.size());
	}

	@Test
	public void testAddRegExMatcherClassCall_42()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);
		int variableIndex = 7;
		String pattern = "";
		String classQualifiedName = "0123456789";

		InsnList result = InstrumentUtil.addRegExMatcherClassCall(logBean, variableIndex, pattern, classQualifiedName);

		assertNotNull(result);
		assertEquals(9, result.size());
	}

	@Test
	public void testAddReturnLogging_1()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMsg = "";

		InsnList result = InstrumentUtil.addReturnLogging(className, methodName, logMsg);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testAddReturnLogging_2()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMsg = "0123456789";

		InsnList result = InstrumentUtil.addReturnLogging(className, methodName, logMsg);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testAddReturnLogging_3()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMsg = "";

		InsnList result = InstrumentUtil.addReturnLogging(className, methodName, logMsg);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testAddReturnLogging_4()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMsg = "0123456789";

		InsnList result = InstrumentUtil.addReturnLogging(className, methodName, logMsg);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testAddReturnLogging_5()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMsg = "";

		InsnList result = InstrumentUtil.addReturnLogging(className, methodName, logMsg);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testAddReturnLogging_6()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMsg = "0123456789";

		InsnList result = InstrumentUtil.addReturnLogging(className, methodName, logMsg);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testAddReturnLogging_7()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMsg = "";

		InsnList result = InstrumentUtil.addReturnLogging(className, methodName, logMsg);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testAddReturnLogging_8()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMsg = "0123456789";

		InsnList result = InstrumentUtil.addReturnLogging(className, methodName, logMsg);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testAddTimerLogging_1()
		throws Exception {
		String className = "";
		String methodName = "";
		int variable = 0;
		String logMsg = "";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_2()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		int variable = 1;
		String logMsg = "0123456789";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_3()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		int variable = 7;
		String logMsg = "0123456789";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_4()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		int variable = 1;
		String logMsg = "";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_5()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		int variable = 7;
		String logMsg = "";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_6()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		int variable = 0;
		String logMsg = "0123456789";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_7()
		throws Exception {
		String className = "";
		String methodName = "";
		int variable = 1;
		String logMsg = "0123456789";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_8()
		throws Exception {
		String className = "";
		String methodName = "";
		int variable = 0;
		String logMsg = "0123456789";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_9()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		int variable = 7;
		String logMsg = "";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_10()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		int variable = 0;
		String logMsg = "0123456789";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_11()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		int variable = 1;
		String logMsg = "0123456789";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_12()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		int variable = 0;
		String logMsg = "";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_13()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		int variable = 1;
		String logMsg = "";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_14()
		throws Exception {
		String className = "";
		String methodName = "";
		int variable = 7;
		String logMsg = "0123456789";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_15()
		throws Exception {
		String className = "";
		String methodName = "";
		int variable = 1;
		String logMsg = "";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_16()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		int variable = 7;
		String logMsg = "0123456789";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_17()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		int variable = 1;
		String logMsg = "";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_18()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		int variable = 0;
		String logMsg = "";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_19()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		int variable = 7;
		String logMsg = "";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_20()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		int variable = 0;
		String logMsg = "0123456789";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_21()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		int variable = 1;
		String logMsg = "0123456789";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_22()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		int variable = 0;
		String logMsg = "";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_23()
		throws Exception {
		String className = "";
		String methodName = "";
		int variable = 7;
		String logMsg = "";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddTimerLogging_24()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		int variable = 7;
		String logMsg = "0123456789";

		InsnList result = InstrumentUtil.addTimerLogging(className, methodName, variable, logMsg);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddloopCounterLogging_1()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMsg = "";
		int variable = 0;
		String info = "";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_2()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMsg = "0123456789";
		int variable = 1;
		String info = "0123456789";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_3()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMsg = "";
		int variable = 7;
		String info = "0123456789";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_4()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMsg = "";
		int variable = 1;
		String info = "";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_5()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMsg = "0123456789";
		int variable = 7;
		String info = "";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_6()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMsg = "0123456789";
		int variable = 0;
		String info = "0123456789";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_7()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMsg = "";
		int variable = 1;
		String info = "0123456789";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_8()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMsg = "";
		int variable = 0;
		String info = "";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_9()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMsg = "0123456789";
		int variable = 1;
		String info = "";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_10()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMsg = "";
		int variable = 7;
		String info = "0123456789";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_11()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMsg = "";
		int variable = 0;
		String info = "0123456789";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_12()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMsg = "0123456789";
		int variable = 7;
		String info = "";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_13()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMsg = "0123456789";
		int variable = 0;
		String info = "";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_14()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMsg = "";
		int variable = 1;
		String info = "0123456789";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_15()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMsg = "0123456789";
		int variable = 7;
		String info = "0123456789";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_16()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMsg = "0123456789";
		int variable = 1;
		String info = "";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_17()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMsg = "";
		int variable = 7;
		String info = "";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_18()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMsg = "";
		int variable = 0;
		String info = "0123456789";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_19()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMsg = "0123456789";
		int variable = 1;
		String info = "0123456789";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_20()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMsg = "0123456789";
		int variable = 0;
		String info = "";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_21()
		throws Exception {
		String className = "0123456789";
		String methodName = "";
		String logMsg = "";
		int variable = 1;
		String info = "";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_22()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMsg = "0123456789";
		int variable = 7;
		String info = "0123456789";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_23()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMsg = "0123456789";
		int variable = 0;
		String info = "0123456789";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_24()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMsg = "";
		int variable = 7;
		String info = "";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_25()
		throws Exception {
		String className = "";
		String methodName = "0123456789";
		String logMsg = "";
		int variable = 0;
		String info = "";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_26()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMsg = "0123456789";
		int variable = 1;
		String info = "0123456789";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_27()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMsg = "";
		int variable = 7;
		String info = "0123456789";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_28()
		throws Exception {
		String className = "";
		String methodName = "";
		String logMsg = "";
		int variable = 1;
		String info = "";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_29()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMsg = "0123456789";
		int variable = 7;
		String info = "";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testAddloopCounterLogging_30()
		throws Exception {
		String className = "0123456789";
		String methodName = "0123456789";
		String logMsg = "0123456789";
		int variable = 0;
		String info = "0123456789";

		InsnList result = InstrumentUtil.addloopCounterLogging(className, methodName, logMsg, variable, info);

		assertNotNull(result);
		assertEquals(7, result.size());
	}

	@Test
	public void testCreateBasicLoggerInsns_1()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("", "", "", "");

		InsnList result = InstrumentUtil.createBasicLoggerInsns(logBean);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testCreateBasicLoggerInsns_2()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("0123456789", "0123456789", "0123456789", "0123456789");

		InsnList result = InstrumentUtil.createBasicLoggerInsns(logBean);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testCreateBasicLoggerInsns_3()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");

		InsnList result = InstrumentUtil.createBasicLoggerInsns(logBean);

		assertNotNull(result);
		assertEquals(5, result.size());
	}

	@Test
	public void testCreateBasicLoggerInsns_4()
		throws Exception {
		LogInfoBean logBean = new LogInfoBean((String) null, (String) null, (String) null, (String) null);

		InsnList result = InstrumentUtil.createBasicLoggerInsns(logBean);

		assertNotNull(result);
		assertEquals(4, result.size());
	}

	
	

	@Test
	public void testGetAllUDFArray_3()
		throws Exception {
		ArrayList<MethodInsnNode> list = new ArrayList<MethodInsnNode>();
		list.add(new MethodInsnNode(0, "", "", ""));

		String[][] result = InstrumentUtil.getAllUDFArray(list);

		assertNotNull(result);
		assertEquals(1, result.length);
		assertNotNull(result[0]);
		assertEquals(3, result[0].length);
		assertEquals("", result[0][0]);
		assertEquals("", result[0][1]);
		assertEquals("", result[0][2]);
	}

	@Test
	public void testGetAllUDFArray_4()
		throws Exception {
		ArrayList<MethodInsnNode> list = new ArrayList<MethodInsnNode>();
		list.add(new MethodInsnNode(1, "0123456789", "0123456789", "0123456789"));

		String[][] result = InstrumentUtil.getAllUDFArray(list);

		assertNotNull(result);
		assertEquals(1, result.length);
		assertNotNull(result[0]);
		assertEquals(3, result[0].length);
		assertEquals("0123456789", result[0][0]);
		assertEquals("0123456789", result[0][1]);
		assertEquals("0123456789", result[0][2]);
	}

	@Test
	public void testGetAllUDFArray_5()
		throws Exception {
		ArrayList<MethodInsnNode> list = new ArrayList<MethodInsnNode>();
		list.add(new MethodInsnNode(7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt"));

		String[][] result = InstrumentUtil.getAllUDFArray(list);

		assertNotNull(result);
		assertEquals(1, result.length);
		assertNotNull(result[0]);
		assertEquals(3, result[0].length);
		assertEquals("An��t-1.0.txt", result[0][0]);
		assertEquals("An��t-1.0.txt", result[0][1]);
		assertEquals("An��t-1.0.txt", result[0][2]);
	}


	@Test
	public void testGetAllUDFArray_8()
		throws Exception {
		LinkedList<MethodInsnNode> list = new LinkedList<MethodInsnNode>();
		list.add(new MethodInsnNode(0, "", "", ""));

		String[][] result = InstrumentUtil.getAllUDFArray(list);

		assertNotNull(result);
		assertEquals(1, result.length);
		assertNotNull(result[0]);
		assertEquals(3, result[0].length);
		assertEquals("", result[0][0]);
		assertEquals("", result[0][1]);
		assertEquals("", result[0][2]);
	}

	@Test
	public void testGetAllUDFArray_9()
		throws Exception {
		LinkedList<MethodInsnNode> list = new LinkedList<MethodInsnNode>();
		list.add(new MethodInsnNode(1, "0123456789", "0123456789", "0123456789"));

		String[][] result = InstrumentUtil.getAllUDFArray(list);

		assertNotNull(result);
		assertEquals(1, result.length);
		assertNotNull(result[0]);
		assertEquals(3, result[0].length);
		assertEquals("0123456789", result[0][0]);
		assertEquals("0123456789", result[0][1]);
		assertEquals("0123456789", result[0][2]);
	}

	@Test
	public void testGetAllUDFArray_10()
		throws Exception {
		LinkedList<MethodInsnNode> list = new LinkedList<MethodInsnNode>();
		list.add(new MethodInsnNode(7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt"));

		String[][] result = InstrumentUtil.getAllUDFArray(list);

		assertNotNull(result);
		assertEquals(1, result.length);
		assertNotNull(result[0]);
		assertEquals(3, result[0].length);
		assertEquals("An��t-1.0.txt", result[0][0]);
		assertEquals("An��t-1.0.txt", result[0][1]);
		assertEquals("An��t-1.0.txt", result[0][2]);
	}



	@Test
	public void testGetAllUDFArray_13()
		throws Exception {
		Vector<MethodInsnNode> list = new Vector<MethodInsnNode>();
		list.add(new MethodInsnNode(0, "", "", ""));

		String[][] result = InstrumentUtil.getAllUDFArray(list);

		assertNotNull(result);
		assertEquals(1, result.length);
		assertNotNull(result[0]);
		assertEquals(3, result[0].length);
		assertEquals("", result[0][0]);
		assertEquals("", result[0][1]);
		assertEquals("", result[0][2]);
	}

	@Test
	public void testGetAllUDFArray_14()
		throws Exception {
		Vector<MethodInsnNode> list = new Vector<MethodInsnNode>();
		list.add(new MethodInsnNode(1, "0123456789", "0123456789", "0123456789"));

		String[][] result = InstrumentUtil.getAllUDFArray(list);

		assertNotNull(result);
		assertEquals(1, result.length);
		assertNotNull(result[0]);
		assertEquals(3, result[0].length);
		assertEquals("0123456789", result[0][0]);
		assertEquals("0123456789", result[0][1]);
		assertEquals("0123456789", result[0][2]);
	}

	@Test
	public void testGetAllUDFArray_15()
		throws Exception {
		Vector<MethodInsnNode> list = new Vector<MethodInsnNode>();
		list.add(new MethodInsnNode(7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt"));

		String[][] result = InstrumentUtil.getAllUDFArray(list);

		assertNotNull(result);
		assertEquals(1, result.length);
		assertNotNull(result[0]);
		assertEquals(3, result[0].length);
		assertEquals("An��t-1.0.txt", result[0][0]);
		assertEquals("An��t-1.0.txt", result[0][1]);
		assertEquals("An��t-1.0.txt", result[0][2]);
	}

	@Test
	public void testGetAllUDFArray_16()
		throws Exception {
		List<MethodInsnNode> list = new ArrayList<MethodInsnNode>();

		String[][] result = InstrumentUtil.getAllUDFArray(list);

		assertNotNull(result);
		assertEquals(0, result.length);
	}

	@Test
	public void testGetAllUDFArray_17()
		throws Exception {
		List<MethodInsnNode> list = new LinkedList<MethodInsnNode>();

		String[][] result = InstrumentUtil.getAllUDFArray(list);

		assertNotNull(result);
		assertEquals(0, result.length);
	}

	@Test
	public void testGetAllUDFArray_18()
		throws Exception {
		List<MethodInsnNode> list = new Vector<MethodInsnNode>();

		String[][] result = InstrumentUtil.getAllUDFArray(list);

		assertNotNull(result);
		assertEquals(0, result.length);
	}


	@Test
	public void testGetBytesFromFile_3()
		throws Exception {
		File inputFile = File.createTempFile("0123456789", "0123456789");

		byte[] result = InstrumentUtil.getBytesFromFile(inputFile);

		assertNotNull(result);
		assertEquals(0, result.length);
	}

	@Test
	public void testGetBytesFromFile_4()
		throws Exception {
		File inputFile = File.createTempFile("0123456789", "0123456789", (File) null);

		byte[] result = InstrumentUtil.getBytesFromFile(inputFile);

		assertNotNull(result);
		assertEquals(0, result.length);
	}

	@Test
	public void testGetBytesFromFile_5()
		throws Exception {
		File inputFile = File.createTempFile("An��t-1.0.txt", "An��t-1.0.txt", (File) null);

		byte[] result = InstrumentUtil.getBytesFromFile(inputFile);

		assertNotNull(result);
		assertEquals(0, result.length);
	}



	@Test(expected = java.io.FileNotFoundException.class)
	public void testGetBytesFromFile_7()
		throws Exception {
		File inputFile = new File("");

		byte[] result = InstrumentUtil.getBytesFromFile(inputFile);

		assertNotNull(result);
	}

	@Test(expected = java.io.FileNotFoundException.class)
	public void testGetBytesFromFile_8()
		throws Exception {
		File inputFile = new File("", "");

		byte[] result = InstrumentUtil.getBytesFromFile(inputFile);

		assertNotNull(result);
	}

	@Test(expected = java.io.FileNotFoundException.class)
	public void testGetBytesFromFile_9()
		throws Exception {
		File inputFile = new File("0123456789", "0123456789");

		byte[] result = InstrumentUtil.getBytesFromFile(inputFile);

		assertNotNull(result);
	}

	@Test(expected = java.io.FileNotFoundException.class)
	public void testGetBytesFromFile_10()
		throws Exception {
		File inputFile = new File((File) null, "");

		byte[] result = InstrumentUtil.getBytesFromFile(inputFile);

		assertNotNull(result);
	}

	@Test(expected = java.io.FileNotFoundException.class)
	public void testGetBytesFromFile_11()
		throws Exception {
		File inputFile = new File((File) null, "0123456789");

		byte[] result = InstrumentUtil.getBytesFromFile(inputFile);

		assertNotNull(result);
	}

	@Test
	public void testGetEntryBytesFromZip_1()
		throws Exception {
		ZipInputStream inputStream = new ZipInputStream(new ByteArrayInputStream("".getBytes()));

		byte[] result = InstrumentUtil.getEntryBytesFromZip(inputStream);

		assertNotNull(result);
		assertEquals(0, result.length);
	}

	

	@Test
	public void testIsExitMethod_5()
		throws Exception {
		MethodNode min = new MethodNode(0, "", "", "", new String[] {"", "0123456789", "An��t-1.0.txt", null});

		boolean result = InstrumentUtil.isExitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsExitMethod_6()
		throws Exception {
		MethodNode min = new MethodNode(0, 0, "", "", "", new String[] {"", "0123456789", "An��t-1.0.txt", null});

		boolean result = InstrumentUtil.isExitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsExitMethod_7()
		throws Exception {
		MethodNode min = new MethodNode(0, 0, "An��t-1.0.txt", "An��t-1.0.txt", "0123456789", new String[] {null});

		boolean result = InstrumentUtil.isExitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsExitMethod_8()
		throws Exception {
		MethodNode min = new MethodNode(1, "0123456789", "0123456789", "0123456789", new String[] {""});

		boolean result = InstrumentUtil.isExitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsExitMethod_9()
		throws Exception {
		MethodNode min = new MethodNode(1, 1, "0123456789", "0123456789", "0123456789", new String[] {""});

		boolean result = InstrumentUtil.isExitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsExitMethod_10()
		throws Exception {
		MethodNode min = new MethodNode(7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", new String[] {"0123456789"});

		boolean result = InstrumentUtil.isExitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsExitMethod_11()
		throws Exception {
		MethodNode min = new MethodNode(7, 7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", new String[] {"0123456789"});

		boolean result = InstrumentUtil.isExitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsExitMethod_12()
		throws Exception {
		MethodNode min = new MethodNode(Integer.MAX_VALUE, Integer.MIN_VALUE, "0123456789", "0123456789", "", new String[] {null});

		boolean result = InstrumentUtil.isExitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsExitMethod_13()
		throws Exception {
		MethodNode min = new MethodNode(Integer.MIN_VALUE, "0123456789", "0123456789", "", new String[] {null});

		boolean result = InstrumentUtil.isExitMethod(min);

		assertEquals(false, result);
	}








	@Test
	public void testIsInitMethod_5()
		throws Exception {
		MethodNode min = new MethodNode(0, "", "", "", new String[] {"", "0123456789", "An��t-1.0.txt", null});

		boolean result = InstrumentUtil.isInitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsInitMethod_6()
		throws Exception {
		MethodNode min = new MethodNode(0, 0, "", "", "", new String[] {"", "0123456789", "An��t-1.0.txt", null});

		boolean result = InstrumentUtil.isInitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsInitMethod_7()
		throws Exception {
		MethodNode min = new MethodNode(0, 0, "An��t-1.0.txt", "An��t-1.0.txt", "0123456789", new String[] {null});

		boolean result = InstrumentUtil.isInitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsInitMethod_8()
		throws Exception {
		MethodNode min = new MethodNode(1, "0123456789", "0123456789", "0123456789", new String[] {""});

		boolean result = InstrumentUtil.isInitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsInitMethod_9()
		throws Exception {
		MethodNode min = new MethodNode(1, 1, "0123456789", "0123456789", "0123456789", new String[] {""});

		boolean result = InstrumentUtil.isInitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsInitMethod_10()
		throws Exception {
		MethodNode min = new MethodNode(7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", new String[] {"0123456789"});

		boolean result = InstrumentUtil.isInitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsInitMethod_11()
		throws Exception {
		MethodNode min = new MethodNode(7, 7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", new String[] {"0123456789"});

		boolean result = InstrumentUtil.isInitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsInitMethod_12()
		throws Exception {
		MethodNode min = new MethodNode(Integer.MAX_VALUE, Integer.MIN_VALUE, "0123456789", "0123456789", "", new String[] {null});

		boolean result = InstrumentUtil.isInitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsInitMethod_13()
		throws Exception {
		MethodNode min = new MethodNode(Integer.MIN_VALUE, "0123456789", "0123456789", "", new String[] {null});

		boolean result = InstrumentUtil.isInitMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsJobSubmissionMethod_2()
		throws Exception {
		MethodInsnNode min = new MethodInsnNode(0, "", "", "");

		boolean result = InstrumentUtil.isJobSubmissionMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsJobSubmissionMethod_3()
		throws Exception {
		MethodInsnNode min = new MethodInsnNode(1, "0123456789", "0123456789", "0123456789");

		boolean result = InstrumentUtil.isJobSubmissionMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsJobSubmissionMethod_4()
		throws Exception {
		MethodInsnNode min = new MethodInsnNode(7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");

		boolean result = InstrumentUtil.isJobSubmissionMethod(min);

		assertEquals(false, result);
	}



	@Test
	public void testIsOutputMethod_2()
		throws Exception {
		MethodInsnNode min = new MethodInsnNode(0, "", "", "");

		boolean result = InstrumentUtil.isOutputMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsOutputMethod_3()
		throws Exception {
		MethodInsnNode min = new MethodInsnNode(1, "0123456789", "0123456789", "0123456789");

		boolean result = InstrumentUtil.isOutputMethod(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsOutputMethod_4()
		throws Exception {
		MethodInsnNode min = new MethodInsnNode(7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");

		boolean result = InstrumentUtil.isOutputMethod(min);

		assertEquals(false, result);
	}

	
	@Test
	public void testIsOwnerJob_2()
		throws Exception {
		MethodInsnNode min = new MethodInsnNode(0, "", "", "");

		boolean result = InstrumentUtil.isOwnerJob(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsOwnerJob_3()
		throws Exception {
		MethodInsnNode min = new MethodInsnNode(1, "0123456789", "0123456789", "0123456789");

		boolean result = InstrumentUtil.isOwnerJob(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsOwnerJob_4()
		throws Exception {
		MethodInsnNode min = new MethodInsnNode(7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");

		boolean result = InstrumentUtil.isOwnerJob(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsSysntheticAccess_1()
		throws Exception {
		int access = 0;

		boolean result = InstrumentUtil.isSysntheticAccess(access);

		assertEquals(false, result);
	}

	@Test
	public void testIsSysntheticAccess_2()
		throws Exception {
		int access = 1;

		boolean result = InstrumentUtil.isSysntheticAccess(access);

		assertEquals(false, result);
	}

	@Test
	public void testIsSysntheticAccess_3()
		throws Exception {
		int access = 7;

		boolean result = InstrumentUtil.isSysntheticAccess(access);

		assertEquals(false, result);
	}

	
	@Test
	public void testSeparateMessage_2()
		throws Exception {
		Object msg = null;

		String result = InstrumentUtil.separateMessage(msg);

		assertEquals("|null", result);
	}


	@Test
	public void testSeparateMessage_4()
		throws Exception {
		Object msg = null;
		char separator = '\n';

		String result = InstrumentUtil.separateMessage(msg, separator);

		assertEquals("\nnull", result);
	}

	

	@Test
	public void testSeparateMessage_6()
		throws Exception {
		Object msg = null;
		char separator = '�';

		String result = InstrumentUtil.separateMessage(msg, separator);

		assertEquals("�null", result);
	}

	@Test
	public void testSeparateMessage_7()
		throws Exception {
		Object msg = null;
		char separator = ' ';

		String result = InstrumentUtil.separateMessage(msg, separator);

		assertEquals(" null", result);
	}

	

	@Test
	public void testSeparateMessage_9()
		throws Exception {
		Object msg = null;
		char separator = 'a';

		String result = InstrumentUtil.separateMessage(msg, separator);

		assertEquals("anull", result);
	}

	

	@Test
	public void testValidateMapMethod_1()
		throws Exception {
		MethodNode mn = new MethodNode();

		boolean result = InstrumentUtil.validateMapMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapMethod_2()
		throws Exception {
		MethodNode mn = new MethodNode(-1, -1, (String) null, (String) null, (String) null, new String[] {"An��t-1.0.txt"});

		boolean result = InstrumentUtil.validateMapMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapMethod_3()
		throws Exception {
		MethodNode mn = new MethodNode(-1, (String) null, (String) null, (String) null, new String[] {"An��t-1.0.txt"});

		boolean result = InstrumentUtil.validateMapMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapMethod_4()
		throws Exception {
		MethodNode mn = new MethodNode(0);

		boolean result = InstrumentUtil.validateMapMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapMethod_5()
		throws Exception {
		MethodNode mn = new MethodNode(0, "", "", "", new String[] {"", "0123456789", "An��t-1.0.txt", null});

		boolean result = InstrumentUtil.validateMapMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapMethod_6()
		throws Exception {
		MethodNode mn = new MethodNode(0, 0, "", "", "", new String[] {"", "0123456789", "An��t-1.0.txt", null});

		boolean result = InstrumentUtil.validateMapMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapMethod_7()
		throws Exception {
		MethodNode mn = new MethodNode(0, 0, "An��t-1.0.txt", "An��t-1.0.txt", "0123456789", new String[] {null});

		boolean result = InstrumentUtil.validateMapMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapMethod_8()
		throws Exception {
		MethodNode mn = new MethodNode(1, "0123456789", "0123456789", "0123456789", new String[] {""});

		boolean result = InstrumentUtil.validateMapMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapMethod_9()
		throws Exception {
		MethodNode mn = new MethodNode(1, 1, "0123456789", "0123456789", "0123456789", new String[] {""});

		boolean result = InstrumentUtil.validateMapMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapMethod_10()
		throws Exception {
		MethodNode mn = new MethodNode(7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", new String[] {"0123456789"});

		boolean result = InstrumentUtil.validateMapMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapMethod_11()
		throws Exception {
		MethodNode mn = new MethodNode(7, 7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", new String[] {"0123456789"});

		boolean result = InstrumentUtil.validateMapMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapMethod_12()
		throws Exception {
		MethodNode mn = new MethodNode(Integer.MAX_VALUE, Integer.MIN_VALUE, "0123456789", "0123456789", "", new String[] {null});

		boolean result = InstrumentUtil.validateMapMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapMethod_13()
		throws Exception {
		MethodNode mn = new MethodNode(Integer.MIN_VALUE, "0123456789", "0123456789", "", new String[] {null});

		boolean result = InstrumentUtil.validateMapMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapReduceMethod_1()
		throws Exception {
		MethodNode mn = new MethodNode();

		boolean result = InstrumentUtil.validateMapReduceMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapReduceMethod_2()
		throws Exception {
		MethodNode mn = new MethodNode(-1, -1, (String) null, (String) null, (String) null, new String[] {"An��t-1.0.txt"});

		boolean result = InstrumentUtil.validateMapReduceMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapReduceMethod_3()
		throws Exception {
		MethodNode mn = new MethodNode(-1, (String) null, (String) null, (String) null, new String[] {"An��t-1.0.txt"});

		boolean result = InstrumentUtil.validateMapReduceMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapReduceMethod_4()
		throws Exception {
		MethodNode mn = new MethodNode(0);

		boolean result = InstrumentUtil.validateMapReduceMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapReduceMethod_5()
		throws Exception {
		MethodNode mn = new MethodNode(0, "", "", "", new String[] {"", "0123456789", "An��t-1.0.txt", null});

		boolean result = InstrumentUtil.validateMapReduceMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapReduceMethod_6()
		throws Exception {
		MethodNode mn = new MethodNode(0, 0, "", "", "", new String[] {"", "0123456789", "An��t-1.0.txt", null});

		boolean result = InstrumentUtil.validateMapReduceMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapReduceMethod_7()
		throws Exception {
		MethodNode mn = new MethodNode(0, 0, "An��t-1.0.txt", "An��t-1.0.txt", "0123456789", new String[] {null});

		boolean result = InstrumentUtil.validateMapReduceMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapReduceMethod_8()
		throws Exception {
		MethodNode mn = new MethodNode(1, "0123456789", "0123456789", "0123456789", new String[] {""});

		boolean result = InstrumentUtil.validateMapReduceMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapReduceMethod_9()
		throws Exception {
		MethodNode mn = new MethodNode(1, 1, "0123456789", "0123456789", "0123456789", new String[] {""});

		boolean result = InstrumentUtil.validateMapReduceMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapReduceMethod_10()
		throws Exception {
		MethodNode mn = new MethodNode(7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", new String[] {"0123456789"});

		boolean result = InstrumentUtil.validateMapReduceMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapReduceMethod_11()
		throws Exception {
		MethodNode mn = new MethodNode(7, 7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", new String[] {"0123456789"});

		boolean result = InstrumentUtil.validateMapReduceMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapReduceMethod_12()
		throws Exception {
		MethodNode mn = new MethodNode(Integer.MAX_VALUE, Integer.MIN_VALUE, "0123456789", "0123456789", "", new String[] {null});

		boolean result = InstrumentUtil.validateMapReduceMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMapReduceMethod_13()
		throws Exception {
		MethodNode mn = new MethodNode(Integer.MIN_VALUE, "0123456789", "0123456789", "", new String[] {null});

		boolean result = InstrumentUtil.validateMapReduceMethod(mn);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMethodName_1()
		throws Exception {
		String srcName = "";
		String cmpName = "";

		boolean result = InstrumentUtil.validateMethodName(srcName, cmpName);

		assertEquals(true, result);
	}

	@Test
	public void testValidateMethodName_2()
		throws Exception {
		String srcName = "0123456789";
		String cmpName = "0123456789";

		boolean result = InstrumentUtil.validateMethodName(srcName, cmpName);

		assertEquals(true, result);
	}

	@Test
	public void testValidateMethodName_3()
		throws Exception {
		String srcName = "0123456789";
		String cmpName = "";

		boolean result = InstrumentUtil.validateMethodName(srcName, cmpName);

		assertEquals(false, result);
	}

	@Test
	public void testValidateMethodName_4()
		throws Exception {
		String srcName = "";
		String cmpName = "0123456789";

		boolean result = InstrumentUtil.validateMethodName(srcName, cmpName);

		assertEquals(false, result);
	}

	@Before
	public void setUp()
		throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
}