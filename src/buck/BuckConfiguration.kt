package buck

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.execution.configurations.RuntimeConfigurationException
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.DefaultJDOMExternalizer
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import org.jdom.Element

/**
 * Created by anatoly on 12.04.15.
 */
public class BuckConfiguration (project: Project)
: RunConfigurationBase(project, getBuckConfigurationType().factory, "Buck") {
    val state = ConfigurationPersistentState()



    override fun getConfigurationEditor(): SettingsEditor<out BuckConfiguration> {
        return BuckConfigurationEditor()
    }

    throws(javaClass<RuntimeConfigurationException>())
    override fun checkConfiguration() {
        if (state.appName == null || state.activityName == null || state.packageName == null){
            throw RuntimeConfigurationError("you have to specify appname and packageName")
        }
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? {
        return MyProfileState(getProject(),environment, this)
    }


    throws(javaClass<InvalidDataException>())
    override fun readExternal(element: Element) {
        super.readExternal(element)
        DefaultJDOMExternalizer.readExternal(state, element)
    }

    throws(javaClass<WriteExternalException>())
    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        DefaultJDOMExternalizer.writeExternal(state, element)
    }
}
