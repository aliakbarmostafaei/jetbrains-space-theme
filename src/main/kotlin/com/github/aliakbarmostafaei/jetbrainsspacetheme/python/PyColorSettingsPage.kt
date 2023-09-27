package com.github.aliakbarmostafaei.jetbrainsspacetheme.python

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.PythonLanguage
import com.jetbrains.python.psi.LanguageLevel
import javax.swing.Icon

class PyColorSettingsPage : ColorSettingsPage {
    override fun getDisplayName(): String {
        return PyBundle.message("settings.color.page.python")
    }

    override fun getIcon(): Icon? {
        return PythonFileType.INSTANCE.icon
    }

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return ATTRS
    }

    override fun getColorDescriptors(): Array<ColorDescriptor> {
        return ColorDescriptor.EMPTY_ARRAY
    }

    override fun getHighlighter(): SyntaxHighlighter {
        val factory = SyntaxHighlighterFactory.LANGUAGE_FACTORY.forLanguage(PythonLanguage.getInstance())
        return if (factory is PySyntaxHighlighterFactory) {
            factory.getSyntaxHighlighterForLanguageLevel(
                LanguageLevel.getLatest()
            )
        } else factory.getSyntaxHighlighter(null, null)
    }

    override fun getDemoText(): String {
        return "from <py.module>pprint</py.module> import <py.method>pprint</py.method>\n" +
                "\n" +
                "import <py.module>pandas</py.module> as <py.module>pd</py.module>\n"
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> {
        val map: MutableMap<String, TextAttributesKey> = mutableMapOf()
        map["py.module"] = PY_IMPORT_MODULE
        map["py.method"] = PY_IMPORT_METHOD
        return map
    }

    companion object {
        private val ATTRS: Array<AttributesDescriptor>
        val PY_IMPORT_MODULE =
            TextAttributesKey.createTextAttributesKey(PyBundle.message("python.colors.import.module.namespace"))
        val PY_IMPORT_METHOD =
            TextAttributesKey.createTextAttributesKey(PyBundle.message("python.colors.import.method.namespace"))

        init {
            ATTRS = arrayOf(
                AttributesDescriptor(PyBundle.message("python.colors.import.module"), PY_IMPORT_MODULE),
                AttributesDescriptor(PyBundle.message("python.colors.import.method"), PY_IMPORT_METHOD),
            )
        }
    }
}
