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

package com.ritense.processdocument.domain.impl.listener;

import com.ritense.processdocument.domain.ProcessDocumentDefinition;
import com.ritense.processdocument.service.ProcessDocumentAssociationService;
import com.ritense.valtimo.contract.event.UndeployDocumentDefinitionEvent;
import com.ritense.valtimo.service.CamundaProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class UndeployDocumentDefinitionEventListener {

    private final ProcessDocumentAssociationService processDocumentAssociationService;
    private final CamundaProcessService camundaProcessService;
    private static final String REASON = "Triggerd undeployment of document definition";

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleEvent(UndeployDocumentDefinitionEvent event) {
        logger.debug("process document definition to be removed due to undeployment document definition with name: {}", event.getDocumentDefinitionName());

        String documentDefinitionName = event.getDocumentDefinitionName();
        Optional<? extends ProcessDocumentDefinition> processDocumentDefinitionOptional = processDocumentAssociationService.findByDocumentDefinitionName(documentDefinitionName);

        if (processDocumentDefinitionOptional.isPresent()) {

            val processDocumentDefinition = processDocumentDefinitionOptional.get();

            camundaProcessService.deleteAllProcesses(
                processDocumentDefinition.processDocumentDefinitionId().processDefinitionKey().toString(), REASON
            );

            processDocumentAssociationService.deleteProcessDocumentInstances(processDocumentDefinition.processName());

            processDocumentAssociationService.deleteProcessDocumentDefinition(documentDefinitionName);
        }
    }
}