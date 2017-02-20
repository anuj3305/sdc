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

package org.openecomp.sdc.ci.tests.tosca.datatypes;

import java.util.List;

//	spec page 104	
public class ToscaTopologyTemplateDefinition {

	String description;
	List<ToscaInputsTopologyTemplateDefinition> toscaInputsTopologyTemplateDefinition;
	List<ToscaNodeTemplatesTopologyTemplateDefinition> toscaNodeTemplatesTopologyTemplateDefinition;
	// List<ToscaRelationshipTemplatesTopologyTemplateDefinition>
	List<ToscaGroupsTopologyTemplateDefinition> toscaGroupsTopologyTemplateDefinition;
	// List<ToscaPoliciesTopologyTemplateDefinition>
	// toscaPolociesTopologyTemplateDefinition;
	// List<ToscaOutputsTopologyTemplateDefinition>
	// toscaOutputsTopologyTemplateDefinition;

	public ToscaTopologyTemplateDefinition() {
		super();
	}

	public List<ToscaInputsTopologyTemplateDefinition> getToscaInputsTopologyTemplateDefinition() {
		return toscaInputsTopologyTemplateDefinition;
	}

	public void setToscaInputsTopologyTemplateDefinition(
			List<ToscaInputsTopologyTemplateDefinition> toscaInputsTopologyTemplateDefinition) {
		this.toscaInputsTopologyTemplateDefinition = toscaInputsTopologyTemplateDefinition;
	}

	public List<ToscaNodeTemplatesTopologyTemplateDefinition> getToscaNodeTemplatesTopologyTemplateDefinition() {
		return toscaNodeTemplatesTopologyTemplateDefinition;
	}

	public void setToscaNodeTemplatesTopologyTemplateDefinition(
			List<ToscaNodeTemplatesTopologyTemplateDefinition> toscaNodeTemplatesTopologyTemplateDefinition) {
		this.toscaNodeTemplatesTopologyTemplateDefinition = toscaNodeTemplatesTopologyTemplateDefinition;
	}

	public List<ToscaGroupsTopologyTemplateDefinition> getToscaGroupsTopologyTemplateDefinition() {
		return toscaGroupsTopologyTemplateDefinition;
	}

	public void setToscaGroupsTopologyTemplateDefinition(
			List<ToscaGroupsTopologyTemplateDefinition> toscaGroupsTopologyTemplateDefinition) {
		this.toscaGroupsTopologyTemplateDefinition = toscaGroupsTopologyTemplateDefinition;
	}

	@Override
	public String toString() {
		return "ToscaTopologyTemplateDefinition [toscaInputsTopologyTemplateDefinition="
				+ toscaInputsTopologyTemplateDefinition + ", toscaNodeTemplatesTopologyTemplateDefinition="
				+ toscaNodeTemplatesTopologyTemplateDefinition + ", toscaGroupsTopologyTemplateDefinition="
				+ toscaGroupsTopologyTemplateDefinition + "]";
	}

}