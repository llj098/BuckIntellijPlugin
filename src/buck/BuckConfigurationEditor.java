package buck;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by anatoly on 12.04.15.
 */
public class BuckConfigurationEditor extends SettingsEditor<BuckConfiguration> {
    private JPanel rootPanel;
    private JTextField appName;
    private JTextField packageName;
    private JTextField activityName;

    @Override
    protected void resetEditorFrom(BuckConfiguration s) {
        appName.setText(s.getState().appName);
        packageName.setText(s.getState().packageName);
        activityName.setText(s.getState().activityName);
    }

    @Override
    protected void applyEditorTo(BuckConfiguration s) throws ConfigurationException {
        s.getState().appName = appName.getText();
        s.getState().activityName = activityName.getText();
        s.getState().packageName = packageName.getText();
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return rootPanel;
    }
}
