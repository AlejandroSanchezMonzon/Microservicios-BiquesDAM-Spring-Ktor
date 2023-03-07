package biques.dam.es.repositories

import biques.dam.es.models.OrderLine
import kotlinx.coroutines.flow.Flow

/**
 * Interface that defines the basic CRUD operations (CREATE, READ, UPDATE, DELETE) for a repository
 * @param T The type of the entity
 * @param ID The type of the entity's ID
 * @author The BiquesDAM Team
 */
interface CrudRepository<T, ID> {
    fun findAll(): Flow<T>
    suspend fun findById(id: ID): T
    suspend fun save(entity: T): T
    suspend fun delete(entity: T): Boolean
    suspend fun update(entity: T): T
}