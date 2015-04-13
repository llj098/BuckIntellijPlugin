package buck

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.runners.DefaultProgramRunner

/**
 * Created by anatoly on 13.04.15.
 */

class BuckDebugRunner():
        DefaultProgramRunner(){

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return true
    }

    override fun getRunnerId(): String {
        return "BuckRunner"
    }

}