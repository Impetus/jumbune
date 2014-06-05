package org.jumbune.debugger.instrumentation.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jumbune.common.beans.InstructionsBean;
import org.jumbune.debugger.instrumentation.utils.MethodByteCodeUtil;
import org.junit.*;
import static org.junit.Assert.*;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class MethodByteCodeUtilTest {


	@Test
	public void testAddMethodCall_1()
		throws Exception {
		String owner = "";
		String methodName = "";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {0, 1, 7, -1, Integer.MAX_VALUE, Integer.MIN_VALUE};
		int variable = 0;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddMethodCall_2()
		throws Exception {
		String owner = "";
		String methodName = "";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {0};
		int variable = 1;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_3()
		throws Exception {
		String owner = "";
		String methodName = "";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {1};
		int variable = 7;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_4()
		throws Exception {
		String owner = "";
		String methodName = "";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {7};
		int variable = 7;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_5()
		throws Exception {
		String owner = "";
		String methodName = "";
		String signature = "0123456789";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {0};
		int variable = 0;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_6()
		throws Exception {
		String owner = "";
		String methodName = "";
		String signature = "0123456789";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {1};
		int variable = 1;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_7()
		throws Exception {
		String owner = "";
		String methodName = "";
		String signature = "0123456789";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {7};
		int variable = 1;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_8()
		throws Exception {
		String owner = "";
		String methodName = "0123456789";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {0, 1, 7, -1, Integer.MAX_VALUE, Integer.MIN_VALUE};
		int variable = 7;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddMethodCall_9()
		throws Exception {
		String owner = "";
		String methodName = "0123456789";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {1};
		int variable = 0;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_10()
		throws Exception {
		String owner = "";
		String methodName = "0123456789";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {7};
		int variable = 0;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_11()
		throws Exception {
		String owner = "";
		String methodName = "0123456789";
		String signature = "0123456789";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {0, 1, 7, -1, Integer.MAX_VALUE, Integer.MIN_VALUE};
		int variable = 1;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddMethodCall_12()
		throws Exception {
		String owner = "";
		String methodName = "0123456789";
		String signature = "0123456789";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {0};
		int variable = 7;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_13()
		throws Exception {
		String owner = "";
		String methodName = "0123456789";
		String signature = "0123456789";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {1};
		int variable = 7;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_14()
		throws Exception {
		String owner = "0123456789";
		String methodName = "";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {0, 1, 7, -1, Integer.MAX_VALUE, Integer.MIN_VALUE};
		int variable = 0;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddMethodCall_15()
		throws Exception {
		String owner = "0123456789";
		String methodName = "";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {0};
		int variable = 1;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_16()
		throws Exception {
		String owner = "0123456789";
		String methodName = "";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {1};
		int variable = 1;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_17()
		throws Exception {
		String owner = "0123456789";
		String methodName = "";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {7};
		int variable = 7;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_18()
		throws Exception {
		String owner = "0123456789";
		String methodName = "";
		String signature = "0123456789";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {0};
		int variable = 0;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_19()
		throws Exception {
		String owner = "0123456789";
		String methodName = "";
		String signature = "0123456789";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {1};
		int variable = 0;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_20()
		throws Exception {
		String owner = "0123456789";
		String methodName = "";
		String signature = "0123456789";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {7};
		int variable = 1;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_21()
		throws Exception {
		String owner = "0123456789";
		String methodName = "0123456789";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {0, 1, 7, -1, Integer.MAX_VALUE, Integer.MIN_VALUE};
		int variable = 7;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddMethodCall_22()
		throws Exception {
		String owner = "0123456789";
		String methodName = "0123456789";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {0};
		int variable = 7;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_23()
		throws Exception {
		String owner = "0123456789";
		String methodName = "0123456789";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {7};
		int variable = 0;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_24()
		throws Exception {
		String owner = "0123456789";
		String methodName = "0123456789";
		String signature = "0123456789";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {0, 1, 7, -1, Integer.MAX_VALUE, Integer.MIN_VALUE};
		int variable = 1;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddMethodCall_25()
		throws Exception {
		String owner = "0123456789";
		String methodName = "0123456789";
		String signature = "0123456789";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {0};
		int variable = 1;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_26()
		throws Exception {
		String owner = "0123456789";
		String methodName = "0123456789";
		String signature = "0123456789";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {1};
		int variable = 7;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_27()
		throws Exception {
		String owner = "";
		String methodName = "";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {0, 1, 7, -1, Integer.MAX_VALUE, Integer.MIN_VALUE};
		int variable = 1;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(8, result.size());
	}

	@Test
	public void testAddMethodCall_28()
		throws Exception {
		String owner = "";
		String methodName = "";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {0};
		int variable = 7;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_29()
		throws Exception {
		String owner = "";
		String methodName = "";
		String signature = "";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {7};
		int variable = 0;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testAddMethodCall_30()
		throws Exception {
		String owner = "";
		String methodName = "";
		String signature = "0123456789";
		Map<String, String[]> parameters = null;
		int[] variableIndexs = new int[] {0, 1, 7, -1, Integer.MAX_VALUE, Integer.MIN_VALUE};
		int variable = 1;

		InsnList result = MethodByteCodeUtil.addMethodCall(owner, methodName, signature, parameters, variableIndexs, variable);

		assertNotNull(result);
		assertEquals(8, result.size());
	}


	@Test
	public void testCreateTempVariableAndCopyValue_1()
		throws Exception {
		int variableIndex = 0;

		InsnList result = MethodByteCodeUtil.createTempVariableAndCopyValue(variableIndex);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testCreateTempVariableAndCopyValue_2()
		throws Exception {
		int variableIndex = 1;

		InsnList result = MethodByteCodeUtil.createTempVariableAndCopyValue(variableIndex);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testCreateTempVariableAndCopyValue_3()
		throws Exception {
		int variableIndex = 7;

		InsnList result = MethodByteCodeUtil.createTempVariableAndCopyValue(variableIndex);

		assertNotNull(result);
		assertEquals(3, result.size());
	}

	@Test
	public void testGetParamStartNode_1()
		throws Exception {
		AbstractInsnNode node = null;
		ArrayList<LocalVariableNode> locaVariables = new ArrayList<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("", "", "", new LabelNode(), new LabelNode(), 0));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_2()
		throws Exception {
		AbstractInsnNode node = null;
		ArrayList<LocalVariableNode> locaVariables = new ArrayList<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("", "", "", new LabelNode(), new LabelNode(), 0));
		locaVariables.add(new LocalVariableNode("", "", "", new LabelNode(new Label()), new LabelNode(), -1));
		locaVariables.add(new LocalVariableNode("0123456789", "0123456789", "0123456789", new LabelNode(new Label()), new LabelNode(new Label()), 1));
		locaVariables.add(new LocalVariableNode("0123456789", "0123456789", "0123456789", (LabelNode) null, new LabelNode(new Label()), Integer.MAX_VALUE));
		locaVariables.add(new LocalVariableNode("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", (LabelNode) null, (LabelNode) null, 7));
		locaVariables.add(new LocalVariableNode("An��t-1.0.txt", "An��t-1.0.txt", (String) null, new LabelNode(), (LabelNode) null, Integer.MIN_VALUE));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_3()
		throws Exception {
		AbstractInsnNode node = null;
		ArrayList<LocalVariableNode> locaVariables = new ArrayList<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("", "", "", new LabelNode(new Label()), new LabelNode(), -1));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_4()
		throws Exception {
		AbstractInsnNode node = null;
		ArrayList<LocalVariableNode> locaVariables = new ArrayList<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("0123456789", "0123456789", "0123456789", new LabelNode(new Label()), new LabelNode(new Label()), 1));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_5()
		throws Exception {
		AbstractInsnNode node = null;
		ArrayList<LocalVariableNode> locaVariables = new ArrayList<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("0123456789", "0123456789", "0123456789", (LabelNode) null, new LabelNode(new Label()), Integer.MAX_VALUE));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_6()
		throws Exception {
		AbstractInsnNode node = null;
		ArrayList<LocalVariableNode> locaVariables = new ArrayList<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", (LabelNode) null, (LabelNode) null, 7));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_7()
		throws Exception {
		AbstractInsnNode node = null;
		ArrayList<LocalVariableNode> locaVariables = new ArrayList<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("An��t-1.0.txt", "An��t-1.0.txt", (String) null, new LabelNode(), (LabelNode) null, Integer.MIN_VALUE));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_8()
		throws Exception {
		AbstractInsnNode node = null;
		LinkedList<LocalVariableNode> locaVariables = new LinkedList<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("", "", "", new LabelNode(), new LabelNode(), 0));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_9()
		throws Exception {
		AbstractInsnNode node = null;
		LinkedList<LocalVariableNode> locaVariables = new LinkedList<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("", "", "", new LabelNode(), new LabelNode(), 0));
		locaVariables.add(new LocalVariableNode("", "", "", new LabelNode(new Label()), new LabelNode(), -1));
		locaVariables.add(new LocalVariableNode("0123456789", "0123456789", "0123456789", new LabelNode(new Label()), new LabelNode(new Label()), 1));
		locaVariables.add(new LocalVariableNode("0123456789", "0123456789", "0123456789", (LabelNode) null, new LabelNode(new Label()), Integer.MAX_VALUE));
		locaVariables.add(new LocalVariableNode("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", (LabelNode) null, (LabelNode) null, 7));
		locaVariables.add(new LocalVariableNode("An��t-1.0.txt", "An��t-1.0.txt", (String) null, new LabelNode(), (LabelNode) null, Integer.MIN_VALUE));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_10()
		throws Exception {
		AbstractInsnNode node = null;
		LinkedList<LocalVariableNode> locaVariables = new LinkedList<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("", "", "", new LabelNode(new Label()), new LabelNode(), -1));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_11()
		throws Exception {
		AbstractInsnNode node = null;
		LinkedList<LocalVariableNode> locaVariables = new LinkedList<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("0123456789", "0123456789", "0123456789", new LabelNode(new Label()), new LabelNode(new Label()), 1));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_12()
		throws Exception {
		AbstractInsnNode node = null;
		LinkedList<LocalVariableNode> locaVariables = new LinkedList<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("0123456789", "0123456789", "0123456789", (LabelNode) null, new LabelNode(new Label()), Integer.MAX_VALUE));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_13()
		throws Exception {
		AbstractInsnNode node = null;
		LinkedList<LocalVariableNode> locaVariables = new LinkedList<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", (LabelNode) null, (LabelNode) null, 7));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_14()
		throws Exception {
		AbstractInsnNode node = null;
		LinkedList<LocalVariableNode> locaVariables = new LinkedList<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("An��t-1.0.txt", "An��t-1.0.txt", (String) null, new LabelNode(), (LabelNode) null, Integer.MIN_VALUE));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_15()
		throws Exception {
		AbstractInsnNode node = null;
		Vector<LocalVariableNode> locaVariables = new Vector<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("", "", "", new LabelNode(), new LabelNode(), 0));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_16()
		throws Exception {
		AbstractInsnNode node = null;
		Vector<LocalVariableNode> locaVariables = new Vector<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("", "", "", new LabelNode(), new LabelNode(), 0));
		locaVariables.add(new LocalVariableNode("", "", "", new LabelNode(new Label()), new LabelNode(), -1));
		locaVariables.add(new LocalVariableNode("0123456789", "0123456789", "0123456789", new LabelNode(new Label()), new LabelNode(new Label()), 1));
		locaVariables.add(new LocalVariableNode("0123456789", "0123456789", "0123456789", (LabelNode) null, new LabelNode(new Label()), Integer.MAX_VALUE));
		locaVariables.add(new LocalVariableNode("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", (LabelNode) null, (LabelNode) null, 7));
		locaVariables.add(new LocalVariableNode("An��t-1.0.txt", "An��t-1.0.txt", (String) null, new LabelNode(), (LabelNode) null, Integer.MIN_VALUE));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_17()
		throws Exception {
		AbstractInsnNode node = null;
		Vector<LocalVariableNode> locaVariables = new Vector<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("", "", "", new LabelNode(new Label()), new LabelNode(), -1));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_18()
		throws Exception {
		AbstractInsnNode node = null;
		Vector<LocalVariableNode> locaVariables = new Vector<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("0123456789", "0123456789", "0123456789", new LabelNode(new Label()), new LabelNode(new Label()), 1));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_19()
		throws Exception {
		AbstractInsnNode node = null;
		Vector<LocalVariableNode> locaVariables = new Vector<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("0123456789", "0123456789", "0123456789", (LabelNode) null, new LabelNode(new Label()), Integer.MAX_VALUE));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_20()
		throws Exception {
		AbstractInsnNode node = null;
		Vector<LocalVariableNode> locaVariables = new Vector<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt", (LabelNode) null, (LabelNode) null, 7));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_21()
		throws Exception {
		AbstractInsnNode node = null;
		Vector<LocalVariableNode> locaVariables = new Vector<LocalVariableNode>();
		locaVariables.add(new LocalVariableNode("An��t-1.0.txt", "An��t-1.0.txt", (String) null, new LabelNode(), (LabelNode) null, Integer.MIN_VALUE));

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_22()
		throws Exception {
		AbstractInsnNode node = null;
		List<LocalVariableNode> locaVariables = new ArrayList<LocalVariableNode>();

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_23()
		throws Exception {
		AbstractInsnNode node = null;
		List<LocalVariableNode> locaVariables = new LinkedList<LocalVariableNode>();

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	@Test
	public void testGetParamStartNode_24()
		throws Exception {
		AbstractInsnNode node = null;
		List<LocalVariableNode> locaVariables = new Vector<LocalVariableNode>();

		AbstractInsnNode result = MethodByteCodeUtil.getParamStartNode(node, locaVariables);

		assertEquals(null, result);
	}

	
	@Test
	public void testIsAnonymousInitialization_1()
		throws Exception {
		MethodInsnNode min = new MethodInsnNode(-1, (String) null, (String) null, (String) null);

		boolean result = MethodByteCodeUtil.isAnonymousInitialization(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsAnonymousInitialization_2()
		throws Exception {
		MethodInsnNode min = new MethodInsnNode(0, "", "", "");

		boolean result = MethodByteCodeUtil.isAnonymousInitialization(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsAnonymousInitialization_3()
		throws Exception {
		MethodInsnNode min = new MethodInsnNode(1, "0123456789", "0123456789", "0123456789");

		boolean result = MethodByteCodeUtil.isAnonymousInitialization(min);

		assertEquals(false, result);
	}

	@Test
	public void testIsAnonymousInitialization_4()
		throws Exception {
		MethodInsnNode min = new MethodInsnNode(7, "An��t-1.0.txt", "An��t-1.0.txt", "An��t-1.0.txt");

		boolean result = MethodByteCodeUtil.isAnonymousInitialization(min);

		assertEquals(false, result);
	}

	

	@Before
	public void setUp()
		throws Exception {
	}

	@After
	public void tearDown()
		throws Exception {
	}
}