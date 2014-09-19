AsyncBean
===================

Allows android application to have background tasks that does not get lost
during screen configuration change, activity recreation, or even during process recreation.

This is demo application only, the code is under heavy testing/development.


INTRO
--------------------

There are two approaches in Android for maintaining a view, fragment or activity's state.
First one is to completely forget about the state and reload all content on creation.
Second approach is to save and restore the state according to onCreate/onSaveInstanceState
template.

First approach is easy to implement but it makes user's experience not so fluent.

Second approach leads us to numerous problems. It is really hard to maintain all parts
of an application to be ready for save/restore and to execute background tasks the same time.
Let's review some of such problems.

1. When a fragment or activity calls a background task to be executed (in example, the activity wants to fetch its content from
  a server) the result of such task can't be delivered to the activity if activity is destroyed or
  recreated because of screen configuration change, activity or entire process recreation.

2. Advised Fragment.setRetainInstanceState(true) can't help here because such fragment is retained only
	for screen configuration changes. Keeping reference to the fragment can also lead us to memory leaks.

3. There is no simple way to check if a background task has already been started. Classic way is to check if
  savedInstanceState == null then start background task. However, if background task has been lost during
  process recreation, it will not start second time and user will get "ProgressBar screen" forever what is
  a common bug even for the greatest applications.

4. Maintaining a list of background tasks in static variables or in the Application object is a nice idea, but
  it can't really help because they can be lost when the process is being recreated because of low memory.

State of background tasks and state of the activity should be in harmony.
Is there any solution for this numerous problems?


SOLUTION
--------------------

So, from one side we have activity which maintains its state, and from another we have static variables.
Sometimes Activity survives, sometimes static variables and process environment survives.

Solution is to keep all background tasks in static variables and deliver that their results to activity.
If the activity is not in the resumed state then hold results on until activity become resumed.

In parallel save/restore list of background tasks for activity instance because static variables can be lost
due to process termination and they need to be recreated and re-run from activity's restored state.

So, AsyncBean was born.


HOW TO USE ASYNCTASK
--------------------

	public class YourAsyncTaskBean extends AsyncBean {

		YourAsyncTaskBean(<arguments you like>) {
		}

		@Override
		protected void run(boolean retry) {

			// Start a background task. When completed, the background task
			// should call AsyncBean.deliver() in the main thread.

			deliver();
		}
	}

	// somewhere in your activity/fragment/view definition
	
		YourAsyncTaskBean yourBean;
	
	// somewhere in activity/fragment/view onCreate/onRestoreInstanceState

		if (savedInstanceState != null)
			yourBean = AsyncBean.restoreInstance((AsyncBean)savedInstanceState.getSerializable("yourBean"));

	// how to execute
	
		yourBean.execute(yourBeanListener);

	// save/attach/detach code:

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("yourBean", yourBean);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (yourBean != null)
			yourBean.onResume(yourBeanListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (yourBean != null)
			yourBean.onPause();
	}

There is a lot of boilerplate code, so create base class that will handle all
management for you, how I did it in the demo application's BaseActivity.


DEMO APP
--------------------

This example keeps two background tasks during screen flips and activity recreation, showing progress
the same time. When the process is being terminated and the activity is recreated from the saved state,
background tasks automatically runs again. State of background tasks and state of the activity are
in harmony this way.

Example program should survive:

1. Activity recreation - a) do a screen flip or b) open device's developer settings and check 'Do not keep activities' checkbox.
	Switch between applications during background tasks execution. The background tasks should continue normally.
2. Process recreation - open task manager and tap 'Clear memory'. Switch to the test app to see background tasks
	being restarted. 

OUTRO
--------------------

AsyncBean fills the greatest gap in android programming.

Please, make this solution widely known, you will help a lot to our community.

Any comments and suggestions are welcome!

