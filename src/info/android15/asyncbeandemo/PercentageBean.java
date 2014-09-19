package info.android15.asyncbeandemo;

import android.os.AsyncTask;

@SuppressWarnings("serial")
public class PercentageBean extends AsyncBean {

	int duration;

	PercentageBean(int duration) {
		this.duration = duration;
	}

	@Override
	protected void run(boolean restart) {

		new AsyncTask<Integer, Integer, Void>() {

			@Override
			protected Void doInBackground(Integer... params) {
				int left = params[0];

				while (left > 0) {
					int sleep = Math.min(left, 100);
					try {
						Thread.sleep(sleep);
						publishProgress((int)(100f - 100f * left / params[0]));
					}
					catch (Exception e) {
						e.printStackTrace();
						return null;
					}
					left -= sleep;
				}

				return null;
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				AsyncBeanListener listener = getListener();
				if (listener != null)
					listener.onAsyncBeanEvent(PercentageBean.this, values[0]);
			}

			@Override
			protected void onPostExecute(Void result) {
				onProgressUpdate(100);
				deliver();
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, duration);
	}
}
