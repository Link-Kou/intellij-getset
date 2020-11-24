package com.plugins.mygetset.ui;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiType;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.ui.CollectionListModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class set extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JTable table1;
	private JRadioButton linkRadioButton;
	private JRadioButton voidOtherRadioButton;
	private JPanel JPanel1;

	private Editor editors;
	private PsiClass psiClasss;
	private String selectval;
	private int[] textRange;

	private static List<FieldsOrMethod> fieldsOrMethods;

	public set(PsiClass psiClass, Editor editor, String selectval, int[] textRange) {

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
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		contentPane.registerKeyboardAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		//endregion

		JPanel1.setBorder(new EmptyBorder(-1, -1, -3, -3));
		table1.setBorder(new EmptyBorder(-1, -1, -3, -3));

		linkRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JRadioButton temp = (JRadioButton) e.getSource();
				if (temp.isSelected()) {
					for (FieldsOrMethod fom : fieldsOrMethods) {
						if (fom.getSetMethod().getReturnType().getPresentableText().equals(psiClasss.getName())) {
							fom.setSetshow(true);
						} else {
							fom.setSetshow(false);
						}
					}
					MyAbstractTableModel1 myAbstractTableModel1 = new MyAbstractTableModel1(fieldsOrMethods);
					table1.setModel(myAbstractTableModel1);
					table1.setShowGrid(false);
				}
			}
		});

		voidOtherRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JRadioButton temp = (JRadioButton) e.getSource();
				if (temp.isSelected()) {
					for (FieldsOrMethod fom : fieldsOrMethods) {
						if (!fom.getSetMethod().getReturnType().getPresentableText().equals(psiClasss.getName())) {
							fom.setSetshow(true);
						} else {
							fom.setSetshow(false);
						}
					}
					MyAbstractTableModel1 myAbstractTableModel1 = new MyAbstractTableModel1(fieldsOrMethods);
					table1.setModel(myAbstractTableModel1);
					table1.setShowGrid(false);
				}
			}
		});

		this.editors = editor;
		this.psiClasss = psiClass;
		this.selectval = selectval;
		this.textRange = textRange;
		getAllMethod();
		MyAbstractTableModel1 myAbstractTableModel1 = new MyAbstractTableModel1(fieldsOrMethods);
		table1.setModel(myAbstractTableModel1);
		table1.setShowGrid(false);
	}

	/**
	 * 获取类里所有的方法
	 */
	private void getAllMethod() {
		List<PsiMethod> list = new CollectionListModel<PsiMethod>(psiClasss.getMethods()).getItems();
		List<FieldsOrMethod> methodSet = new ArrayList<FieldsOrMethod>();
		for (PsiMethod m : list) {
			if (m.getModifierList().hasModifierProperty(PsiModifier.PUBLIC)) {
				//所有的方法
				if (m.getParameterList().getParametersCount() > 0) {
					FieldsOrMethod fieldsOrMethod = new FieldsOrMethod();
					fieldsOrMethod.setSetMethod(m)
							.setSettext(m.getName());
					if (m.getReturnType().isConvertibleFrom(PsiType.VOID)) {
						fieldsOrMethod.setSetreturn("void").setSetbuild(false);
					} else if (m.getReturnType().getPresentableText().equals(psiClasss.getName())) {
						fieldsOrMethod.setSetreturn(psiClasss.getName()).setSetshow(true).setSetbuild(true);
					} else {
						fieldsOrMethod.setSetreturn(m.getReturnType().getPresentableText()).setSetbuild(false);
					}
					methodSet.add(fieldsOrMethod);
				}
			}
		}
		fieldsOrMethods = methodSet;
	}

	/**
	 * 类型对象
	 */
	private class FieldsOrMethod {
		/**
		 * 当前行
		 */
		private int row;
		/**
		 * 是否构建
		 */
		private boolean setshow;
		/**
		 * 是否构建
		 */
		private boolean setbuild;
		/**
		 * 方法对象
		 */
		private PsiMethod setMethod;
		/**
		 * 构建的文本
		 */
		private String settext;
		/**
		 * 返回对象的文本名称
		 */
		private String setreturn;


		/**
		 * 获取 当前行
		 */
		public int getRow() {
			return this.row;
		}

		/**
		 * 设置 当前行
		 */
		public FieldsOrMethod setRow(int row) {
			this.row = row;
			return this;
		}

		/**
		 * 获取 是否构建
		 */
		public boolean isSetshow() {
			return this.setshow;
		}

		/**
		 * 设置 是否构建
		 */
		public FieldsOrMethod setSetshow(boolean setshow) {
			this.setshow = setshow;
			return this;
		}

		/**
		 * 获取 是否构建
		 */
		public boolean isSetbuild() {
			return this.setbuild;
		}

		/**
		 * 设置 是否构建
		 */
		public FieldsOrMethod setSetbuild(boolean setbuild) {
			this.setbuild = setbuild;
			return this;
		}

		/**
		 * 获取 方法对象
		 */
		public PsiMethod getSetMethod() {
			return this.setMethod;
		}

		/**
		 * 设置 方法对象
		 */
		public FieldsOrMethod setSetMethod(PsiMethod setMethod) {
			this.setMethod = setMethod;
			return this;
		}

		/**
		 * 获取 构建的文本
		 */
		public String getSettext() {
			return this.settext;
		}

		/**
		 * 设置 构建的文本
		 */
		public FieldsOrMethod setSettext(String settext) {
			this.settext = settext;
			return this;
		}

		/**
		 * 获取 返回对象的文本名称
		 */
		public String getSetreturn() {
			return this.setreturn;
		}

		/**
		 * 设置 返回对象的文本名称
		 */
		public FieldsOrMethod setSetreturn(String setreturn) {
			this.setreturn = setreturn;
			return this;
		}
	}

	private void buildSet() {
		List<FieldsOrMethod> lvoid = new ArrayList<FieldsOrMethod>();
		List<FieldsOrMethod> lvthis = new ArrayList<FieldsOrMethod>();
		for (FieldsOrMethod fom : fieldsOrMethods) {
			if (fom.isSetbuild()) {
				if (fom.getSetMethod().getReturnType().isConvertibleFrom(PsiType.VOID)) {
					lvoid.add(fom);
				}
				if (fom.getSetMethod().getReturnType().getPresentableText().equals(psiClasss.getName())) {
					lvthis.add(fom);
				}else{
					//其他
					lvoid.add(fom);
				}
			}
		}
		final String selectedText = selectval;
		//构建viod或link
		if (linkRadioButton.isSelected()) {
			StringBuilder sbthis = new StringBuilder();
			if (lvthis.size() > 0) {
				sbthis.append(selectedText.replace(";", ""));
				for (FieldsOrMethod fom : lvthis) {
					sbthis.append(".").append(fom.getSetMethod().getName()).append("()\n");
				}
			}
			String text = sbthis.toString();
			replaceString(text.substring(0, text.length() - 1) + ";");
		} else {
			StringBuilder sbvoid = new StringBuilder();
			if (lvoid.size() > 0) {
				String name = psiClasss.getName().toLowerCase();
				sbvoid.append(selectedText);
				sbvoid.append("\n");
				for (FieldsOrMethod fom : lvoid) {
					sbvoid.append(name).append(".").append(fom.getSetMethod().getName() + "();\n");
				}
			}
			replaceString(sbvoid.toString());
		}
	}

	private void replaceString(String text) {
		final int start = textRange[0];
		final int end = textRange[1];

		WriteCommandAction.runWriteCommandAction(psiClasss.getProject(), new Runnable() {
			@Override
			public void run() {
				//TextRange
				editors.getDocument().replaceString(
						start,
						end,
						text);
				CodeStyleManager.getCurrentFormattingMode(psiClasss.getProject());//.getInstance(psiClasss.getProject()).reformatRange(psiClasss,start,end);
				//.reformat(psiClasss);
			}
		});
		editors.getSelectionModel().removeSelection();

	}

	private void onOK() {
		buildSet();
		dispose();
	}

	private void onCancel() {
		// add your code here if necessary
		dispose();
	}

	@SuppressWarnings("serial")
	class MyAbstractTableModel1 extends AbstractTableModel {

		List<Object[]> lo = new ArrayList<Object[]>();

		public MyAbstractTableModel1(List<FieldsOrMethod> lfm) {
			int i = 0;
			for (FieldsOrMethod fom : lfm) {
				fom.setRow(i++);
				if (fom.isSetshow()) {
					lo.add(new Object[]{fom.getSetMethod().getName(), fom.isSetbuild(), fom.getSetreturn(), fom});
				}
			}
			data = lo.toArray(new Object[0][0]);
		}

		// 定义表头数据
		String[] head = {"Name", "Set", "return"};

		Class[] typeArray = {Object.class, Boolean.class, Object.class, Object.class};

		// 定义表的内容数据
		Object[][] data;

		// 获得表格的列数
		@Override
		public int getColumnCount() {
			return head.length;
		}

		// 获得表格的行数
		@Override
		public int getRowCount() {
			return data.length;
		}

		// 获得表格的列名称
		@Override
		public String getColumnName(int column) {
			return head[column];
		}

		// 获得表格的单元格的数据
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return data[rowIndex][columnIndex];
		}

		// 使表格具有可编辑性
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return false;
			}
			return true;
		}

		// 替换单元格的值
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			data[rowIndex][columnIndex] = aValue;
			if (columnIndex == 1) {
				FieldsOrMethod fieldsOrMethod = (FieldsOrMethod) data[rowIndex][3];
				//fieldsOrMethods.get(rowIndex)
				fieldsOrMethod.setSetbuild((Boolean) aValue);
			}
			fireTableCellUpdated(rowIndex, columnIndex);
		}

		@Override
		public Class getColumnClass(int columnIndex) {
			return typeArray[columnIndex];// 返回每一列的数据类型
		}
	}
}
