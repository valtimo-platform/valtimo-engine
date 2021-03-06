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

package com.ritense.document.service.impl;

import com.ritense.document.domain.DocumentDefinition;
import com.ritense.document.domain.impl.JsonSchema;
import com.ritense.document.domain.impl.JsonSchemaDocumentDefinition;
import com.ritense.document.domain.impl.JsonSchemaDocumentDefinitionId;
import com.ritense.document.exception.UnknownDocumentDefinitionException;
import com.ritense.document.repository.DocumentDefinitionRepository;
import com.ritense.document.service.DocumentDefinitionService;
import com.ritense.document.service.result.DeployDocumentDefinitionResult;
import com.ritense.document.service.result.DeployDocumentDefinitionResultFailed;
import com.ritense.document.service.result.DeployDocumentDefinitionResultSucceeded;
import com.ritense.document.service.result.error.DocumentDefinitionError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static com.ritense.valtimo.contract.utils.AssertionConcern.assertArgumentNotNull;

@Slf4j
@RequiredArgsConstructor
@Transactional
public class JsonSchemaDocumentDefinitionService implements DocumentDefinitionService {

    private static final String PATH = "classpath*:config/document/definition/*.json";

    private final ResourceLoader resourceLoader;
    private final DocumentDefinitionRepository<JsonSchemaDocumentDefinition> documentDefinitionRepository;

    @Override
    public Page<JsonSchemaDocumentDefinition> findAll(Pageable pageable) {
        return documentDefinitionRepository.findAll(pageable);
    }

    @Override
    public JsonSchemaDocumentDefinitionId findIdByName(String name) {
        return findLatestByName(name)
            .orElseThrow(() -> new UnknownDocumentDefinitionException(name))
            .getId();
    }

    @Override
    public Optional<JsonSchemaDocumentDefinition> findBy(DocumentDefinition.Id id) {
        return documentDefinitionRepository.findById(id);
    }

    @Override
    public Optional<JsonSchemaDocumentDefinition> findLatestByName(String documentDefinitionName) {
        return documentDefinitionRepository.findFirstByIdNameOrderByIdVersionDesc(documentDefinitionName);
    }

    @Override
    public void deployAll() {
        logger.info("Deploy all schema's");
        try {
            final Resource[] resources = loadResources();
            for (Resource resource : resources) {
                if (resource.getFilename() != null) {
                    deploy(resource.getInputStream());
                }
            }
        } catch (Exception ex) {
            logger.error("Error deploying document schema's", ex);
        }
    }

    @Override
    public void deploy(InputStream inputStream) throws IOException {
        var jsonSchema = JsonSchema.fromString(StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8));
        deploy(jsonSchema, true);
    }

    @Override
    public DeployDocumentDefinitionResult deploy(String schema) {
        try {
            var jsonSchema = JsonSchema.fromString(schema);
            return deploy(jsonSchema, false);
        } catch (Exception ex) {
            DocumentDefinitionError error = ex::getMessage;
            return new DeployDocumentDefinitionResultFailed(List.of(error));
        }
    }

    private DeployDocumentDefinitionResult deploy(JsonSchema jsonSchema, boolean readOnly) {
        try {
            var documentDefinitionName = jsonSchema.getSchema().getId().replace(".schema", "");
            var existingDefinition = findLatestByName(documentDefinitionName);
            var documentDefinitionId = JsonSchemaDocumentDefinitionId.newId(documentDefinitionName);

            if (existingDefinition.isPresent()) {
                var existingDocumentDefinition = existingDefinition.get();

                // Check read-only of previous definition
                if (existingDocumentDefinition.isReadOnly()) {
                    DocumentDefinitionError error = () -> "This schema cannot be updated, because its readonly in previous versions";
                    return new DeployDocumentDefinitionResultFailed(List.of(error));
                }

                if (existingDocumentDefinition.getSchema().equals(jsonSchema)) {
                    logger.info("Schema already deployed - {} - {} ", existingDocumentDefinition.getId(), jsonSchema.getSchema().getId());
                    DocumentDefinitionError error = () -> "This exact schema is already deployed";
                    return new DeployDocumentDefinitionResultFailed(List.of(error));
                } else {
                    // Definition changed increase version
                    documentDefinitionId = JsonSchemaDocumentDefinitionId.nextVersion(existingDocumentDefinition.id());
                    logger.info("Schema changed. Deploying next version - {} - {} ", documentDefinitionId.toString(), jsonSchema.getSchema().getId());
                }
            }

            var documentDefinition = new JsonSchemaDocumentDefinition(documentDefinitionId, jsonSchema);

            if (readOnly) {
                documentDefinition.markReadOnly();
            }

            store(documentDefinition);
            logger.info("Deployed schema - {} - {} ", documentDefinitionId.toString(), jsonSchema.getSchema().getId());
            return new DeployDocumentDefinitionResultSucceeded(documentDefinition);
        } catch (Exception ex) {
            DocumentDefinitionError error = ex::getMessage;
            return new DeployDocumentDefinitionResultFailed(List.of(error));
        }
    }

    @Override
    public void store(JsonSchemaDocumentDefinition documentDefinition) {
        assertArgumentNotNull(documentDefinition, "documentDefinition is required");
        documentDefinitionRepository.findById(documentDefinition.id())
            .ifPresentOrElse(
                existingDocumentDefinition -> {
                    if (!existingDocumentDefinition.equals(documentDefinition)) {
                        throw new UnsupportedOperationException("Schema already deployed, will cannot redeploy");
                    }
                }, () -> documentDefinitionRepository.saveAndFlush(documentDefinition)
            );
    }

    @Override
    public void removeDocumentDefinition(String documentDefinitionName) {
        documentDefinitionRepository.deleteByIdName(documentDefinitionName);
    }

    private Resource[] loadResources() throws IOException {
        return ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(PATH);
    }

}