package com.springjobportal.job.portal.services;

import com.springjobportal.job.portal.entity.JobPostActivity;
import com.springjobportal.job.portal.entity.JobSeekerProfile;
import com.springjobportal.job.portal.entity.JobSeekerSave;
import com.springjobportal.job.portal.repository.JobSeekerSaveRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSeekerSaveService {

    private final JobSeekerSaveRepository jobSeekerSaveRepository;

    public JobSeekerSaveService(JobSeekerSaveRepository jobSeekerSaveRepository) {
        this.jobSeekerSaveRepository = jobSeekerSaveRepository;
    }

    public List<JobSeekerSave> getCandidatesJob(JobSeekerProfile userAccountId){
        return jobSeekerSaveRepository.findByUserId(userAccountId);
    }
    //给定一个用户实体（JobSeekerProfile），返回所有和这个用户相关联的 JobSeekerSave 记录。也就是“这个用户保存过哪些职位”。

    public List<JobSeekerSave> getJobCandidates(JobPostActivity job){
        return jobSeekerSaveRepository.findByJob(job);
    }

    public void addNew(JobSeekerSave jobSeekerSave) {
        jobSeekerSaveRepository.save(jobSeekerSave);
    }
    //给定一个职位实体（JobPostActivity），返回所有和这个职位相关联的 JobSeekerSave 记录。也就是“哪些用户保存过这个职位”。
}
