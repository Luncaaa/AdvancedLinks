package me.lucaaa.advancedlinks.common.data;

import java.util.Collection;
import java.util.List;

public interface LinkReceiver<T, S extends Enum<S>> {
    void sendLinks(Collection<T> globalLinks, List<ParsedLink<S>> links);

    String replacePapiPlaceholders(String text);
}