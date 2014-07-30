package com.mackwell.nlight.adapter;

import java.util.List;
import java.util.Map;

import com.mackwell.nlight.R;
import com.mackwell.nlight.models.Device;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * A custom adapter extends SimpleAdapter for displaying device information
 * Device Failure status will display "RED" if device has error
 * 
 * 
 * @author weiyuan zhu
 * @
 */
public class DeviceInfoListAdapter extends SimpleAdapter {
	
	
	Device mDevice;
	private Context mContext;
	private int[] mTo;
	private String[] mFrom;
	
	private List<? extends Map<String, ?>> mData;
	
	private int mResource;
	private LayoutInflater mInflater;
	

	public DeviceInfoListAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to, Device device) {
		super(context, data, resource, from, to);
		mTo = to;
		mFrom = from;
		mData = data;
		mResource = resource;
		mContext = context;
		mDevice = device;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Resources res = mContext.getResources();
		
		
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = mInflater.inflate(mResource, parent,false);
		
		
		TextView description = (TextView) rowView.findViewById(mTo[0]);
		TextView content = (TextView) rowView.findViewById(mTo[1]);
		
		int stringId = (Integer) mData.get(position).get(mFrom[0]);
		String descriptionString = res.getString(stringId);
		String contentString = mData.get(position).get(mFrom[1]).toString();
		
		
		description.setText(descriptionString);
		content.setText(contentString );
		
		if(stringId==R.string.text_fragment_deviceInfo_failureStatus)
		{
			if(!mDevice.isFaulty()){
				content.setTextColor(Color.BLACK);
			}
			else{
				content.setTextColor(Color.RED);
			}
		}
		
		if(stringId == R.string.text_fragment_deviceInfo_communicationStatus){
			content.setTextColor(mDevice.isCommunicationStatus()? Color.BLACK : Color.RED);
		}
			
		
		
		return rowView;
		
		
		
		
		
		
	}

	
	
	
}
