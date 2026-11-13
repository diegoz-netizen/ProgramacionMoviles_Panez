package com.tecsup.pokemon.data.local

import androidx.room.Entity

/**
 * Favorito de una cuenta: la clave es la pareja (uid, itemId).
 * Así cada usuario tiene sus propios favoritos aunque compartan el mismo celular.
 */
@Entity(tableName = "favoritos", primaryKeys = ["uid", "itemId"])
data class FavoritoEntity(
    val uid: String,
    val itemId: String
)
