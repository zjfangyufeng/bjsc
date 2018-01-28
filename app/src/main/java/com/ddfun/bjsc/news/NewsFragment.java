package com.ddfun.bjsc.news;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddfun.bjsc.R;
import com.ddfun.bjsc.bean.NewsBean;
import com.ddfun.bjsc.bean.NewsCategoryBean;
import com.ff.imgloader.ImageLoader;

import java.util.List;

public class NewsFragment extends Fragment implements INewsFragmentView, View.OnClickListener {

	View rootView;
	View loading_progressBar,net_err_lay;
	RecyclerView success_lay;

	NewsCategoryBean bean;

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
		success_lay.setLayoutManager(new GridLayoutManager(getContext(),2));
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
		presenter.initData();
		return rootView;
	}

	class ViewHolder extends RecyclerView.ViewHolder{

		ImageView iv;
		TextView tv_title,tv_sub_title;

		public ViewHolder(View itemView) {
			super(itemView);
			iv = (ImageView) itemView.findViewById(R.id.iv);
			tv_title = (TextView) itemView.findViewById(R.id.tv_title);
			tv_sub_title = (TextView) itemView.findViewById(R.id.tv_sub_title);
		}

		public void setItem(NewsBean newsBean) {
			tv_title.setText(newsBean.title);
			tv_sub_title.setText(newsBean.freecontent);
			ImageLoader.getInstance().loadIcon(newsBean.cover,iv,ImageLoader.PREVIEWPICSIZE,ImageLoader.PREVIEWPICSIZE,false);
			itemView.requestLayout();
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
			presenter.initData();
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
