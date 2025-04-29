package one.wabbit.text.check


data class Parentheses(val open: Char, val close: Char)

val List = listOf(
    Parentheses('(', ')'), Parentheses('[', ']'), Parentheses('{', '}'),
    Parentheses('<', '>'), Parentheses('«', '»'), Parentheses('„', '“'),
    Parentheses('‘', '’'), Parentheses('‹', '›'), Parentheses('｟', '｠'),
    Parentheses('｢', '｣'),
    // Wide parentheses （）
    Parentheses('（', '）'), Parentheses('［', '］'), Parentheses('｛', '｝'),
    Parentheses('〈', '〉'), Parentheses('《', '》'), Parentheses('「', '」'),
)

val OpenToClose = List.associate { it.open to it.close }
val CloseToOpen = List.associate { it.close to it.open }

fun hasUnbalancedParentheses(text: String): Boolean {
    var index = 0
    val stack = mutableListOf<Char>()

    while (index < text.length) {
        val char = text[index]

        if (char in OpenToClose) {
            stack.add(OpenToClose[char]!!)
        } else if (char in CloseToOpen) {
            if (stack.isEmpty()) {
                return true
            } else if (stack.last() != char) {
                return true
            } else {
                stack.removeLast()
            }
        }

        index += 1
    }

    return stack.isNotEmpty()
}