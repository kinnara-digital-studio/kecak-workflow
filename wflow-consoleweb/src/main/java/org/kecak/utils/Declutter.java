package org.kecak.utils;

import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.kecak.commons.util.StreamHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @@author aristo
 *
 * Common library to handle repetition code
 */
public interface Declutter extends StreamHelper {
    /**
     * Nullsafe. If string is null or empty
     *
     * @param value
     * @return
     */
    default boolean isEmpty(@Nullable Object value) {
        return Optional.ofNullable(value)
                .map(String::valueOf)
                .map(String::isEmpty)
                .orElse(true);
    }

    default boolean isEmpty(@Nullable Object[] array) {
        return Optional.ofNullable(array)
                .map(a -> a.length)
                .map(i -> i == 0)
                .orElse(true);
    }

    /**
     * Nullsafe. If object is not null and not empty
     *
     * @param value
     * @return
     */
    default boolean isNotEmpty(@Nullable Object value) {
        return !isEmpty(value);
    }

    /**
     * Nullsafe. If collection is null or empty
     *
     * @param collection
     * @param <T>
     * @return
     */
    default <T> boolean isEmpty(@Nullable Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::isEmpty)
                .orElse(true);
    }

    /**
     * Nullsafe. If collection is not null and not empty
     *
     * @param collection
     * @param <T>
     * @return
     */
    default <T> boolean isNotEmpty(@Nullable Collection<T> collection) {
        return !isEmpty(collection);
    }


    /**
     * If value null then return failover
     *
     * @param value
     * @param then
     * @param <T>
     * @return
     */
    @Nonnull
    default <T, U extends T> T ifNullThen(@Nullable T value, @Nonnull U then) {
        return value == null ? then : value;
    }

    /**
     * If value empty or null then return failover
     *
     * @param value
     * @param then
     * @param <T>
     * @return
     */
    default <T, U extends T> T ifEmptyThen(@Nullable T value, @Nonnull U then) {
        return isEmpty(value) ? then : value;
    }

    /**
     * Return null if string empty
     *
     * @param s
     * @return
     */
    default String nullIfEmpty(String s) {
        return s.isEmpty() ? null : s;
    }

    /**
     * Stream element children
     *
     * @param element
     * @return
     */
    @Nonnull
    default Stream<Element> elementStream(@Nonnull Element element, FormData formData) {
        if (!element.isAuthorize(formData)) {
            return Stream.empty();
        }

        Stream<Element> stream = Stream.of(element);
        for (Element child : element.getChildren()) {
            stream = Stream.concat(stream, elementStream(child, formData));
        }
        return stream;
    }
}
