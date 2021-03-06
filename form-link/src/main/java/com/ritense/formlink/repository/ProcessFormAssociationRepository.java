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

package com.ritense.formlink.repository;

import com.ritense.formlink.domain.impl.formassociation.CamundaProcessFormAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProcessFormAssociationRepository extends JpaRepository<CamundaProcessFormAssociation, UUID> {

    @Query(" SELECT  cpfa " +
        "    FROM    CamundaProcessFormAssociation cpfa" +
        "    WHERE   cpfa.processDefinitionKey = :processDefinitionKey ")
    Optional<CamundaProcessFormAssociation> findByProcessDefinitionKey(@Param("processDefinitionKey") String processDefinitionKey);

}