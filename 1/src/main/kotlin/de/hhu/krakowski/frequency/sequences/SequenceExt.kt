package de.hhu.krakowski.frequency.sequences

fun <T> Sequence<T>.duplicates(): Sequence<T> {
    return this.duplicatesBy { it }
}

fun <T, K> Sequence<T>.duplicatesBy(selector: (T) -> K): Sequence<T> {
    return DuplicatesSequence(this, selector)
}