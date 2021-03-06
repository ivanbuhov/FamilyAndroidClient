package com.buhov.family;

import com.buhov.family.FamilyHttpClient.Entities.PedigreeDTO;
import android.app.*;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AddPedigreeFragment extends DialogFragment {
	
	private View layout;
	PedigreeAddHandler handler;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.handler = (PedigreeAddHandler) getActivity();
		AlertDialog.Builder builder = this.getBuilder();
		
		// Initialize the layout
		LayoutInflater inflator = getActivity().getLayoutInflater();
		this.layout = inflator.inflate(R.layout.dialog_edit_pedigree, null);
		builder.setView(this.layout);
		return builder.create();
	}
	
	private AlertDialog.Builder getBuilder() {
		
		return new AlertDialog.Builder(getActivity())
			.setTitle(R.string.add_pedigree_title)
			.setNegativeButton(R.string.cancel, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.setPositiveButton(R.string.add, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String newTitle = ((EditText) layout.findViewById(R.id.edit_pedigree_title)).getText().toString();
					PedigreeDTO newPedigree = new PedigreeDTO(newTitle);
					dialog.dismiss();
					handler.onPedigreeAdditionFinished(newPedigree);
				}
			});
	}

	public interface PedigreeAddHandler {
		
		void onPedigreeAdditionFinished(PedigreeDTO newPedigree);
	}
}
