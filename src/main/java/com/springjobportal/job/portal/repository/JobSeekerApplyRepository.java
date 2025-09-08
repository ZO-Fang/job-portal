package com.springjobportal.job.portal.repository;

import com.springjobportal.job.portal.entity.JobPostActivity;
import com.springjobportal.job.portal.entity.JobSeekerApply;
import com.springjobportal.job.portal.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {

    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

    List<JobSeekerApply> findByJob(JobPostActivity job);
}
