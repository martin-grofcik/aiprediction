package org.crp.aiflow.services;

import com.google.common.collect.ImmutableMap;
import org.flowable.engine.impl.TaskServiceImpl;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.form.api.FormInfo;
import org.flowable.form.model.SimpleFormModel;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AiPredictionTaskService extends TaskServiceImpl {

    public static final Map<String, Double> nationalityMap = ImmutableMap.of(
            "Slovak", 0.0,
            "Polish", 1.0,
            "Swiss", 2.0
    );
    public static final Map<String, Double> ageMap = ImmutableMap.of(
            "20-30", 0.0,
            "30-40", 1.0,
            "40-50", 2.0,
            "50-60", 3.0,
            "60-", 4.0
    );

    public static final Map<Double, Boolean> resultMap = ImmutableMap.of(
            0.0, false,
            1.0, true
    );

//    private final LogisticRegressionModel sameModel;

    public AiPredictionTaskService(ProcessEngineConfigurationImpl processEngineConfiguration) {
        super(processEngineConfiguration);
//        SparkConf conf = new SparkConf().setAppName("Main")
//                .setMaster("local[2]")
//                .set("spark.executor.memory", "3g")
//                .set("spark.driver.memory", "3g");
//        JavaSparkContext sc = new JavaSparkContext(conf);
//        sameModel = LogisticRegressionModel.load(sc.sc(), "ai/model-92/logistic-regression");
    }

    @Override
    public FormInfo getTaskFormModel(String taskId) {
        FormInfo taskFormModel = super.getTaskFormModel(taskId);
        if ("loanApproval".equals(taskFormModel.getKey())) {
            SimpleFormModel formModel = (SimpleFormModel) taskFormModel.getFormModel();
            formModel.getFields().stream().filter(field -> "approved".equals(field.getId())).findFirst().ifPresent(
                    formField -> formField.setValue(getPredictionForTask(formModel))
            );
        }
        return taskFormModel;
    }

    private Object getPredictionForTask(SimpleFormModel formModel) {
//        Map<String, FormField> fieldMap = formModel.allFieldsAsMap();
//        return resultMap.get(mlPredict(
//            nationalityMap.get(fieldMap.get("nationality").getValue()),
//            ageMap.get(fieldMap.get("age").getValue()),
//                1.0,
//                1.0)
//        );
        return Boolean.TRUE;
    }

//    public Double mlPredict(double... values) {
//        return sameModel.predict(Vectors.dense(values));
//    }
}
