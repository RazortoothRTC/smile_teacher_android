/**
Copyright 2012-2013 SMILE Consortium, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
**/
package org.smilec.smile.ui.adapter;

import java.util.List;

import org.smilec.smile.R;
import org.smilec.smile.bu.Constants;
import org.smilec.smile.bu.SmilePlugServerManager;
import org.smilec.smile.domain.CurrentMessageStatus;
import org.smilec.smile.domain.Question;
import org.smilec.smile.domain.Results;
import org.smilec.smile.ui.GeneralActivity;
import org.smilec.smile.ui.widget.checkbox.InertCheckBox;
import org.smilec.smile.util.ActivityUtil;
import org.smilec.smile.util.CloseClickListenerUtil;
import org.smilec.smile.util.ImageLoader;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionListAdapter extends ArrayAdapter<Question> {
    
	private Context context;
    private String ip;
    private int currentQuestion;

    public QuestionListAdapter(Context context, List<Question> items, Results results, String ip) {
        super(context, android.R.layout.simple_list_item_multiple_choice, items);

        this.context = context;
        this.ip = ip;
    }

    // For each position (<=> question in the session), we prepare the row
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Question question = getItem(position);

        if (convertView == null) {
            Context context = getContext();
            LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.questions_item, parent, false);
        }

		InertCheckBox cbox = (org.smilec.smile.ui.widget.checkbox.InertCheckBox) convertView.findViewById(R.id.cb_item_checkbox);
		cbox.setChecked(true);

        TextView tvNumber = (TextView) convertView.findViewById(R.id.tv_number);
        tvNumber.setText(String.valueOf(question.getNumber()));

        TextView tvOwner = (TextView) convertView.findViewById(R.id.tv_owner);
        tvOwner.setText(question.getOwner());

        TextView tvIp = (TextView) convertView.findViewById(R.id.tv_ip);
        tvIp.setText(question.getIp());

        TextView tvHitAverage = (TextView) convertView.findViewById(R.id.tv_hit_average);

        tvHitAverage.setText(String.valueOf(question.getPerCorrect()));

        final float rating = (float) question.getRating();

        /**
         * Here we display the detail page of a question
         * 
         * If the "details" button is an ImageView >> adapting to mobile and large format
         * Else, we are in x-large format >> the "details" button is an instance of Button
         */
        if(convertView.findViewById(R.id.iv_details) instanceof ImageView) {
        	
        	ImageView ivDetails = (ImageView) convertView.findViewById(R.id.iv_details);
        	ivDetails.setOnClickListener(new OpenItemDetailsListener(question));
        } else {
        	Button ivDetails = (Button) convertView.findViewById(R.id.iv_details);
        	ivDetails.setOnClickListener(new OpenItemDetailsListener(question));
        }

        final RatingBar rbRatingBar = (RatingBar) convertView.findViewById(R.id.rb_ratingbar);
        rbRatingBar.setRating(rating);

        return convertView;
    }

    @Override
    public Question getItem(int position) {
        return super.getItem(position);
    }

    /**
     * Class called to display the detail of a question 
     */
    private class OpenItemDetailsListener implements OnClickListener {

        private Question question;

        public OpenItemDetailsListener(Question question) {
            this.question = question;
        }

        @Override
        public void onClick(View v) {
            
        	// Preparing "Details" view
        	Dialog detailsDialog = new Dialog(context, R.style.Dialog);
            detailsDialog.setContentView(R.layout.question_details);
            Display displaySize = ActivityUtil.getDisplaySize(getContext());
            detailsDialog.getWindow().setLayout(displaySize.getWidth(), displaySize.getHeight());
            detailsDialog.show();

            // Preparing the values in the "Details" view
            loadDetails(detailsDialog, question);
        }
    }
    
    private void loadDetails(final Dialog detailsDialog, Question question) {
    	
    	ImageButton btClose = (ImageButton) detailsDialog.findViewById(R.id.bt_close);
    	ImageButton btBack = (ImageButton) detailsDialog.findViewById(R.id.bt_back);
    	ImageButton btTrash = (ImageButton) detailsDialog.findViewById(R.id.bt_trash);
    	
    	// note: first question equal to '1'
    	currentQuestion = question.getNumber()-1;
    	
        btClose.setOnClickListener(new CloseClickListenerUtil(detailsDialog));
        btBack.setOnClickListener(new CloseClickListenerUtil(detailsDialog));
        
        // If the user click on the trash icon 
        if(!GeneralActivity.getStatus().equals(CurrentMessageStatus.START_MAKE.name())) {
        	btTrash.setVisibility(View.INVISIBLE);
        }
        btTrash.setOnClickListener(new Button.OnClickListener() {  
        	
        	 @Override
             public void onClick(View v) {
                 
        		// Preparing the "Confirmation" view
				final Dialog confirmDialog = new Dialog(context, R.style.Dialog);
				confirmDialog.setContentView(R.layout.question_delete);
				Display displaySize = ActivityUtil.getDisplaySize(getContext());
				confirmDialog.getWindow().setLayout(displaySize.getWidth(), displaySize.getHeight());
				confirmDialog.show();
				
				// Adding listeners on each button
				Button btCancel = (Button) confirmDialog.findViewById(R.id.bt_cancel);
				Button btConfirm = (Button) confirmDialog.findViewById(R.id.bt_confirm);
             	
                btCancel.setOnClickListener(new CloseClickListenerUtil(confirmDialog));
                btConfirm.setOnClickListener(new Button.OnClickListener() {
                	
                	public void onClick(View v) {
                		//
                        // #91 XXX TODO: Add some error handling to check whether we really need to delete or not before we
                        // incorrectly assume our REST call to the server worked.
                		try {
							SmilePlugServerManager spsm = new SmilePlugServerManager();
                            String status = spsm.deleteQuestionInSessionByNumber(ip, context, currentQuestion);
                            Toast.makeText(context, status, Toast.LENGTH_LONG).show();
						} catch (NetworkErrorException e) {
							e.printStackTrace();
                            Toast.makeText(context, "Error deleting question, reason: " + e.getMessage(), Toast.LENGTH_LONG).show();
						}
                		
            			confirmDialog.dismiss();
            			detailsDialog.dismiss();
            			
            			// QuestionsManager.addDeletedQuestionInLocalFile(context, currentQuestion);
					};
                });
        	 }
		});
        
        TextView tvOwner = (TextView) detailsDialog.findViewById(R.id.tv_create_by);
        tvOwner.setText("( " + context.getString(R.string.create_by) + " " + question.getOwner()
            + " )");

        ImageView tvImage = (ImageView) detailsDialog.findViewById(R.id.iv_image);

        Display displaySize = ActivityUtil.getDisplaySize(context);
        float percentWidth = (float) (displaySize.getWidth() * 0.6);
        float percentHeight = (float) (displaySize.getHeight() * 0.6);
        int width = (int) (displaySize.getWidth() - percentWidth);
        int height = (int) (displaySize.getHeight() - percentHeight);
        LayoutParams lp = new LayoutParams(width, height);
        lp.setMargins(35, 20, 50, 0);
        tvImage.setLayoutParams(lp);

        if (question.hasImage()) {
            byte[] data = ImageLoader.loadBitmap(Constants.HTTP + ip + question.getImageUrl());

            if (data != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                if (bitmap != null) {
                    tvImage.setImageBitmap(bitmap);
                }
            }

        } else {
            tvImage.setVisibility(View.GONE);
        }

        TextView tvQuestion = (TextView) detailsDialog.findViewById(R.id.tv_question);
        if (!question.getQuestion().equals("")) {
            tvQuestion.setText(question.getQuestion());
        } else {
            tvQuestion.setVisibility(View.GONE);
        }

        TextView tvAlternative1 = (TextView) detailsDialog.findViewById(R.id.tv_alternative1);
        if (!question.getOption1().equals("")) {
            tvAlternative1.setText(context.getString(R.string.alternative1) + " "
                + question.getOption1());
        } else {
            tvAlternative1.setVisibility(View.GONE);
        }

        TextView tvAlternative2 = (TextView) detailsDialog.findViewById(R.id.tv_alternative2);
        if (!question.getOption1().equals("")) {
            tvAlternative2.setText(context.getString(R.string.alternative2) + " "
                + question.getOption2());
        } else {
            tvAlternative2.setVisibility(View.GONE);
        }

        TextView tvAlternative3 = (TextView) detailsDialog.findViewById(R.id.tv_alternative3);
        if (!question.getOption1().equals("")) {
            tvAlternative3.setText(context.getString(R.string.alternative3) + " "
                + question.getOption3());
        } else {
            tvAlternative3.setVisibility(View.GONE);
        }

        TextView tvAlternative4 = (TextView) detailsDialog.findViewById(R.id.tv_alternative4);
        if (!question.getOption1().equals("")) {
            tvAlternative4.setText(context.getString(R.string.alternative4) + " "
                + question.getOption4());
        } else {
            tvAlternative4.setVisibility(View.GONE);
        }

        TextView tvCorrectAnswer = (TextView) detailsDialog.findViewById(R.id.tv_correct_answer);
        tvCorrectAnswer.setText(context.getString(R.string.correct_answer) + ": "
            + question.getAnswer());
    }

}
