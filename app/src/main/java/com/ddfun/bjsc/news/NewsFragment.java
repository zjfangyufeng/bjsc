package com.ddfun.bjsc.news;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddfun.bjsc.R;
import com.ddfun.bjsc.bean.NewsBean;
import com.ddfun.bjsc.bean.NewsCategoryBean;
import com.ff.common.ImmediatelyToast;
import com.ff.common.LogUtil;
import com.ff.imgloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewsFragment extends Fragment implements INewsFragmentView, View.OnClickListener {

	View rootView;
	View loading_progressBar,net_err_lay;
	RecyclerView success_lay;

	NewsCategoryBean bean;
	int page;

	NewsFragmentPresenter presenter;

	public static NewsFragment newInstance(NewsCategoryBean bean) {
		NewsFragment f = new NewsFragment();
		Bundle b = new Bundle();
		b.putParcelable("bean", bean);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			bean = args.getParcelable("bean");
		}
		presenter = new NewsFragmentPresenter(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.news_fragment, container, false);
		initView();
		success_lay.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
		success_lay.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
			@Override
			public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
				return new ViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment_item_lay,parent,false));
			}

			@Override
			public void onBindViewHolder(ViewHolder holder, int position) {
				holder.setItem(presenter.iModel.data.get(position));
			}

			@Override
			public int getItemCount() {
				return presenter.iModel.data == null?0:presenter.iModel.data.size();
			}
		});
		success_lay.addOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				if(newState == RecyclerView.SCROLL_STATE_IDLE){
					if(recyclerView.canScrollVertically(1)){
						LogUtil.i("没到底");
					}else {
						LogUtil.i("到底了");
						page++;
						presenter.fetchData(bean.sortid,page);
					}
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			}
		});
		presenter.initData(bean.sortid,page);
		return rootView;
	}

	@Override
	public boolean addData(ArrayList<NewsBean> data) {
		boolean added = false;
		if(data == null || data.isEmpty()){
			ImmediatelyToast.showLongMsg("没有更多数据");
		}else {
			List<NewsBean> total_data = presenter.iModel.data;
			for(NewsBean bean : data){
				if(!total_data.contains(bean)){
					presenter.iModel.data.add(bean);
					added = true;
				}
			}
		}
		if(added)
			success_lay.getAdapter().notifyDataSetChanged();
		return added;
	}

	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		NewsBean bean;
		ImageView iv;
		TextView tv_title,tv_sub_title;

		public ViewHolder(View itemView) {
			super(itemView);
			iv = (ImageView) itemView.findViewById(R.id.iv);
			tv_title = (TextView) itemView.findViewById(R.id.tv_title);
			tv_sub_title = (TextView) itemView.findViewById(R.id.tv_sub_title);
			itemView.setOnClickListener(this);
		}

		public void setItem(NewsBean newsBean) {
			bean = newsBean;
			tv_title.setText(newsBean.title);
			tv_sub_title.setText(newsBean.freecontent);
			ImageLoader.getInstance().loadIcon(newsBean.cover,iv,ImageLoader.PREVIEWPICSIZE,ImageLoader.PREVIEWPICSIZE,false);
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(v.getContext(), NewsDetailActivity.class);
			intent.putExtra("fid",bean.aid);
			startActivity(intent);
		}
	}

	@Override
	public void setData(List<NewsBean> data) {

	}

	@Override
	public void showLoadingLayout() {
		loading_progressBar.setVisibility(View.VISIBLE);
		net_err_lay.setVisibility(View.GONE);
		success_lay.setVisibility(View.GONE);
	}

	@Override
	public void showSuccessLayout() {
		loading_progressBar.setVisibility(View.GONE);
		net_err_lay.setVisibility(View.GONE);
		success_lay.setVisibility(View.VISIBLE);
	}

	@Override
	public void showErrorLayout() {
		loading_progressBar.setVisibility(View.GONE);
		net_err_lay.setVisibility(View.VISIBLE);
		success_lay.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.net_err_lay:
			presenter.initData(bean.sortid,page);
			break;
		default:
			break;
		}
	}

	public void initView() {
		loading_progressBar = rootView.findViewById(R.id.loading_progressBar);
		net_err_lay =  rootView.findViewById(R.id.net_err_lay);
		success_lay = (RecyclerView) rootView.findViewById(R.id.success_lay);
		net_err_lay.setOnClickListener(this);

	}

	ProgressDialog pd;

	@Override
	public void showWaitingDialog() {
		if(pd==null){
			if(getActivity()==null)
				return;
			pd = new ProgressDialog(getActivity());
			pd.setCancelable(false);
		}
		pd.show();
	}

	@Override
	public void dismissWaitingDialog() {
		if(pd!=null&&pd.isShowing())
			pd.dismiss();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if(pd!=null&&pd.isShowing())
			pd.dismiss();
	}

}
