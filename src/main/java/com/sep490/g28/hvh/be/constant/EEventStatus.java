package com.sep490.g28.hvh.be.constant;

public enum EEventStatus {
    EDITING, //aka drafted
    SUMMITED,
    APPROVED_BY_MNG,
    REJECTED_BY_MNG,
    REJECTED_BY_AD,
    RECRUITING,
    UPCOMING,
    ONGOING,
    ENDED,
    FINISHED,
    CANCELLED;

    public static boolean editable(EEventStatus status) {
        return  (status.equals(EDITING) || status.equals(REJECTED_BY_MNG) || status.equals(REJECTED_BY_AD));
    }

    public static boolean updatetable(EEventStatus status) {
        return  (status.equals(RECRUITING) || status.equals(UPCOMING));
    }

//    public static boolean cancellable(EEventStatus status) {
//        return  (status.equals(RECRUITING) || status.equals(UPCOMING));
//    }
}
