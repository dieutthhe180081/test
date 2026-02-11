package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.activityDomain.CreateActivityDomainRequest;
import com.sep490.g28.hvh.be.dto.activityDomain.UpdateActivityDomainRequest;
import com.sep490.g28.hvh.be.dto.activityDomain.UpdateActivitySubDomainRequest;
import com.sep490.g28.hvh.be.entity.ActivityDomain;
import com.sep490.g28.hvh.be.entity.ActivitySubDomain;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.ActivityDomainErrorCode;
import com.sep490.g28.hvh.be.repository.ActivityDomainRepository;
import com.sep490.g28.hvh.be.repository.ActivitySubDomainRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActivityDomainServiceImpl implements ActivityDomainService{

    ActivityDomainRepository activityDomainRepository;
    ActivitySubDomainRepository activitySubDomainRepository;

    @Override
    public void createActivityDomain(CreateActivityDomainRequest request) {

        //check if the latest end time > the earliest start time
        if(request.getLatestEndTime().isBefore(request.getEarliestStartTime())) {
            throw new AppException(ActivityDomainErrorCode.SESSION_TIME_OVERLAP);
        }

        //check if special session max time > default session max time
        if (request.getSpecialSessionMaxTime() < request.getDefaultSessionMaxTime()) {
            throw new AppException(ActivityDomainErrorCode.SPECIAL_SESSION_EXCEEDS_DEFAULT);
        }

        //Create activity domain in the db
        ActivityDomain activityDomain = new ActivityDomain();
        activityDomain.setName(request.getName());
        activityDomain.setEarliestStartTime(request.getEarliestStartTime());
        activityDomain.setLatestEndTime(request.getLatestEndTime());
        activityDomain.setDefaultSessionMaxTime(request.getDefaultSessionMaxTime());
        activityDomain.setSpecialSessionMaxTime(request.getSpecialSessionMaxTime());
        activityDomain.setActive(true);

        activityDomainRepository.save(activityDomain);

        //Create activity subdomain in the db
        for(String subDomain : request.getActivitySubDomain()) {
            ActivitySubDomain activitySubDomain = new ActivitySubDomain();
            activitySubDomain.setName(subDomain);
            activitySubDomain.setActivityDomain(activityDomain);
            activityDomain.setActive(true);

            activitySubDomainRepository.save(activitySubDomain);
        }
    }

    @Override
    public String updateActivityDomain(Short id, UpdateActivityDomainRequest request) {

        //check if the latest end time > the earliest start time
        if(request.getLatestEndTime().isBefore(request.getEarliestStartTime())) {
            throw new AppException(ActivityDomainErrorCode.SESSION_TIME_OVERLAP);
        }

        //check if special session max time > default session max time
        if (request.getSpecialSessionMaxTime() < request.getDefaultSessionMaxTime()) {
            throw new AppException(ActivityDomainErrorCode.SPECIAL_SESSION_EXCEEDS_DEFAULT);
        }

        ActivityDomain activityDomain = activityDomainRepository.findById(id).orElseThrow(
                () -> new AppException(ActivityDomainErrorCode.DOMAIN_NOT_EXISTED)
        );

        activityDomain.setName(request.getName());
        activityDomain.setEarliestStartTime(request.getEarliestStartTime());
        activityDomain.setLatestEndTime(request.getLatestEndTime());
        activityDomain.setDefaultSessionMaxTime(request.getDefaultSessionMaxTime());
        activityDomain.setSpecialSessionMaxTime(request.getSpecialSessionMaxTime());

        activityDomainRepository.save(activityDomain);

        for(UpdateActivitySubDomainRequest ur : request.getActivitySubDomainUpdateRequests()) {

            switch(ur.getAction()) {
                case "EDIT":
                    ActivitySubDomain activitySubDomainEdit = activitySubDomainRepository.findById(ur.getId()).orElseThrow(
                            () -> new AppException(ActivityDomainErrorCode.SUBDOMAIN_NOT_EXISTED)
                    );
                    activitySubDomainEdit.setName(ur.getName());
                    activitySubDomainRepository.save(activitySubDomainEdit);

                case "DELETE":
                    ActivitySubDomain activitySubDomainDelete = activitySubDomainRepository.findById(ur.getId()).orElseThrow(
                            () -> new AppException(ActivityDomainErrorCode.SUBDOMAIN_NOT_EXISTED)
                    );
                    activitySubDomainRepository.delete(activitySubDomainDelete);

                case "ADD":
                    ActivitySubDomain activitySubDomainNew = new ActivitySubDomain();
                    activitySubDomainNew.setName(ur.getName());
                    activitySubDomainRepository.save(activitySubDomainNew);

            }
        }



        return "";
    }
}
