package info.android15.asyncbeandemo;

import info.android15.asyncbeandemo.AsyncBean.AsyncBeanListener;
import info.android15.asyncbeandemo.AsyncBean.AsyncBeanState;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public abstract class BaseActivity extends Activity implements AsyncBeanListener {

	private static final String ASYNC_BEANS_KEY = "async_beans";

	ArrayList<AsyncBean> asyncBeans = new ArrayList<AsyncBean>();

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			asyncBeans = (ArrayList<AsyncBean>)savedInstanceState.getSerializable(ASYNC_BEANS_KEY);

			for (int b = 0; b < asyncBeans.size(); b++)
				asyncBeans.set(b, AsyncBean.restoreInstance(asyncBeans.get(b)));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(ASYNC_BEANS_KEY, asyncBeans);
	}

	@Override
	protected void onResume() {
		super.onResume();

		for (int b = 0; b < asyncBeans.size(); b++)
			asyncBeans.get(b).onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		for (int b = 0; b < asyncBeans.size(); b++)
			asyncBeans.get(b).onPause();
	}

	protected void execute(AsyncBean bean) {
		asyncBeans.add(bean);
		bean.execute(this);
	}

	@Override
	public void onAsyncBeanStateChanged(AsyncBean bean, AsyncBeanState state) {
		Log.v("BaseActivity", "onAsyncBeanStateChanged " + bean.getClass().getSimpleName() + " -> " + state.name());

		if (state == AsyncBeanState.COMPLETED)
			asyncBeans.remove(bean);
	}
}
