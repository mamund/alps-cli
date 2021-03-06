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
import java.util.HashSet;
import java.util.Set;

import com.apicatalog.alps.dom.element.Link;
import com.apicatalog.alps.error.DocumentError;
import com.apicatalog.alps.error.InvalidDocumentException;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;

final class JsonLink implements Link {

    private URI href;
    private String rel;
    
    @Override
    public URI href() {
        return href;
    }

    @Override
    public String rel() {
        return rel;
    }

    public static final Set<Link> parse(final JsonValue value) throws InvalidDocumentException {
        
        final Set<Link> links = new HashSet<>();
        
        for (final JsonValue item : JsonUtils.toArray(value)) {
            
            if (JsonUtils.isNotObject(item)) {
                throw new InvalidDocumentException(DocumentError.INVALID_LINK, "Link property must be JSON object but was " + item.getValueType());
            }
            
            links.add(parseObject(item.asJsonObject()));
        }
        
        return links;
    }
    
    private static final Link parseObject(final JsonObject linkObject) throws InvalidDocumentException {
        
        final JsonLink link = new JsonLink();
        
        if (!linkObject.containsKey(JsonConstants.HREF)) {
            throw new InvalidDocumentException(DocumentError.MISSING_HREF, "Link object must contain 'href' property");
        }

        if (!linkObject.containsKey(JsonConstants.RELATION)) {
            throw new InvalidDocumentException(DocumentError.MISSING_REL, "Link object must contain 'rel' property");
        }
        
        final JsonValue href = linkObject.get(JsonConstants.HREF);
        
        if (JsonUtils.isNotString(href)) {
            throw new InvalidDocumentException(DocumentError.MALFORMED_URI, "Link.href property must be URI but was " + href.getValueType());
        }
        
        try {
            
            link.href = URI.create(JsonUtils.getString(href));
            
        } catch (IllegalArgumentException e) {
            throw new InvalidDocumentException(DocumentError.MALFORMED_URI, "Link.href property must be URI but was " + href);
        }

        final JsonValue rel = linkObject.get(JsonConstants.RELATION);
        
        if (JsonUtils.isNotString(href)) {
            throw new InvalidDocumentException(DocumentError.INVALID_REL, "Link.rel property must be string but was " + rel.getValueType());
        }

        link.rel = JsonUtils.getString(rel);
        
        return link;
    }
    
    public static final JsonValue toJson(Set<Link> links) {
        
        if (links.size() == 1) {
            return toJson(links.iterator().next());
        }
        
        final JsonArrayBuilder jsonLinks = Json.createArrayBuilder();
        
        links.stream().map(JsonLink::toJson).forEach(jsonLinks::add);
        
        return jsonLinks.build();
    }

    public static final JsonValue toJson(Link link) {
        
        final JsonObjectBuilder jsonLink = Json.createObjectBuilder();
        
        if (link.href() != null) {
            jsonLink.add(JsonConstants.HREF, link.href().toString());
        }
        
        if (link.rel() != null && !link.rel().isBlank()) {
            jsonLink.add(JsonConstants.RELATION, link.rel());
        }
        
        return jsonLink.build();
    }
}
