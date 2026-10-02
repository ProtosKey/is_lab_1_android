package dev.protoskey.presentation.tools

/**
 * Ввод дат одними цифрами: в форме хранятся только цифры,
 * разделители подставляются при отображении и при отправке на сервер.
 */
enum class DateMask(val pattern: String, val placeholder: String) {
    // Сервер принимает дату основания как 2024-05-12T14:30
    DATE_TIME("####-##-## ##:##", "ГГГГ-ММ-ДД ЧЧ:ММ"),

    // Дату рождения — как 2024-05-12
    DATE("####-##-##", "ГГГГ-ММ-ДД");

    val length: Int = pattern.count { it == '#' }

    /** Цифры из значения сервера: "2024-05-12T14:30" -> "202405121430". */
    fun digits(value: String?): String = value.orEmpty().filter(Char::isDigit).take(length)

    /** Значение для сервера или null, если цифры введены не полностью. */
    fun toServer(digits: String): String? {
        if (digits.length != length) return null
        val text = format(digits)
        return if (this == DATE_TIME) text.replace(' ', 'T') else text
    }

    /** Подставляет разделители после введённых цифр: "20240512" -> "2024-05-12". */
    fun format(digits: String): String = buildString {
        var index = 0
        for (symbol in pattern) {
            if (index >= digits.length) break
            if (symbol == '#') append(digits[index++]) else append(symbol)
        }
    }
}
