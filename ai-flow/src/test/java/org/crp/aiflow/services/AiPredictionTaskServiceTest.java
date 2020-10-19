package org.crp.aiflow.services;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"test"})
class AiPredictionTaskServiceTest {

    @Autowired
    AiPredictionTaskService aiPredictionTaskService;

    @Test
    void testMlPrediction() {
//        assertThat(aiPredictionTaskService.mlPredict(2.0,2.0,3.0,0.0)).isEqualTo(1.0d);
    }

}