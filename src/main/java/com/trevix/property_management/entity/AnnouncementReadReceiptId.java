package com.trevix.property_management.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementReadReceiptId implements Serializable {
    private UUID announcement;
    private UUID tenant;
}