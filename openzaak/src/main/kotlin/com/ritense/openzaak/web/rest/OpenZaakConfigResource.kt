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

package com.ritense.openzaak.web.rest

import com.ritense.openzaak.domain.configuration.OpenZaakConfig
import com.ritense.openzaak.domain.request.CreateOpenZaakConfigRequest
import com.ritense.openzaak.domain.request.ModifyOpenZaakConfigRequest
import com.ritense.openzaak.service.result.CreateOpenZaakConfigResult
import com.ritense.openzaak.service.result.ModifyOpenZaakConfigResult
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping(value = ["/api/openzaak/config"], produces = [MediaType.APPLICATION_JSON_VALUE])
interface OpenZaakConfigResource {

    @GetMapping
    fun getConfig(): ResponseEntity<OpenZaakConfig>

    @PostMapping
    fun createConfig(@Valid @RequestBody request: CreateOpenZaakConfigRequest): ResponseEntity<CreateOpenZaakConfigResult>

    @PutMapping
    fun modifyConfig(@Valid @RequestBody request: ModifyOpenZaakConfigRequest): ResponseEntity<ModifyOpenZaakConfigResult>

    @DeleteMapping
    fun deleteConfig(): ResponseEntity<Void>
}