package com.nabob.conch.akka.graph.dag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * DAG工作流
 */
public class ProcessInstance {
    Long processId;
    String processInstanceId;

    public ProcessInstance(Long processId, String processInstanceId) {
        this.processId = processId;
        this.processInstanceId = processInstanceId;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
}