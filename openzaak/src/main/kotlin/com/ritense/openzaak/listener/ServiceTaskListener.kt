/*
 * Copyright 2020 Dimpact.
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

package com.ritense.openzaak.listener

import com.ritense.document.domain.impl.JsonSchemaDocumentId
import com.ritense.document.service.DocumentService
import com.ritense.openzaak.service.ZaakTypeLinkService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.ExecutionListener
import org.camunda.bpm.extension.reactor.bus.CamundaSelector
import org.camunda.bpm.extension.reactor.spring.listener.ReactorExecutionListener
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@CamundaSelector(type = "serviceTask", event = ExecutionListener.EVENTNAME_START)
open class ServiceTaskListener(
    private val zaakTypeLinkService: ZaakTypeLinkService,
    private val documentService: DocumentService
) : ReactorExecutionListener() {

    @Transactional
    override fun notify(execution: DelegateExecution) {
        val processBusinessKey = execution.processBusinessKey
        val documentId = JsonSchemaDocumentId.existingId(UUID.fromString(processBusinessKey))
        val document = documentService.findBy(documentId).orElseThrow()
        val zaakTypeLink = zaakTypeLinkService.get(document.definitionId().name())
        if (zaakTypeLink != null) {
            zaakTypeLink.handleServiceTask(execution, documentId.id)
            zaakTypeLinkService.modify(zaakTypeLink)
        }
    }

}