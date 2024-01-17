package com.nabob.conch.akka.pipeline.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Adam
 * @since 2024/1/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Timeout implements Command {

    private long whichId;
}
