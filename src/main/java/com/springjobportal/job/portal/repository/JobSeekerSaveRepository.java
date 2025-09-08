package com.springjobportal.job.portal.repository;

import com.springjobportal.job.portal.entity.JobPostActivity;
import com.springjobportal.job.portal.entity.JobSeekerProfile;
import com.springjobportal.job.portal.entity.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer> {

    List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountId);

    List<JobSeekerSave> findByJob(JobPostActivity job);
}
