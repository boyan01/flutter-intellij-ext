package tech.soit.flutter.idea.contributor

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.parentOfType
import com.intellij.ui.TextFieldWithAutoCompletionListProvider
import org.jetbrains.yaml.psi.YAMLKeyValue
import org.jetbrains.yaml.psi.YAMLSequence
import java.nio.file.Paths

class FlutterYamlCompletionContributor : CompletionContributor() {

    private fun PsiElement.canPopupFilePathContributor(): Boolean {
        return isYamlAssetsValue() || isFontAssetValue()
    }

    private fun PsiElement.isYamlAssetsValue(): Boolean {
        val keyValue = parentOfType<YAMLKeyValue>() ?: return false
        if (keyValue.keyText != "assets" || keyValue.value !is YAMLSequence) {
            return false
        }
        return true
    }

    private fun PsiElement.isFontAssetValue(): Boolean {
        val keyValue = parentOfType<YAMLKeyValue>() ?: return false
        if (keyValue.keyText != "asset") {
            return false
        }
        val fonts = keyValue.parentOfType<YAMLKeyValue>() ?: return false
        if (fonts.keyText != "fonts") {
            return false
        }
        return true
    }


    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        val psiElement = parameters.position

        if (!psiElement.canPopupFilePathContributor()) {
            LOG.debug("current psi element do not support completion $psiElement")
            return
        }

        val prefix = TextFieldWithAutoCompletionListProvider.getCompletionPrefix(parameters)
        val projectDir = psiElement.project.guessProjectDir() ?: return

        val paths = findRecommendPath(prefix, projectDir)

        LOG.debug("find paths by ($prefix) : $paths ")

        result.caseInsensitive()
            .withPrefixMatcher(prefix)
            .addAllElements(paths.map { it.toLookupElement(parameters, projectDir) })
    }

    private fun VirtualFile.toLookupElement(parameters: CompletionParameters, projectDir: VirtualFile): LookupElement {
        val projectPath = Paths.get(projectDir.path)
        var lookupString = projectPath.relativize(Paths.get(path)).toString().replace('\\', '/')

        val psiFile = PsiManager.getInstance(parameters.position.project).findFile(this)

        LOG.debug(" $this 's psiFile: $psiFile ")

        var icon = psiFile?.getIcon(0)
        if (icon == null && isDirectory) {
            icon = AllIcons.Nodes.Folder
        }
        if (isDirectory) {
            lookupString = "$lookupString/"
        }

        return LookupElementBuilder.create(lookupString)
            .withIcon(icon)
    }


    companion object {

        private val LOG = Logger.getInstance(FlutterYamlCompletionContributor::class.java)

        private fun findRecommendPath(completionPrefix: String, projectDir: VirtualFile): List<VirtualFile> {
            ProgressManager.checkCanceled()
            val virtualFile = findVirtualFile(
                completionPrefix,
                projectDir
            ) ?: return emptyList()
            return virtualFile.children.toList()
        }

        private fun findVirtualFile(input: String, projectDir: VirtualFile): VirtualFile? {
            ProgressManager.checkCanceled()

            val leading = input.substringBefore('/', "")
            if (leading.isEmpty()) return projectDir
            val child = projectDir.children.find { it.name == leading } ?: return projectDir
            return findVirtualFile(
                input.substringAfter('/', ""),
                child
            )
        }

    }

}