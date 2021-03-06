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

package com.ritense.document.autoconfigure;

import com.ritense.document.domain.impl.JsonSchemaDocument;
import com.ritense.document.domain.impl.JsonSchemaDocumentDefinition;
import com.ritense.document.domain.impl.listener.ApplicationReadyEventListenerImpl;
import com.ritense.document.domain.impl.listener.DocumentRelatedFileSubmittedEventListenerImpl;
import com.ritense.document.domain.impl.listener.RelatedJsonSchemaDocumentAvailableEventListenerImpl;
import com.ritense.document.domain.impl.sequence.JsonSchemaDocumentDefinitionSequenceRecord;
import com.ritense.document.repository.DocumentDefinitionRepository;
import com.ritense.document.repository.DocumentDefinitionSequenceRepository;
import com.ritense.document.repository.DocumentRepository;
import com.ritense.document.service.DocumentDefinitionService;
import com.ritense.document.service.DocumentSearchService;
import com.ritense.document.service.DocumentSequenceGeneratorService;
import com.ritense.document.service.DocumentService;
import com.ritense.document.service.UndeployDocumentDefinitionService;
import com.ritense.document.service.impl.JsonSchemaDocumentDefinitionSequenceGeneratorService;
import com.ritense.document.service.impl.JsonSchemaDocumentDefinitionService;
import com.ritense.document.service.impl.JsonSchemaDocumentSearchService;
import com.ritense.document.service.impl.JsonSchemaDocumentService;
import com.ritense.document.service.impl.UndeployJsonSchemaDocumentDefinitionService;
import com.ritense.document.web.rest.DocumentDefinitionResource;
import com.ritense.document.web.rest.DocumentResource;
import com.ritense.document.web.rest.DocumentSearchResource;
import com.ritense.document.web.rest.impl.JsonSchemaDocumentDefinitionResource;
import com.ritense.document.web.rest.impl.JsonSchemaDocumentResource;
import com.ritense.document.web.rest.impl.JsonSchemaDocumentSearchResource;
import com.ritense.resource.service.ResourceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.persistence.EntityManager;

@Configuration
@EnableJpaRepositories(basePackages = "com.ritense.document.repository")
@EntityScan("com.ritense.document.domain")
public class DocumentAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DocumentService.class)
    public JsonSchemaDocumentService documentService(
        final DocumentRepository documentRepository,
        final JsonSchemaDocumentDefinitionService documentDefinitionService,
        final JsonSchemaDocumentDefinitionSequenceGeneratorService documentSequenceGeneratorService,
        final ResourceService resourceService
    ) {
        return new JsonSchemaDocumentService(
            documentRepository,
            documentDefinitionService,
            documentSequenceGeneratorService,
            resourceService
        );
    }

    @Bean
    @ConditionalOnMissingBean(DocumentDefinitionService.class)
    public JsonSchemaDocumentDefinitionService documentDefinitionService(
        final ResourceLoader resourceLoader,
        final DocumentDefinitionRepository<JsonSchemaDocumentDefinition> documentDefinitionRepository
    ) {
        return new JsonSchemaDocumentDefinitionService(
            resourceLoader,
            documentDefinitionRepository
        );
    }

    @Bean
    @ConditionalOnMissingBean(DocumentSequenceGeneratorService.class)
    public JsonSchemaDocumentDefinitionSequenceGeneratorService documentSequenceGeneratorService(
        final DocumentDefinitionSequenceRepository<JsonSchemaDocumentDefinitionSequenceRecord> documentDefinitionSequenceRepository
    ) {
        return new JsonSchemaDocumentDefinitionSequenceGeneratorService(documentDefinitionSequenceRepository);
    }

    @Bean
    @ConditionalOnMissingBean(UndeployDocumentDefinitionService.class)
    public UndeployJsonSchemaDocumentDefinitionService undeployDocumentDefinitionService(
        final JsonSchemaDocumentDefinitionService documentDefinitionService,
        final DocumentService documentService,
        final ApplicationEventPublisher applicationEventPublisher
    ) {
        return new UndeployJsonSchemaDocumentDefinitionService(
            documentDefinitionService,
            documentService,
            applicationEventPublisher
        );
    }

    @Bean
    @ConditionalOnMissingBean(DocumentSearchService.class)
    public JsonSchemaDocumentSearchService documentSearchService(
        final EntityManager entityManager
    ) {
        return new JsonSchemaDocumentSearchService(entityManager);
    }

    @Bean
    @ConditionalOnMissingBean(ApplicationReadyEventListenerImpl.class)
    public ApplicationReadyEventListenerImpl applicationReadyEventListenerImpl(
        final DocumentDefinitionService documentDefinitionService
    ) {
        return new ApplicationReadyEventListenerImpl(documentDefinitionService);
    }

    @Bean
    @ConditionalOnMissingBean(RelatedJsonSchemaDocumentAvailableEventListenerImpl.class)
    public RelatedJsonSchemaDocumentAvailableEventListenerImpl relatedDocumentAvailableEventListener(
        final DocumentService documentService
    ) {
        return new RelatedJsonSchemaDocumentAvailableEventListenerImpl(documentService);
    }

    @Bean
    @ConditionalOnMissingBean(DocumentRelatedFileSubmittedEventListenerImpl.class)
    public DocumentRelatedFileSubmittedEventListenerImpl documentRelatedFileSubmittedEventListener(
        final DocumentService documentService,
        final ResourceService resourceService
    ) {
        return new DocumentRelatedFileSubmittedEventListenerImpl(documentService, resourceService);
    }

    //API
    @Bean
    @ConditionalOnMissingBean(DocumentDefinitionResource.class)
    public JsonSchemaDocumentDefinitionResource documentDefinitionResource(
        final DocumentDefinitionService documentDefinitionService,
        final UndeployDocumentDefinitionService undeployDocumentDefinitionService
    ) {
        return new JsonSchemaDocumentDefinitionResource(documentDefinitionService, undeployDocumentDefinitionService);
    }

    @Bean
    @ConditionalOnMissingBean(DocumentResource.class)
    public JsonSchemaDocumentResource documentResource(DocumentService documentService) {
        return new JsonSchemaDocumentResource(documentService);
    }

    @Bean
    @ConditionalOnMissingBean(DocumentSearchResource.class)
    public JsonSchemaDocumentSearchResource documentSearchResource(DocumentSearchService documentSearchService) {
        return new JsonSchemaDocumentSearchResource(documentSearchService);
    }

}
