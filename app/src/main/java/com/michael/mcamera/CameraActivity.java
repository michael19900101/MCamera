package com.michael.mcamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;

import com.michael.mcamera.camera.CameraInterface;
import com.michael.mcamera.camera.preview.CameraSurfaceView;
import com.michael.mcamera.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends Activity implements CameraInterface.CamOpenOverCallback {
	private static final String TAG = "CameraActivity";
	CameraSurfaceView surfaceView = null;
	ImageButton shutterBtn;
	RecyclerView rvThumbnailContainer;
	ThumbNailAdapter adapter;
	List<Bitmap> thumbNailList = new ArrayList<>();
	float previewRate = -1f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread openThread = new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				CameraInterface.getInstance().doOpenCamera(CameraActivity.this);
				CameraInterface.getInstance().setThumbnailInterace(new ThumbnailInterace() {
					@Override
					public void getThumbnail(Bitmap thumbnail) {
						thumbNailList.add(thumbnail);
						adapter.setmDataset(thumbNailList);
						adapter.notifyDataSetChanged();
					}
				});
			}
		};
		openThread.start();
		setContentView(R.layout.activity_camera);
		initUI();
		initViewParams();
		
		shutterBtn.setOnClickListener(new BtnListeners());
	}

	private void initUI(){
		surfaceView = (CameraSurfaceView) findViewById(R.id.camera_surfaceview);
		shutterBtn = (ImageButton) findViewById(R.id.btn_shutter);
		rvThumbnailContainer = (RecyclerView) findViewById(R.id.rv_thumbnail_container);
		// 创建一个线性布局管理器
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		// 设置布局管理器
		rvThumbnailContainer.setLayoutManager(layoutManager);
		// 设置间隔
		rvThumbnailContainer.addItemDecoration(new SpacesItemDecoration(DisplayUtil.dip2px(this,10)));
		adapter = new ThumbNailAdapter();
		rvThumbnailContainer.setAdapter(adapter);
	}

	private void initViewParams(){
		LayoutParams params = surfaceView.getLayoutParams();
		Point p = DisplayUtil.getScreenMetrics(this);
		params.width = p.x;
		params.height = p.y;
		previewRate = DisplayUtil.getScreenRate(this); //默认全屏的比例预览
		surfaceView.setLayoutParams(params);

		//手动设置拍照ImageButton的大小为120dip×120dip,原图片大小是64×64
		LayoutParams p2 = shutterBtn.getLayoutParams();
		p2.width = DisplayUtil.dip2px(this, 80);
		p2.height = DisplayUtil.dip2px(this, 80);
		shutterBtn.setLayoutParams(p2);	

	}

	@Override
	public void cameraHasOpened() {
		// TODO Auto-generated method stub
		SurfaceHolder holder = surfaceView.getSurfaceHolder();
		CameraInterface.getInstance().doStartPreview(holder, previewRate);
	}
	private class BtnListeners implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_shutter:
				CameraInterface.getInstance().doTakePicture(CameraActivity.this);
				break;
			default:break;
			}
		}

	}

}
