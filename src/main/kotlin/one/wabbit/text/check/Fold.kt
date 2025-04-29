package one.wabbit.text.check

internal class BoolVar(var value: Boolean)
internal class IntVar(var value: Int)
internal class LongVar(var value: Long)
internal class DoubleVar(var value: Double)
class PairVar<A, B>(var first: A, var second: B)

private fun <A, B> Iterator<A>.map(f: (A) -> B): Iterator<B> {
    val self = this
    return object : Iterator<B> {
        override fun hasNext(): Boolean = self.hasNext()
        override fun next(): B = f(self.next())
    }
}

interface Fold<I, O> {
    interface WithState<I, S, O> : Fold<I, O> {
        fun newState(): S
        // Note that S MAY be modified in place and returned
        fun append(state: S, input: I): S
        // Note that S MAY be modified in place and returned
        fun append(state: S, input: Iterator<I>): S

        // Note that both left and right MAY be modified in place and returned
        fun combine(left: S, right: S): S

        // Note that S MAY be modified in place and returned
        fun finalize(state: S): O

        override fun fold(list: Iterator<I>): O {
            var state = newState()
            for (i in list) {
                state = append(state, i)
            }
            return finalize(state)
        }

        fun <O1> mapResult(f: (O) -> O1): Fold<I, O1> {
            val self = this
            return object : Fold.WithState<I, S, O1> {
                override fun newState(): S = self.newState()
                override fun append(state: S, input: I): S = self.append(state, input)
                override fun append(state: S, input: Iterator<I>): S = self.append(state, input)
                override fun combine(left: S, right: S): S = self.combine(left, right)
                override fun finalize(state: S): O1 = f(self.finalize(state))
            }
        }

        fun <I1> mapInput(f: (I1) -> I): Fold<I1, O> {
            val self = this
            return object : Fold.WithState<I1, S, O> {
                override fun newState(): S = self.newState()
                override fun append(state: S, input: I1): S = self.append(state, f(input))
                override fun append(state: S, input: Iterator<I1>): S = self.append(state, input.map(f))
                override fun combine(left: S, right: S): S = self.combine(left, right)
                override fun finalize(state: S): O = self.finalize(state)
            }
        }

        fun <S1, O1> zip(other: Fold.WithState<I, S1, O1>): Fold.WithState<I, PairVar<S, S1>, Pair<O, O1>> {
            val self = this
            return object : Fold.WithState<I, PairVar<S, S1>, Pair<O, O1>> {
                override fun newState(): PairVar<S, S1> = PairVar(self.newState(), other.newState())
                override fun append(state: PairVar<S, S1>, input: I): PairVar<S, S1> {
                    state.first = self.append(state.first, input)
                    state.second = other.append(state.second, input)
                    return state
                }
                override fun append(state: PairVar<S, S1>, input: Iterator<I>): PairVar<S, S1> {
                    for (i in input) {
                        state.first = self.append(state.first, i)
                        state.second = other.append(state.second, i)
                    }
                    return state
                }
                override fun combine(left: PairVar<S, S1>, right: PairVar<S, S1>): PairVar<S, S1> {
                    left.first = self.combine(left.first, right.first)
                    left.second = other.combine(left.second, right.second)
                    return left
                }
                override fun finalize(state: PairVar<S, S1>): Pair<O, O1> = Pair(self.finalize(state.first), other.finalize(state.second))
            }
        }
    }

    fun fold(list: Iterator<I>): O

    companion object {
        val intSum: Fold<Int, Int> = object: Fold.WithState<Int, IntVar, Int> {
            override fun newState(): IntVar = IntVar(0)
            override fun append(state: IntVar, input: Int): IntVar {
                state.value += input
                return state
            }
            override fun append(state: IntVar, input: Iterator<Int>): IntVar {
                for (i in input) {
                    state.value += i
                }
                return state
            }
            override fun combine(left: IntVar, right: IntVar): IntVar {
                left.value += right.value
                return left
            }
            override fun finalize(state: IntVar): Int = state.value
            override fun fold(list: Iterator<Int>): Int {
                var s = 0
                for (i in list) {
                    s += i
                }
                return s
            }
        }
    }
}