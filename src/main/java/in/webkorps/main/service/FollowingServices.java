package in.webkorps.main.service;


import in.webkorps.main.entity.Following;
import in.webkorps.main.repository.FollowingsRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FollowingServices {

    @Autowired
    private UserServices userServices;

    @Autowired
    private FollowingsRepo followingsRepo;


    public Following findByFollowerId(Integer id){
        return  followingsRepo.findByFollowerId((int)id);
    }

    public Following getByUserIdAndFollowerId(Integer userId, Integer followerId){
        return followingsRepo.findByUserIdAndFollowerId(userId,followerId);
    }


    @Transactional
    public Following SetFollowing(Integer followerId, Integer userId){
        if (followingsRepo.findByUserIdAndFollowerId(userId, followerId)==null){
            Following following = new Following();
            following.setFollowerId(followerId);
            following.setUserId(userId);
            following.setFollowStatus(Following.FollowStatus.REQUESTED);
            followingsRepo.save(following);
            return followingsRepo.findByUserIdAndFollowerId(userId, followerId);
        }else{
            followingsRepo.deleteByUserIdAndFollowerId(userId, followerId);
            return new Following();
        }
    }

    public List<Following> getFollowing(Integer userId) {
        return followingsRepo.findByUserId(userId);
    }

    public Following getByUserIdAndFollower(Integer userId, Integer followerId){
        return followingsRepo.findByUserIdAndFollowerId(userId, followerId);
    }


    @Transactional
    public Following approveRequest(Integer userId, Integer followerId) {
        Following following = followingsRepo.findByUserIdAndFollowerId(userId, followerId);
        if (following == null) {
            throw new RuntimeException("UserNotFound");
        }else{
            if(following.getFollowStatus().equals(Following.FollowStatus.REQUESTED)){
                following.setFollowStatus(Following.FollowStatus.UNFOLLOW);
                followingsRepo.save(following);
                return following;
            }else{
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
        }else{
            if(following.getFollowStatus().equals(Following.FollowStatus.REQUESTED)){
                followingsRepo.deleteByUserIdAndFollowerId(userId, followerId);
            }
        }
        return new Following();
    }

    public Integer getFollowerCount(Integer userId){
        List<Following> followers = getFollowers(userId).stream().filter(x->x.getFollowStatus().toString().equals("REQUESTED")).toList();
        return followers.size();
    }


    public List<Following> getFollowers(Integer userId) {
        return followingsRepo.findByFollowerId(userId);
    }

}

