package com.plugins.mygetset;

import com.intellij.codeInsight.editorActions.SelectWordUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiJavaCodeReferenceElementImpl;
import com.intellij.psi.impl.source.tree.java.PsiNewExpressionImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.plugins.mygetset.selecttext.SelectWordUtilCompat;
import com.plugins.mygetset.ui.getset;
import com.plugins.mygetset.ui.set;
import com.plugins.mygetset.ui.setconverttree;

import java.util.ArrayList;

/**
 * 生成属性的get set方法，并将属性上的注释添加到方法上
 * Created by huqiang on 15/8/15.
 */
public class generateAction extends AnAction {


	public generateAction() {
		super("BuildGetSet");
	}

	@Override
	public void actionPerformed(AnActionEvent e) {
		openform(e);
	}

	private PsiElement thisPrev;
	private String selectval = "";
	private int[] textRange;

	/**
	 * 确定打开的窗口
	 *
	 * @param e
	 */
	public void openform(AnActionEvent e) {
		Editor editor = e.getData(PlatformDataKeys.EDITOR);
		if (getSelectedText(editor)) {
			PsiClass psiClass = getPsiElementClass(thisPsiElement(e));
			if(psiClass == null){
				Notification n = new Notification("无法识别对象","无法识别对象","无法识别对象", NotificationType.INFORMATION);
				Notifications.Bus.notify(n);
			}else{
				set dialog = new set(psiClass, editor, selectval, textRange);
				dialog.pack();
				dialog.setTitle("Set");
				dialog.setSize(350, 255);
				dialog.setLocationRelativeTo(null);
				dialog.setResizable(false);
				dialog.setVisible(true);
			}
		} else {
			PsiMethod psiMethod = getPsiMethod(thisPsiElement(e));
			PsiClass psiClass = getPsiElemenParentClass(thisPsiElement(e));
			if (psiMethod != null) {
				setconverttree dialog = new setconverttree(psiClass,psiMethod,e);
				dialog.pack();
				dialog.setTitle("SetConvert");
				dialog.setSize(550, 355);
				dialog.setLocationRelativeTo(null);
				dialog.setVisible(true);
			} else {
				getset dialog = new getset(psiClass, thisPrev);
				dialog.pack();
				dialog.setTitle("Get/Set");
				dialog.setSize(350, 255);
				dialog.setLocationRelativeTo(null);
				dialog.setResizable(false);
				dialog.setVisible(true);
			}
		}
	}

	/**
	 * 获取光标选择内容
	 *
	 * @param editor
	 * @return true 有选择的内容 ，false无选择的内容
	 */
	private boolean getSelectedText(Editor editor) {
		SelectionModel selectionModel = editor.getSelectionModel();
		//鼠标选择
		if (selectionModel.hasSelection()) {
			selectval = selectionModel.getSelectedText();
			textRange = new int[]{selectionModel.getSelectionStart(), selectionModel.getSelectionEnd()};
			return true;
		}
		//鼠标无选择，IDEA默认自动选择
		else {
			final ArrayList<TextRange> ranges = new ArrayList<>();
			final int offset = editor.getCaretModel().getOffset();
			SelectWordUtilCompat.addWordOrLexemeSelection(false, editor, offset, ranges, SelectWordUtil.JAVA_IDENTIFIER_PART_CONDITION);
			if (ranges.size() > 0) {
				TextRange textRanges = ranges.get(0);
				selectval = editor.getDocument().getText(textRanges);
				//不支持();带有参数
				textRange = new int[]{textRanges.getStartOffset(), textRanges.getEndOffset()};
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 光标有选择内容的Class
	 *
	 * @param e
	 * @return
	 */
	private PsiClass selectClass(AnActionEvent e) {
		if (e.getData(LangDataKeys.PSI_ELEMENT) instanceof PsiClass) {
			return (PsiClass) e.getData(LangDataKeys.PSI_ELEMENT);
		}
		return null;
	}


	/**
	 * 获取到当前是否有选择相关的PsiElement内容
	 *
	 * @param e
	 * @return
	 */
	private PsiElement thisPsiElement(AnActionEvent e) {
		PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
		Editor editor = e.getData(PlatformDataKeys.EDITOR);
		if (psiFile == null || editor == null) {
			e.getPresentation().setEnabled(false);
			return null;
		}
		//用来获取当前光标处的PsiElement
		int offset = editor.getCaretModel().getOffset();
		this.thisPrev = psiFile.findElementAt(offset).getPrevSibling();
		PsiElement elementAt = psiFile.findElementAt(offset);
		return elementAt;
	}

	/**
	 * 获取光标当前所在的Class
	 *
	 * @param elementAt
	 * @return
	 */
	private PsiClass getPsiElemenParentClass(PsiElement elementAt) {
		return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
	}

	/**
	 * 获取光标当前的方法
	 *
	 * @param elementAt
	 * @return
	 */
	private PsiMethod getPsiMethod(PsiElement elementAt) {
		return PsiTreeUtil.getParentOfType(elementAt, PsiMethod.class);
	}

	/**
	 * 获取光标手动选择或自动选择的文本的class
	 *
	 * @param elementAt
	 * @return
	 */
	private PsiClass getPsiElementClass(PsiElement elementAt) {
		final PsiElement parent = elementAt.getParent();
		if(parent instanceof PsiJavaCodeReferenceElementImpl){
			return (PsiClass) ((PsiJavaCodeReferenceElementImpl) elementAt.getParent()).resolve();
		}else if(parent instanceof PsiNewExpressionImpl){
			return (PsiClass)((PsiNewExpressionImpl) elementAt.getParent()).resolveConstructor().getParent();
		}
		return null;
	}

}
