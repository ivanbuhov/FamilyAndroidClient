package com.buhov.family;

import com.buhov.family.FamilyHttpClient.Entities.Pedigree;
import com.buhov.family.FamilyHttpClient.Entities.PedigreeNew;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class EditPedigreeFragment extends DialogFragment {
	
	public static final String PEDIGREE_ID_KEY = "pedigree_id";
	public static final String PEDIGREE_TITLE_KEY = "pedigree_title";
	public static final String PEDIGREE_OWNERID_KEY = "pedigree_ownerId";
	
	private View layout;
	private Pedigree pedigree;
	PedigreeEditHandler handler;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.handler = (PedigreeEditHandler) getActivity();
		this.initPedigree();
		AlertDialog.Builder builder = this.getBuilder();
		
		// Initialize the layout
		LayoutInflater inflator = getActivity().getLayoutInflater();
		this.layout = inflator.inflate(R.layout.dialog_edit_pedigree, null);
		EditText titleView = (EditText) this.layout.findViewById(R.id.edit_pedigree_title);
		titleView.setText(this.pedigree.getTitle());
		builder.setView(this.layout);
		return builder.create();
	}
	
	private AlertDialog.Builder getBuilder() {
		
		return new AlertDialog.Builder(getActivity())
			.setNegativeButton(R.string.cancel, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.setPositiveButton(R.string.update, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String newTitle = ((EditText) layout.findViewById(R.id.edit_pedigree_title)).getText().toString();
					int id = pedigree.getId();
					PedigreeNew newPedigree = new PedigreeNew(id, newTitle);
					dialog.dismiss();
					handler.onPedigreeEditionFinished(newPedigree);
				}
			});
	}
	
	private void initPedigree() {
		int id = getArguments().getInt(PEDIGREE_ID_KEY);
		String title = getArguments().getString(PEDIGREE_TITLE_KEY);
		int ownerId = getArguments().getInt(PEDIGREE_OWNERID_KEY);
		this.pedigree = new Pedigree(id, title, ownerId);
	}

	public interface PedigreeEditHandler {
		
		void onPedigreeEditionFinished(PedigreeNew newPedigree);
	}
}
