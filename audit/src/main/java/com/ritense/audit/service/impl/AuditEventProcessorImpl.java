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

package com.ritense.audit.service.impl;

import com.ritense.audit.domain.AuditRecord;
import com.ritense.audit.domain.AuditRecordBuilder;
import com.ritense.audit.domain.MetaData;
import com.ritense.audit.domain.MetaDataBuilder;
import com.ritense.audit.exception.AuditRecordAlreadyProcessedException;
import com.ritense.audit.exception.AuditRuntimeException;
import com.ritense.audit.repository.impl.AuditRecordImplRepository;
import com.ritense.audit.service.AuditEventProcessor;
import com.ritense.valtimo.contract.audit.AuditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;

import static com.ritense.valtimo.contract.utils.AssertionConcern.assertArgumentNotNull;

@Slf4j
@RequiredArgsConstructor
public class AuditEventProcessorImpl implements AuditEventProcessor {

    private final AuditRecordImplRepository auditRecordRepository;

    @Override
    @Transactional
    public void process(final AuditEvent event) {
        logger.debug("Enter: {}.{} with argument[s] = {}",
            AuditEventProcessorImpl.class,
            "process(AuditEvent event)",
            event
        );
        try {
            assertArgumentNotNull(event, "auditEvent is required");
            final MetaData metaData = new MetaDataBuilder()
                .origin(event.getOrigin())
                .occurredOn(event.getOccurredOn())
                .user(event.getUser())
                .build();
            final AuditRecord auditRecord = new AuditRecordBuilder()
                .id(event.getId())
                .metaData(metaData)
                .auditEvent(event)
                .build();
            auditRecordRepository.saveAndFlush(auditRecord);
            logger.debug("Processed AuditEvent: {}", auditRecord);
        } catch (IllegalArgumentException e) {
            throw new AuditRuntimeException("Error due to invalid argument.", e);
        } catch (DataIntegrityViolationException e) {
            if (e.getMostSpecificCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new AuditRecordAlreadyProcessedException(e);
            } else {
                throw new AuditRuntimeException("Unexpected error occurred.", e);
            }
        } catch (Exception e) {
            throw new AuditRuntimeException("Unexpected error occurred.", e);
        }
    }

}