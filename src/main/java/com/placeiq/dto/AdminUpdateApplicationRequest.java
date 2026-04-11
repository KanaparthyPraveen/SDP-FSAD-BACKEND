package com.placeiq.dto;

import lombok.Data;

@Data
public class AdminUpdateApplicationRequest {
    /** Optional: override application status directly */
    private String status;

    /** Index in the rounds list (0-based) */
    private Integer roundIndex;

    /** New status for that round: CLEARED | REJECTED | ONGOING | PENDING */
    private String roundStatus;

    /** Admin notes */
    private String notes;
}
