 package com.hyperativatechtest.features.card.dto.batch;

import com.hyperativatechtest.features.fileprocessing.dto.ProcessingMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CardFileProcessingMessage extends ProcessingMessage {
    private String lotId;
    private Integer expectedRecordCount;
}

