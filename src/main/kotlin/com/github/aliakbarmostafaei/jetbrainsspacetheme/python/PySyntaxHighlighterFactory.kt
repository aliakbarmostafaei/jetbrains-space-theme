package com.github.aliakbarmostafaei.jetbrainsspacetheme.python

import com.intellij.injected.editor.VirtualFileWindow
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.util.containers.FactoryMap
import com.jetbrains.python.console.isInPydevConsole
import com.jetbrains.python.highlighting.PyHighlighter
import com.jetbrains.python.parsing.console.PyConsoleHighlightingLexer
import com.jetbrains.python.psi.LanguageLevel
import com.jetbrains.python.psi.impl.PythonLanguageLevelPusher

class PySyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    private val myMap = FactoryMap.create { key: LanguageLevel? -> PyHighlighter(key) }
    private val myConsoleMap = FactoryMap.create<LanguageLevel, PyHighlighter> { key: LanguageLevel? ->
        object : PyHighlighter(key) {
            override fun createHighlightingLexer(languageLevel: LanguageLevel) =
                PyConsoleHighlightingLexer(languageLevel)
        }
    }

    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
        val level = getLanguageLevel(project, virtualFile)
        return if (useConsoleLexer(
                project,
                virtualFile
            )
        ) {
            myConsoleMap[level]!!
        } else getSyntaxHighlighterForLanguageLevel(level)
    }

    /**
     * Returns a syntax highlighter targeting the specified version of Python.
     */
    fun getSyntaxHighlighterForLanguageLevel(level: LanguageLevel): SyntaxHighlighter {
        return myMap[level]!!
    }

    companion object {
        private fun getLanguageLevel(project: Project?, virtualFile: VirtualFile?): LanguageLevel {
            return if (project != null && virtualFile != null) PythonLanguageLevelPusher.getLanguageLevelForVirtualFile(
                project,
                virtualFile
            ) else LanguageLevel.getDefault()
        }

        private fun useConsoleLexer(project: Project?, virtualFile: VirtualFile?): Boolean {
            if (virtualFile == null || project == null || virtualFile is VirtualFileWindow) {
                return false
            }
            val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
            return psiFile != null && isInPydevConsole(psiFile)
        }
    }
}
