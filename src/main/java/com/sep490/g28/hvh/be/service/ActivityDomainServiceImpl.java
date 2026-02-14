package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.activityDomain.ActivityDomainDetailsResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActivityDomainServiceImpl implements ActivityDomainService{

    ActivityDomainRepository activityDomainRepository;
    ActivitySubDomainRepository activitySubDomainRepository;

    @Override
    public void createActivityDomain(CreateActivityDomainRequest request) {

        //Create activity domain in the db
        ActivityDomain activityDomain = new ActivityDomain();
        activityDomain.setName(request.getName());
        activityDomain.setSpecialSessionMaxTime(request.getSpecialSessionMaxTime());
        activityDomain.setActive(true);

        activityDomainRepository.save(activityDomain);

        //Check if the activity subdomain is existed
        if(request.getActivitySubDomain() == null) {
            return;
        }

        //Else create activity subdomain in the db
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

        //update for activity domain
        ActivityDomain activityDomain = activityDomainRepository.findById(id).orElseThrow(
                () -> new AppException(ActivityDomainErrorCode.DOMAIN_NOT_EXISTED)
        );

        activityDomain.setName(request.getName());
        activityDomain.setSpecialSessionMaxTime(request.getSpecialSessionMaxTime());

        activityDomainRepository.save(activityDomain);

        //Check if any subdomain update request is existed
        if(request.getActivitySubDomainUpdateRequests() == null) {
            return "";
        }

        //Else update for activity subdomain
        for(UpdateActivitySubDomainRequest ur : request.getActivitySubDomainUpdateRequests()) {

            switch(ur.getAction()) {
                case "EDIT":
                    ActivitySubDomain activitySubDomainEdit = activitySubDomainRepository.findById(ur.getId()).orElseThrow(
                            () -> new AppException(ActivityDomainErrorCode.SUBDOMAIN_NOT_EXISTED)
                    );
                    activitySubDomainEdit.setName(ur.getName());
                    activitySubDomainRepository.save(activitySubDomainEdit);
                    break;

                case "DELETE":
                    ActivitySubDomain activitySubDomainDelete = activitySubDomainRepository.findById(ur.getId()).orElseThrow(
                            () -> new AppException(ActivityDomainErrorCode.SUBDOMAIN_NOT_EXISTED)
                    );
                    activitySubDomainRepository.delete(activitySubDomainDelete);
                    break;

                case "ADD":
                    ActivitySubDomain activitySubDomainNew = new ActivitySubDomain();
                    activitySubDomainNew.setName(ur.getName());
                    activitySubDomainNew.setActivityDomain(activityDomain);
                    activitySubDomainRepository.save(activitySubDomainNew);
                    break;

            }
        }

        return "";
    }

    @Override
    public Page<ActivityDomainDetailsResponse> getActivityDomains(int pageNumber, int pageSize, String inputActive, String name) {
        //parse active
        Boolean active =
                (inputActive == null || inputActive.isBlank())
                ? null
                : Boolean.parseBoolean(inputActive);

        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.ASC, "createdAt")
        );

        //search
        return activityDomainRepository.search(active, name, pageable)
                .map(ActivityDomainDetailsResponse::from);
    }


}
