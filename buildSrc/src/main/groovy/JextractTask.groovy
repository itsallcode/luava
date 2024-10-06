import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity 

@CacheableTask
class JextractTask extends DefaultTask {
    @InputFile @PathSensitive(PathSensitivity.RELATIVE) File jextractBinary

    @InputDirectory @PathSensitive(PathSensitivity.RELATIVE) File includeDir

    @OutputDirectory File outputDir

    @TaskAction
    void generate() {
        logger.info("Generating using binary ${jextractBinary} from ${includeDir} to ${outputDir}...")
        def arguments = [
                '--include-dir', includeDir,
                '--output', outputDir,
                '--target-package', 'org.itsallcode.luava.ffi',
                '--library', 'lua',
                '--header-class-name', 'Lua',
                "$includeDir/all_lua.h"
        ]
        project.exec {
            workingDir project.rootDir
            executable jextractBinary
            args arguments
        }
    }
}
