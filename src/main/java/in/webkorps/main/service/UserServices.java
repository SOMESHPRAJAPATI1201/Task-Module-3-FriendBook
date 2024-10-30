package in.webkorps.main.service;

import in.webkorps.main.dto.WrapperUser;
import in.webkorps.main.entity.User;
import in.webkorps.main.repository.UserRepo;
import in.webkorps.main.utlls.Validate_Email;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServices {

    @Autowired
    private UserRepo userRepo;


    public User getStudent(int id) {
        return userRepo.findById(id).get();
    }

    public Map<String, Object> saveUserWithValidations(User user1, MultipartFile file) throws IOException {
        Map<String, Object> map = new HashMap<>();
        String mesg = "User Updated Successfully";
        User user2 = new User();
        if (user1.getFirstname() != null && !user1.getFirstname().trim().equals("")) {
            user2.setFirstname(user1.getFirstname());
        } else {
            mesg = "Failed to update first name.";
        }
        if (user1.getLastname() != null && !user1.getLastname().trim().equals("")) {
            user2.setLastname(user1.getLastname());
        } else {
            mesg = "Failed to update last name.";
        }
        if (user1.getPassword() != null && !user1.getPassword().trim().equals("")) {
            user2.setPassword(user1.getPassword());
        } else {
            mesg = "Failed to update password.";
        }
        if (user1.getState() != null && !user1.getState().equals("")) {
            user2.setState(user1.getState());
        } else {
            mesg = "Failed to update state.";
        }
        if (user1.getAddress() != null && !user1.getAddress().trim().equals("")) {
            user2.setAddress(user1.getAddress());
        } else {
            mesg = "Failed to update Address1.";
        }
        if (user1.getAddress2() != null && !user1.getAddress2().trim().equals("")) {
            user2.setAddress2(user1.getAddress2());
        } else {
            mesg = "Failed to update Address1.";
        }
        if (user1.getZip() != null && !user1.getZip().trim().equals("")) {
            user2.setZip(user1.getZip());
        } else {
            mesg = "Failed to update zip.";
        }
        if (user1.getUsername() != null && !user1.getUsername().trim().equals("") && Validate_Email.isAddressValid(user1.getUsername())) {
            if (userRepo.findAll().stream().filter(x -> x.getUsername().equalsIgnoreCase(user1.getUsername())).collect(Collectors.toSet()).size() <= 0) {
                System.out.println(Validate_Email.isAddressValid(user1.getUsername()));
                user2.setUsername(user1.getUsername());
                mesg = "Duplicate Email";
            }
        } else {
            mesg = "Invalid Email In User Name";
        }
        if (file != null && !file.isEmpty() && mesg.equalsIgnoreCase("User Updated Successfully")) {
            File uploadDir = new File("C:\\Users\\Dell\\Documents\\FriendBook\\users\\");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String fileName = file.getOriginalFilename();
            File serverFile = new File(uploadDir, fileName);

            if (serverFile.exists()) {
                String newFileName = System.currentTimeMillis() + "_" + fileName; // Append current time to make it unique
                serverFile = new File(uploadDir, newFileName);
            }

            try (FileOutputStream fos = new FileOutputStream(serverFile)) {
                fos.write(file.getBytes());
                System.out.println("File saved successfully: " + serverFile.getAbsolutePath());
            }
            user2.setImageName(file.getOriginalFilename().trim());
            if (mesg.equalsIgnoreCase("User Updated Successfully")) {
                userRepo.save(user2);
            }
        } else {
            mesg = "File is empty.";
        }
        map.put("mesg", mesg);
        map.put("user", user2);
        return map;
    }


    public Map<String, Object> saveStudent(User user, MultipartFile file) throws IOException {
        return saveUserWithValidations(user, file);
    }

    public String GenerateDisplayName(String firstname) {
        Random random = new Random();
        int randomNumber = 100 + random.nextInt(900);
        String str = firstname.toUpperCase() + String.valueOf(randomNumber);
        return str;
    }

    public User loginUser(String username, String password) {
        User user = userRepo.findByUsername(username);
        System.out.println(user.getUsername() + "::" + user.getPassword());
        if (user.getPassword().equals(password)) {
            return user;
        } else {
            return null;
        }
    }

    public User getById(Integer id) {
        return userRepo.findById(id).get();
    }

    public List<WrapperUser> getUsersList() {
        List<WrapperUser> wrapperUsers = new ArrayList<>();
        for (User user : userRepo.findAll()) {
            wrapperUsers.add(new WrapperUser(user.getUserId(), user.getFirstname(), user.getLastname(), user.getUsername(), user.getAddress(), user.getAddress2(), user.getDisplayname(), user.getZip(), user.getState(), user.getCity(), user.getImageName(), user.getFollowStatus(), user.getFollowBackStatus()));
        }
        return wrapperUsers;
    }

    public List<User> getUserList() {
        List<User> users = new ArrayList<>();
        return users;
    }

    public List<WrapperUser> getUsersList1(Integer userId) {
        List<WrapperUser> wrapperUsers = new ArrayList<>();
        for (User user : userRepo.findAll()) {
            if (!user.getUserId().equals(userId)) {
                if (user.getFollowStatus() == null || user.getFollowStatus().equalsIgnoreCase("REQUESTED")) {
                    wrapperUsers.add(new WrapperUser(user.getUserId(), user.getFirstname(), user.getLastname(), user.getUsername(), user.getAddress(), user.getAddress2(), user.getDisplayname(), user.getZip(), user.getState(), user.getCity(), user.getImageName(), user.getFollowStatus(), user.getFollowBackStatus()));
                }
            }
        }
        return wrapperUsers;
    }

    public List<WrapperUser> getFollowingUsersList(Integer userId) {
        List<WrapperUser> wrapperUsers = new ArrayList<>();
        for (User user : userRepo.findAll()) {
            if (!user.getUserId().equals(userId)) {
                wrapperUsers.add(new WrapperUser(user.getUserId(), user.getFirstname(), user.getLastname(), user.getUsername(), user.getAddress(), user.getAddress2(), user.getDisplayname(), user.getZip(), user.getState(), user.getCity(), user.getImageName(), user.getFollowStatus(), user.getFollowBackStatus()));
            }
        }
        return wrapperUsers;
    }

    public WrapperUser getUsersById(Integer userId) {
        User user = userRepo.findById(userId).get();
        return new WrapperUser(user.getUserId(), user.getFirstname(), user.getLastname(), user.getUsername(), user.getAddress(), user.getAddress2(), user.getDisplayname(), user.getZip(), user.getState(), user.getCity(), user.getImageName(), user.getFollowStatus(), user.getFollowBackStatus());
    }

    @Transactional
    public String updateUserInTransaction(User user1, MultipartFile file) throws IOException {
        User user2 = userRepo.findById(user1.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String mesg = "User Updated Successfully";
        if (user1.getFirstname() != null && !user1.getFirstname().trim().equals("")) {
            user2.setFirstname(user1.getFirstname());
        } else {
            mesg = "Failed to update first name.";
        }
        if (user1.getLastname() != null && !user1.getLastname().trim().equals("")) {
            user2.setLastname(user1.getLastname());
        } else {
            mesg = "Failed to update last name.";
        }
        if (user1.getPassword() != null && !user1.getPassword().trim().equals("")) {
            user2.setPassword(user1.getPassword());
        } else {
            mesg = "Failed to update password.";
        }
        if (user1.getState() != null && !user1.getState().equals("")) {
            user2.setState(user1.getState());
        } else {
            mesg = "Failed to update state.";
        }
        if (user1.getAddress() != null && !user1.getAddress().trim().equals("")) {
            user2.setAddress(user1.getAddress());
        } else {
            mesg = "Failed to update Address1.";
        }
        if (user1.getAddress2() != null && !user1.getAddress2().trim().equals("")) {
            user2.setAddress2(user1.getAddress2());
        } else {
            mesg = "Failed to update Address1.";
        }
        if (user1.getZip() != null && !user1.getZip().trim().equals("")) {
            user2.setZip(user1.getZip());
        } else {
            mesg = "Failed to update zip.";
        }
        if (user1.getUsername() != null && !user1.getUsername().trim().equals("") && Validate_Email.isAddressValid(user1.getUsername())) {
            System.out.println(Validate_Email.isAddressValid(user1.getUsername()));
            user2.setUsername(user1.getUsername());
        } else {
            mesg = "Invalid Email In User Name";
        }
        if (file != null && !file.isEmpty()) {
            File uploadDir = new File("C:\\Users\\Dell\\Documents\\FriendBook\\users\\");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String fileName = file.getOriginalFilename();
            File serverFile = new File(uploadDir, fileName);

            if (serverFile.exists()) {
                String newFileName = System.currentTimeMillis() + "_" + fileName; // Append current time to make it unique
                serverFile = new File(uploadDir, newFileName);
            }

            try (FileOutputStream fos = new FileOutputStream(serverFile)) {
                fos.write(file.getBytes());
                System.out.println("File saved successfully: " + serverFile.getAbsolutePath());
            }
            user2.setImageName(file.getOriginalFilename().trim());
        }
        userRepo.save(user2);
        return mesg;
    }

    public String updateUserInTransaction(User user1) throws IOException {
        return updateUserInTransaction(user1, null);
    }

}

