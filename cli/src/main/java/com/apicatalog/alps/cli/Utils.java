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
package com.apicatalog.alps.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import com.apicatalog.alps.error.DocumentWriterException;
import com.apicatalog.alps.io.DocumentParser;
import com.apicatalog.alps.io.DocumentWriter;
import com.apicatalog.alps.json.JsonDocumentParser;
import com.apicatalog.alps.json.JsonDocumentWriter;
import com.apicatalog.alps.oas.OpenApiReader;
import com.apicatalog.alps.xml.XmlDocumentParser;
import com.apicatalog.alps.xml.XmlDocumentWriter;
import com.apicatalog.alps.yaml.YamlDocumentWriter;

final class Utils {

    private Utils() {
    }
    
    @Deprecated
    static final String getMediaType(final String type, final String path, final boolean input) {
        
        if (Constants.ARG_PARAM_XML.equalsIgnoreCase(type)) {
            return Constants.MEDIA_TYPE_ALPS_XML;
        }

        if (Constants.ARG_PARAM_JSON.equalsIgnoreCase(type)) {
            return Constants.MEDIA_TYPE_ALPS_JSON;
        }

        if (Constants.ARG_PARAM_YAML.equalsIgnoreCase(type)) {
            return Constants.MEDIA_TYPE_ALPS_YAML;
        }

        if (Constants.ARG_PARAM_OPEN_API.equalsIgnoreCase(type)) {
            return Constants.MEDIA_TYPE_OPEN_API;
        }

        if (type != null) {
            throw new IllegalArgumentException("Unknown type [" + type + "], expected xml, json, yaml or oas.");
        }
        
        if (path != null) {
            if (path.toLowerCase().endsWith(".xml") || path.toLowerCase().endsWith("+xml")) {
                return Constants.MEDIA_TYPE_ALPS_XML;
            }

            if (path.toLowerCase().endsWith(".json") || path.toLowerCase().endsWith("+json")) {
                return Constants.MEDIA_TYPE_ALPS_JSON;
            }
            
            if (path.toLowerCase().endsWith(".yaml") || path.toLowerCase().endsWith(".yml") || path.toLowerCase().endsWith("+yaml")) {
                return Constants.MEDIA_TYPE_ALPS_YAML;
            }
            
            throw new IllegalArgumentException("Can not determine " + (input ? "input" : "output") + " file type [" + path + "], please add --" + (input ? "source" : "target")  + "=(json|xml|yaml) argument.");
        }

        throw new IllegalArgumentException("Can not determine " + (input ? "input" : "output") + " type, please add --" + (input ? "source" : "target")  + "=(json|xml|yaml) argument.");
    }

    static final String detectMediaType(File file) {
        if (file.getName() != null) {
            if (file.getName().toLowerCase().endsWith(".xml") || file.getName().toLowerCase().endsWith("+xml")) {
                return Constants.MEDIA_TYPE_ALPS_XML;
            }

            if (file.getName().toLowerCase().endsWith(".json") || file.getName().toLowerCase().endsWith("+json")) {
                return Constants.MEDIA_TYPE_ALPS_JSON;
            }
            
            if (file.getName().toLowerCase().endsWith(".yaml") || file.getName().toLowerCase().endsWith(".yml") || file.getName().toLowerCase().endsWith("+yaml")) {
                return Constants.MEDIA_TYPE_ALPS_YAML;
            }
        }
        return null;
    }
    
    static final InputStream fileToInputStream(final File file) {
        
        if (!file.exists()) {
            System.err.println("Input file '" + file + "' does not exist.");            
            return null;
        }

        if (!file.canRead()) {
            System.err.println("Input file '" + file + "' is not readable.");
            return null;
        }
        
        try {
            return new FileInputStream(file);
            
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        
        return null;
    }

    static final DocumentParser getParser(final String mediaType) {
        
        if (Constants.MEDIA_TYPE_ALPS_JSON.equals(mediaType)) {
            return new JsonDocumentParser();
        }

        if (Constants.MEDIA_TYPE_ALPS_XML.equals(mediaType)) {
            return new XmlDocumentParser();
        }
        
        if (Constants.MEDIA_TYPE_OPEN_API.equals(mediaType)) {
            return new OpenApiReader();
        }

        throw new IllegalArgumentException("Unsupported source media type [" + mediaType + "].");
    }
    
    static final DocumentWriter getWriter(final Writer writer, final String mediaType, final boolean prettyPrint, final boolean verbose) throws DocumentWriterException {
        
        if ("application/alps+json".equals(mediaType)) {
            return JsonDocumentWriter.create(writer, prettyPrint, verbose);            

        } else if ("application/alps+xml".equals(mediaType)) {
            return XmlDocumentWriter.create(writer, prettyPrint, verbose);
            
        } else if (Constants.MEDIA_TYPE_ALPS_YAML.equals(mediaType)) {
            return YamlDocumentWriter.create(writer, verbose);    
        }

        throw new IllegalArgumentException("Unsupported target media type [" + mediaType + "].");
    }    

}