package com.springjobportal.job.portal.controller;

import com.springjobportal.job.portal.entity.JobPostActivity;
import com.springjobportal.job.portal.entity.JobSeekerProfile;
import com.springjobportal.job.portal.entity.JobSeekerSave;
import com.springjobportal.job.portal.entity.Users;
import com.springjobportal.job.portal.services.JobPostActivityService;
import com.springjobportal.job.portal.services.JobSeekerProfileService;
import com.springjobportal.job.portal.services.JobSeekerSaveService;
import com.springjobportal.job.portal.services.UsersService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class JobSeekerSaveController {

    private final UsersService usersService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerSaveService jobSeekerSaveService;

    public JobSeekerSaveController(UsersService usersService, JobSeekerProfileService jobSeekerProfileService, JobPostActivityService jobPostActivityService, JobSeekerSaveService jobSeekerSaveService) {
        this.usersService = usersService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    @PostMapping("job-details/save/{id}")
    public String save(@PathVariable("id") int jobId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)){
            String currentUsername = authentication.getName();
            Users user = usersService.findByEmail(currentUsername);

            //找到当前用户的profile
            Optional<JobSeekerProfile> jobSeekerProfile = jobSeekerProfileService.getOne(user.getUserId());

            //找到符合这个id的职位
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(jobId);

            if (jobSeekerProfile.isPresent() && jobPostActivity != null) {
                // 创建新的JobSeekerSave对象，避免实体状态问题
                JobSeekerSave newSave = new JobSeekerSave();
//                jobSeekerSave.setJob(jobPostActivity);
//                jobSeekerSave.setUserId(jobSeekerProfile.get());
                newSave.setJob(jobPostActivity);
                newSave.setUserId(jobSeekerProfile.get());
                jobSeekerSaveService.addNew(newSave);
            } else {
                throw new RuntimeException("User not found");
            }

            //jobSeekerSaveService.addNew(jobSeekerSave);
        }
        return "redirect:/dashboard/";
    }

    @GetMapping("saved-jobs/")
    public String savedJobs(Model model) {
        List<JobPostActivity> jobPost = new ArrayList<>();
        Object currentUserProfile = usersService.getCurrentUserProfile();

        //找到当前用户保存的的职位有哪些
        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);

        for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
            jobPost.add(jobSeekerSave.getJob());
        }

        model.addAttribute("jobPost", jobPost);
        model.addAttribute("user", currentUserProfile);

        return "saved-jobs";
    }
}
