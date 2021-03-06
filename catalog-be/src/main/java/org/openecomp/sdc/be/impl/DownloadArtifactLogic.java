/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

package org.openecomp.sdc.be.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fj.data.Either;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.eclipse.jgit.util.Base64;
import org.openecomp.sdc.be.dao.api.ResourceUploadStatus;
import org.openecomp.sdc.be.info.ArtifactAccessInfo;
import org.openecomp.sdc.be.info.ArtifactAccessList;
import org.openecomp.sdc.be.info.ServletJsonResponse;
import org.openecomp.sdc.be.resources.data.ESArtifactData;
import org.openecomp.sdc.common.api.Constants;
import org.openecomp.sdc.common.log.wrappers.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DownloadArtifactLogic {

    private static final Logger log = Logger.getLogger(DownloadArtifactLogic.class);

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Response downloadArtifact(final String artifactName, Either<? extends ESArtifactData, ResourceUploadStatus> getArtifactStatus, String artifactId) {
        Response response = null;

        if (getArtifactStatus.isRight()) {
            log.debug("Could not find artifact for with id: {}", artifactId);
            ResourceUploadStatus status = getArtifactStatus.right().value();
            if (status == ResourceUploadStatus.COMPONENT_NOT_EXIST)
                response = Response.status(HttpStatus.SC_NO_CONTENT).build();
            else
                response = Response.status(HttpStatus.SC_NOT_FOUND).build();

            return response;
        }
        // convert artifact to inputstream
        else {
            ESArtifactData artifactData = getArtifactStatus.left().value();
            byte[] artifactPayload = artifactData.getDataAsArray();

            log.debug("payload is encoded. perform decode");
            byte[] decodedPayload = Base64.decode(new String(artifactPayload));
            final InputStream artifactStream = new ByteArrayInputStream(decodedPayload);
            log.debug("found artifact for with id: {}", artifactId);

            // outputstream for response
            StreamingOutput stream = output -> {
                try {
                    IOUtils.copy(artifactStream, output);
                } catch (IOException e) {
                    log.debug("failed to copy artifact payload into response");
                    throw new WebApplicationException(e);
                }
            };
            return Response.ok(stream).type(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .header("content-disposition", "attachment; filename = " + artifactName)
                    .build();

        }
    }

    public List<ArtifactAccessInfo> convertArtifactList(List<? extends ESArtifactData> artifactsList, String servletPath) {

        List<ArtifactAccessInfo> artifactAccessList = new ArrayList<>();
        for (ESArtifactData artifact : artifactsList) {
            ArtifactAccessInfo accessInfo = new ArtifactAccessInfo(servletPath);
            artifactAccessList.add(accessInfo);
        }
        return artifactAccessList;
    }

    public Response createArtifactListResponse(final String serviceName, Either<List<ESArtifactData>, ResourceUploadStatus> getArtifactsStatus/*
                                                                                                                                                 * List < ? extends IResourceData> artifactsList
                                                                                                                                                 */, String servletPath) {
        Response response;
        List<ArtifactAccessInfo> artifactAccessInfos;
        if (getArtifactsStatus.isRight()) {
            // if there are no artifacts - return No-Content
            ResourceUploadStatus status = getArtifactsStatus.right().value();
            if (status == ResourceUploadStatus.COMPONENT_NOT_EXIST || status == ResourceUploadStatus.SERVICE_NOT_EXIST) {
                log.debug("resource {} does not exist", serviceName);
                response = Response.status(HttpStatus.SC_NOT_FOUND).entity("[]").build();

            } else {
                log.debug("No content was found for {}", serviceName);
                response = Response.status(HttpStatus.SC_NO_CONTENT).entity("[]").build();
            }
            return response;
        } else {
            List<? extends ESArtifactData> artifactsList = getArtifactsStatus.left().value();
            log.debug("{} artifacts were found for {}", artifactsList.size(), serviceName);
            artifactAccessInfos = convertArtifactList(artifactsList, servletPath);

            String artifactDataJson = gson.toJson(new ArtifactAccessList(artifactAccessInfos));
            response = Response.status(HttpStatus.SC_OK).entity(artifactDataJson).type(MediaType.APPLICATION_JSON_TYPE).build();

            return response;
        }
    }

    public Response buildResponse(int status, String errorMessage) {

        ServletJsonResponse jsonResponse = new ServletJsonResponse();
        jsonResponse.setDescription(errorMessage);
        jsonResponse.setSource(Constants.CATALOG_BE);

        return Response.status(status).entity(jsonResponse).build();
    }

}
