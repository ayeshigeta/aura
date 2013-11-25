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
package org.auraframework.impl.css.parser;

import java.util.List;
import java.util.Set;

import org.auraframework.components.aura.ThemedDeclarationRenderer;
import org.auraframework.def.ComponentDefRef;
import org.auraframework.def.StyleDef;
import org.auraframework.impl.root.component.ComponentDefRefImpl;
import org.auraframework.system.Location;
import org.auraframework.throwable.quickfix.StyleParserException;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.phloc.css.ECSSVersion;
import com.phloc.css.decl.CSSDeclaration;
import com.phloc.css.decl.CSSExpression;
import com.phloc.css.decl.CSSExpressionMemberFunction;
import com.phloc.css.decl.ICSSExpressionMember;
import com.phloc.css.writer.CSSWriterSettings;

/**
 * Reworks the custom theme function, <code>theme(...)</code> or <code>t(...)</code>, to an aura:themedDeclaration
 * component.
 * 
 * @see ThemedDeclarationRenderer
 */
final class ReworkThemeFunction implements DynamicRework<CSSDeclaration> {
    private static final CSSWriterSettings SETTINGS = new CSSWriterSettings(ECSSVersion.LATEST);
    private static final String CANT_MIX = "Cannot mix theme functions with other text. " +
            "Try rewriting like theme(\"'text ' + x.y.z\").";

    private final String filename;
    private final String name;
    private final String namespace;
    private final Set<String> allReferences = Sets.newHashSet();

    /**
     * @param namespace Namespace of the {@link StyleDef}
     * @param name Name of the {@link StyleDef}.
     * @param filename Filename of the css file (for error reporting).
     */
    public ReworkThemeFunction(String namespace, String name, String filename) {
        this.namespace = namespace;
        this.name = name;
        this.filename = filename;
    }

    /**
     * Gets all references this rework has encountered thus far.
     */
    public Set<String> getAllReferences() {
        return allReferences;
    }

    @Override
    public ComponentDefRef perform(CSSDeclaration declaration, List<Exception> errors) {
        CSSExpression expression = declaration.getExpression();
        List<String> references = collectReferences(expression);

        if (references.isEmpty()) {
            return null;
        }

        allReferences.addAll(references);

        int line = expression.getSourceLocation().getFirstTokenBeginLineNumber();
        int col = expression.getSourceLocation().getFirstTokenBeginColumnNumber();
        Location l = new Location(filename, line, col, -1);

        if (references.size() != expression.getMemberCount()) {
            // you can't mix theme functions with plain text in declaration values.
            // for example, "margin: 0 theme(spacingLeftRight)" would be invalid mixing.
            errors.add(new StyleParserException(CANT_MIX, l));
            return null;
        }

        ComponentDefRefImpl.Builder builder = new ComponentDefRefImpl.Builder();

        builder.setDescriptor("aura:themedDeclaration");
        builder.setAttribute("namespace", namespace);
        builder.setAttribute("name", name);
        builder.setAttribute("property", declaration.getProperty());
        builder.setAttribute("references", references);
        builder.setAttribute("location", l);

        return builder.build();
    }

    /**
     * Finds all references to theme variables in the given expression.
     */
    private List<String> collectReferences(CSSExpression expression) {
        List<String> references = Lists.newArrayList();

        for (ICSSExpressionMember member : expression.getAllMembers()) {
            if (!(member instanceof CSSExpressionMemberFunction)) {
                continue;
            }

            CSSExpressionMemberFunction f = (CSSExpressionMemberFunction) member;
            if (f.getFunctionName().equals("theme") || f.getFunctionName().equals("t")) {
                references.add(f.getExpression().getAsCSSString(SETTINGS, 0));
            }
        }

        return references;
    }
}
