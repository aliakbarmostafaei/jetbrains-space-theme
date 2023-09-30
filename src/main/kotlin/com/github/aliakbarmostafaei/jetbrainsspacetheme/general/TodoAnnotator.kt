package com.github.aliakbarmostafaei.jetbrainsspacetheme.general

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.refactoring.suggested.endOffset
import com.intellij.util.text.findTextRange

class TodoAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is PsiComment) {
            val comment = element.text
            val elementTextRange = element.textRange
            val tags = arrayOf("TODO", "FIXME", "BUG", "HACK")

            tags.forEach { tag ->
                val tagTextRange = comment.findTextRange(tag)
                tagTextRange?.let {textRange ->

                    val tagAttr =
                        TextAttributesKey.createTextAttributesKey(CommentBundle.message("space.colors.${tag.lowercase()}.tag.namespace"))
                    val descAttr =
                        TextAttributesKey.createTextAttributesKey(CommentBundle.message("space.colors.${tag.lowercase()}.desc.namespace"))

                    holder
                        .newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(
                            TextRange(
                                elementTextRange.startOffset + textRange.startOffset,
                                elementTextRange.startOffset + textRange.endOffset
                            )
                        )
                        .textAttributes(tagAttr)
                        .create()

                    holder
                        .newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(TextRange(elementTextRange.startOffset + textRange.endOffset, element.endOffset))
                        .textAttributes(descAttr)
                        .create()
                }
            }
        }
    }
}
