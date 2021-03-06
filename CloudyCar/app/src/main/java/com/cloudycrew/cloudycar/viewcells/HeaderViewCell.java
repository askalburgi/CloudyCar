package com.cloudycrew.cloudycar.viewcells;

import android.view.View;
import android.widget.TextView;

import com.cloudycrew.cloudycar.R;

import ca.antonious.viewcelladapter.BaseViewHolder;
import ca.antonious.viewcelladapter.GenericSingleViewCell;

/**
 * Created by George on 2016-11-17.
 */

public class HeaderViewCell extends GenericSingleViewCell<HeaderViewCell.ViewHolder, String> {

    public HeaderViewCell(String model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_header;
    }

    @Override
    public void bindViewCell(HeaderViewCell.ViewHolder viewHolder) {
        viewHolder.setHeaderText(getModel());
    }

    public static class ViewHolder extends BaseViewHolder {
        private TextView headerTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            headerTextView = (TextView) itemView.findViewById(R.id.header_text);
        }

        public void setHeaderText(String headerText) {
            headerTextView.setText(headerText);
        }
    }
}
