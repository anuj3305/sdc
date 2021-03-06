/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2019, Nordix Foundation. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.sdc.vendorsoftwareproduct.services.impl.etsi;

import org.apache.commons.io.IOUtils;
import org.onap.sdc.tosca.parser.utils.YamlToObjectConverter;
import org.openecomp.core.utilities.file.FileContentHandler;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.openecomp.sdc.tosca.csar.Manifest;
import org.openecomp.sdc.tosca.csar.OnboardingToscaMetadata;
import org.openecomp.sdc.tosca.csar.ToscaMetadata;

import static org.openecomp.sdc.tosca.csar.CSARConstants.TOSCA_META_ETSI_ENTRY_CHANGE_LOG;
import static org.openecomp.sdc.tosca.csar.CSARConstants.TOSCA_META_ENTRY_DEFINITIONS;
import static org.openecomp.sdc.tosca.csar.CSARConstants.TOSCA_META_ETSI_ENTRY_MANIFEST;
import static org.openecomp.sdc.tosca.csar.CSARConstants.TOSCA_META_ORIG_PATH_FILE_NAME;
import static org.openecomp.sdc.tosca.csar.CSARConstants.TOSCA_META_PATH_FILE_NAME;

public class ETSIServiceImpl implements ETSIService {

    private Configuration configuration;

    public ETSIServiceImpl() throws IOException {
        InputStream io = getClass().getClassLoader().getResourceAsStream("nonManoConfig.yaml");
        if(io == null){
            throw new IOException("Non Mano configuration not found");
        }
        String data = IOUtils.toString(io, StandardCharsets.UTF_8);
        YamlToObjectConverter yamlToObjectConverter = new YamlToObjectConverter();
        configuration = yamlToObjectConverter.convertFromString(data, Configuration.class);
    }

    public ETSIServiceImpl(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean isSol004WithToscaMetaDirectory(FileContentHandler handler) throws IOException {
        Map<String, byte[]> templates = handler.getFiles();
        return isMetaFilePresent(templates) && hasMetaMandatoryEntries(getMetadata(handler));
    }

    @Override
    public void moveNonManoFileToArtifactFolder(FileContentHandler handler, Manifest manifest) {
        for (Map.Entry<String, List<String>> entry : manifest.getNonManoSources().entrySet()) {
            String e = entry.getKey();
            List<String> k = entry.getValue();
            updateNonManoLocation(handler, e, k);
        }
    }

    private InputStream getMetadata(FileContentHandler contentHandler) throws IOException{
        if(contentHandler.containsFile(TOSCA_META_PATH_FILE_NAME)){
            return contentHandler.getFileContent(TOSCA_META_PATH_FILE_NAME);
        }else if(contentHandler.containsFile(TOSCA_META_ORIG_PATH_FILE_NAME)){
            return contentHandler.getFileContent(TOSCA_META_ORIG_PATH_FILE_NAME);
        }
        throw new IOException("TOSCA.meta file does not exist");
    }

    private void updateNonManoLocation(FileContentHandler handler, String nonManoKey, List<String> sources) {
        Map<String, byte[]> files = handler.getFiles();
        for (String key : sources) {
            if (files.containsKey(key)) {
                updateLocation(key, nonManoKey, files);
            }
        }
    }

    private void updateLocation(String key, String nonManoKey, Map<String, byte[]> files){
        if (nonManoKey == null || nonManoKey.isEmpty()) {
            return;
        }
        Map<String, NonManoType> map = configuration.getNonManoKeyFolderMapping();
        if (map.containsKey(nonManoKey)) {
            NonManoType nonManoPair = map.get(nonManoKey);
            String newLocation = nonManoPair.getType() + "/" +
                    nonManoPair.getLocation() + "/" + getFileName(key);
            if (!files.containsKey(newLocation)) {
                files.put(newLocation, files.remove(key));
            }
        }
    }


    private String getFileName(String key) {
        return key.substring(key.lastIndexOf('/') + 1);
    }

    private boolean hasMetaMandatoryEntries(InputStream metadataInputStream) throws IOException {

        ToscaMetadata toscaMetadata = OnboardingToscaMetadata.parseToscaMetadataFile(metadataInputStream);
        Map<String, String> metaDataEntries = toscaMetadata.getMetaEntries();
        return metaDataEntries.containsKey(TOSCA_META_ENTRY_DEFINITIONS) && metaDataEntries.containsKey(TOSCA_META_ETSI_ENTRY_MANIFEST)
                && metaDataEntries.containsKey(TOSCA_META_ETSI_ENTRY_CHANGE_LOG);
    }

    private boolean isMetaFilePresent(Map<String, byte[]> handler) {
        return handler.containsKey(TOSCA_META_PATH_FILE_NAME) || handler.containsKey(TOSCA_META_ORIG_PATH_FILE_NAME);
    }
}
