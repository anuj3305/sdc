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

package org.openecomp.sdc.translator.services.heattotosca.impl.resourcetranslation;

import static org.openecomp.sdc.translator.services.heattotosca.HeatToToscaLogConstants.LOG_UNSUPPORTED_VOL_ATTACHMENT_VOLUME_REQUIREMENT_CONNECTION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.onap.sdc.tosca.datatypes.model.NodeTemplate;
import org.onap.sdc.tosca.datatypes.model.NodeType;
import org.onap.sdc.tosca.datatypes.model.RequirementDefinition;
import org.openecomp.sdc.common.errors.CoreException;
import org.openecomp.sdc.heat.datatypes.manifest.FileData;
import org.openecomp.sdc.heat.datatypes.model.HeatOrchestrationTemplate;
import org.openecomp.sdc.heat.datatypes.model.HeatResourcesTypes;
import org.openecomp.sdc.heat.datatypes.model.Resource;
import org.openecomp.sdc.tosca.datatypes.ToscaCapabilityType;
import org.openecomp.sdc.tosca.datatypes.ToscaNodeType;
import org.openecomp.sdc.tosca.datatypes.ToscaRelationshipType;
import org.openecomp.sdc.tosca.datatypes.ToscaServiceModel;
import org.openecomp.sdc.tosca.services.ToscaAnalyzerService;
import org.openecomp.sdc.tosca.services.impl.ToscaAnalyzerServiceImpl;
import org.openecomp.sdc.translator.datatypes.heattotosca.AttachedResourceId;
import org.openecomp.sdc.translator.datatypes.heattotosca.TranslationContext;
import org.openecomp.sdc.translator.datatypes.heattotosca.to.ResourceFileDataAndIDs;
import org.openecomp.sdc.translator.datatypes.heattotosca.to.TranslateTo;
import org.openecomp.sdc.translator.datatypes.heattotosca.to.TranslatedHeatResource;
import org.openecomp.sdc.translator.services.heattotosca.HeatToToscaUtil;
import org.openecomp.sdc.translator.services.heattotosca.errors.MissingMandatoryPropertyErrorBuilder;
import org.openecomp.sdc.translator.services.heattotosca.helper.VolumeTranslationHelper;

class NovaToVolResourceConnection extends ResourceConnectionUsingRequirementHelper {

    NovaToVolResourceConnection(ResourceTranslationBase resourceTranslationBase,
                                TranslateTo translateTo, FileData nestedFileData,
                                NodeTemplate substitutionNodeTemplate, NodeType nodeType) {
        super(resourceTranslationBase, translateTo, nestedFileData, substitutionNodeTemplate, nodeType);
    }

    @Override
    boolean isDesiredNodeTemplateType(NodeTemplate nodeTemplate) {
        ToscaAnalyzerService toscaAnalyzerService = new ToscaAnalyzerServiceImpl();
        ToscaServiceModel toscaServiceModel =
                HeatToToscaUtil.getToscaServiceModel(translateTo.getContext());
        return toscaAnalyzerService.isTypeOf(nodeTemplate, ToscaNodeType.NOVA_SERVER,
                translateTo.getContext().getTranslatedServiceTemplates()
                        .get(translateTo.getResource().getType()), toscaServiceModel);
    }

    @Override
    List<Predicate<RequirementDefinition>> getPredicatesListForConnectionPoints() {
        ArrayList<Predicate<RequirementDefinition>> predicates = new ArrayList<>();
        predicates
                .add(req -> req.getCapability().equals(ToscaCapabilityType.NATIVE_ATTACHMENT)
                        && req.getNode().equals(ToscaNodeType.NATIVE_BLOCK_STORAGE)
                        && req.getRelationship()
                        .equals(ToscaRelationshipType.NATIVE_ATTACHES_TO));
        return predicates;
    }

    @Override
    Optional<List<String>> getConnectorPropertyParamName(String heatResourceId, Resource heatResource,
                                                         HeatOrchestrationTemplate
                                                                 nestedHeatOrchestrationTemplate,
                                                         String nestedHeatFileName) {


        Optional<AttachedResourceId> volumeId = HeatToToscaUtil
                .extractAttachedResourceId(nestedFileData.getFile(), nestedHeatOrchestrationTemplate,
                        translateTo.getContext(), heatResource.getProperties().get("volume_id"));
        if (volumeId.isPresent() && volumeId.get().isGetParam()
                && volumeId.get().getEntityId() instanceof String) {
            return Optional.of(Collections.singletonList((String) volumeId.get().getEntityId()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    String getDesiredResourceType() {
        return HeatResourcesTypes.CINDER_VOLUME_ATTACHMENT_RESOURCE_TYPE.getHeatResource();
    }

    @Override
    void addRequirementToConnectResources(
            Map.Entry<String, RequirementDefinition> requirementDefinitionEntry,
            List<String> paramNames) {


        if (paramNames == null || paramNames.isEmpty()) {
            return;
        }

        List<String> supportedVolumeTypes =
                Collections.singletonList(HeatResourcesTypes.CINDER_VOLUME_RESOURCE_TYPE.getHeatResource());

        for (String paramName : paramNames) {
            Object paramValue = translateTo.getResource().getProperties().get(paramName);
            addRequirementToConnectResource(requirementDefinitionEntry, paramName, paramValue,
                    supportedVolumeTypes);
        }

    }

    @Override
    boolean validateResourceTypeSupportedForReqCreation(String nestedResourceId,
                                                        String nestedPropertyName,
                                                        String connectionPointId,
                                                        Resource connectedResource,
                                                        List<String> supportedTypes) {


        if (resourceTranslationBase.isUnsupportedResourceType(connectedResource, supportedTypes)) {
            logger.warn(LOG_UNSUPPORTED_VOL_ATTACHMENT_VOLUME_REQUIREMENT_CONNECTION, nestedResourceId,
                    nestedPropertyName, connectedResource.getType(), connectionPointId, supportedTypes.toString());
            return false;
        }

        return true;
    }

    @Override
    protected Optional<List<Map.Entry<String, Resource>>> getResourceByTranslatedResourceId(
            String translatedResourceId, HeatOrchestrationTemplate nestedHeatOrchestrationTemplate) {


        List<Predicate<Map.Entry<String, Resource>>> predicates =
                buildPredicates(nestedFileData.getFile(), nestedHeatOrchestrationTemplate,
                        translatedResourceId);
        List<Map.Entry<String, Resource>> list =
                nestedHeatOrchestrationTemplate.getResources().entrySet()
                        .stream()
                        .filter(entry -> predicates
                                .stream()
                                .allMatch(p -> p.test(entry)))
                        .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            return Optional.empty();
        } else {
            return Optional.of(list);
        }
    }

    @Override
    Optional<String> getConnectionTranslatedNodeUsingGetParamFunc(
            Map.Entry<String, RequirementDefinition> requirementDefinitionEntry, String paramName,
            List<String> supportedTargetNodeTypes) {


        Optional<String> targetTranslatedNodeId = super
                .getConnectionTranslatedNodeUsingGetParamFunc(requirementDefinitionEntry, paramName,
                        supportedTargetNodeTypes);
        if (targetTranslatedNodeId.isPresent()) {
            return targetTranslatedNodeId;
        }
        Optional<AttachedResourceId> attachedResourceId =
                HeatToToscaUtil.extractAttachedResourceId(translateTo, paramName);
        if (!attachedResourceId.isPresent()) {
            return Optional.empty();
        }
        AttachedResourceId resourceId = attachedResourceId.get();
        if (resourceId.isGetParam() && resourceId.getEntityId() instanceof String) {
            TranslatedHeatResource shareResource =
                    translateTo.getContext().getHeatSharedResourcesByParam().get(resourceId.getEntityId());
            if (Objects.nonNull(shareResource)) {
                return Optional.empty();
            }
            List<FileData> allFilesData = translateTo.getContext().getManifest().getContent().getData();
            Optional<FileData> fileData = HeatToToscaUtil.getFileData(translateTo.getHeatFileName(), allFilesData);
            if (fileData.isPresent()) {
                Optional<ResourceFileDataAndIDs> fileDataContainingResource = new VolumeTranslationHelper(logger)
                                .getFileDataContainingVolume(fileData.get().getData(),
                                        (String) resourceId.getEntityId(), translateTo, FileData.Type.HEAT_VOL);
                if (fileDataContainingResource.isPresent()) {
                    return Optional.of(fileDataContainingResource.get().getTranslatedResourceId());
                }
            }
        }

        return Optional.empty();
    }

    private List<Predicate<Map.Entry<String, Resource>>> buildPredicates(
            String fileName,
            HeatOrchestrationTemplate heatOrchestrationTemplate,
            String novaTranslatedResourceId) {
        List<Predicate<Map.Entry<String, Resource>>> list = new ArrayList<>();
        list.add(entry -> entry.getValue().getType().equals(getDesiredResourceType()));
        list.add(entry -> {
            Object instanceUuidProp = entry.getValue().getProperties().get("instance_uuid");
            TranslationContext context = translateTo.getContext();
            Optional<AttachedResourceId> instanceUuid = HeatToToscaUtil
                    .extractAttachedResourceId(fileName, heatOrchestrationTemplate, context,
                            instanceUuidProp);
            if (instanceUuid.isPresent()) {
                Optional<String> resourceTranslatedId =
                        ResourceTranslationBase.getResourceTranslatedId(fileName, heatOrchestrationTemplate,
                                (String) instanceUuid.get().getTranslatedId(), context);
                return resourceTranslatedId.isPresent()
                        && resourceTranslatedId.get().equals(novaTranslatedResourceId);

            } else {
                throw new CoreException(new MissingMandatoryPropertyErrorBuilder("instance_uuid").build());
            }
        });
        return list;
    }
}
