package org.crp.aiflow.services;

import org.flowable.engine.impl.TaskServiceImpl;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.form.api.FormInfo;
import org.flowable.form.model.SimpleFormModel;
import org.springframework.stereotype.Service;

@Service
public class AiPredictionTaskService extends TaskServiceImpl {
    public AiPredictionTaskService(ProcessEngineConfigurationImpl processEngineConfiguration) {
        super(processEngineConfiguration);
    }

    @Override
    public FormInfo getTaskFormModel(String taskId) {
        FormInfo taskFormModel = super.getTaskFormModel(taskId);
        if ("loanApproval".equals(taskFormModel.getKey())) {
            SimpleFormModel formModel = (SimpleFormModel) taskFormModel.getFormModel();
            formModel.getFields().stream().filter(field -> "approved".equals(field.getId())).findFirst().ifPresent(
                    formField -> formField.setValue(true)
            );
        }
        return taskFormModel;
    }
}
