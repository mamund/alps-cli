/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.apicatalog.alps.json;

import java.net.URI;

import com.apicatalog.alps.error.DocumentError;
import com.apicatalog.alps.error.InvalidDocumentException;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

final class JsonUtils {

    public static final boolean isObject(final JsonValue value) {
        return value != null && ValueType.OBJECT.equals(value.getValueType());
    }

    public static final boolean isNotObject(final JsonValue value) {
        return value == null || !ValueType.OBJECT.equals(value.getValueType());
    }

    public static final boolean isString(final JsonValue value) {
        return value != null && ValueType.STRING.equals(value.getValueType());
    }

    public static final boolean isNotString(final JsonValue value) {
        return value == null || !ValueType.STRING.equals(value.getValueType());
    }

    public static final String getString(final JsonValue value) {
        return ((JsonString)value).getString();
    }
    
    public static final boolean isArray(final JsonValue value) {
        return value != null && ValueType.ARRAY.equals(value.getValueType());
    }
    
    public static final JsonArray toArray(final JsonValue value) {
        return isArray(value)
                    ? value.asJsonArray()
                    : Json.createArrayBuilder().add(value).build();
    }
    
    public static final URI getHref(final JsonObject object) throws InvalidDocumentException {
        
        final JsonValue href = object.get(JsonConstants.HREF);
        
        if (JsonUtils.isNotString(href)) {
            throw new InvalidDocumentException(DocumentError.MALFORMED_URI, "The 'href' property value must be URI represented as JSON string but was " + href);
        }

        try {
            return URI.create(JsonUtils.getString(href));
            
        } catch (IllegalArgumentException e) {
            throw new InvalidDocumentException(DocumentError.MALFORMED_URI, "The 'href' property value must be URI represented as JSON string but was " + href);
        }
    }

    public static final URI getDefinition(final JsonObject object) throws InvalidDocumentException {
        
        final JsonValue definition = object.get(JsonConstants.DEFINITION);
        
        if (JsonUtils.isNotString(definition)) {
            throw new InvalidDocumentException(DocumentError.MALFORMED_URI, "The 'def' property value must be IRI represented as JSON string but was " + definition);
        }

        try {
            return URI.create(JsonUtils.getString(definition));
            
        } catch (IllegalArgumentException e) {
            throw new InvalidDocumentException(DocumentError.MALFORMED_URI, "The 'def' property value must be IRI represented as JSON string but was " + definition);
        }
    }

    private JsonUtils() {
    }

    public static boolean isNull(JsonValue jsonValue) {
        return jsonValue == null || ValueType.NULL.equals(jsonValue.getValueType());
    }
    
    public static boolean isNotNull(JsonValue jsonValue) {
        return jsonValue != null && !ValueType.NULL.equals(jsonValue.getValueType());
    }
    
    public static boolean isScalar(final JsonValue value) {
        return isNotObject(value) && isNotArray(value);
    }

    public static final boolean isNotArray(final JsonValue value) {
        return value == null || !ValueType.ARRAY.equals(value.getValueType());
    }
    
    public static final JsonValue toValue(final String value) {
        if (value == null) {
            return JsonValue.NULL;
        }
        
        if ("true".equalsIgnoreCase(value)) {
            return JsonValue.TRUE;
        }
        
        if ("false".equalsIgnoreCase(value)) {
            return JsonValue.FALSE;
        }
        
        try {
            return Json.createValue(Double.parseDouble(value));
        } catch (NumberFormatException e) {/*ignored*/}
        
        try {
            return Json.createValue(Long.parseLong(value));
        } catch (NumberFormatException e) {/*ignored*/}
        
        return Json.createValue(value);
    }
}
