package one.wabbit.text.check

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TextCheckSpec {
    @Test
    fun hasUnbalancedParentheses_detects_balanced_text() {
        assertFalse(hasUnbalancedParentheses("(abc)[def]{ghi}"))
        assertFalse(hasUnbalancedParentheses("«quoted» and （wide）"))
    }

    @Test
    fun hasUnbalancedParentheses_detects_unbalanced_text() {
        assertTrue(hasUnbalancedParentheses("(abc"))
        assertTrue(hasUnbalancedParentheses("abc)"))
        assertTrue(hasUnbalancedParentheses("(abc]"))
    }

    @Test
    fun evenQuoteCount_counts_double_quotes() {
        assertTrue(evenQuoteCount.fold("a\"b\"c".iterator()))
        assertFalse(evenQuoteCount.fold("a\"b\"c\"".iterator()))
    }

    @Test
    fun intSum_still_works() {
        assertTrue(Fold.intSum.fold(listOf(1, 2, 3).iterator()) == 6)
    }
}
