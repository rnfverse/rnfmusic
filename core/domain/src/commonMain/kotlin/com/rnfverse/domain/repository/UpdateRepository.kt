package com.rnfverse.domain.repository

import com.rnfverse.domain.data.model.update.UpdateData
import com.rnfverse.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UpdateRepository {
    fun checkForGithubReleaseUpdate(): Flow<Resource<UpdateData>>
    fun checkForFdroidUpdate(): Flow<Resource<UpdateData>>

    fun getFdroidSigningKeys(): Flow<Resource<List<String>>>
}