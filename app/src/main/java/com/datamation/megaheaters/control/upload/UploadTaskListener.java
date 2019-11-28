package com.datamation.megaheaters.control.upload;


import com.datamation.megaheaters.control.TaskType;

import java.util.List;

public interface UploadTaskListener {
    void onTaskCompleted(TaskType taskType, List<String> list);
}