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

package com.ritense.valtimo.autoconfigure;

import com.ritense.valtimo.repository.ProcessDataObjectRelationRepository;
import com.ritense.valtimo.service.ProcessDataObjectService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan("com.ritense.valtimo.domain.process")
public class ProcessDataObjectAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ProcessDataObjectService.class)
    public ProcessDataObjectService processDataObjectService(
        final ProcessDataObjectRelationRepository processDataObjectRelationRepository,
        final ApplicationContext applicationContext,
        final SqlSessionFactory camundaSqlSessionFactory
    ) {
        return new ProcessDataObjectService(processDataObjectRelationRepository, applicationContext, camundaSqlSessionFactory);
    }

}
