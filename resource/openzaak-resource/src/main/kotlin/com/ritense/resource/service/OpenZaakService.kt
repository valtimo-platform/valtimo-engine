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

package com.ritense.resource.service

import com.ritense.openzaak.service.impl.DocumentenService
import com.ritense.resource.domain.OpenZaakResource
import com.ritense.resource.domain.ResourceId
import com.ritense.resource.repository.OpenZaakResourceRepository
import com.ritense.resource.service.request.FileUploadRequest
import com.ritense.resource.service.request.MultipartFileUploadRequest
import com.ritense.resource.web.ObjectContentDTO
import com.ritense.resource.web.ObjectUrlDTO
import com.ritense.resource.web.ResourceDTO
import com.ritense.valtimo.contract.resource.FileStatus
import com.ritense.valtimo.contract.resource.Resource
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.time.LocalDateTime
import java.util.UUID

class OpenZaakService(
    val documentenService: DocumentenService,
    val openZaakResourceRepository: OpenZaakResourceRepository
): ResourceService {

    override fun store(key: String, multipartFile: MultipartFile): Resource {
        TODO("Not yet implemented")
    }

    override fun store(key: String, multipartFile: MultipartFile, fileStatus: FileStatus): Resource {
        TODO("Not yet implemented")
    }

    override fun store(documentDefinitionName: String, name: String, multipartFile: MultipartFile): OpenZaakResource {
        val informatieObjectUrl =
            documentenService.createEnkelvoudigInformatieObject(documentDefinitionName, multipartFile)

        val uploadRequest = MultipartFileUploadRequest.from(multipartFile)

        val openZaakResource = OpenZaakResource(
            ResourceId.newId(UUID.randomUUID()),
            informatieObjectUrl,
            name,
            uploadRequest.getExtension(),
            uploadRequest.getSize(),
            LocalDateTime.now()
        )
        return openZaakResourceRepository.saveAndFlush(openZaakResource)
    }

    override fun store(key: String, fileUploadRequest: FileUploadRequest): Resource {
        TODO("Not yet implemented")
    }

    override fun store(key: String, fileUploadRequest: FileUploadRequest, fileStatus: FileStatus): Resource {
        TODO("Not yet implemented")
    }

    override fun getResourceUrl(id: UUID): ObjectUrlDTO {
        TODO("Not yet implemented")
    }

    override fun getResourceUrl(fileName: String): URL {
        TODO("Not yet implemented")
    }

    override fun getResourceContent(id: UUID): ObjectContentDTO {
        TODO("Not yet implemented")
    }

    override fun removeResource(id: UUID) {
        TODO("Not yet implemented")
    }

    override fun removeResource(key: String) {
        TODO("Not yet implemented")
    }

    override fun registerResource(resourceDTO: ResourceDTO): ResourceDTO {
        TODO("Not yet implemented")
    }

    override fun getResource(id: UUID): OpenZaakResource {
        return openZaakResourceRepository.findById(ResourceId.existingId(id)).orElseThrow()
    }

    override fun getResourceByKey(fileName: String): Resource {
        TODO("Not yet implemented")
    }

    override fun activate(id: UUID) {
        TODO("Not yet implemented")
    }

    override fun pending(id: UUID) {
        TODO("Not yet implemented")
    }
}