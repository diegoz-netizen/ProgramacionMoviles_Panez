package com.tecsup.lavacampus.model

/** Casilleros de recojo disponibles en la lavandería: C-01 a C-30. */
object Casilleros {
    val todos: List<String> = (1..30).map { "C-%02d".format(it) }
}
