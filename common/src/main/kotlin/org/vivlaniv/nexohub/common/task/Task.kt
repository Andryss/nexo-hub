package org.vivlaniv.nexohub.common.task

// task interfaces

sealed interface Task {
    val id: String
}

sealed interface TaskResult {
    // task id
    val tid: String

    // status code (0 - OK, >0 - error depends on handler)
    val code: Int
        get() = 0

    // error message if task resulted into error
    val errorMessage: String?
        get() = null
}
