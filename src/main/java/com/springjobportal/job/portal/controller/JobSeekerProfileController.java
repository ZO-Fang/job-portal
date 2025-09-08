package com.springjobportal.job.portal.controller;

import com.springjobportal.job.portal.entity.JobSeekerProfile;
import com.springjobportal.job.portal.entity.Skills;
import com.springjobportal.job.portal.entity.Users;
import com.springjobportal.job.portal.repository.UsersRepository;
import com.springjobportal.job.portal.services.JobSeekerProfileService;
import com.springjobportal.job.portal.services.S3Service;
import com.springjobportal.job.portal.util.FileDownloadUtil;
import com.springjobportal.job.portal.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/job-seeker-profile")
public class JobSeekerProfileController {

    private JobSeekerProfileService jobSeekerProfileService;

    private UsersRepository usersRepository;

    private S3Service s3Service;

    @Autowired
    public JobSeekerProfileController(JobSeekerProfileService jobSeekerProfileService, UsersRepository usersRepository, S3Service s3Service) {
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.usersRepository = usersRepository;
        this.s3Service = s3Service;
    }

    @GetMapping("/")
    public String JobSeekerProfile(Model model) {
        JobSeekerProfile jobSeekerProfile = new JobSeekerProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Skills> skills = new ArrayList<>();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Users user = usersRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());

            if (seekerProfile.isPresent()){
                jobSeekerProfile = seekerProfile.get();

//                if(jobSeekerProfile.getSkills().isEmpty()){
//                    skills.add(new Skills());
//                    jobSeekerProfile.setSkills(skills);
//                }
            }


            //  无论是否查到，都确保 profile.skills 可渲染（null/空都补一行）
            if (jobSeekerProfile.getSkills() == null || jobSeekerProfile.getSkills().isEmpty()) {
                skills.add(new Skills());
                jobSeekerProfile.setSkills(skills);
            }

            model.addAttribute("skills", skills);
            model.addAttribute("profile", jobSeekerProfile);
        }

        return "job-seeker-profile";
    }


    @PostMapping("/addNew")
    public String addNew(JobSeekerProfile jobSeekerProfile, @RequestParam("image") MultipartFile image, @RequestParam("resumepdf") MultipartFile pdf1, @RequestParam("portfoliopdf") MultipartFile pdf2, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String userId = "";

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Users user = usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            userId = user.getUserId() + "";
            jobSeekerProfile.setUserId(user);
            //jobSeekerProfile.setUserAccountId(user.getUserId());  不要加这个
        }

        List<Skills> skillsList = new ArrayList<>();
        model.addAttribute("profile", jobSeekerProfile);
        model.addAttribute("skills", skillsList);

        for (Skills skills: jobSeekerProfile.getSkills()) {
            skills.setJobSeekerProfile(jobSeekerProfile);
        }

        String imageName = "";
        String resumeName = "";
        String portfolioName = "";

        if (!Objects.equals(image.getOriginalFilename(), "")){
            imageName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            jobSeekerProfile.setProfilePhoto(imageName);
        }

        if (!Objects.equals(pdf1.getOriginalFilename(), "")){
            resumeName = StringUtils.cleanPath(Objects.requireNonNull(pdf1.getOriginalFilename()));
            jobSeekerProfile.setResume(resumeName);
        }

        if (!Objects.equals(pdf2.getOriginalFilename(), "")){
            portfolioName = StringUtils.cleanPath(Objects.requireNonNull(pdf2.getOriginalFilename()));
            jobSeekerProfile.setPortfolio(portfolioName);
        }

        JobSeekerProfile seekerProfile = jobSeekerProfileService.addNew(jobSeekerProfile);

        try {
            if (!Objects.equals(image.getOriginalFilename(), "")){
                s3Service.uploadFile(userId, imageName, image);
            }

            if (!Objects.equals(pdf1.getOriginalFilename(), "")){
                s3Service.uploadFile(userId, resumeName, pdf1);
            }

            if (!Objects.equals(pdf2.getOriginalFilename(), "")){
                s3Service.uploadFile(userId, portfolioName, pdf2);
            }

        }  catch (IOException e){
            throw new RuntimeException(e);
        }


//        try{
//            String uploadDir = "photos/candidate/" + jobSeekerProfile.getUserAccountId();
//
//            if(!Objects.equals(image.getOriginalFilename(), "")){
//                FileUploadUtil.saveFile(uploadDir, imageName, image);
//            }
//
//            if(!Objects.equals(pdf1.getOriginalFilename(), "")){
//                FileUploadUtil.saveFile(uploadDir, resumeName, pdf1);
//            }
//
//            if(!Objects.equals(pdf2.getOriginalFilename(), "")){
//                FileUploadUtil.saveFile(uploadDir, portfolioName, pdf2);
//            }
//        }
//        catch (IOException e){
//            throw new RuntimeException(e);
//        }

        return "redirect:/dashboard/";
    }


    @GetMapping("/{id}")
    public String candidateProfile(@PathVariable("id") int id, Model model) {

        Optional<JobSeekerProfile> jobSeekerProfile = jobSeekerProfileService.getOne(id);
        model.addAttribute("profile", jobSeekerProfile.get());
        return "job-seeker-profile";
    }



    @GetMapping("/downloadResume")
    public ResponseEntity<byte[]> downloadResume(@RequestParam("fileName") String fileName,
                                                 @RequestParam("userID") String userId) {
        try {
            byte[] data = s3Service.downloadFile(userId, fileName);

            // 默认作为附件下载
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(data);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


    @GetMapping("/downloadPortfolio")
    public ResponseEntity<byte[]> downloadPortfolio(@RequestParam("fileName") String fileName,
                                                 @RequestParam("userID") String userId) {
        try {
            byte[] data = s3Service.downloadFile(userId, fileName);

            // 默认作为附件下载
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(data);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

//    @GetMapping("/downloadResume")
//    public ResponseEntity<?> downloadResume(@RequestParam(value="fileName") String fileName, @RequestParam(value="userID") String userId) {
//        FileDownloadUtil fileDownloadUtil = new FileDownloadUtil();
//        Resource resource = null;
//
//        try {
//            resource = fileDownloadUtil.getFileAsResourse("photos/candidate/" + userId, fileName);
//        } catch (IOException io) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        if (resource == null) {
//            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
//        }
//
//        String contentType = "application/octet-stream";
//        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
//
//        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
//                .body(resource);
//    }


//    @GetMapping("/downloadPortfolio")
//    public ResponseEntity<?> downloadPortfolio(@RequestParam(value="fileName") String fileName, @RequestParam(value="userID") String userId) {
//        FileDownloadUtil fileDownloadUtil = new FileDownloadUtil();
//        Resource resource = null;
//
//        try {
//            resource = fileDownloadUtil.getFileAsResourse("photos/candidate/" + userId, fileName);
//        } catch (IOException io) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        if (resource == null) {
//            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
//        }
//
//        String contentType = "application/octet-stream";
//        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
//
//        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
//                .body(resource);
//    }

}
