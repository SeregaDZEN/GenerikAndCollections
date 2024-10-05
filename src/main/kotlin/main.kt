fun main() {

}


data class Note(
    val noteId: Int,
    val title: String,
    val text: String,
    val isDeleteComment: Boolean = true,
    val comments: MutableList<Comment>
)

data class Comment(val idComment: Int, val message: String, var isDeleteComment: Boolean = true)

object CommentChange {

    private val notes: MutableList<Note> = mutableListOf()
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
        for (n in notes) {
            if (note.noteId == n.noteId) {
                for (c in n.comments.indices) {
                    if (upDateComment.idComment == n.comments[c].idComment) {
                        val number = n.comments.size + 1
                        n.comments[c] = upDateComment.copy(message = "new comment $number")
                        return true
                    }
                }
            }
        }
        throw RuntimeException("не найдена заметка")
    }

    fun getComments(): List<Comment> {
        return this.comments
    }

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
    fun add(id : Int, title: String ) {


    }

    fun delete() {

    }

    fun edit() {

    }

    fun get() {

    }

    fun getById() {

    }


}
