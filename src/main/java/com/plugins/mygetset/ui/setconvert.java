package com.plugins.mygetset.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.util.PsiUtil;
import com.intellij.ui.CollectionListModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class setconvert extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JRadioButton linkRadioButton;
	private JRadioButton voidRadioButton;
	private JTable tablethis;
	private JTable tablefrom;
	private JLabel labelfrom;
	private JLabel labelthis;
	private JSpinner spinner1;

	private AnActionEvent a;
	private PsiMethod psiMethod;
	private PsiClass psiClass;
	private static List<Info[]> lfm;

	public setconvert(PsiClass psiClass,PsiMethod psiMethod,AnActionEvent e) {


		//#region 系统
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


		ChangeListener listener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				//SpinnerModel source = (SpinnerModel) e.getSource();
				MyAbstractTableModel1 myAbstractTableModel1 = new MyAbstractTableModel1(GetConstructTabelField(a), thisClass(a));
				tablefrom.setModel(myAbstractTableModel1);
				tablefrom.setShowGrid(false);
			}
		};
		spinner1.addChangeListener(listener);

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
		//#endregion

		this.a = e;
		this.psiMethod = psiMethod;
		this.psiClass = psiClass;

		MyAbstractTableModel1 myAbstractTableModel1 = new MyAbstractTableModel1(GetConstructTabelField(e), thisClass(e));
		tablefrom.setModel(myAbstractTableModel1);
		tablefrom.setShowGrid(false);

		MyAbstractTableModel2 myAbstractTableModel2 = new MyAbstractTableModel2(thisClass(e));
		tablethis.setModel(myAbstractTableModel2);
		tablethis.setShowGrid(false);
	}

	/**
	 * 获取类中所有的字段对应的Set与Get方法
	 *
	 * @param e
	 * @return
	 */
	private List<Info> thisClass(AnActionEvent e) {
		List<Info> infos = new ArrayList<Info>();
		PsiClass paraname = psiClass;
		List<PsiField> fields = new CollectionListModel<PsiField>(paraname.getFields()).getItems();
		List<PsiMethod> methods = new CollectionListModel<PsiMethod>(paraname.getMethods()).getItems();
		for (PsiField pf : fields) {
			Info info = new Info();
			for (PsiMethod pmd : methods) {
				if (pmd.getName().toLowerCase().equals("set" + pf.getName().toLowerCase())) {
					info.setPsimethod(pmd).setPsiField(pf);
				}
			}
			infos.add(info);
		}
		labelthis.setText(paraname.getName());
		return infos;
	}

	/**
	 * 获取当前的光标在的方法的类
	 *
	 * @param e
	 * @return
	 */
	private List<Info> thisMethodClass(AnActionEvent e) {
		PsiMethod pm = psiMethod;
		/**
		 * 获取到类中的字段
		 */
		PsiParameterList pl = pm.getParameterList();
		List<Info> infos = new ArrayList<Info>();
		if(pl.getParameters().length > 0){
			String paramname = pl.getParameters()[0].getName();
			PsiClass paraname = PsiUtil.resolveClassInType(pl.getParameters()[0].getTypeElement().getType());
			List<PsiField> fields = new CollectionListModel<PsiField>(paraname.getFields()).getItems();
			List<PsiMethod> methods = new CollectionListModel<PsiMethod>(paraname.getMethods()).getItems();
			for (PsiField pf : fields) {
				Info info = new Info();
				for (PsiMethod pmd : methods) {
					if (pmd.getName().toLowerCase().equals("get" + pf.getName().toLowerCase())) {
						info.setPsimethod(pmd).setPsiField(pf).setParaname(paramname);
					}
				}
				infos.add(info);
			}
			labelfrom.setText(paraname.getName());
			return infos;
		}
		return null;
	}

	/**
	 * 构建table
	 *
	 * @param e
	 * @return
	 */
	private List<Info[]> GetConstructTabelField(AnActionEvent e) {
		List<Info> set = thisClass(e);
		List<Info> get = thisMethodClass(e);
		List<Info[]> setget = new ArrayList<Info[]>();
		for (Info infoget : get) {
			int d = setget.size();
			for (Info infoset : set) {
				if (infoset.getPsimethod() != null && infoget.getPsimethod() != null) {
					if (infoset.getPsiField().getName().equals(infoget.getPsiField().getName())) {
						Info[] infos = new Info[]{infoget, infoset};
						setget.add(infos);
					} else if ((int) spinner1.getValue() > 0) {
						String met = infoget.getPsiField().getName().substring((Integer) spinner1.getValue(), infoget.getPsiField().getName().length());
						if (infoset.getPsiField().getName().equals(met)) {
							Info[] infos = new Info[]{infoget, infoset};
							setget.add(infos);
						}
					}
				}
			}
			if(d == setget.size()){
				Info[] infos = new Info[]{infoget, null};
				setget.add(infos);
			}
		}
		return setget;
	}

	private void GetConstructField(AnActionEvent e) {

		StringBuilder sb = new StringBuilder();
		if (linkRadioButton.isSelected()) {
			sb.append("this");
		}
		for (Info[] list : lfm) {
			if (list.length == 2 && list[0] != null && list[1] != null) {
				if (linkRadioButton.isSelected()) {
					thisset(list[1].getPsimethod(), list[0].getPsimethod(), list[0].getParaname(), sb);
				} else if (voidRadioButton.isSelected()) {
					voidset(list[1].getPsimethod(), list[0].getPsimethod(), list[0].getParaname(), sb);
				}
			}
		}
		String buil = sb.toString();
		if (linkRadioButton.isSelected()) {
			buil = buil.substring(0, buil.length() - 1) + ";";
		} else if (voidRadioButton.isSelected()) {
			buil = buil.substring(0, buil.length() - 1);
		}
		Editor editor = e.getData(PlatformDataKeys.EDITOR);
		final int start = editor.getSelectionModel().getSelectionStart();
		final int end = editor.getSelectionModel().getSelectionEnd();
		String finalBuil = buil;
		WriteCommandAction.runWriteCommandAction(e.getProject(), new Runnable() {
			@Override
			public void run() {
				editor.getDocument().replaceString(
						start,
						end,
						finalBuil);
				//CodeStyleManager.getInstance(e.getProject()).re;
			}
		});
		editor.getSelectionModel().removeSelection();
	}

	private class Info {
		private int row;
		private PsiField PsiField;
		private PsiMethod psimethod;
		private String paraname;

		public int getRow() {
			return row;
		}

		public Info setRow(int row) {
			this.row = row;
			return this;
		}

		public PsiField getPsiField() {
			return PsiField;
		}

		public Info setPsiField(PsiField psiField) {
			PsiField = psiField;
			return this;
		}

		public PsiMethod getPsimethod() {
			return psimethod;
		}

		public Info setPsimethod(PsiMethod psimethod) {
			this.psimethod = psimethod;
			return this;
		}

		public String getParaname() {
			return paraname;
		}

		public Info setParaname(String paraname) {
			this.paraname = paraname;
			return this;
		}
	}

	private void thisset(PsiMethod set, PsiMethod get, String getname, StringBuilder sb) {
		sb.append(".")
				.append(set.getName())
				.append("(")
				.append(getname)
				.append(".")
				.append(get.getName())
				.append("()")
				.append(")")
				.append("\n");
	}

	private void voidset(PsiMethod set, PsiMethod get, String getname, StringBuilder sb) {
		sb.append(set.getName())
				.append("(")
				.append(getname)
				.append(".")
				.append(get.getName())
				.append("()")
				.append(");")
				.append("\n");
	}




	@SuppressWarnings("serial")
	class MyAbstractTableModel1 extends AbstractTableModel {

		List<Object[]> lo = new ArrayList<Object[]>();
		List<Info> thisClass;

		public MyAbstractTableModel1(List<Info[]> infos, List<Info> thisClass) {
			this.thisClass = thisClass;
			lfm = infos;
			int i = 0;
			for (Info[] fom : lfm) {
				if (fom.length == 2) {
					if (fom[0] != null && fom[1] != null) {
						fom[0].setRow(i++);
						lo.add(new Object[]{fom[0].getPsimethod().getName(), fom[1].getPsimethod().getName()});
					} else if (fom[0] != null && fom[1] == null) {
						fom[0].setRow(i++);
						lo.add(new Object[]{fom[0].getPsimethod().getName(), null});
					}
				}
			}
			data = lo.toArray(new Object[0][0]);
		}

		// 定义表头数据
		String[] head = {"Name", "Set"};

		Class[] typeArray = {Object.class, Object.class};

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
			for (Info[] infos : lfm) {
				if (infos[0].getRow() == rowIndex) {
					for (Info info : thisClass) {
						if (info.getPsimethod() != null) {
							if (info.getPsimethod().getName().equals(aValue)) {
								infos[1] = info;
								data[rowIndex][columnIndex] = aValue;
								fireTableCellUpdated(rowIndex, columnIndex);
							}
						}
					}
				}
			}
		}

		// 实现了如果是boolean自动转成JCheckbox
		@Override
		public Class getColumnClass(int columnIndex) {
			return typeArray[columnIndex];// 返回每一列的数据类型
		}
	}

	@SuppressWarnings("serial")
	class MyAbstractTableModel2 extends AbstractTableModel {

		List<Object[]> lo = new ArrayList<Object[]>();
		List<Info> lfm;

		public MyAbstractTableModel2(List<Info> infos) {
			this.lfm = infos;
			int i = 0;
			for (Info fom : lfm) {
				if (fom.getPsimethod() != null) {
					lo.add(new Object[]{fom.getPsimethod().getName()});
				}
			}
			data = lo.toArray(new Object[0][0]);
		}

		// 定义表头数据
		String[] head = {"Name"};

		Class[] typeArray = {Object.class};

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
			return true;
		}

		// 替换单元格的值
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			/*data[rowIndex][columnIndex] = aValue;
			fireTableCellUpdated(rowIndex, columnIndex);*/
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

	private void onOK() {
		GetConstructField(a);
		// add your code here
		dispose();
	}

	private void onCancel() {
		// add your code here if necessary
		dispose();
	}

}
