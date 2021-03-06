/*
 * Copyright © 2016-2018 European Support Limited
 *
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
 */

package org.openecomp.sdc.ci.tests.utils.rest;

import com.google.gson.Gson;
import org.openecomp.sdc.be.model.User;
import org.openecomp.sdc.ci.tests.api.Urls;
import org.openecomp.sdc.ci.tests.config.Config;
import org.openecomp.sdc.ci.tests.datatypes.CapabilityDetails;
import org.openecomp.sdc.ci.tests.datatypes.http.RestResponse;

import java.util.List;

public class CapabilityRestUtils extends BaseRestUtils {
    private static Gson gson = new Gson();
    private static final String COMPONENT_TYPE = "services";

    public static RestResponse createCapability(String componentId,
                                                 List<CapabilityDetails> capabilityDetailsList,
                                                 User user) throws Exception{
        Config config = Config.instance();
        String url = String.format(Urls.CREATE_CAPABILITY, config.getCatalogBeHost(), config.getCatalogBePort(),
                COMPONENT_TYPE, componentId);

        String data = "{ \"capabilities\" : {" + "\"" +capabilityDetailsList.get(0).getType()+ "\"" +" : "
                + gson.toJson(capabilityDetailsList) + "  } }";

        return sendPost(url, data , user.getUserId(), acceptHeaderData);
    }

    public static RestResponse updateCapability(String componentId,
                                                 List<CapabilityDetails> capabilityDetailsList,
                                                 User user) throws Exception{
        Config config = Config.instance();
        String url = String.format(Urls.UPDATE_CAPABILITY, config.getCatalogBeHost(), config.getCatalogBePort(),
                COMPONENT_TYPE, componentId);

        String data = "{ \"capabilities\" : {" + "\"" +capabilityDetailsList.get(0).getType()+ "\"" +" : "
                + gson.toJson(capabilityDetailsList) + "  } }";

        return sendPost(url, data , user.getUserId(), acceptHeaderData);
    }

    public static RestResponse deleteCapability(String componentId,
                                                 String requirementId,
                                                 User user) throws Exception{
        Config config = Config.instance();
        String url = String.format(Urls.DELETE_CAPABILITY, config.getCatalogBeHost(), config.getCatalogBePort(),
                COMPONENT_TYPE, componentId, requirementId);
        return sendDelete(url, user.getUserId());
    }

    public static RestResponse getCapability(String componentId,
                                              String requirementId,
                                              User user) throws Exception{
        Config config = Config.instance();
        String url = String.format(Urls.GET_CAPABILITY, config.getCatalogBeHost(), config.getCatalogBePort(),
                COMPONENT_TYPE, componentId, requirementId);
        return sendDelete(url, user.getUserId());
    }

}
