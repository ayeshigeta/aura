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
({
	/**
	 * Test multiple inputRichText components rendered.
	 */
	testMultipleRichTextComponents: {
		test : function(component) {
			this.assertRichTextInitalized(component.find("Text"));
			this.assertRichTextInitalized(component.find("rtCustom"));
		}
	},
	
    /**
     * Test html content.
     */
    testRichTextHtmlContent:{
    	attributes : {testContent: "<b>some content</b></html>"},
    	test : [function(component) {
    		component.find("base").find("submitBtn").get("e.press").fire();
    		$A.test.addWaitFor(false, function(){
    			var content = component.find("base").find("outputValue").get("v.value");
    			return content != "";
			});
    	}, function(component) {
    		var rtValue = component.find("Text").get("v.value");
    		$A.test.assertEquals("<b>some content</b></html>", rtValue, 
    			"Rich text value expected is incorrect");
    	}]
    },
	
	assertRichTextInitalized : function(rtCmp) {
    	var textArea = rtCmp.find("textAreaElem");
    	$A.test.assertNotNull(textArea, "Component did not initialize correctly");	
    }
})