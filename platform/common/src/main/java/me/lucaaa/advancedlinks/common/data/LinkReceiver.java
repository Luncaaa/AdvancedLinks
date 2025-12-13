package me.lucaaa.advancedlinks.common.data;

import java.util.List;

public interface LinkReceiver<T extends Enum<T>> {
    void sendLinks(List<ParsedLink<T>> links);

    String replacePapiPlaceholders(String text);
}