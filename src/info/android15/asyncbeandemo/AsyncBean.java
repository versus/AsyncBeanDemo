package info.android15.asyncbeandemo;

import java.io.Serializable;

import android.util.SparseArray;

@SuppressWarnings("serial")
public abstract class AsyncBean implements Serializable {

	enum AsyncBeanState {
		EXECUTING, RESTARTING, RESUMING, COMPLETED
	};

	public interface AsyncBeanListener {
		void onAsyncBeanStateChanged(AsyncBean bean, AsyncBeanState state); // is being called on first execution, on resuming execution and on execution restart
		void onAsyncBeanEvent(AsyncBean bean, Object event);
	}

	int uid;
	boolean completed;

	transient boolean restart;
	transient AsyncBeanListener listener;

	public static SparseArray<AsyncBean> beans = new SparseArray<AsyncBean>();

	public AsyncBean() {
		uid = UidGenerator.generate();
	}

	public static AsyncBean restoreInstance(AsyncBean bean) {
		if (bean == null)
			return null;

		AsyncBean staticBean = beans.get(bean.uid);
		if (staticBean != null)
			return staticBean;

		bean.restart = true;
		beans.put(bean.uid, bean);
		return bean;
	}

	public void execute(AsyncBeanListener listener) {
		this.listener = listener;
		beans.put(uid, this);

		run(false);
		listener.onAsyncBeanStateChanged(this, AsyncBeanState.EXECUTING);
	}

	protected abstract void run(boolean restart);

	protected void deliver() {
		completed = true;

		if (listener != null) {
			beans.remove(uid);
			listener.onAsyncBeanStateChanged(this, AsyncBeanState.COMPLETED);
		}
	}

	public void onResume(AsyncBeanListener listener) {
		this.listener = listener;

		if (completed) {
			beans.remove(uid);
			listener.onAsyncBeanStateChanged(this, AsyncBeanState.COMPLETED);
		}
		else if (restart) {
			restart = false;
			run(true);
			listener.onAsyncBeanStateChanged(this, AsyncBeanState.RESTARTING);
		}
		else
			listener.onAsyncBeanStateChanged(this, AsyncBeanState.RESUMING);
	}

	public void onPause() {
		listener = null;
	}

	public AsyncBeanListener getListener() {
		return listener;
	}
}
