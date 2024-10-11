package in.webkorps.main.service;

import in.webkorps.main.dto.WrapperUser;
import in.webkorps.main.entity.User;
import in.webkorps.main.repository.UserRepo;
import in.webkorps.main.utlls.Validate_Email;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class UserServices {

    @Autowired
    private UserRepo userRepo;


    public User getStudent(int id) {
        return userRepo.findById(id).get();
    }

    public User saveStudent(User user, MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            user.setImageType(file.getContentType());
            user.setImageName(file.getOriginalFilename());
            user.setImageFile(file.getBytes());
        }
        if (Validate_Email.isAddressValid(user.getUsername())) {
            return userRepo.save(user);
        } else {
            throw new RuntimeException("Email Doesn't Exists");
        }
    }

    public User updateStudent(User user, MultipartFile file) throws IOException {
        User newUser = userRepo.findById(user.getUserId()).get();
        System.out.println("Inside Update Profile");
        newUser.setFirstname(user.getFirstname());
        newUser.setLastname(user.getLastname());
        newUser.setPassword(user.getPassword());
        newUser.setState(user.getState());
        newUser.setAddress(user.getAddress());
        newUser.setAddress(user.getAddress2());
        if (!file.isEmpty()) {
            newUser.setImageType(file.getContentType());
            System.out.println(file.getContentType());
            System.out.println(file.getOriginalFilename());
            newUser.setImageName(file.getOriginalFilename());
            newUser.setImageFile(file.getBytes());
        }
        return userRepo.save(newUser);
    }

    public User loginUser(User student) {
        User user = userRepo.findByUsername(student.getUsername());
        System.out.println(user.getUsername() + "::" + user.getPassword());
        System.out.println(user.getPassword().equals(student.getPassword()));
        if (user.getPassword().equals(student.getPassword())) {
            return user;
        } else {
            throw new RuntimeException("UserNotFoundOrInvalidCreds");
        }
    }

    public User getById(Integer id) {
        return userRepo.findById(id).get();
    }

    public List<WrapperUser> getUsersList() {
        List<WrapperUser> wrapperUsers = new ArrayList<>();
        for (User user : userRepo.findAll()) {
            wrapperUsers.add(new WrapperUser(user.getUserId(), user.getFirstname(), user.getLastname(), user.getUsername(), user.getAddress(), user.getAddress2(), user.getZip(), user.getState(), user.getCountry(), user.getImageType(), user.getImageType(), user.getImageFile(), "data:image/png;base64," + Base64.getEncoder().encodeToString(user.getImageFile()), user.getFollowStatus(), user.getFollowBackStatus()));
        }
        return wrapperUsers;
    }

    public List<WrapperUser> getFollowingUsersList(Integer userId) {
        List<WrapperUser> wrapperUsers = new ArrayList<>();
        for (User user : userRepo.findAll()) {
            if (!user.getUserId().equals(userId)) {
                wrapperUsers.add(new WrapperUser(user.getUserId(), user.getFirstname(), user.getLastname(), user.getUsername(), user.getAddress(), user.getAddress2(), user.getZip(), user.getState(), user.getCountry(), user.getImageType(), user.getImageType(), user.getImageFile(), "data:image/png;base64," + Base64.getEncoder().encodeToString(user.getImageFile()), user.getFollowStatus(), user.getFollowBackStatus()));
            }
        }
        return wrapperUsers;
    }

    @Transactional
    public String updateUserInTransaction(User user1, MultipartFile file) throws IOException {
        User user2 = userRepo.findById(user1.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String mesg = "User Updated Successfully";
        if (user1.getFirstname() != null && !user1.getFirstname().equals("")) {
            user2.setFirstname(user1.getFirstname());
        }
        if (user1.getLastname() != null && !user1.getLastname().equals("")) {
            user2.setLastname(user1.getLastname());
        }
        if (user1.getPassword() != null && !user1.getPassword().equals("")) {
            user2.setPassword(user1.getPassword());
        }
        if (user1.getState() != null && !user1.getState().equals("")) {
            user2.setState(user1.getState());
        }
        if (user1.getAddress() != null && !user1.getAddress().equals("")) {
            user2.setAddress(user1.getAddress());
        }
        if (user1.getAddress2() != null && !user1.getAddress2().equals("")) {
            user2.setAddress(user1.getAddress2());
        }
        if (user1.getZip() != null && !user1.getZip().equals("")) {
            user2.setZip(user1.getZip());
        }
        if (user1.getUsername() != null && !user1.getUsername().equals("") && Validate_Email.isAddressValid(user1.getUsername())) {
            System.out.println(Validate_Email.isAddressValid(user1.getUsername()));
            user2.setUsername(user1.getUsername());
        } else {
            mesg = "Invalid Email In User Name";
        }
        if (!file.isEmpty()) {
            user2.setImageType(file.getContentType());
            user2.setImageName(file.getOriginalFilename());
            user2.setImageFile(file.getBytes());
        }
        userRepo.save(user2);
        return mesg;
    }


}

