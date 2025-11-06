package one.wabbit.text.check

val evenQuoteCount: Fold<Char, Boolean> =
    object : Fold.WithState<Char, IntVar, Boolean> {
        override fun newState(): IntVar = IntVar(0)

        override fun append(state: IntVar, input: Char): IntVar {
            if (input == '"') state.value++
            return state
        }

        override fun append(state: IntVar, input: Iterator<Char>): IntVar {
            for (c in input) {
                if (c == '"') state.value++
            }
            return state
        }

        override fun combine(left: IntVar, right: IntVar): IntVar {
            left.value += right.value
            return left
        }

        override fun finalize(state: IntVar): Boolean = (state.value % 2 == 0)

        // Optional direct fold implementation for single-thread usage:
        override fun fold(list: Iterator<Char>): Boolean {
            var count = 0
            for (c in list) {
                if (c == '"') count++
            }
            return (count % 2 == 0)
        }
    }
