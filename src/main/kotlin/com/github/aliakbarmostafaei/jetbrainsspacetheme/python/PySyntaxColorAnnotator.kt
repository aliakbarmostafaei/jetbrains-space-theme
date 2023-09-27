package com.github.aliakbarmostafaei.jetbrainsspacetheme.python

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.jetbrains.python.psi.PyFromImportStatement
import com.jetbrains.python.psi.PyImportStatement
import com.jetbrains.python.validation.PyAnnotator

class PySyntaxColorAnnotator : PyAnnotator() {
    object Attributes {
        val importModuleAttr = TextAttributesKey.createTextAttributesKey(PyBundle.message("python.colors.import.module.namespace"))
        val importMethodAttr = TextAttributesKey.createTextAttributesKey(PyBundle.message("python.colors.import.method.namespace"))
    }

    override fun visitPyImportStatement(node: PyImportStatement) {
        super.visitPyImportStatement(node)
        annotatePyImportStatements(node)
    }

    override fun visitPyFromImportStatement(node: PyFromImportStatement) {
        super.visitPyFromImportStatement(node)
        annotatePyFromImportStatements(node)
    }

    private fun annotatePyFromImportStatements(node: PyFromImportStatement) {
        node.importSource?.let {
            holder
                .newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(it)
                .textAttributes(Attributes.importModuleAttr)
                .create()
        }

        // If the import statement is in `import x as y` format, color both x and y only
        node.importElements[0].asNameElement?.let {
            holder
                .newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(node.importElements[0].textRange.startOffset, it.textRange.startOffset - 3))
                .textAttributes(Attributes.importModuleAttr)
                .create()

            holder
                .newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(it)
                .textAttributes(Attributes.importModuleAttr)
                .create()
        } ?: run {
            node.importElements.forEach {
                holder
                    .newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(it)
                    .textAttributes(Attributes.importMethodAttr)
                    .create()
            }
        }
    }

    private fun annotatePyImportStatements(node: PyImportStatement) {
        // If the import statement is in `import x as y` format, color both x and y only
        node.importElements[0].asNameElement?.let {
            holder
                .newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(node.importElements[0].textRange.startOffset, it.textRange.startOffset - 3))
                .textAttributes(Attributes.importModuleAttr)
                .create()

            holder
                .newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(it)
                .textAttributes(Attributes.importModuleAttr)
                .create()
        } ?: run {
            node.importElements.forEach {
                holder
                    .newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(it)
                    .textAttributes(Attributes.importModuleAttr)
                    .create()
            }
        }
    }
}
