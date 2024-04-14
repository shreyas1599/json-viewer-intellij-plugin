package com.shreyas.jsonstringviewer;

import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

import java.io.IOException;


public class TestJsonViewer extends LightJavaCodeInsightFixtureTestCase {

    public static class OverridenTestJsonViewer extends JsonViewer {
        // After editing this is the new text. Cannot simulate dialog in light weight test
        @Override
        public String callJsonEditorDialog(Project project, String initialJson) {
            return '"' + "{ \\\"hello\\\": \\\"new\\\" }" + '"';
        }
    }

    public void testModifiedTextIsPersisted() throws IOException {
        myFixture.configureByText("MyClass.java", """
                public class MyClass {
                    public getExpression() {
                        return "{ \\"hello\\":<caret> \\"old\\" }"
                    }
                }
                """);
        assertTrue(myFixture.getEditor().getDocument().getText().contains("{ \\\"hello\\\": \\\"old\\\" }"));
        myFixture.testAction(new OverridenTestJsonViewer());

        assertTrue(myFixture.getEditor().getDocument().getText().contains("{ \\\"hello\\\": \\\"ne\\\" }"));
    }
}
