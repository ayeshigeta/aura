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
package org.auraframework.css.parser;

import java.util.Set;

import org.auraframework.def.DefDescriptor;
import org.auraframework.def.ThemeDef;
import org.auraframework.expression.PropertyReference;
import org.auraframework.instance.ValueProvider;
import org.auraframework.system.Location;
import org.auraframework.throwable.quickfix.QuickFixException;

/**
 * Responsible for evaluating a theme expression to the string value. The expression may contain multiple references to
 * theme variables, as well as valid aura expression syntax.
 * 
 * <p>
 * Fully qualified references (For example, "namespace.themeName.variableName") will resolve as specified to the
 * referenced theme variable value. It's possible for the returned value to be different from the one referenced if
 * theme overrides are provided (see {@link ThemeOverrideMap}).
 * 
 * <p>
 * This can also handle taking a string expression and resolving to the actual {@link DefDescriptor} instead. See
 * {@link #resolveToDescriptor(String)}.
 */
public interface ThemeValueProvider extends ValueProvider {
    /**
     * Use this to resolve an expression from a String. Do <b>not</b> create your own {@link PropertyReference} to pass
     * to {@link #getValue(PropertyReference)} as that will most likely result in trying to evaluate the wrong thing
     * (because of quotes). Only use that method if you already have a correct {@link PropertyReference} on hand.
     * 
     * @param expression The expression to evaluate.
     * @param location The location of the expression in the source.
     * 
     * @return The value, same as from {@link #getValue(PropertyReference)}
     * 
     * @throws QuickFixException
     */
    Object getValue(String expression, Location location) throws QuickFixException;

    /**
     * Resolve a reference to the specified {@link DefDescriptor}. Note that the descriptor returned here *may not* be
     * the same one that is used to retrieve the actual value in {@link #resolve(String)}. It may be different if the
     * descriptor is overridden.
     * 
     * @param reference Find the descriptor in this reference.
     * 
     * @return The descriptor referenced.
     */
    DefDescriptor<ThemeDef> getDescriptor(PropertyReference reference);

    /**
     * Similar to {@link #getDescriptor(PropertyReference)}, except it takes a string. Returns a set because there might
     * be multiple descriptors in the expression.
     * 
     * @param expression Find the descriptors in this expression.
     * @param location The location of the expression in the source.
     * 
     * @throws QuickFixException
     */
    Set<DefDescriptor<ThemeDef>> getDescriptors(String expression, Location location) throws QuickFixException;
}
