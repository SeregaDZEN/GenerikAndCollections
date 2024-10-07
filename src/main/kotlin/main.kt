fun main() {

}

interface Elem {
    val id: Int
    var text: String
}

data class Note(
    override val id: Int,
    val title: String,
    override var text: String,
    val comments: MutableList<Comment> = mutableListOf()
) : Elem

data class Comment(override val id: Int, override var text: String, var isDeleteComment: Boolean = true) : Elem


open class CrudService<T : Elem>(val elems: MutableList<T>) {

    open fun add(elem: T): T {
        elems += elem
        return elems.last()
    }

    fun get() = elems

    fun edit(elem: T): T {
        val editElem = elems.find { it.id == elem.id } ?: throw RuntimeException(" не найден по такому айди")
        if (editElem is Comment && editElem.isDeleteComment) {
            throw RuntimeException("коментарий удалён")
        }
        editElem.text = elem.text
        return editElem
    }

    fun delete(elemId: Int): Boolean {
        val searchElem = elems.find { it.id == elemId } ?: return false
        return if (searchElem is Comment) {
            searchElem.isDeleteComment = true
            true
        } else elems.remove(searchElem)
    }

    fun getById(elemId: Int): T {
        return elems.find { it.id == elemId } ?: throw RuntimeException("element not find for id")
    }
}

class CommentService(list: MutableList<Comment>) : CrudService<Comment>(list) {
    fun restoreComment(commentId: Int): Boolean {
        val elemForRestore = elems.find { it.id == commentId && it.isDeleteComment } ?: return false
        elemForRestore.isDeleteComment = false
        return true
    }
}

object NoteService {

    private val noteService = CrudService<Note>(mutableListOf())

    fun clear() {
        noteService.elems.clear()
    }

    fun add(elem: Note) = noteService.add(elem)
    fun addComment(comment: Comment, note: Note) = CommentService(noteService.getById(note.id).comments).add(comment)
    fun delete(noteId: Int) = noteService.delete(noteId)

    fun edit(note: Note) = noteService.edit(note)

    fun deleteComment(comment: Comment, note: Note) =
        CommentService(noteService.getById(note.id).comments).delete(comment.id)

    fun editComment(comment: Comment, note: Note) = CommentService(noteService.getById(note.id).comments).edit(comment)

    fun getNotes() = noteService.get()
    fun getById(idNote : Int) = noteService.getById(idNote)

    fun getComments (noteId: Int) = noteService.getById(noteId).comments

    fun restoreComment (commentId: Int, idNote: Int) = CommentService(noteService.getById(idNote).comments).restoreComment(commentId)
}


object CommentChange {

    private val notes: MutableMap<Int, Note> = mutableMapOf()
    private val comments: MutableList<Comment> = mutableListOf()

    fun createComment(comment: Comment): Int {
        val newComment = comment.copy(id = comments.size + 1)
        comments += newComment
        return newComment.id
    }

    fun deleteComment(id: Int): Boolean {
        for ((i, p) in comments.withIndex()) {
            if (p.id == id) {
                comments.removeAt(i)
                return true
            }
        }
        return false
    }

    fun editComment(upDateComment: Comment, note: Note): Boolean {
        for ((key, n) in notes) {
            if (note.id == key) {
                val commentIndex = n.comments.indexOfFirst { it.id == upDateComment.id }
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
            if (i.id == comment.id) {
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
        val note = Note(id = noteId, title, "new version")
        notes[noteId] = note
        return note.id
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
