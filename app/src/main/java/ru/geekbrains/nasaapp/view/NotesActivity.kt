package ru.geekbrains.nasaapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.geekbrains.nasaapp.R
import ru.geekbrains.nasaapp.model.NotesData

class NotesActivity : AppCompatActivity() {

    lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val data = arrayListOf(
            Pair(NotesData(1, 1, title = "Заметка 1", description = "Длинное содержимое заметки"), false),
            Pair(NotesData(2, 1, title = "Заметка 2", description = "Длинное содержимое заметки"), false),
            Pair(NotesData(3, 2, name = "Имя 1", contacts = "Номер телефона"), false),
            Pair(NotesData(4, 1, title = "Заметка 3", description = "Длинное содержимое заметки"), false),
            Pair(NotesData(5, 1, title = "Заметка 4", description = "Длинное содержимое заметки"), false),
            Pair(NotesData(6, 2, name = "Имя 2", contacts = "Email адрес"), false)
        )
        data.add(0, Pair(NotesData(0, 0), false)) // Заголовок

        val adapter = NotesRecyclerAdapter(
            object : NotesRecyclerAdapter.OnListItemClickListener {
                override fun onItemClick(data: NotesData) {
                    Toast.makeText(
                        this@NotesActivity,
                        "NotesRecyclerAdapter.OnListItemClickListener",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            data,
            object : NotesRecyclerAdapter.OnStartDragListener {
                override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                    itemTouchHelper.startDrag(viewHolder)
                }
            }
        )
        findViewById<RecyclerView>(R.id.recyclerView).also {
            it.adapter = adapter
            itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter))
            itemTouchHelper.attachToRecyclerView(it)
        }
        findViewById<FloatingActionButton>(R.id.FAB).setOnClickListener { adapter.appendItem() }

    }
}