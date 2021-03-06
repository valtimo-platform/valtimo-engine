/*
 * Copyright 2015-2020 Ritense BV, the Netherlands.
 *
 * Licensed under EUPL, Version 1.2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ritense.valtimo.security.config;

import com.ritense.valtimo.contract.security.config.HttpConfigurerConfigurationException;
import com.ritense.valtimo.contract.security.config.HttpSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static com.ritense.valtimo.contract.authentication.AuthoritiesConstants.ADMIN;
import static com.ritense.valtimo.contract.authentication.AuthoritiesConstants.USER;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

public class ProcessHttpSecurityConfigurer implements HttpSecurityConfigurer {

    @Override
    public void configure(HttpSecurity http) {
        try {
            http.authorizeRequests()
                .antMatchers(GET, "/api/process/definition").hasAuthority(USER)
                .antMatchers(GET, "/api/process/definition/{processDefinitionId}/count").hasAuthority(USER)
                .antMatchers(GET, "/api/process/definition/{processDefinitionId}/xml").hasAuthority(USER)
                .antMatchers(PUT, "/api/process/definition/{processDefinitionId}/xml/timer").hasAuthority(USER)
                .antMatchers(GET, "/api/process/definition/{processDefinitionKey}/search-properties").hasAuthority(USER)
                .antMatchers(GET, "/api/process/definition/{processDefinitionKey}").hasAuthority(USER)
                .antMatchers(GET, "/api/process/definition/{processDefinitionKey}/versions").hasAuthority(USER)
                .antMatchers(GET, "/api/process/definition/{processDefinitionKey}/start-form").hasAuthority(USER)
                .antMatchers(GET, "/api/process/definition/{processDefinitionKey}/usertasks").hasAuthority(USER)
                .antMatchers(GET, "/api/process/definition/{processDefinitionKey}/heatmap/count").hasAuthority(USER)
                .antMatchers(GET, "/api/process/definition/{processDefinitionKey}/heatmap/duration").hasAuthority(USER)
                .antMatchers(POST, "/api/process/definition/{processDefinitionKey}/{businessKey}/start").hasAuthority(USER)
                .antMatchers(GET, "/api/process/definition/{sourceProcessDefinitionId}/{targetProcessDefinitionId}/flownodes").hasAuthority(USER)
                .antMatchers(POST, "/api/process/definition/{sourceProcessDefinitionId}/{targetProcessDefinitionId}/migrate").hasAuthority(ADMIN)
                .antMatchers(GET, "/api/process/{processInstanceId}").hasAuthority(USER)
                .antMatchers(GET, "/api/process/{processInstanceId}/history").hasAuthority(USER)
                .antMatchers(GET, "/api/process/{processInstanceId}/log").hasAuthority(USER)
                .antMatchers(GET, "/api/process/{processInstanceId}/tasks").hasAuthority(USER)
                .antMatchers(GET, "/api/process/{processInstanceId}/activetask").hasAuthority(USER)
                .antMatchers(GET, "/api/process/{processInstanceId}/xml").hasAuthority(USER)
                .antMatchers(GET, "/api/process/{processInstanceId}/activities").hasAuthority(USER)
                .antMatchers(GET, "/api/process/{processInstanceId}/comments").hasAuthority(USER)
                .antMatchers(GET, "/api/process/{processDefinitionName}/search").hasAuthority(USER)
                .antMatchers(GET, "/api/process/{processDefinitionName}/count").hasAuthority(USER)
                .antMatchers(POST, "/api/process/{processInstanceId}/comment").hasAuthority(USER)
                .antMatchers(POST, "/api/process/{processInstanceId}/delete").hasAuthority(ADMIN)
                //V2
                .antMatchers(POST, "/api/v2/process/definition/{processDefinitionId}/count").hasAuthority(USER)
                .antMatchers(POST, "/api/v2/process/{processDefinitionName}/search").hasAuthority(USER)
                .antMatchers(POST, "/api/v2/process/{processDefinitionName}/count").hasAuthority(USER);
        } catch (Exception e) {
            throw new HttpConfigurerConfigurationException(e);
        }
    }

}