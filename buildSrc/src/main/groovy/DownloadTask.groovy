import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.CacheableTask

@CacheableTask
class DownloadTask extends DefaultTask {
    @Input
    String sourceUrl

    @OutputFile
    File target

    @TaskAction
    void download() {
        logger.info("Downloading file ${sourceUrl} to ${target}...")
        getAnt().get(src: sourceUrl, dest: target)
    }
}
