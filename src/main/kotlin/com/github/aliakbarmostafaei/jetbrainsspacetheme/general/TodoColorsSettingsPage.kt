package com.github.aliakbarmostafaei.jetbrainsspacetheme.general

import com.intellij.lang.Language
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class TodoColorsSettingsPage : ColorSettingsPage {
    override fun getDisplayName(): String {
        return CommentBundle.message("settings.color.page.todo")
    }

    override fun getIcon(): Icon? {
        return FileTypes.PLAIN_TEXT.icon
    }

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return ATTRS
    }

    override fun getColorDescriptors(): Array<ColorDescriptor> {
        return ColorDescriptor.EMPTY_ARRAY
    }

    override fun getHighlighter(): SyntaxHighlighter {
        val factory = SyntaxHighlighterFactory.LANGUAGE_FACTORY.forLanguage(Language.ANY)
        return factory.getSyntaxHighlighter(null, null)
    }

    override fun getDemoText(): String {
        return "# <todo.tag>TODO</todo.tag><todo.desc>: Do it to get that rewarding feeling!</todo.desc>\n" +
                "# <hack.tag>HACK</hack.tag><hack.desc>: You can do better than a hack!</hack.desc>\n" +
                "# <fixme.tag>FIXME</fixme.tag><fixme.desc>: Put this bug in a jar for now!</fixme.desc>\n" +
                "# <bug.tag>BUG</bug.tag><bug.desc>: This bug is bigger than our jar!</bug.desc>"
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> {
        val map: MutableMap<String, TextAttributesKey> = mutableMapOf()
        map["todo.tag"] = TODO_TAG
        map["todo.desc"] = TODO_DESC
        map["fixme.tag"] = FIXME_TAG
        map["fixme.desc"] = FIXME_DESC
        map["hack.tag"] = HACK_TAG
        map["hack.desc"] = HACK_DESC
        map["bug.tag"] = BUG_TAG
        map["bug.desc"] = BUG_DESC
        return map
    }

    companion object {
        private val ATTRS: Array<AttributesDescriptor>
        val TODO_TAG =
            TextAttributesKey.createTextAttributesKey(CommentBundle.message("space.colors.todo.tag.namespace"))
        val TODO_DESC =
            TextAttributesKey.createTextAttributesKey(CommentBundle.message("space.colors.todo.desc.namespace"))
        val FIXME_TAG =
            TextAttributesKey.createTextAttributesKey(CommentBundle.message("space.colors.fixme.tag.namespace"))
        val FIXME_DESC =
            TextAttributesKey.createTextAttributesKey(CommentBundle.message("space.colors.fixme.desc.namespace"))
        val HACK_TAG =
            TextAttributesKey.createTextAttributesKey(CommentBundle.message("space.colors.hack.tag.namespace"))
        val HACK_DESC =
            TextAttributesKey.createTextAttributesKey(CommentBundle.message("space.colors.hack.desc.namespace"))
        val BUG_TAG =
            TextAttributesKey.createTextAttributesKey(CommentBundle.message("space.colors.bug.tag.namespace"))
        val BUG_DESC =
            TextAttributesKey.createTextAttributesKey(CommentBundle.message("space.colors.bug.desc.namespace"))

        init {
            ATTRS = arrayOf(
                AttributesDescriptor(CommentBundle.message("space.colors.todo.tag"), TODO_TAG),
                AttributesDescriptor(CommentBundle.message("space.colors.todo.desc"), TODO_DESC),
                AttributesDescriptor(CommentBundle.message("space.colors.fixme.tag"), FIXME_TAG),
                AttributesDescriptor(CommentBundle.message("space.colors.fixme.desc"), FIXME_DESC),
                AttributesDescriptor(CommentBundle.message("space.colors.hack.tag"), HACK_TAG),
                AttributesDescriptor(CommentBundle.message("space.colors.hack.desc"), HACK_DESC),
                AttributesDescriptor(CommentBundle.message("space.colors.bug.tag"), BUG_TAG),
                AttributesDescriptor(CommentBundle.message("space.colors.bug.desc"), BUG_DESC),
            )
        }
    }
}
