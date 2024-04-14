package com.shreyas.jsonstringviewer;

import com.intellij.json.JsonLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBScrollPane;
import groovy.json.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;

public class JsonEditorDialog extends DialogWrapper {
    private final EditorTextField editorTextField;
    private final JBScrollPane scrollPane;

    public JsonEditorDialog(@Nullable Project project, String initialJson) {
        super(project);

        String strippedString = initialJson.substring(1, initialJson.length() - 1);
        JSONObject jsonObject = new JSONObject(new JSONTokener(StringEscapeUtils.unescapeJava(strippedString)));
        String prettifiedJson = jsonObject.toString(4);

        editorTextField = new EditorTextField(prettifiedJson, project, JsonLanguage.INSTANCE.getAssociatedFileType());
        editorTextField.setOneLineMode(false);
        scrollPane = new JBScrollPane(editorTextField.getComponent());

        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        return scrollPane;
    }

    public String getJson() {
        JSONObject jsonObject = new JSONObject(new JSONTokener(editorTextField.getText()));
        return '"' + StringEscapeUtils.escapeJava(jsonObject.toString()) + '"';
    }
}
