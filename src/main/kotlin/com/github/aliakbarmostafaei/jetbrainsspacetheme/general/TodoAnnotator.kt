package com.github.aliakbarmostafaei.jetbrainsspacetheme.general

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.refactoring.suggested.endOffset

class TodoAnnotator : Annotator {
    object Holder {
        val tags = arrayOf("TODO", "FIXME", "BUG", "HACK")
    }
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is PsiComment) {
            val comment = element.text.trimStart()
            val elementTextRange = element.textRange

            Holder.tags.forEach { tag ->
                val tagRegex = "\\b$tag\\b".toRegex()
                val tagMatch = tagRegex.find(comment)
                tagMatch?.let {
                    val tagAttr =
                        TextAttributesKey.createTextAttributesKey(CommentBundle.message("space.colors.${tag.lowercase()}.tag.namespace"))
                    val descAttr =
                        TextAttributesKey.createTextAttributesKey(CommentBundle.message("space.colors.${tag.lowercase()}.desc.namespace"))

                    holder
                        .newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(
                            TextRange(
                                elementTextRange.startOffset + it.range.first,
                                elementTextRange.startOffset + it.range.last + 1
                            )
                        )
                        .textAttributes(tagAttr)
                        .create()

                    holder
                        .newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(TextRange(elementTextRange.startOffset + it.range.last + 1, element.endOffset))
                        .textAttributes(descAttr)
                        .create()
                }
            }
        }
    }
}
