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
package org.auraframework.impl.preload;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.auraframework.def.ApplicationDef;
import org.auraframework.http.AuraBaseServlet;
import org.auraframework.system.AuraContext.Mode;
import org.auraframework.system.AuraContext.Format;
import org.auraframework.test.AuraHttpTestCase;
import org.auraframework.test.annotation.TestLabels;
import org.auraframework.util.json.JsonReader;

/**
 * Basic HTTP retrieve test for checking preloaded namespaces and componentDefs.
 */
public class PreloadNameSpaceHttpTest extends AuraHttpTestCase {
    public PreloadNameSpaceHttpTest(String name) {
        super(name);
    }

    /**
     * Verify that when a component is serialized down to the client, the component Def only has the descriptor and
     * nothing else.
     * <ol>
     * <li>Obtain a valid CSRF token to be used on a get request for a component in JSON format.</li>
     * <li>Request a component in JSON format.</li>
     * </ol>
     */
    @SuppressWarnings("unchecked")
    @TestLabels("auraSanity")
    public void testComponentDef() throws Exception {
        String response = obtainResponseCheckStatus();

        // Obtain a component which uses preloading namespaces
        String componentInJson = response.substring(AuraBaseServlet.CSRF_PROTECT.length());
        Map<String, Object> outerMap = (Map<String, Object>) new JsonReader().read(componentInJson);
        Map<String, Object> component = (Map<String, Object>) outerMap.get("component");
        Map<String, Object> value = (Map<String, Object>) component.get("value");
        Map<String, Object> componentDef = (Map<String, Object>) value.get("componentDef");
        componentDef = (Map<String, Object>) componentDef.get("value");

        // Verify that Descriptor was the only value sent back as part of the componentDef
        assertTrue(componentDef.size() == 1);
        assertTrue(componentDef.containsKey("descriptor"));
        assertEquals(componentDef.get("descriptor"), "markup://preloadTest:test_Preload_Cmp_SameNameSpace");
    }

    /**
     * Test there are no more preloaded namespaces.
     */
    @SuppressWarnings("unchecked")
    public void testPreloadsOnContext() throws Exception {
        String response = obtainResponseCheckStatus();

        // Grab the preloads attached to the context
        String componentInJson = response.substring(AuraBaseServlet.CSRF_PROTECT.length());
        Map<String, Object> outerMap = (Map<String, Object>) new JsonReader().read(componentInJson);
        Map<String, Object> context = (Map<String, Object>) outerMap.get("context");
        ArrayList<String> preloads = (ArrayList<String>) context.get("preloads");

        assertNull("Preloads found in the Context", preloads);
    }

    private String obtainResponseCheckStatus() throws Exception {
        String url = String.format("/aura?aura.tag=%s&aura.deftype=APPLICATION&aura.context=%s", "preloadTest:test_Preload_Cmp_SameNameSpace",
            URLEncoder.encode(getContext(Mode.FTEST, Format.JSON, "preloadTest:test_Preload_Cmp_SameNameSpace",
                ApplicationDef.class, false), "UTF-8"));
        HttpGet get = obtainGetMethod(url);
        HttpResponse httpResponse = perform(get);
        int statusCode = getStatusCode(httpResponse);
        String response = getResponseBody(httpResponse);
        get.releaseConnection();
        assertTrue("Failed to reach aura servlet", statusCode == HttpStatus.SC_OK);

        return response;
    }
}
