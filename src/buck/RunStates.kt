package buck

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.Client
import com.intellij.debugger.ui.DebuggerPanelsManager
import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RemoteConnection
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.xdebugger.DefaultDebugProcessHandler
import java.util.ArrayList
import java.util.concurrent.TimeUnit

//{
//AndroidDebugBridge.init(false)
//}
//AndroidDebugBridge.init(false)
var adbInitialized = false
fun getAdb(): AndroidDebugBridge {
    if (!adbInitialized){
        AndroidDebugBridge.init(true)
        adbInitialized = true
    }
    return AndroidDebugBridge.createBridge("/Users/anatoly/Applications/android-sdk-macosx/platform-tools/adb", false)
}
/**
 * Created by anatoly on 12.04.15.
 */
public class MyProfileState (val project: Project, val e: ExecutionEnvironment, val cfg: BuckConfiguration)

: RunProfileState {

    val myProcessHandler: DefaultDebugProcessHandler = DefaultDebugProcessHandler()
    var myConsole: ConsoleView? = null
    private var myExecutor: Executor? = null
    private var myRunner: ProgramRunner<*>? = null

    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
        myExecutor = executor
        myRunner = runner

        myConsole = TextConsoleBuilderFactory.getInstance()
                .createBuilder(project)
                .getConsole()
        myConsole!!.attachToProcess(myProcessHandler)

        val debug = executor.getId() == DefaultDebugExecutor.EXECUTOR_ID

        val result = DefaultExecutionResult(myConsole, myProcessHandler)
        ApplicationManager.getApplication()
                .executeOnPooledThread {
                    if (debug) {
                        installStartAndAttach(result)
                    } else {
                        installAndRun()
                    }


                }


        return result
    }

    private fun installAndRun() {
        if (0 == executeCommand(
                listOf("buck", "install", "-r", "${cfg.state.appName}")) ) {
            myProcessHandler.destroyProcess()
        }
    }

    private fun attach(res: DefaultExecutionResult) {
        val adb = getAdb()

        val ds = adb.getDevices()
        assert (ds.size () > 0)

        val packageName = cfg.state.packageName

        val clients = ds[0].getClients()
        if (clients.isEmpty()){
            return
        }
        val client :Client = ds[0].getClient(packageName)//todo


        val con = RemoteConnection(true, "localhost", "${client.getDebuggerListenPort()}", false)
        val a = ExecutionEnvironmentBuilder(e)
                .executor(myExecutor!!)
                .runner(myRunner!!)
                .build()
        //
        //
        val manager = DebuggerPanelsManager.getInstance(project)
        ApplicationManager.getApplication().invokeLater{
            manager.attachVirtualMachine(a, DebugState(DefaultExecutionResult(myConsole, DefaultDebugProcessHandler())), con, false)
        }
    }

    private fun startApplicationAndAttach(res: DefaultExecutionResult) {
        val cmd = ArrayList (
                listOf("adb",
                        "shell", "am", "start", "-D", "-n", "${cfg.state.packageName}/${ cfg.state.activityName}"))
        if (0 == executeCommand(cmd)) {
            attach(res)
        }
    }

    private fun installStartAndAttach(res: DefaultExecutionResult) {
        if (0 == executeCommand(
                listOf("buck", "install", "${ cfg.state.appName  }")) ) {
            startApplicationAndAttach(res)
        }
    }

    private fun executeCommand(cmd: List<String>): Int {
        val h = OSProcessHandler(
                GeneralCommandLine(cmd)
                        .withWorkDirectory(project.getBasePath()))
        h.addProcessListener(object : ProcessAdapter() {
            override fun onTextAvailable(event: ProcessEvent?, outputType: Key<*>?) {
                if (event != null) {
                    myProcessHandler.notifyTextAvailable(event.getText(), outputType)
                }
            }
        })
        h.startNotify()

        val exitCode = h.getProcess().waitFor()
        if (exitCode != 0) {
            myProcessHandler.destroyProcess()
        }
        return exitCode
    }


}

class DebugState(val res: DefaultExecutionResult) : RunProfileState{
    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult? {
        return res
    }

}


