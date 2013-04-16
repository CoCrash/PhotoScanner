package com.coolroy.waterfall;

import java.util.ArrayList;

import com.coolroy.photoscanner.R;
import com.coolroy.waterfall.LazyScrollView.OnScrollListener;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class WaterFallActivity extends Activity {
	private LazyScrollView waterfall_scroll;
	private LinearLayout waterfall_container;
	private ArrayList<LinearLayout> waterfall_items;
	private Display display;
	private AssetManager assetManager;

	private int itemWidth;

	private int column_count = 3;// 显示列数
	private int page_count = 15;// 每次加载15张图片

	private int current_page = 0;
	
	private ArrayList<String> image_filenames;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waterfall);

		display = this.getWindowManager().getDefaultDisplay();
		itemWidth = display.getWidth() / column_count;// 根据屏幕大小计算每列大小
		assetManager = this.getAssets();

		// 获取所有图片路径
		Intent intent = getIntent();
		image_filenames = intent.getStringArrayListExtra("data");
		
		InitLayout();
	}
	
	private void InitLayout() {
		waterfall_scroll = (LazyScrollView) findViewById(R.id.waterfall_scroll);
		waterfall_scroll.getView();
		waterfall_scroll.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onTop() {
				// 滚动到最顶端
				Log.d("LazyScroll", "Scroll to top");
			}

			@Override
			public void onScroll() {
				// 滚动中
				Log.d("LazyScroll", "Scroll");
			}

			@Override
			public void onBottom() {
				// 滚动到最低端
				AddItemToContainer(++current_page, page_count);
			}
		});

		waterfall_container = (LinearLayout) this
				.findViewById(R.id.waterfall_container);
		waterfall_items = new ArrayList<LinearLayout>();
		
		for (int i = 0; i < column_count; i++) {
			LinearLayout itemLayout = new LinearLayout(this);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					itemWidth, LayoutParams.WRAP_CONTENT);
			// itemParam.width = itemWidth;
			// itemParam.height = LayoutParams.WRAP_CONTENT;
			itemLayout.setPadding(2, 2, 2, 2);
			itemLayout.setOrientation(LinearLayout.VERTICAL);

			itemLayout.setLayoutParams(itemParam);

			waterfall_items.add(itemLayout);
			waterfall_container.addView(itemLayout);
		}

		// 第一次加载
		AddItemToContainer(current_page, page_count);
	}
	
	private void AddItemToContainer(int pageindex, int pagecount) {
		int j = 0;
		
		if(image_filenames == null)return;
		int imagecount = image_filenames.size();
		
		for (int i = pageindex * pagecount; i < pagecount * (pageindex + 1)
				&& i < imagecount; i++) {
			j = j >= column_count ? j = 0 : j;
			String file_name = image_filenames.get(i);
			AddImage(file_name, j++,i);
		}
	}

	private void AddImage(String filename, int columnIndex,int number) {
		ImageView item = (ImageView) LayoutInflater.from(this).inflate(
				R.layout.waterfallitem, null);
		
		item.setTag(new Integer(number));
		
		waterfall_items.get(columnIndex).addView(item);

		item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Integer imgId = (Integer) ((ImageView)v).getTag();

				Intent it = new Intent(WaterFallActivity.this, com.coolroy.photoscanner.ImageSwitcher.class);
				it.putStringArrayListExtra("pathes", image_filenames);
				it.putExtra("index", imgId.intValue());
				startActivity(it);
			}
		});
		
		TaskParam param = new TaskParam();
		param.setAssetManager(assetManager);
		param.setFilename(filename);
		param.setItemWidth(itemWidth);
		ImageLoaderTask task = new ImageLoaderTask(item);
		task.execute(param);
	}
}
