fun main() {

}


data class Note(
    val noteId: Int,
    val title: String,
    val text: String,
    val isDeleteComment: Boolean = true,
    val comments: MutableList<Comment> = mutableListOf()
)

data class Comment(val idComment: Int, val message: String, var isDeleteComment: Boolean = true)

object CommentChange {

    private val notes: MutableMap<Int, Note> = mutableMapOf()
    private val comments: MutableList<Comment> = mutableListOf()

    fun createComment(comment: Comment): Int {
        val newComment = comment.copy(idComment = comments.size + 1)
        comments += newComment
        return newComment.idComment
    }

    fun deleteComment(id: Int): Boolean {
        for ((i, p) in comments.withIndex()) {
            if (p.idComment == id) {
                comments.removeAt(i)
                return true
            }
        }
        return false
    }

    fun editComment(upDateComment: Comment, note: Note): Boolean {
        for ((key, n) in notes) {
            if (note.noteId == key) {
                val commentIndex = n.comments.indexOfFirst { it.idComment == upDateComment.idComment }
                if (commentIndex != -1) {
                    n.comments[commentIndex] = upDateComment
                    return true
                }
                throw RuntimeException("не найдена заметка")
            }
        }
        throw RuntimeException("Заметка не найдена")
    }

    fun getComments() = comments


    fun restoresComment(comment: Comment): Boolean {
        for (i in comments) {
            if (i.idComment == comment.idComment) {
                if (i.isDeleteComment) { // Проверка, что комментарий был удален
                    i.isDeleteComment = false // Восстанавливаем комментарий
                    return true // Успешно восстановлен
                }
            }
        }
        return false // Не найден или уже восстановлен
    }
}

object NoteChange {

    private val notes: MutableMap<Int, Note> = mutableMapOf()
    private var nextId = 1
    fun add(title: String): Int {
        val noteId = nextId++
        val note = Note(noteId = noteId, title, "new version")
        notes[noteId] = note
        return note.noteId
    }


    fun delete(noteId: Int): Boolean {
        return notes.remove(noteId) != null
    }


    fun edit(noteId: Int, text: String): Boolean {
        val note = notes[noteId]
        return if (note != null) {
            val upDateNote = note.copy(text = text)
            notes[noteId] = upDateNote
            true
        } else throw RuntimeException("заметка не найдена")

    }

    fun get() = notes

    fun getById(idNote: Int): Note {
       return notes[idNote] ?: throw RuntimeException("заметка не найдена")
    }


}
