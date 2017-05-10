package sa.com.sure.task.webservicesconsumer.recycler.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import sa.com.sure.task.webservicesconsumer.R;

/**
 * Created by HussainHajjar on 5/10/2017.
 */

public class UsStateViewHolder extends RecyclerView.ViewHolder {

    private TextView mCityTextView;
    private TextView mStateTextView;
    private TextView mTimeZoneTextView;
    private TextView mZipCodeTextView;
    private TextView mAreaCodeTextView;

    public UsStateViewHolder(View itemView) {
        super(itemView);
        mCityTextView = (TextView) itemView.findViewById(R.id.tv_city);
        mStateTextView = (TextView) itemView.findViewById(R.id.tv_state);
        mTimeZoneTextView = (TextView) itemView.findViewById(R.id.tv_time_zone);
        mZipCodeTextView = (TextView) itemView.findViewById(R.id.tv_zip_code);
        mAreaCodeTextView = (TextView) itemView.findViewById(R.id.tv_area_code);
    }

    public void setCity(String city){ mCityTextView.setText(city); }

    public void setState(String state){ mStateTextView.setText(state); }

    public void setTimeZone(String timeZone){ mTimeZoneTextView.setText(timeZone); }

    public void setZipCode(String zipCode){ mZipCodeTextView.setText(zipCode); }

    public void setAreaCode(String areaCode){ mAreaCodeTextView.setText(areaCode); }
}