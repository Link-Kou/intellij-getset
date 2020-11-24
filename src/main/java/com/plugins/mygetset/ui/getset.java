package com.plugins.mygetset.ui;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;
import com.intellij.ui.CollectionListModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class getset extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTable table1;
	private JLabel label_tishi;
	private JRadioButton radioButton_zueijia;
	private JRadioButton radioButton_loushi;
	private javax.swing.JScrollPane JScrollPane;
	private JPanel JPanel2;

	private static final String GET = "获取";
	private static final String SET = "设置";

	private PsiElement thisPrev;
	private PsiClass psiClasss;
	/**
	 * 字段数据
	 */
	public static List<FieldsOrMethod>  lfom;

	/**
	 * 构造
	 * @param e
	 * @param thisPrev
	 */
	public getset(PsiClass e, PsiElement thisPrev) {
		this.thisPrev = thisPrev;
		//this.setUndecorated(true);

		//#region 其他
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
		JScrollPane.setBorder(null);
		JPanel2.setBorder(new EmptyBorder(-1, -1, -3, -3));
		//#endregion

		table(e);

	}

	/**
	 * 设置表格属性
	 * @param e
	 */
	private void table(PsiClass e){
		lfom = getAllCanFields(e);
		MyAbstractTableModel1 myAbstractTableModel1 = new MyAbstractTableModel1(lfom);
		table1.setBorder(null);
		table1.setBackground(null);
		table1.setModel(myAbstractTableModel1);
		table1.setShowGrid(false);
	}

	/**
	 * 根据当前class文件，构建可以getset的对象列表
	 * @param psiClass
	 * @return
	 */
	private List<FieldsOrMethod> getAllCanFields(PsiClass psiClass){
		this.psiClasss = psiClass;
		List<FieldsOrMethod> fieldsname = new ArrayList<FieldsOrMethod>();
		List<PsiField> fields = new CollectionListModel<PsiField>(psiClass.getFields()).getItems();
		if (fields == null) {
			return fieldsname;
		}
		List<PsiMethod> list = new CollectionListModel<PsiMethod>(psiClass.getMethods()).getItems();
		Set<String> methodSet = new HashSet<String>();
		for (PsiMethod m : list) {
			methodSet.add(m.getName());
		}
		PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
		for (PsiField field : fields) {
			FieldsOrMethod fieldsOrMethod = new FieldsOrMethod();
			if (field.getModifierList().hasModifierProperty(PsiModifier.FINAL)) {
				continue;
			}
			fieldsOrMethod.setField(field);
			String methodText = buildGet(field);
			PsiMethod toMethod = elementFactory.createMethodFromText(methodText, psiClass);
			if (methodSet.contains(toMethod.getName())) {
				fieldsOrMethod.setGet(false);
			}else {
				fieldsOrMethod.setGet(true).setGetMethod(toMethod).setGettext(methodText);
			}
			String setmethodText = buildSet(field);
			String setvoidmethodText = buildSetVoid(field);
			elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
			PsiMethod tothisMethod = elementFactory.createMethodFromText(setmethodText, psiClass);
			PsiMethod tovoidMethod = elementFactory.createMethodFromText(setvoidmethodText, psiClass);
			if (methodSet.contains(toMethod.getName())) {
				fieldsOrMethod.setSet(false);
			}else {
				fieldsOrMethod.setSet(true).setSetVoidMethod(tovoidMethod).setSetMethod(tothisMethod).setSetVoidtext(setvoidmethodText).setSettext(setmethodText);
			}
			if(fieldsOrMethod.isGet() || fieldsOrMethod.isSet()){
				fieldsname.add(fieldsOrMethod);
			}

		}
		return fieldsname;
	}

	/**
	 * 字段对象
	 */
	private class FieldsOrMethod{
		private int row;
		private PsiField field;
		private boolean set;
		private boolean setbuild;
		private boolean get;
		private boolean getbuild;
		private PsiMethod getMethod;
		private PsiMethod setMethod;
		private PsiMethod setVoidMethod;
		private String setVoidtext;
		private String gettext;
		private String settext;

		public int getRow() {
			return row;
		}

		public FieldsOrMethod setRow(int row) {
			this.row = row;
			return this;
		}

		public PsiField getField() {
			return field;
		}

		public FieldsOrMethod setField(PsiField field) {
			this.field = field;
			return this;
		}

		public boolean isSet() {
			return set;
		}

		public FieldsOrMethod setSet(boolean set) {
			this.set = set;
			this.setbuild = set;
			return this;
		}

		public boolean isGet() {
			return get;
		}

		public FieldsOrMethod setGet(boolean get) {
			this.get = get;
			this.getbuild = get;
			return this;
		}

		public boolean isSetbuild() {
			return setbuild;
		}

		public FieldsOrMethod setSetbuild(boolean setbuild) {
			this.setbuild = setbuild;
			return this;
		}

		public boolean isGetbuild() {
			return getbuild;
		}

		public FieldsOrMethod setGetbuild(boolean getbuild) {
			this.getbuild = getbuild;
			return this;
		}

		public PsiMethod getGetMethod() {
			return getMethod;
		}

		public FieldsOrMethod setGetMethod(PsiMethod getMethod) {
			this.getMethod = getMethod;
			return this;
		}

		public PsiMethod getSetMethod() {
			return setMethod;
		}

		public FieldsOrMethod setSetMethod(PsiMethod setMethod) {
			this.setMethod = setMethod;
			return this;
		}

		public PsiMethod getSetVoidMethod() {
			return setVoidMethod;
		}

		public FieldsOrMethod setSetVoidMethod(PsiMethod setVoidMethod) {
			this.setVoidMethod = setVoidMethod;
			return this;
		}

		public String getSetVoidtext() {
			return setVoidtext;
		}

		public FieldsOrMethod setSetVoidtext(String setVoidtext) {
			this.setVoidtext = setVoidtext;
			return this;
		}

		public String getGettext() {
			return gettext;
		}

		public FieldsOrMethod setGettext(String gettext) {
			this.gettext = gettext;
			return this;
		}

		public String getSettext() {
			return settext;
		}

		public FieldsOrMethod setSettext(String settext) {
			this.settext = settext;
			return this;
		}
	}

	/**
	 * 创建getset
	 * @param thisPrev
	 */
	private void createGetSet(PsiElement thisPrev) {
		new WriteCommandAction.Simple(psiClasss.getProject(), psiClasss.getContainingFile()) {
			@Override
			protected void run() throws Throwable {
				MyAbstractTableModel1 myAbstractTableModel1 = (MyAbstractTableModel1)table1.getModel();
				List<FieldsOrMethod>  lfo =myAbstractTableModel1.getFieldsOrMethod();
				PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClasss.getProject());
				for (FieldsOrMethod fod : lfo){
					if(fod.isGetbuild()){
						PsiMethod toMethod = elementFactory.createMethodFromText(fod.getGettext(), psiClasss);
						if(thisPrev == null || radioButton_zueijia.isSelected()){
							psiClasss.add(toMethod);
						}else {
							psiClasss.addAfter(toMethod,thisPrev);
						}
					}
					if(fod.isSetbuild()){
						PsiMethod toMethod;
						if(radioButton_loushi.isSelected()){
							toMethod = elementFactory.createMethodFromText(fod.getSettext(), psiClasss);
						}else {
							toMethod = elementFactory.createMethodFromText(fod.getSetVoidtext(), psiClasss);
						}
						if(thisPrev == null || radioButton_zueijia.isSelected()){
							psiClasss.add(toMethod);
						}else {
							psiClasss.addAfter(toMethod,thisPrev);
						}
					}
				}
			}
		}.execute();

	}

	/**
	 * 构建文本
	 * @param field
	 * @return
	 */
	private String buildGet(PsiField field) {
		StringBuilder sb = new StringBuilder();
		String doc = format(GET, field);
		if (doc != null) {
			sb.append(doc);
		}
		sb.append("public ");
		//判断字段是否是static
		if (field.getModifierList().hasModifierProperty(PsiModifier.STATIC)) {
			sb.append("static ");
		}
		sb.append(field.getType().getPresentableText() + " ");
		if (field.getType().getPresentableText().equals("boolean")) {
			sb.append("is");
		} else {
			sb.append("get");
		}
		sb.append(getFirstUpperCase(field.getName()));
		sb.append("(){\n");
		sb.append(" return this." + field.getName() + ";}\n");

		return sb.toString();
	}

	/**
	 * 构建文本
	 * @param field
	 * @return
	 */
	private String buildSet(PsiField field) {
		StringBuilder sb = new StringBuilder();
		String doc = format(SET, field);
		if (doc != null) {
			sb.append(doc);
		}
		sb.append("public ");
		//判断字段是否是static
		if (field.getModifierList().hasModifierProperty(PsiModifier.STATIC)) {
			sb.append("static ");
		}
		sb.append(field.getContainingClass().getName() + " ");
		sb.append("set" + getFirstUpperCase(field.getName()));
		sb.append("(" + field.getType().getPresentableText() + " " + field.getName() + "){\n");
		sb.append("this." + field.getName() + " = " + field.getName() + ";");
		sb.append("return this ;");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * 构建文本
	 * @param field
	 * @return
	 */
	private String buildSetVoid(PsiField field) {
		StringBuilder sb = new StringBuilder();
		String doc = format(SET, field);
		if (doc != null) {
			sb.append(doc);
		}
		sb.append("public ");
		//判断字段是否是static
		if (field.getModifierList().hasModifierProperty(PsiModifier.STATIC)) {
			sb.append("static ");
		}
		sb.append("void" + " ");
		sb.append("set" + getFirstUpperCase(field.getName()));
		sb.append("(" + field.getType().getPresentableText() + " " + field.getName() + "){\n");
		sb.append("this." + field.getName() + " = " + field.getName() + ";");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * 格式化
	 * @param string
	 * @param field
	 * @return
	 */
	private String format(String string, PsiField field) {
		String oldContent;
		if (field.getDocComment() == null) {
			oldContent = field.getText().substring(0, field.getText().lastIndexOf("\n") + 1);
		} else {
			oldContent = field.getDocComment().getText();
		}
		if (oldContent.startsWith("//")) {
			oldContent = oldContent.replace("// ", "// " + string + " ");
		} else if (oldContent.startsWith("/**")) {
			String content = oldContent.replace("/**\n", "").replace("*/", "").replace("\n", "").trim();
			oldContent = oldContent.replace(content, content.replace("* ", "* " + string + " "));
		}
		return oldContent;
	}

	/**
	 * 大小写转换
	 * @param oldStr
	 * @return
	 */
	private String getFirstUpperCase(String oldStr) {
		return oldStr.substring(0, 1).toUpperCase() + oldStr.substring(1);
	}

	private void onOK() {
		// add your code here
		//table(psiClasss);
		createGetSet(thisPrev);
		dispose();
	}

	private void onCancel() {
		// add your code here if necessary
		dispose();
	}


	@SuppressWarnings("serial")
	class MyAbstractTableModel1 extends AbstractTableModel {

		List<Object[]> lo = new ArrayList<Object[]>();
		List<FieldsOrMethod> lfm ;
		public MyAbstractTableModel1(List<FieldsOrMethod> lfm){
			this.lfm = lfm;
			int i = 0;
			for(FieldsOrMethod fom : lfm){
				fom.setRow(i++);
				lo.add(new Object[]{fom.getField().getName(),fom.isGet(),fom.isSet()});
			}
			data = lo.toArray(new Object[0][0]);
		}

		// 定义表头数据
		String[] head = { "Name","Get","Set"};

		// 定义表的内容数据
		//Object[] data1 = { "200913420125", new Boolean(true)};
		// 定义表格每一列的数据类型

		Class[] typeArray = { Object.class,Boolean.class, Boolean.class};

		Object[][] data;


		public List<FieldsOrMethod> getFieldsOrMethod() {
			return lfm;
		}

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
			if(columnIndex == 0){
				return false;
			}else {
				if(columnIndex == 1){
					if(lfm.get(rowIndex).isGet() == false ){
						label_tishi.setText(lfm.get(rowIndex).getField().getName() + " Get 已经设置");
						return false;
					}
				}else {
					if(lfm.get(rowIndex).isSet() == false ){
						label_tishi.setText(lfm.get(rowIndex).getField().getName() + " Set 已经设置");
						return false;
					}
				}
			}
			return true;
		}

		// 替换单元格的值
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			data[rowIndex][columnIndex] = aValue;
			if(columnIndex == 1){
				lfm.get(rowIndex).setGetbuild((Boolean) aValue);
			}else if(columnIndex == 2) {
				lfm.get(rowIndex).setSetbuild((Boolean) aValue);
			}
			fireTableCellUpdated(rowIndex, columnIndex);
		}

		// 实现了如果是boolean自动转成JCheckbox
		/*
		 * 需要自己的celleditor这么麻烦吧。jtable自动支持Jcheckbox，
		 * 只要覆盖tablemodel的getColumnClass返回一个boolean的class， jtable会自动画一个Jcheckbox给你，
		 * 你的value是true还是false直接读table里那个cell的值就可以
		 */
		@Override
		public Class getColumnClass(int columnIndex) {
			return typeArray[columnIndex];// 返回每一列的数据类型
		}

	}
}
