package ru.geekbrains.nasaapp.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.RecyclerView
import ru.geekbrains.nasaapp.R
import ru.geekbrains.nasaapp.model.NotesData

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(data: Pair<NotesData, Boolean>)
}

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
}

interface ItemTouchHelperViewHolder {
    fun onItemSelected()
    fun onItemClear()
}

class NotesRecyclerAdapter(
    private var onListItemClickListener: OnListItemClickListener,
    private var data: MutableList<Pair<NotesData, Boolean>>,
    private val dragListener: OnStartDragListener
) :
    RecyclerView.Adapter<BaseViewHolder>(), ItemTouchHelperAdapter {

    interface OnListItemClickListener {
        fun onItemClick(data: NotesData)
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_INFO = 1
        private const val TYPE_PERSON = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_INFO -> InfoViewHolder(
                inflater.inflate(R.layout.activity_notes_recycler_item_info, parent, false) as View
            )
            TYPE_PERSON -> PersonViewHolder(
                inflater.inflate(
                    R.layout.activity_notes_recycler_item_person,
                    parent,
                    false
                ) as View
            )
            else -> HeaderViewHolder(
                inflater.inflate(
                    R.layout.activity_notes_recycler_item_header,
                    parent,
                    false
                ) as View
            )
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].first.type
    }

    fun appendItem() {
        data.add(generateItem())
        notifyItemInserted(itemCount - 1)
    }

    private fun generateItem(): Pair<NotesData, Boolean> {
        return Pair(
            NotesData(
                data.size,
                1,
                title = "Новая заметка",
                description = "Длинное содержимое заметки"
            ), false
        )
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        data.removeAt(fromPosition).apply {
            data.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, this)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class HeaderViewHolder(view: View) : BaseViewHolder(view) {

        override fun bind(data: Pair<NotesData, Boolean>) {
            itemView.setOnClickListener { onListItemClickListener.onItemClick(data.first) }
        }
    }

    inner class InfoViewHolder(view: View) : BaseViewHolder(view), ItemTouchHelperViewHolder {

        override fun bind(data: Pair<NotesData, Boolean>) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.findViewById<ImageView>(R.id.iv_icon)
                    .setOnClickListener { onListItemClickListener.onItemClick(data.first) }
                itemView.findViewById<TextView>(R.id.tv_title).text = data.first.title
                itemView.findViewById<TextView>(R.id.tv_title)
                    .setOnClickListener { toggleText() }
                itemView.findViewById<TextView>(R.id.tv_description).text = data.first.description
                itemView.findViewById<TextView>(R.id.tv_description).visibility =
                    if (data.second) View.VISIBLE else View.GONE
                itemView.findViewById<ImageView>(R.id.iv_dragHandle)
                    .setOnTouchListener { _, event ->
                        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                            dragListener.onStartDrag(this)
                        }
                        false
                    }
            }
        }

        private fun toggleText() {
            data[layoutPosition] = data[layoutPosition].let {
                it.first to !it.second
            }
            notifyItemChanged(layoutPosition)
        }

        private fun addItem() {
            data.add(layoutPosition, generateItem())
            notifyItemInserted(layoutPosition)
        }

        private fun removeItem() {
            data.removeAt(layoutPosition)
            notifyItemRemoved(layoutPosition)
        }

        private fun moveUp() {
            layoutPosition.takeIf { it > 1 }?.also { currentPosition ->
                data.removeAt(currentPosition).apply {
                    data.add(currentPosition - 1, this)
                }
                notifyItemMoved(currentPosition, currentPosition - 1)
            }
        }

        private fun moveDown() {
            layoutPosition.takeIf { it < data.size - 1 }?.also { currentPosition ->
                data.removeAt(currentPosition).apply {
                    data.add(currentPosition + 1, this)
                }
                notifyItemMoved(currentPosition, currentPosition + 1)
            }
        }

        @SuppressLint("ResourceAsColor")
        override fun onItemSelected() {
//            itemView.setBackgroundColor(R.color.design_default_color_secondary)
        }

        override fun onItemClear() {
//            itemView.setBackgroundColor(0)
        }
    }

    inner class PersonViewHolder(view: View) : BaseViewHolder(view), ItemTouchHelperViewHolder {

        override fun bind(data: Pair<NotesData, Boolean>) {
            itemView.findViewById<ImageView>(R.id.iv_photo)
                .setOnClickListener { onListItemClickListener.onItemClick(data.first) }
            itemView.findViewById<TextView>(R.id.tv_name).text = data.first.name
            itemView.findViewById<TextView>(R.id.tv_contacts).text = data.first.contacts
            itemView.findViewById<ImageView>(R.id.iv_dragHandle).setOnTouchListener { _, event ->
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    dragListener.onStartDrag(this)
                }
                false
            }
        }

        @SuppressLint("ResourceAsColor")
        override fun onItemSelected() {
//            itemView.setBackgroundColor(R.color.design_default_color_secondary)
        }

        override fun onItemClear() {
//            itemView.setBackgroundColor(0)
        }
    }
}

