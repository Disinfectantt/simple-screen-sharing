package com.cringee.simplescreensharing.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Data {
    private String senderId;
    private String targetId;
    private Object data;
    private String type;
}
