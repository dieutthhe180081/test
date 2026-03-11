package com.sep490.g28.hvh.be.constant;

public enum EOrgType {
    //ĐÃ đăng kí với DHA
    SOCIAL_FUND, //"Quỹ xã hội"
    CHARITY_FUND, //("Quỹ từ thiện"),
    NGO, //("Tổ chức phi chính phủ"),
    SOCIAL_ORGANIZATION, //("Tổ chức xã hội"),

    //chưa đăng kí DHA
    GOVERNMENT_AGENCY_BASED, //("Được thành lập trong cơ quan chính quyền"),
    PUBLIC_SERVICE_UNIT_BASED, //("Được thành lập trong đơn vị sự nghiệp công lập"),
    MASS_ORGANIZATION, //("Tổ chức quần chúng (phường, xã, làng)"),
    UNIVERSITY_BASED, //("Được thành lập trong trường đại học"),
    GENERAL_EDUCATION_BASED, //("Được thành lập trong cơ sở giáo dục phổ thông"),
    STATE_OWNED_ENTERPRISE_BASED, //("Được thành lập trong doanh nghiệp nhà nước"),
    PRIVATE_ENTERPRISE_BASED, //("Được thành lập trong doanh nghiệp tư nhân"),
    SELF_GOVERNED_ORGANIZATION, //("Tổ chức xã hội tự quản"),
    OTHER //("Khác");
}