package com.destro.morningdiary.backend;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.destro.morningdiary.R;
import com.destro.morningdiary.backend.TaskModel.TaskAdapter;
import com.destro.morningdiary.ui.newTask.NewTask;

public class RecyclerItemHelper extends ItemTouchHelper.SimpleCallback {

    private TaskAdapter adapter;
    private Context context;

    public RecyclerItemHelper(TaskAdapter adapter, Context context){
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.context = context;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
        if(direction == ItemTouchHelper.RIGHT){//delete task
            //Toast.makeText(context, "RIGHT", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = Utils.getDialogBuilder(context, "Delete Item", "Are you sure you want to delete this item?",
                    "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            adapter.deleteItem(viewHolder.getAdapterPosition());
                            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        }
                    }, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        }
                    });
            builder.create().show();
        }else{//edit task
            //Toast.makeText(context,"LEFT", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, NewTask.class);
            intent.putExtra("isEdit", true);
            intent.putExtra("editTaskId",adapter.getItem(viewHolder.getAdapterPosition()).get_id());
            context.startActivity(intent);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        Drawable icon;
        ColorDrawable background;

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 15;

        if(dX > 0){
            icon = ContextCompat.getDrawable(context, R.drawable.icon_delete);
            background = new ColorDrawable(Color.RED);
        }else{
            icon = ContextCompat.getDrawable(context, R.drawable.icon_edit);
            background = new ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }

        assert icon!=null;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if(dX > 0){
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
        }else if(dX < 0) {
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else background.setBounds(0, 0, 0, 0);

        background.draw(c);
        icon.draw(c);
    }
}
