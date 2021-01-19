package org.kecak.utils;

import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * @@author aristo
 *
 * Common library to handle repetition code
 */
public interface StreamHelper {
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
