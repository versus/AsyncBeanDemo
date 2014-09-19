package info.android15.asyncbeandemo;

import android.os.AsyncTask;

@SuppressWarnings("serial")
public class ProgressBean extends AsyncBean {

	int duration;

	ProgressBean(int duration) {
		this.duration = duration;
	}

	@Override
	protected void run(boolean restart) {

		new AsyncTask<Integer, Integer, Void>() {

			@Override
			protected Void doInBackground(Integer... params) {
				try {
					Thread.sleep(duration);
				}
				catch (Exception e) {
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				deliver();
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, duration);
	}
}
