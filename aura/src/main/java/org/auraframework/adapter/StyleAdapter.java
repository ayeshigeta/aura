/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.auraframework.adapter;

import org.auraframework.css.parser.ThemeOverrideMap;
import org.auraframework.css.parser.ThemeValueProvider;
import org.auraframework.def.ThemeDef;

/**
 * Adapter for CSS/Style stuff.
 */
public interface StyleAdapter extends AuraAdapter {
    /**
     * Gets a {@link ThemeValueProvider}, used for resolving {@link ThemeDef} variables. Uses the default overrides
     * specified in the currently loaded application.
     */
    ThemeValueProvider getThemeValueProvider();

    /**
     * Gets a {@link ThemeValueProvider}, used for resolving {@link ThemeDef} variables.
     * 
     * @param overrides Overridden {@link ThemeDef}s.
     */
    ThemeValueProvider getThemeValueProvider(ThemeOverrideMap overrides);

    /**
     * Gets a {@link ThemeValueProvider}, used for resolving {@link ThemeDef} variables. This will ignore any overrides.
     */
    ThemeValueProvider getThemeValueProviderNoOverrides();
}
