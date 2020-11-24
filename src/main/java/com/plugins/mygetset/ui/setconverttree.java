package com.plugins.mygetset.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.intellij.ui.CollectionListModel;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreePath;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class setconverttree extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JScrollPane thisJScrollPane;
	private JScrollPane toJScrollPane;
	private JPanel JPanel1;
	private JSplitPane JSplitPane1;
	private JPanel JPanel2;
	private JPanel JPanel3;
	private JSpinner spinner1;
	private JRadioButton voidRadioButton;
	private JRadioButton linkRadioButton;

	private AnActionEvent a;
	private PsiMethod psiMethod;
	private PsiClass psiClass;

	private JXTreeTable thistreetable;
	private JXTreeTable totreetable;

	public setconverttree(PsiClass psiClass, PsiMethod psiMethod, AnActionEvent e) {

		//region 系统
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});

		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		//endregion

		JPanel1.setOpaque(false);
		JPanel1.setBorder(new EmptyBorder(-1, -1, -1, -1));
		JPanel2.setBorder(new EmptyBorder(-1, -1, -1, -1));
		JPanel3.setBorder(new EmptyBorder(-1, -1, -1, -1));
		JSplitPane1.setBorder(new EmptyBorder(-1, -1, -1, -1));

		this.a = e;
		this.psiMethod = psiMethod;
		this.psiClass = psiClass;

		SpinnerModel model = new SpinnerNumberModel();
		spinner1 = new JSpinner(model);
		JFormattedTextField tf = ((JSpinner.NumberEditor)spinner1.getEditor()).getTextField();
		tf.setEditable(false);
		spinner1.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				//SpinnerModel source = (SpinnerModel) e.getSource();
				thistreetable.setTreeTableModel(new TreeTableModel().newthisTreeTableModel(GetConstructTabelField()));
				thistreetable.expandAll();

			}
		});
		createtree();
	}

	/**
	 * 获取类中所有的字段对应的Set与Get方法
	 * @param e
	 * @return
	 */
	private TreeNode thisClass(AnActionEvent e) {
		PsiClass paraname = psiClass;
		TreeNode treeNodeRoot = new TreeNode();

		List<PsiClass> psiClasses = new ArrayList<>();
		psiClasses.add(paraname);
		getExtendsListTypes(psiClasses,paraname);
		for(PsiClass psiClass : psiClasses){
			TreeNode treeNodeclass = new TreeNode();
			treeNodeclass.setIndex(1).setParent(treeNodeRoot).setName(psiClass.getName()).setValue("");
			treeNodeRoot.setChildren(treeNodeclass);
			List<PsiField> fields = new CollectionListModel<PsiField>(psiClass.getFields()).getItems();
			List<PsiMethod> methods = new CollectionListModel<PsiMethod>(psiClass.getMethods()).getItems();
			for (PsiField pf : fields) {
				for (PsiMethod pmd : methods) {
					TreeNode treeNode = new TreeNode();
					if (pmd.getName().toLowerCase().equals("set" + pf.getName().toLowerCase())) {
						treeNode.setName(pmd.getName()).setPsimethod(pmd).setPsiField(pf).setParent(treeNodeRoot);
						treeNodeclass.setChildren(treeNode);
					}
				}
			}
		}

		return treeNodeRoot;
	}

	/**
	 * 获取当前的光标在的方法的类
	 *
	 * @param e
	 * @return
	 */
	private TreeNode thisMethodClass(AnActionEvent e) {
		PsiMethod pm = psiMethod;
		/**
		 * 获取到类中的字段
		 */
		PsiParameterList pl = pm.getParameterList();
		TreeNode treeNodeRoot = new TreeNode();
		if (pl.getParameters().length > 0) {
			String paramname = pl.getParameters()[0].getName();
			//只有支持单一参数
			List<PsiClass> psiClasses = new ArrayList<>();
			PsiClass paraname = PsiUtil.resolveClassInType(pl.getParameters()[0].getTypeElement().getType());
			psiClasses.add(paraname);
			getExtendsListTypes(psiClasses,paraname);
			for(PsiClass psiClass : psiClasses){
				TreeNode treeNodeclass = new TreeNode();
				//方法里面变量名称
				treeNodeclass.setVariablename(paramname).setName(psiClass.getName()).setValue("").setIndex(1).setParent(treeNodeRoot);
				treeNodeRoot.setChildren(treeNodeclass);
				List<PsiField> fields = new CollectionListModel<PsiField>(psiClass.getFields()).getItems();
				List<PsiMethod> methods = new CollectionListModel<PsiMethod>(psiClass.getMethods()).getItems();
				for (PsiField pf : fields) {
					for (PsiMethod pmd : methods) {
						TreeNode treeNode = new TreeNode();
						if (pmd.getName().toLowerCase().equals("get" + pf.getName().toLowerCase())) {
							treeNode.setPsimethod(pmd).setPsiField(pf).setParent(treeNodeclass).setName(pmd.getName());
							treeNodeclass.setChildren(treeNode);
						}
					}
				}
			}
		}
		return treeNodeRoot;
	}

	/**
	 * 递归获取到继承的父类
	 * @param psiClassList
	 * @param paraname
	 */
	private void getExtendsListTypes(List<PsiClass> psiClassList,PsiClass paraname){
		PsiClassType[] psiClassTypes = paraname.getExtendsListTypes();
		if(psiClassTypes.length > 0){
			PsiClass psiClasses = psiClassTypes[0].resolve();
			psiClassList.add(psiClasses);
			getExtendsListTypes(psiClassList,psiClasses);
		}
	}

	/**
	 * 获取到对比情况
	 *
	 * @return
	 */
	private TreeNode GetConstructTabelField() {
		TreeNode treeNode = thisClass(a);
		TreeNode treeNode1 = thisMethodClass(a);
		if (treeNode.getChildren().size() > 0 && treeNode1.getChildren().size() > 0) {
			List<TreeNode> treeNode_f2s = new ArrayList<TreeNode>();
			List<TreeNode> treeNode_f3s = new ArrayList<TreeNode>();
			//类
			for (TreeNode treeNode2 : treeNode.getChildren()) {
				//字段
				for (TreeNode treeNode_f2 : treeNode2.getChildren()) {
					treeNode_f2s.add(treeNode_f2);
				}
			}
			for (TreeNode treeNode3 : treeNode1.getChildren()) {
				for (TreeNode treeNode_f3 : treeNode3.getChildren()) {
					treeNode_f3s.add(treeNode_f3);
				}
			}
			for (TreeNode nf1 : treeNode_f2s) {
				for (TreeNode nf2 : treeNode_f3s) {
					if (nf1.getPsimethod() != null && nf2.getPsimethod() != null) {
						if (nf1.getPsiField().getName().equals(nf2.getPsiField().getName())) {
							nf1.setValue(nf2.getPsimethod().getName()).setConvert(nf2);
						} else if ((int) spinner1.getValue() > 0) {
							String met = nf2.getPsiField().getName().substring((Integer) spinner1.getValue(), nf2.getPsiField().getName().length());
							if (nf1.getPsiField().getName().equals(met)) {
								nf1.setValue(nf2.getPsimethod().getName()).setConvert(nf2);
							}
						}
					}
				}
			}
		}
		return treeNode;
	}

	/**
	 * 创建树
	 */
	private void createtree() {
		thistreetable = new JXTreeTable(new TreeTableModel().newthisTreeTableModel(GetConstructTabelField()));
		thistreetable.expandAll();
		thistreetable.setCellSelectionEnabled(false);
		final DefaultListSelectionModel thisdefaultListSelectionModel = new DefaultListSelectionModel();
		thistreetable.setSelectionModel(thisdefaultListSelectionModel);
		thisdefaultListSelectionModel.setSelectionMode(SINGLE_SELECTION);
		thistreetable.setRowHeight(25);
		thisJScrollPane.setViewportView(thistreetable);

		totreetable = new JXTreeTable(new TreeTableModel().newtoTreeTableModel(thisMethodClass(a)));
		totreetable.expandAll();
		totreetable.setCellSelectionEnabled(false);
		final DefaultListSelectionModel todefaultListSelectionModel = new DefaultListSelectionModel();
		totreetable.setSelectionModel(todefaultListSelectionModel);
		todefaultListSelectionModel.setSelectionMode(SINGLE_SELECTION);
		totreetable.setRowHeight(25);
		toJScrollPane.setViewportView(totreetable);

	}


	public class TreeTableModel {

		public thisTreeTableModel newthisTreeTableModel(TreeNode myroot) {
			return new thisTreeTableModel(myroot);
		}

		public toTreeTableModel newtoTreeTableModel(TreeNode myroot) {
			return new toTreeTableModel(myroot);
		}

		public class thisTreeTableModel extends AbstractTreeTableModel {
			private TreeNode myroot;

			public thisTreeTableModel(TreeNode myroot) {
				this.myroot = myroot;
			}

			public TreeNode getdata() {
				return myroot;
			}

			@Override
			public int getColumnCount() {
				return 2;
			}

			// 定义表头数据
			@Override
			public String getColumnName(int column) {
				switch (column) {
					case 0:
						return "FieldName";
					case 1:
						return "SetValue";
					default:
						return "Unknown";
				}
			}

			public TreeNode[] getPathToRoot(TreeNode aNode) {
				List<TreeNode> path = new ArrayList();

				TreeNode node;
				for (node = aNode; node != this.root; node = node.getParent()) {
					path.add(0, node);
				}
				return (TreeNode[]) path.toArray(new TreeNode[0]);
			}

			/**
			 * 替换值
			 *
			 * @param value
			 * @param node
			 * @param column
			 */
			@Override
			public void setValueAt(Object value, Object node, int column) {
				//System.out.println("getValueAt: " + node + ", " + column);

				toTreeTableModel toTreeTableModel = (TreeTableModel.toTreeTableModel) totreetable.getTreeTableModel();
				TreeNode treeNode = toTreeTableModel.getdata();
				if(treeNode.getChildren() == null){
					return;
				}
				//类
				for (TreeNode treeNode1 : treeNode.getChildren()){
					//字段
					for (TreeNode treeNode2 : treeNode1.getChildren()){
						if(treeNode2.getName().equals((String)value)){
							TreeNode ttn = (TreeNode) node;
							ttn.setValue((String) value);
							ttn.setConvert(treeNode2);
							modelSupport.firePathChanged(new TreePath(getPathToRoot(ttn)));
						}
					}
				}

			}

			// 使表格具有可编辑性
			@Override
			public boolean isCellEditable(Object node, int column) {
				if (column == 0) {
					return false;
				}
				return true;
			}

			@Override
			public Object getValueAt(Object node, int column) {
				//System.out.println("getValueAt: " + node + ", " + column);
				TreeNode treenode = (TreeNode) node;
				switch (column) {
					case 0:
						return treenode.getName();
					case 1:
						return treenode.getValue();
					default:
						return "Unknown";
				}
			}

			@Override
			public Object getChild(Object node, int index) {
				TreeNode treenode = (TreeNode) node;
				return treenode.getChildren().get(index);
			}

			@Override
			public int getChildCount(Object parent) {
				TreeNode treenode = (TreeNode) parent;
				return treenode.getChildren().size();
			}

			@Override
			public int getIndexOfChild(Object parent, Object child) {
				TreeNode treenode = (TreeNode) parent;
				for (int i = 0; i > treenode.getChildren().size(); i++) {
					if (treenode.getChildren().get(i) == child) {
						return i;
					}
				}
				return 0;
			}

			@Override
			public boolean isLeaf(Object node) {
				TreeNode treenode = (TreeNode) node;
				if (treenode.getChildren().size() > 0) {
					return false;
				}
				return true;
			}

			@Override
			public Object getRoot() {
				return myroot;
			}

		}

		public class toTreeTableModel extends AbstractTreeTableModel {
			private TreeNode myroot;

			public toTreeTableModel(TreeNode myroot) {
				this.myroot = myroot;
			}

			public TreeNode getdata() {
				return myroot;
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			// 定义表头数据
			@Override
			public String getColumnName(int column) {
				switch (column) {
					case 0:
						return "FieldName";
					default:
						return "Unknown";
				}
			}

			public TreeNode[] getPathToRoot(TreeNode aNode) {
				List<TreeNode> path = new ArrayList();

				TreeNode node;
				for (node = aNode; node != this.root; node = node.getParent()) {
					path.add(0, node);
				}
				return (TreeNode[]) path.toArray(new TreeNode[0]);
			}

			/**
			 * 替换值
			 *
			 * @param value
			 * @param node
			 * @param column
			 */
			@Override
			public void setValueAt(Object value, Object node, int column) {
				//System.out.println("getValueAt: " + node + ", " + column);
				/*TreeNode ttn = (TreeNode) node;
				ttn.setValue((String) value);
				modelSupport.firePathChanged(new TreePath(getPathToRoot(ttn)));*/
			}

			// 使表格具有可编辑性
			@Override
			public boolean isCellEditable(Object node, int column) {
				return true;
			}

			@Override
			public Object getValueAt(Object node, int column) {
				//System.out.println("getValueAt: " + node + ", " + column);
				TreeNode treenode = (TreeNode) node;
				switch (column) {
					case 0:
						return treenode.getName();
					case 1:
						return treenode.getValue();
					default:
						return "Unknown";
				}
			}

			@Override
			public Object getChild(Object node, int index) {
				TreeNode treenode = (TreeNode) node;
				return treenode.getChildren().get(index);
			}

			@Override
			public int getChildCount(Object parent) {
				TreeNode treenode = (TreeNode) parent;
				return treenode.getChildren().size();
			}

			@Override
			public int getIndexOfChild(Object parent, Object child) {
				TreeNode treenode = (TreeNode) parent;
				for (int i = 0; i > treenode.getChildren().size(); i++) {
					if (treenode.getChildren().get(i) == child) {
						return i;
					}
				}
				return 0;
			}

			@Override
			public boolean isLeaf(Object node) {
				TreeNode treenode = (TreeNode) node;
				if (treenode.getChildren().size() > 0) {
					return false;
				}
				return true;
			}

			@Override
			public Object getRoot() {
				return myroot;
			}

		}
	}

	/**
	 * 构建代码
	 */
	public void build() {
		PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiMethod.getProject());
		class bulidCode {
			StringBuilder stringBuilder = new StringBuilder();
			void bulidLink(String method, String getclassname, String getfname) {
				stringBuilder.append(".").append(method);
				stringBuilder.append("(").append(getclassname).append(".").append(getfname).append("()").append(")").append("\n");
			}

			public String toLString() {
				String bu = stringBuilder.toString();
				return "this"+bu.substring(0, bu.length() - 1)+";";
			}

			List<PsiStatement> psiStatements = new ArrayList<>();
			void bulidVoid(String method, String getclassname, String getfname) {
				StringBuilder stringBuilderl = new StringBuilder();
				stringBuilderl.append("this.")
						.append(method)
						.append("(").append(getclassname).append(".").append(getfname).append("()").append(")").append(";\n");
				psiStatements.add(elementFactory.createStatementFromText(stringBuilderl.toString(),null));
			}
			public List<PsiStatement> toVString() {
				return psiStatements;
			}
		}

		TreeTableModel.thisTreeTableModel thisTreeTableModel = (TreeTableModel.thisTreeTableModel) thistreetable.getTreeTableModel();
		TreeNode treeNode = thisTreeTableModel.getdata();
		if(treeNode.getChildren() == null){
			return;
		}
		//类名称
		//TreeNode treeNode1 = treeNode.getChildren().get(0);
		//字段
		bulidCode link = new bulidCode();
		for (TreeNode treeNode1 : treeNode.getChildren()){
			List<TreeNode> treeNode2 = treeNode1.getChildren();
			for (TreeNode treeNode3 : treeNode2) {
				if(treeNode3.getConvert()!=null){
					if(linkRadioButton.isSelected()){
						link.bulidLink(
								treeNode3.getPsimethod().getName(),
								treeNode3.getConvert().getParent().getVariablename(),
								treeNode3.getConvert().getPsimethod().getName()
						);
					}else{
						link.bulidVoid(
								treeNode3.getPsimethod().getName(),
								treeNode3.getConvert().getParent().getVariablename(),
								treeNode3.getConvert().getPsimethod().getName()
						);
					}
				}
			}
		}
		WriteCommandAction.runWriteCommandAction(a.getProject(), new Runnable() {
			@Override
			public void run() {
				if(linkRadioButton.isSelected()){
					psiMethod.getBody().add(elementFactory.createStatementFromText(link.toLString(),null));
				}else{
					for(PsiStatement psiStatement : link.toVString()){
						psiMethod.getBody().add(psiStatement);
					}
				}
			}
		});
	}

	/**
	 * 目录节点
	 */
	private class TreeNode {
		private int index;
		private String name;
		//变量名称
		private String variablename;
		private String value;
		private PsiField psiField;
		private PsiMethod psimethod;
		private TreeNode parent;
		/**
		 * 匹配相同的
		 */
		private TreeNode convert;

		private List<TreeNode> children = new ArrayList<TreeNode>();

		public int getIndex() {
			return index;
		}

		public TreeNode setIndex(int index) {
			this.index = index;
			return this;
		}

		public String getName() {
			return this.name;
		}

		public TreeNode setName(String name) {
			this.name = name;
			return this;
		}

		public String getVariablename() {
			return variablename;
		}

		public TreeNode setVariablename(String variablename) {
			this.variablename = variablename;
			return this;
		}

		public String getValue() {
			return this.value;
		}

		public TreeNode setValue(String value) {
			this.value = value;
			return this;
		}

		public TreeNode getParent() {
			return parent;
		}

		public TreeNode setParent(TreeNode parent) {
			this.parent = parent;
			return this;
		}

		public com.intellij.psi.PsiField getPsiField() {
			return psiField;
		}

		public TreeNode setPsiField(com.intellij.psi.PsiField psiField) {
			this.psiField = psiField;
			return this;
		}

		public PsiMethod getPsimethod() {
			return psimethod;
		}

		public TreeNode setPsimethod(PsiMethod psimethod) {
			this.psimethod = psimethod;
			return this;
		}

		public List<TreeNode> getChildren() {
			return this.children;
		}

		public TreeNode setChildren(TreeNode children) {
			this.children.add(children);
			return this;
		}

		public TreeNode getConvert() {
			return convert;
		}

		public TreeNode setConvert(TreeNode convert) {
			this.convert = convert;
			return this;
		}
	}

	private void onOK() {
		// add your code here
		build();
		dispose();
	}

	private void onCancel() {
		// add your code here if necessary
		dispose();
	}

}
