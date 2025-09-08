package com.springjobportal.job.portal.controller;

import com.springjobportal.job.portal.entity.RecruiterProfile;
import com.springjobportal.job.portal.entity.Users;
import com.springjobportal.job.portal.repository.UsersRepository;
import com.springjobportal.job.portal.services.RecruiterProfileService;
import com.springjobportal.job.portal.services.S3Service;
import com.springjobportal.job.portal.util.FileUploadUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {

    private final UsersRepository usersRepository;
    private final RecruiterProfileService recruiterProfileService;
    private final S3Service s3Service;

    public RecruiterProfileController(UsersRepository usersRepository, RecruiterProfileService recruiterProfileService, S3Service s3Service) {
        this.usersRepository = usersRepository;
        this.recruiterProfileService = recruiterProfileService;
        this.s3Service = s3Service;
    }

    @GetMapping("/")
    public String recruiterProfile(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("could not found " + currentUsername));

            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getOne(users.getUserId());

            if(!recruiterProfile.isEmpty()){
                model.addAttribute("profile", recruiterProfile.get());
            }

        }

        return "recruiter-profile";
    }


    @PostMapping("/addNew")
    public String addNew(RecruiterProfile recruiterProfile, @RequestParam("image") MultipartFile multipartFile, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = "";
        int userId = 0;

        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("could not found"));

            recruiterProfile.setUserId(users);
            userId = users.getUserId();
            recruiterProfile.setUserAccountId(userId);
        }
        model.addAttribute("profile", recruiterProfile);

        String fileName = "";

        if(!multipartFile.getOriginalFilename().equals("")){
            fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            recruiterProfile.setProfilePhoto(fileName);
        }

        RecruiterProfile savedUser = recruiterProfileService.addNew(recruiterProfile);

        String userID = userId + "";

        try {
            s3Service.uploadFile(userID, fileName, multipartFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/dashboard/";
    }


//    @PostMapping("/addNew")
//    public String addNew(RecruiterProfile recruiterProfile, @RequestParam("image") MultipartFile multipartFile, Model model){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if(!(authentication instanceof AnonymousAuthenticationToken)) {
//            String currentUsername = authentication.getName();
//            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("could not found " + currentUsername));
//
//            recruiterProfile.setUserId(users);
//            recruiterProfile.setUserAccountId(users.getUserId());
//        }
//        model.addAttribute("profile", recruiterProfile);
//
//        String fileName = "";
//
//        if(!multipartFile.getOriginalFilename().equals("")){
//            fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
//
//            recruiterProfile.setProfilePhoto(fileName);
//        }
//
//        RecruiterProfile savedUser = recruiterProfileService.addNew(recruiterProfile);
//
//        String uploadDir = "photos/recruiter/" + savedUser.getUserAccountId();
//
//        try {
//            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return "redirect:/dashboard/";
//    }
}
