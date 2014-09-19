package info.android15.asyncbeandemo;

import info.android15.asyncbeandemo.AsyncBean.AsyncBeanState;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		findViewById(R.id.execute1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				execute(new PercentageBean(10000));
			}
		});

		findViewById(R.id.execute2).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				execute(new ProgressBean(10000));
			}
		});
	}

	@Override
	public void onAsyncBeanStateChanged(AsyncBean bean, AsyncBeanState state) {
		super.onAsyncBeanStateChanged(bean, state);

		if (bean instanceof PercentageBean) {
			if (state != AsyncBeanState.COMPLETED)
				findViewById(R.id.execute1).setEnabled(false);
			else {
				findViewById(R.id.execute1).setEnabled(true);
				findViewById(R.id.status1).setBackgroundColor(Color.GREEN);
			}
		}
		else {
			if (state != AsyncBeanState.COMPLETED) {
				findViewById(R.id.execute2).setEnabled(false);
				findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
				findViewById(R.id.progressBar2).animate().alpha(1).setDuration(500).setListener(null);
			}
			else {
				findViewById(R.id.execute2).setEnabled(true);
				findViewById(R.id.progressBar2).animate().alpha(0).setDuration(500).setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						findViewById(R.id.progressBar2).setVisibility(View.INVISIBLE);
					}
				});
			}
		}
	}

	@Override
	public void onAsyncBeanEvent(AsyncBean bean, Object event) {
		if (bean instanceof PercentageBean) {
			TextView t = (TextView)findViewById(R.id.status1);
			t.setBackgroundColor(Color.RED);
			t.setText("" + (int)event + "%");
		}
	}
}
