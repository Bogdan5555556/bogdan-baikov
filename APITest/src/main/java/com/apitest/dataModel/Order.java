package com.apitest.dataModel;

import lombok.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private long id;
    private long petId;
    private int quantity;
    private Date shipDate;
    private String status;
    private boolean complete;

    @Getter
    @AllArgsConstructor
    public enum OrderStatuses {
        PLACED ("placed"),
        DELIVERED ("delivered"),
        APPROVED ("approved"),
        UNDEFINED("undefined");

        private final String status;

        public static List<String> getOrderStatusesList(){
            return Arrays.asList(OrderStatuses.values())
                    .stream()
                    .map(status -> status.getStatus())
                    .collect(Collectors.toList());
        }
    }
}
