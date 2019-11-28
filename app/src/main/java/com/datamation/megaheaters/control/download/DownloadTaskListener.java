package com.datamation.megaheaters.control.download;

import com.datamation.megaheaters.control.TaskType;

public interface DownloadTaskListener {

    void onTaskCompleted(TaskType taskType, String result);

}
