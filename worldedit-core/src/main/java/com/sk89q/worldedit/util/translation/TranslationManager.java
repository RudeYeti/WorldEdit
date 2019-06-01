/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.worldedit.util.translation;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.renderer.FriendlyComponentRenderer;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class TranslationManager {

    private final Map<Locale, Map<String, String>> translationMap = new HashMap<>();
    private final FriendlyComponentRenderer<Locale> friendlyComponentRenderer = FriendlyComponentRenderer.from(
            (locale, key) -> new MessageFormat(getTranslationMap(locale).getOrDefault(key, key), locale));
    private Locale defaultLocale = Locale.US;

    public TranslationManager() {
        // Temporary store until we have file loads
        Map<String, String> us = new HashMap<>();
        us.put("worldedit.region.expanded", "Region expanded {0} block(s)");
        translationMap.put(Locale.US, us);
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    private Map<String, String> getTranslationMap(Locale locale) {
        Map<String, String> translations = translationMap.get(locale);
        if (translations == null) {
            translations = translationMap.get(defaultLocale);
        }

        return translations;
    }

    private Component convertComponent(TranslatableComponent component, Locale locale) {
        return friendlyComponentRenderer.render(component, locale);
    }

    public Component convertText(Component component, Locale locale) {
        if (component instanceof TranslatableComponent) {
            return convertComponent((TranslatableComponent) component, locale);
        } else {
            if (component.children().isEmpty()) {
                return component;
            }
            return component.children(component.children().stream().map(comp -> convertText(comp, locale)).collect(Collectors.toList()));
        }
    }
}
