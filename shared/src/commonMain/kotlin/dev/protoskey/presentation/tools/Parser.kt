package dev.protoskey.presentation.tools

object Parser {
    class Result {
        val errors = linkedMapOf<String, String>()

        fun int(field: String, text: String, required: Boolean = true): Int? =
            parse(field, text, required, "Введите целое число") { it.toIntOrNull() }

        fun long(field: String, text: String, required: Boolean = true): Long? =
            parse(field, text, required, "Введите целое число") { it.toLongOrNull() }

        fun float(field: String, text: String, required: Boolean = true): Float? =
            parse(field, text, required, "Введите число") { it.replace(',', '.').toFloatOrNull() }

        fun date(field: String, digits: String, mask: DateMask): String? {
            if (digits.isEmpty()) return null
            return mask.toServer(digits) ?: run {
                errors[field] = "Введите дату полностью: ${mask.placeholder}"
                null
            }
        }

        private fun <T> parse(
            field: String,
            text: String,
            required: Boolean,
            message: String,
            convert: (String) -> T?
        ): T? {
            val value = text.trim()
            if (value.isEmpty()) {
                if (required) errors[field] = "Поле обязательно"
                return null
            }
            return convert(value) ?: run {
                errors[field] = message
                null
            }
        }
    }
}
