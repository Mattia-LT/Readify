package it.unimib.readify.ui.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import it.unimib.readify.R;
import it.unimib.readify.model.Collection;

public class CollectionCreationBottomSheet extends BottomSheetDialogFragment {

    public OnInputListener inputListener;

    public interface OnInputListener {
        void sendInput(Collection newCollection);
    }

    public void onInputListener(OnInputListener listener) {
        this.inputListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_collection_creation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //open dialog
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        TextInputEditText collectionName = view.findViewById(R.id.CollectionCreationName);
        SwitchMaterial collectionVisibility = view.findViewById(R.id.CollectionCreationVisibility);
        Button createCollectionButton = view.findViewById(R.id.CollectionCreationConfirm);

        // TODO: 27/01/2024 aggiungere controllo lunghezza nome raccolta

        //collection creation
        createCollectionButton.setOnClickListener( e -> {
            if(collectionName.getEditableText().toString().equals(""))
                Snackbar.make(view, "Nome non inserito", Snackbar.LENGTH_SHORT).show();
            else {
                Collection newCollection = new Collection(collectionName.getEditableText().toString(),
                        collectionVisibility.isChecked(), null);
                inputListener.sendInput(newCollection);
                //close dialog
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
