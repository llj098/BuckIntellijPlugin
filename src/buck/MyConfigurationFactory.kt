package buck

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import javax.swing.Icon


class BuckConfigurationType : ConfigurationType {
    val factory = MyConfigurationFactory(this)

//    init {
//        getAdb()//todo ftw
//    }
    override fun getConfigurationFactories(): Array<out ConfigurationFactory>? {
        return array(factory)
    }

    override fun getIcon(): Icon? {
        return BUCK_ICON
    }

    override fun getDisplayName(): String? {
        return "Buck"
    }

    override fun getConfigurationTypeDescription(): String? {
        return "Buck"
    }

    override fun getId(): String {
        return "buck.id"
    }



    class MyConfigurationFactory(type: BuckConfigurationType)
    : ConfigurationFactory(type) {
        override fun createTemplateConfiguration(project: Project): RunConfiguration? {
            return BuckConfiguration(project)
        }

    }
}

fun getBuckConfigurationType(): BuckConfigurationType {
    return ConfigurationTypeUtil.findConfigurationType(javaClass<BuckConfigurationType>())
}

