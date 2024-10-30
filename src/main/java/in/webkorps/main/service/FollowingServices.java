package in.webkorps.main.service;


import in.webkorps.main.dto.WrapperUser;
import in.webkorps.main.entity.Following;
import in.webkorps.main.entity.User;
import in.webkorps.main.repository.FollowingsRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowingServices {

    @Autowired
    private UserServices userServices;

    @Autowired
    private FollowingsRepo followingsRepo;


    public Following findByFollowerId(Integer id) {
        return followingsRepo.findByFollowerId((int) id);
    }

    public Following getByUserIdAndFollowerId(Integer userId, Integer followerId) {
        return followingsRepo.findByUserIdAndFollowerId(userId, followerId);
    }


    @Transactional
    public Following SetFollowing(Integer followerId, Integer userId) {
        if (followingsRepo.findByUserIdAndFollowerId(userId, followerId) == null) {
            Following following = new Following();
            following.setFollowerId(followerId);
            following.setUserId(userId);
            following.setFollowStatus(Following.FollowStatus.REQUESTED);
            followingsRepo.save(following);
            return followingsRepo.findByUserIdAndFollowerId(userId, followerId);
        } else {
            followingsRepo.deleteByUserIdAndFollowerId(userId, followerId);
            return new Following();
        }
    }

    public List<Following> getFollowing(Integer userId) {
        return followingsRepo.findByUserId(userId);
    }

    public List<WrapperUser> getFollowing1(Integer userId) {
        List<WrapperUser> wrapperUsers = new ArrayList<>();
        List<WrapperUser> followings = userServices.getFollowingUsersList(userId);
        for (WrapperUser user1 : followings) {
            if (getByUserIdAndFollowerId(userId, user1.getUserId()) == null) {
                user1.setFollowStatus(Following.FollowStatus.FOLLOW.toString());
                wrapperUsers.add(user1);
            } else if (getByUserIdAndFollowerId(userId, user1.getUserId()).getFollowStatus().toString().equalsIgnoreCase("REQUESTED")) {
                user1.setFollowStatus(getByUserIdAndFollowerId(userId, user1.getUserId()).getFollowStatus().toString());
                wrapperUsers.add(user1);
            }
        }
        return wrapperUsers;
    }


    public Following getByUserIdAndFollower(Integer userId, Integer followerId) {
        return followingsRepo.findByUserIdAndFollowerId(userId, followerId);
    }


    @Transactional
    public Following approveRequest(Integer userId, Integer followerId) {
        Following following = followingsRepo.findByUserIdAndFollowerId(userId, followerId);
        if (following == null) {
            throw new RuntimeException("UserNotFound");
        } else {
            if (following.getFollowStatus().equals(Following.FollowStatus.REQUESTED)) {
                following.setFollowStatus(Following.FollowStatus.UNFOLLOW);
                followingsRepo.save(following);
                return following;
            } else {
                followingsRepo.deleteByUserIdAndFollowerId(userId, followerId);
                return new Following();
            }
        }
    }

    @Transactional
    public Following rejectRequest(Integer userId, Integer followerId) {
        Following following = followingsRepo.findByUserIdAndFollowerId(userId, followerId);
        if (following == null) {
            throw new RuntimeException("UserNotFound");
        } else {
            if (following.getFollowStatus().equals(Following.FollowStatus.REQUESTED)) {
                followingsRepo.deleteByUserIdAndFollowerId(userId, followerId);
            }
        }
        return new Following();
    }


    //follower count
    public Integer getFollowerCount(Integer userId) {
        return getFollowers1(userId).size();
    }

    //following count
    public Integer getFollowingCount(Integer userId) {
       return getFollowings(userId).size();
    }

    //RequestCount
    public Integer getRequestCount(Integer userId){
        return getFilteredFollowers(userId).size();
    }


    public List<WrapperUser> getFollowings(Integer userId) {
        List<WrapperUser> followings = userServices.getFollowingUsersList(userId);
        List<WrapperUser> wrapperUsers = new ArrayList<>();
        for (WrapperUser user1 : followings) {
            if (getByUserIdAndFollowerId(userId, user1.getUserId()) == null) {

            } else if (getByUserIdAndFollowerId(userId, user1.getUserId()).getFollowStatus().toString().equalsIgnoreCase("UNFOLLOW")) {
                user1.setFollowStatus(getByUserIdAndFollowerId(userId, user1.getUserId()).getFollowStatus().toString());
                wrapperUsers.add(user1);
            }
        }
        return wrapperUsers;
    }


    public List<Following> getFollowers(Integer userId) {
        return followingsRepo.findByFollowerId(userId);
    }

    public List<WrapperUser> getFilteredFollowers(Integer userId) {
        List<WrapperUser> allUsers = userServices.getUsersList();
        List<WrapperUser> renderUsers = new ArrayList<>();
        List<Following> followers = followingsRepo.findByFollowerId(userId);
        for (Following fellows : followers) {
            for (WrapperUser user : allUsers) {
                if (fellows.getUserId().equals(user.getUserId())) {
                    if (fellows.getFollowStatus().toString().equalsIgnoreCase("REQUESTED")) {
                        user.setFollowStatus(fellows.getFollowStatus().toString());
                        if (getByUserIdAndFollowerId(userId, fellows.getUserId()) != null) {
                            user.setFollowBackStatus(getByUserIdAndFollowerId(userId, fellows.getUserId()).getFollowStatus().toString());
                        }else{
                            user.setFollowBackStatus(Following.FollowStatus.FOLLOW.toString());
                        }
                        renderUsers.add(user);
                    }
                }
            }
        }
        return renderUsers;
    }

    public List<WrapperUser> getFollowers1(Integer userId) {
        List<WrapperUser> allUsers = userServices.getUsersList();
        List<WrapperUser> renderUsers = new ArrayList<>();
        List<Following> followers = getFollowers(userId);
        for (Following fellows : followers) {
            for (WrapperUser user : allUsers) {
                if (fellows.getUserId().equals(user.getUserId())) {
                    if (fellows.getFollowStatus().toString().equalsIgnoreCase("UNFOLLOW")) {
                        user.setFollowStatus(fellows.getFollowStatus().toString());
                        if (getByUserIdAndFollowerId(userId, fellows.getUserId()) != null) {
                            user.setFollowBackStatus(getByUserIdAndFollowerId(userId, fellows.getUserId()).getFollowStatus().toString());
                        } else {
                            user.setFollowBackStatus(Following.FollowStatus.FOLLOW.toString());
                        }
                        renderUsers.add(user);
                    }
                }
            }
        }
        return renderUsers;
    }

}

