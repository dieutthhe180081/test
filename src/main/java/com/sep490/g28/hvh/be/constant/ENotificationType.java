package com.sep490.g28.hvh.be.constant;

public enum ENotificationType {
    //EVENT
    EVENT_CREATED, //sent to mng, event created by host
    EVENT_SUBMITTED, //sent to admin, event submitted by mng
    EVENT_APPROVED_BY_MNG, //send to host and admin
    EVENT_REJECTED_BY_MNG, //send to host only
    EVENT_APPROVED_BY_AD, //send to host and mng
    EVENT_REJECTED_BY_AD, //send to host and mng


}
