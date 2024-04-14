package com.shreyas.jsonstringviewer;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import groovy.json.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONTokener;

import static com.intellij.json.JsonElementTypes.STRING_LITERAL;
import static com.intellij.openapi.ui.DialogWrapper.OK_EXIT_CODE;


public class JsonViewer extends AnAction {
    public JsonViewer() {
        super();
    }
    public static void replacePsiElementText(Project project, PsiElement element, String newText) {
        PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText("temp.txt", element.getNode().getElementType().getLanguage(), newText);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (psiFile.findElementAt(0) != null) {
                PsiElement textElement = psiFile.findElementAt(0);
                assert textElement != null;
                element.replace(textElement);
            }
        });
    }

    protected PsiElement getElementFromAction(@NotNull AnActionEvent event) {
        EditorEx editor = (EditorEx) CommonDataKeys.EDITOR.getData(event.getDataContext());
        if (editor != null) {
            Project project = editor.getProject();
            if (project != null) {
                PsiFile psiFile = PsiManager.getInstance(project).findFile(editor.getVirtualFile());
                if (psiFile != null) {
                    CaretModel caretModel = editor.getCaretModel();
                    return psiFile.findElementAt(caretModel.getOffset());
                }
            }
        }
        return null;
    }

    public String callJsonEditorDialog(Project project, String initialJson) {
        if (initialJson != null) {
            JsonEditorDialog dialog = new JsonEditorDialog(project, initialJson);
            dialog.show();
            if (dialog.getExitCode() == OK_EXIT_CODE) {
                return dialog.getJson();
            }
        }
        return null;
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiElement element = getElementFromAction(e);
        Project project = e.getProject();
        if (element != null) {
            IElementType elementType = element.getNode().getElementType();
            if (STRING_LITERAL.toString().equals(elementType.toString())) {
                String elementText = element.getText();
                String edited = callJsonEditorDialog(project, elementText);
                if (edited != null) {
                    replacePsiElementText(project, element, edited);
                }
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean isVisible = false;
        PsiElement element = getElementFromAction(e);
        if (element != null) {
            isVisible = STRING_LITERAL.toString().equals(element.getNode().getElementType().toString());
            try {
                String strippedString = element.getText().substring(1, element.getText().length() - 1);
                new JSONObject(new JSONTokener(StringEscapeUtils.unescapeJava(strippedString)));
            } catch (Exception exception) {
                isVisible = false;
            }
        }
        e.getPresentation().setVisible(isVisible);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

}
