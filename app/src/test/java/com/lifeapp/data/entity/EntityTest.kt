package com.lifeapp.data.entity

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EntityTest {

    @Test
    fun todo_defaultValues_areCorrect() {
        val todo = Todo(content = "Test task", date = "2026-08-23")
        assertEquals("Test task", todo.content)
        assertEquals("2026-08-23", todo.date)
        assertEquals(1, todo.priority) // medium default
        assertFalse(todo.isCompleted)
        assertEquals(0L, todo.id)
    }

    @Test
    fun todo_copy_preservesValues() {
        val todo = Todo(id = 1, content = "Original", date = "2026-08-23", priority = 0, isCompleted = false)
        val copied = todo.copy(content = "Updated", isCompleted = true)
        assertEquals(1L, copied.id)
        assertEquals("Updated", copied.content)
        assertEquals(0, copied.priority)
        assertTrue(copied.isCompleted)
        assertEquals("2026-08-23", copied.date)
    }

    @Test
    fun entertainment_statusValues_areCorrect() {
        val want = Entertainment(name = "Movie", type = 0, status = 0)
        val watching = Entertainment(name = "Show", type = 1, status = 1)
        val watched = Entertainment(name = "Book", type = 2, status = 2, rating = 5)

        assertEquals(0, want.status)
        assertEquals(1, watching.status)
        assertEquals(2, watched.status)
        assertEquals(5, watched.rating)
    }

    @Test
    fun devTask_statusCycle_works() {
        var task = DevTask(projectId = 1, content = "Task", status = 0)
        assertEquals(0, task.status) // pending
        task = task.copy(status = (task.status + 1) % 3)
        assertEquals(1, task.status) // in_progress
        task = task.copy(status = (task.status + 1) % 3)
        assertEquals(2, task.status) // completed
        task = task.copy(status = (task.status + 1) % 3)
        assertEquals(0, task.status) // back to pending
    }

    @Test
    fun scriptDraft_tags_areStoredAsCommaSeparated() {
        val draft = ScriptDraft(title = "Draft", content = "Content", tags = "tag1,tag2,tag3")
        assertEquals("tag1,tag2,tag3", draft.tags)
        val tags = draft.tags.split(",")
        assertEquals(3, tags.size)
        assertEquals("tag1", tags[0])
    }

    @Test
    fun project_name_isStored() {
        val project = Project(name = "My Project")
        assertEquals("My Project", project.name)
        assertTrue(project.createdAt > 0)
    }

    @Test
    fun memo_content_isStored() {
        val memo = Memo(content = "Quick note")
        assertEquals("Quick note", memo.content)
    }

    @Test
    fun material_typeValues_areCorrect() {
        val image = Material(content = "Photo", type = 0)
        val video = Material(content = "Clip", type = 1)
        val other = Material(content = "File", type = 2)
        assertEquals(0, image.type)
        assertEquals(1, video.type)
        assertEquals(2, other.type)
        assertFalse(image.isCompleted)
    }

    @Test
    fun codeSnippet_sourceFileName_isOptional() {
        val snippet1 = CodeSnippet(projectId = 1, title = "Manual", code = "fun test() {}")
        val snippet2 = CodeSnippet(projectId = 1, title = "Imported", code = "val x = 1", sourceFileName = "test.kt")
        assertEquals(null, snippet1.sourceFileName)
        assertEquals("test.kt", snippet2.sourceFileName)
    }

    @Test
    fun publishPlan_isPublished_defaultsToFalse() {
        val plan = PublishPlan(date = "2026-08-23", note = "Test")
        assertFalse(plan.isPublished)
        val published = plan.copy(isPublished = true)
        assertTrue(published.isPublished)
    }

    @Test
    fun techNote_updatedAt_isSet() {
        val note = TechNote(projectId = 1, title = "Note", content = "Content")
        assertTrue(note.createdAt > 0)
        assertTrue(note.updatedAt >= note.createdAt)
    }
}
