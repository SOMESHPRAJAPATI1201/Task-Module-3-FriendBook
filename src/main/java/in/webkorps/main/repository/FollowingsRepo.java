package in.webkorps.main.repository;

import in.webkorps.main.entity.Following;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowingsRepo extends JpaRepository<Following,Integer> {

    Following findByUserIdAndFollowerId(Integer userId, Integer followerId);

    Integer deleteByUserIdAndFollowerId(Integer userId, Integer followerId);

    List<Following> findByUserId(Integer userId);

    List<Following> findByFollowerId(Integer userId);

    Following findByFollowerId(int userId);
}
