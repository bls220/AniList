/**
 * 
 */
package com.bls220.anilist;

import java.util.ArrayList;

import android.app.Activity;

import com.bls220.anilist.HtmlHelperTask.OnTaskCompleteListener;
import com.bls220.anilist.HtmlHelperTask.RequestParams;
import com.bls220.anilist.HtmlHelperTask.TaskResults;

/**
 * @author bsmith
 * 
 */
public class ExecuteHtmlTaskQueue implements OnTaskCompleteListener {

	public static class Task {
		final RequestParams params;
		final OnTaskCompleteListener callback;

		public Task(RequestParams params, OnTaskCompleteListener callback) {
			this.params = params;
			this.callback = callback;
		}
	}

	final Activity mActivity;
	final ArrayList<Task> mTasks;
	final Runnable mGroupCompleteListener;

	int tasksDone;

	public ExecuteHtmlTaskQueue(Activity activity, Runnable groupCompleteListener) {
		mActivity = activity;
		mTasks = new ArrayList<Task>();
		mGroupCompleteListener = groupCompleteListener;
	}

	public ExecuteHtmlTaskQueue(Activity activity, Runnable groupCompleteListener, Task... tasks) {
		this(activity, groupCompleteListener);
		for (Task task : tasks) {
			add(task);
		}
	}

	public ExecuteHtmlTaskQueue add(Task task) {
		mTasks.add(task);
		return this;
	}

	public void execute() {
		if (!mTasks.isEmpty()) {
			Task start = mTasks.get(0);
			new HtmlHelperTask(mActivity, this).execute(start.params);
		}
	}

	@Override
	public void onTaskComplete(TaskResults results) {
		// Handle individual task callback
		Task task = mTasks.get(tasksDone);
		if (task.callback != null)
			task.callback.onTaskComplete(results);
		tasksDone++;
		if (tasksDone < mTasks.size()) {
			// Go to next task
			task = mTasks.get(tasksDone);
			new HtmlHelperTask(mActivity, this).execute(task.params);
		} else {
			// Done
			if (mGroupCompleteListener != null) {
				mGroupCompleteListener.run();
			}
		}
	}

}
