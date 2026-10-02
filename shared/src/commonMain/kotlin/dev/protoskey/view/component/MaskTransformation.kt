package dev.protoskey.view.component

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import dev.protoskey.presentation.tools.DateMask

/** Показывает введённые цифры по маске, не меняя само значение поля. */
class MaskTransformation(private val mask: DateMask) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val formatted = mask.format(digits)

        val mapping = object : OffsetMapping {
            // Позиция курсора среди цифр -> позиция в отформатированной строке
            override fun originalToTransformed(offset: Int): Int {
                if (offset == 0) return 0
                var seen = 0
                formatted.forEachIndexed { index, symbol ->
                    if (symbol.isDigit() && ++seen == offset) return index + 1
                }
                return formatted.length
            }

            // Позиция в отформатированной строке -> количество цифр до неё
            override fun transformedToOriginal(offset: Int): Int =
                formatted.take(offset).count(Char::isDigit).coerceAtMost(digits.length)
        }

        return TransformedText(AnnotatedString(formatted), mapping)
    }
}
