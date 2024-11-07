package in.webkorps.main.repository;

import in.webkorps.main.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostsRepo extends JpaRepository<Posts,Integer> {


    @Query("SELECT p FROM Posts p WHERE p.user.userId IN :ids")
    List<Posts> findAllByUserIdIn(Iterable<Integer> ids);

    List<Posts> findAllByUserUserId(Integer Id);

}
