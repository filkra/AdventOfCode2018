package de.hhu.krakowski.frequency.sequences

internal class DuplicatesSequence<T, K>(private val source: Sequence<T>, private val keySelector: (T) -> K) : Sequence<T> {
    override fun iterator(): Iterator<T> = DuplicatesIterator(source.iterator(), keySelector)
}

private class DuplicatesIterator<T, K>(private val source: Iterator<T>, private val keySelector: (T) -> K) : AbstractIterator<T>() {
    private val observed = HashMap<K, Int>()

    override fun computeNext() {
        while (source.hasNext()) {
            val next = source.next()
            val key = keySelector(next)

            observed[key] = (observed[key] ?: 0) + 1

            if (observed[key] == 2) {
                setNext(next)
                return
            }
        }

        done()
    }
}