package ru.tpu.hostel.administration.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DocumentType {
    CERTIFICATE("Справка на чесотку и педикулез"),
    FLUOROGRAPHY("Флюорография");

    private final String documentName;
}
