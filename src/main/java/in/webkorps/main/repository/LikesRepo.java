package in.webkorps.main.repository;

import in.webkorps.main.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LikesRepo extends JpaRepository<Likes,Integer> {

    List<Likes> findByPost_PostId(Integer postId);

    Likes findByUserIdAndPostPostId(Integer userId, Integer postId);

    Integer deleteByUserIdAndPostPostId(Integer userId, Integer postId);
}
