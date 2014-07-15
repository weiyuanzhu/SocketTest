/**
 * 
 */
package nlight_android.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import nlight_android.models.Device;
import nlight_android.models.Loop;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nclient.R;

/**
 * @author weiyuan zhu
 *
 */
public class MyExpandableListAdapter extends BaseExpandableListAdapter {
	
	//inner class
	class MyFilter extends Filter{

		@Override
		protected FilterResults performFiltering(CharSequence arg0) {
			//System.out.println("Filter Test");
			
			FilterResults results = new FilterResults();
			
		
			return results;
		}

		
		@Override
		protected void publishResults(CharSequence query, FilterResults results) {
			 //mDataList = (List<? extends Map<String,?>>) results.values;
	            if (results.count > 0) {
	                notifyDataSetChanged();
	            } else {
	                notifyDataSetInvalidated();
	            }
			
		}
		
		
	}
	
	//fields
	
	private boolean multiSelectMode = false;
	private MyFilter mFilter;
	private boolean mNotifyChanged = true;
	private Context mContext;
    private List<Loop> listDataHeader; // header titles
    // child data in format of header title, child title
    private Map<Loop, List<Device>> listDataChild;
    
    
    //a list for storing selected information for all loops
    private List<SparseBooleanArray> checkedList;
    
    //setters and getters
    
    /**
	 * @return the multiSelectMode
	 */
	public boolean isMultiSelectMode() {
		return multiSelectMode;
	}



	/**
	 * @param multiSelectMode the multiSelectMode to set
	 */
	public void setMultiSelectMode(boolean multiSelectMode) {
		this.multiSelectMode = multiSelectMode;
	}



	/**
     * Constructope
     * @param context
     * @param listDataHeader
     * @param listChildData
     */
    public MyExpandableListAdapter(Context context, List<Loop> listDataHeader,
            Map<Loop, List<Device>> listChildData) {
        this.mContext = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        
        checkedList = new ArrayList<SparseBooleanArray> (getGroupCount());
        
        
        for(int i=0; i<getGroupCount();i++){
        	SparseBooleanArray s = new SparseBooleanArray();
        	checkedList.add(s);
            
            for(int j=0; j<getChildCount(i)+1;j++){
            	
            	s.put(i, false);
            	
            }
        	
        }
        
       
        
       
        
    }
 
    
    
    //implements
    
    public int getChildCount(int groupPosition){
    	
    	int temp = listDataChild.get(listDataHeader.get(groupPosition)).size();
    	
    	return temp;
    }
    
    
    
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
    	Device device = (Device) getChild(groupPosition, childPosition);
    	
    	int address = device.getAddress() < 128 ?  device.getAddress() : device.getAddress() - 128;
    	
    	String location = device.getLocation();
    	String childText = null;
    	
    	if(location==null || location.equals("?")){
    		childText = (String) "Device " + address;
    	}
    	else childText = "";
 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.device_list_child, null);
        }
 
        ImageView childImage = (ImageView) convertView
                .findViewById(R.id.childImage);
        ImageView status = (ImageView) convertView.findViewById(R.id.devicelist_child_status_imageView);
        
        if(device.isFaulty()){
        	childImage.setImageResource(R.drawable.redcross);
        }else childImage.setImageResource(R.drawable.greentick);
        
        if(device.getCurrentStatus()==0)
        {
        	status.setVisibility(View.INVISIBLE);
        }
        else {
        	status.setVisibility(View.VISIBLE);
        	status.setImageResource(R.drawable.ic_action_refresh);
        }
        
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.devicelist_child_address_textView);
 
        
        txtListChild.setText(childText);
        
        TextView deviceLocation = (TextView) convertView
                .findViewById(R.id.devicelist_child_location_textView);
        
        deviceLocation.setText(device.getLocation());
        
        updateRowBackground(groupPosition, childPosition, convertView);
        
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
    	
    	Loop loop = (Loop) getGroup(groupPosition);
    	
        String headerTitle;
        
        if(groupPosition==0)
        {
        	headerTitle = "Loop 1";
        	
        }
        else headerTitle = "Loop 2";
        
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.device_list_group, null);
        }
        
        ImageView groupImage = (ImageView) convertView
                .findViewById(R.id.groupImage);
        TextView allDevicesNo = (TextView) convertView.findViewById(R.id.loopDeviceNo);
        
        if(loop.getStatus()==0){
        	groupImage.setImageResource(R.drawable.greentick);
        }else groupImage.setImageResource(R.drawable.redcross);
 
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        
        allDevicesNo.setText("(" + loop.getDeviceNumber() + ")");
        
        updateRowBackground(groupPosition, -1, convertView);
 
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    
    public void sort(Comparator<Device> comparator)
    {
    	
    	Loop l1 = listDataHeader.get(0);
    	Loop l2 = listDataHeader.get(1);
    	Collections.sort(this.listDataChild.get(l1),comparator);
    	Collections.sort(this.listDataChild.get(l2),comparator);
    	
    	if(mNotifyChanged) notifyDataSetChanged();
    }
    
    public void selectItem(int groupPosition, int childPosition)
    {
    	
    	if(!isMultiSelectMode()) {
    		clearCheck();
    	}
    	
    	//get the reverse of current checked status
    	    	
    	boolean check = !checkedList.get(groupPosition).get(childPosition==-1? 0: childPosition+1);
    	
    	//set checked status
    	checkedList.get(groupPosition).put(childPosition==-1? 0: childPosition+1, check);
    }
    
    
    @SuppressLint("NewApi")
	public void updateRowBackground(int groupPosition,int childPosition, View view)
    {
    	int backgroundId = R.drawable.conversation_item_background_read;
    	// +1 because checkedList contains 0 for group position
    	int childPositionInTheArray = childPosition+1;
    	boolean selected = checkedList.get(groupPosition).get(childPositionInTheArray);
    	//view.setBackgroundColor(selected==true? Color.MAGENTA : Color.TRANSPARENT);
    	
    	
    	
    	Drawable backGround = mContext.getResources().getDrawable(backgroundId);
		view.setBackground(backGround);
		
		if(selected){
    		view.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_blue_light));
    	
    	}else{
    		
    	}
		
    	
    }
    
    /**
     * clear checked status for a particular loop
     * @param groupPosition
     */
    public void clearCheck()
    {
    	for(int j=0;j<checkedList.size();j++){
    		for(int i=0; i<checkedList.get(j).size();i++){
        		checkedList.get(j).put(i, false);
        	}
    	}
    	
    }
    
    public int getCheckedCount(){
    	
    	int count =0;
    	
    	for(int j=0;j<checkedList.size();j++){
    		for(int i=0; i<checkedList.get(j).size();i++){
        		if(checkedList.get(j).get(i)){
        			count++;
        		}
        	}
    	}
    	
    	return count;
    }
}
